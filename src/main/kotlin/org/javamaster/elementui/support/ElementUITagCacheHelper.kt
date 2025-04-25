package org.javamaster.elementui.support

import com.intellij.openapi.project.Project
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.util.PsiTreeUtil
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.xml.ElementUIXmlAttributeDescriptor
import java.nio.charset.StandardCharsets

object ElementUITagCacheHelper {
    private val classloader = this::class.java.classLoader

    private val tagMap = mutableMapOf<String, String?>()
    private val tagAttributeMap = mutableMapOf<String, Array<ElementUIXmlAttributeDescriptor>>()

    const val INFINITE_SCROLL = "v-infinite-scroll"
    const val V_LOADING = "v-loading"

    private val descriptorInfiniteScroll = ElementUIXmlAttributeDescriptor(
        INFINITE_SCROLL, "InfiniteScroll 无限滚动", arrayOf(), "",
        "loading", AttributeType.PARAM
    )
    private val descriptorLoading = ElementUIXmlAttributeDescriptor(
        V_LOADING, "Loading 加载", arrayOf(), "",
        "loading", AttributeType.PARAM
    )
    private val descriptorLoadingText = ElementUIXmlAttributeDescriptor(
        "element-loading-text", "值会被渲染为加载文案，并显示在加载图标的下方(搭配 $V_LOADING 使用)", arrayOf(),
        "", "拼命加载中", AttributeType.PARAM
    )
    private val descriptorLoadingSpinner = ElementUIXmlAttributeDescriptor(
        "element-loading-spinner", "设定图标类名(搭配 $V_LOADING 使用)", arrayOf(),
        "", "el-icon-loading", AttributeType.PARAM
    )
    private val descriptorLoadingBackground = ElementUIXmlAttributeDescriptor(
        "element-loading-background", "设定背景色值(搭配 $V_LOADING 使用)", arrayOf(),
        "", "el-icon-loading", AttributeType.PARAM
    )

    private val loadings =
        arrayOf(descriptorLoading, descriptorLoadingText, descriptorLoadingSpinner, descriptorLoadingBackground)

    fun getTagHtml(tagName: String): String? {
        return getTagHtml(tagName, "elementuiDoc/${tagName}.html")
    }

    fun getTagHtmlMock(mockTagName: String): String {
        return getTagHtml(mockTagName, "elementuiAttr/${mockTagName}.html")!!
    }

    private fun getTagHtml(tagName: String, filePath: String): String? {
        var html = tagMap[tagName]
        if (html != null) {
            return html
        }

        if (tagMap.containsKey(tagName)) {
            return null
        }

        synchronized(this) {
            html = tagMap[tagName]
            if (html != null) {
                return html
            }

            if (tagMap.containsKey(tagName)) {
                return null
            }

            val url = classloader.getResource(filePath)
            if (url == null) {
                tagMap[tagName] = null
            } else {
                val txt = url.readText(StandardCharsets.UTF_8)
                html = """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 500px;
                            }
                        </style>
                        <body>
                            $txt
                        </body>
                    """.trimIndent()
                tagMap[tagName] = html
            }
        }

        return html
    }

    fun getTagAttr(tagName: String, attrName: String, project: Project): ElementUIXmlAttributeDescriptor? {
        val descriptors = getTagAttrs(tagName, project)

        return descriptors.firstOrNull {
            it.name == attrName
        }
    }

    fun getTagAttrs(tagName: String, project: Project): Array<ElementUIXmlAttributeDescriptor> {
        val html = getTagHtml(tagName) ?: return emptyArray()

        val tagAttrs = getTagAttrs(tagName, html, project)

        val mockTagAttrsInfiniteScroll = getTagAttrsMock(INFINITE_SCROLL, project)

        return arrayOf(*tagAttrs, *mockTagAttrsInfiniteScroll, descriptorInfiniteScroll, *loadings)
    }

    private fun getTagAttrsMock(
        @Suppress("SameParameterValue") mockTagName: String,
        project: Project,
    ): Array<ElementUIXmlAttributeDescriptor> {
        val mockTagHtml = getTagHtmlMock(mockTagName)

        return getTagAttrs(mockTagName, mockTagHtml, project)
    }

    private fun getTagAttrs(tagName: String, html: String, project: Project): Array<ElementUIXmlAttributeDescriptor> {
        val descriptors = tagAttributeMap[tagName]
        if (descriptors != null) {
            return descriptors
        }

        synchronized(this) {
            var descriptorsTmp = tagAttributeMap[tagName]
            if (descriptorsTmp != null) {
                return descriptorsTmp
            }

            descriptorsTmp = resolveHtmlTagAttrs(html, project)

            tagAttributeMap[tagName] = descriptorsTmp

            return descriptorsTmp
        }
    }

    private fun resolveHtmlTagAttrs(html: String, project: Project): Array<ElementUIXmlAttributeDescriptor> {
        val htmlFile = HtmlPsiFactory.createDummyFile(project, html)

        val body = htmlFile.children[0].children.last()

        val htmlTags = PsiTreeUtil.getChildrenOfType(body, HtmlTag::class.java) ?: return emptyArray()

        val list = htmlTags
            .filter {
                it.name == "table"
            }
            .mapNotNull {
                val children = PsiTreeUtil.getChildrenOfType(it, HtmlTag::class.java) ?: return@mapNotNull null

                val nameMap = children.groupBy { child ->
                    val name = child.name
                    if (name == "thead" || name == "tbody") {
                        return@groupBy name
                    }

                    return@groupBy ""
                }

                val theadTags = nameMap["thead"] ?: return@mapNotNull null

                if (theadTags.isEmpty()) return@mapNotNull null

                var thTags = PsiTreeUtil.findChildrenOfType(theadTags[0], HtmlTag::class.java)

                thTags = thTags.filter { thTag -> thTag.name == "th" }

                if (thTags.isEmpty()) return@mapNotNull null

                val tableType = thTags[0].value.text

                val tbodyTags = nameMap["tbody"] ?: return@mapNotNull null

                if (tbodyTags.isEmpty()) return@mapNotNull null

                var trTags = PsiTreeUtil.findChildrenOfType(tbodyTags[0], HtmlTag::class.java)

                trTags = trTags.filter { trTag -> trTag.name == "tr" }

                if (trTags.isEmpty()) return@mapNotNull null

                when (tableType) {
                    "参数" -> {
                        trTags.map { trTag ->
                            resolveParams(trTag)
                        }
                    }

                    "事件名称" -> {
                        trTags.map { trTag ->
                            resolveEvents(trTag)
                        }
                    }

                    "事件名" -> {
                        trTags.map { trTag ->
                            resolveEvents(trTag)
                        }
                    }

                    else -> {
                        emptyList()
                    }
                }

            }
            .flatten()
            .flatten()

        return list.toTypedArray()
    }

    private fun resolveParams(trTag: HtmlTag): List<ElementUIXmlAttributeDescriptor> {
        var tdTags = PsiTreeUtil.findChildrenOfType(trTag, HtmlTag::class.java)

        tdTags = tdTags.filter { tdTag -> tdTag.name == "td" }

        val name = tdTags[0].value.text.split('/')[0].trim()
        val desc = tdTags[1].value.text
        val type = desc + " " + tdTags[2].value.text

        val defaultValue: String?
        var options: List<String>
        val rawAttributeValueHtml: String
        if (tdTags.size <= 3) {
            rawAttributeValueHtml = ""
            options = emptyList()
            defaultValue = null
        } else if (tdTags.size <= 4) {
            rawAttributeValueHtml = tdTags[3].value.text
            options = rawAttributeValueHtml.split("/")
            defaultValue = null
        } else {
            rawAttributeValueHtml = tdTags[3].value.text
            options = rawAttributeValueHtml.split("/")
            defaultValue = tdTags[4].value.text
        }

        if (options.size == 1 && (options[0] == "—" || options[0] == "-")) {
            options = emptyList()
        }

        options = options.map { it.trim() }

        if (options.any { it.isEmpty() }) {
            options = emptyList()
        }

        return listOf(
            ElementUIXmlAttributeDescriptor(
                name, type, options.toTypedArray(), rawAttributeValueHtml,
                defaultValue, AttributeType.PARAM
            )
        )
    }

    private fun resolveEvents(trTag: HtmlTag): List<ElementUIXmlAttributeDescriptor> {
        var tdTags = PsiTreeUtil.findChildrenOfType(trTag, HtmlTag::class.java)

        tdTags = tdTags.filter { tdTag -> tdTag.name == "td" }

        val name = tdTags[0].value.text
        val type = tdTags[1].value.text
        val defaultValue = tdTags[2].value.text

        return listOf(
            ElementUIXmlAttributeDescriptor(
                "@$name",
                type,
                arrayOf(),
                "",
                defaultValue,
                AttributeType.EVENT
            ),
        )
    }

}
