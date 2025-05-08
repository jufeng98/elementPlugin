package org.javamaster.elementui.support

import org.javamaster.elementui.convert.ConvertMd
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.nls.NlsBundle
import org.javamaster.elementui.xml.ElementUIXmlAttributeDescriptor
import java.nio.charset.StandardCharsets

object ElementUITagCacheHelper {
    private val classloader = this::class.java.classLoader
    private val showDateFormatTagSet = setOf(
        "el-time-picker",
        "el-date-picker",
        "el-datetime-picker",
    )
    private val showDateFormatAttrSet = setOf(
        "format",
        "value-format",
    )

    private val uiComponentMap = mutableMapOf<String, ElementUIComponent?>()
    private val tagHtmlMap = mutableMapOf<String, String?>()
    private val tagAttributeMap = mutableMapOf<String, Array<ElementUIXmlAttributeDescriptor>>()

    const val V_INFINITE_SCROLL = "v-infinite-scroll"
    const val V_LOADING = "v-loading"

    private val loadingAttrs =
        arrayOf(
            ElementUIXmlAttributeDescriptor(
                V_LOADING, "Loading 加载", arrayOf(), "",
                "loading", AttributeType.PARAM
            ),
            ElementUIXmlAttributeDescriptor(
                "element-loading-text", "值会被渲染为加载文案，并显示在加载图标的下方(搭配 $V_LOADING 使用)", arrayOf(),
                "", "拼命加载中", AttributeType.PARAM
            ),
            ElementUIXmlAttributeDescriptor(
                "element-loading-spinner", "设定图标类名(搭配 $V_LOADING 使用)", arrayOf(),
                "", "el-icon-loading", AttributeType.PARAM
            ),
            ElementUIXmlAttributeDescriptor(
                "element-loading-background", "设定背景色值(搭配 $V_LOADING 使用)", arrayOf(),
                "", "el-icon-loading", AttributeType.PARAM
            )
        )

    private val infiniteScrollAttrs = arrayOf(
        ElementUIXmlAttributeDescriptor(
            V_INFINITE_SCROLL, "InfiniteScroll 无限滚动", arrayOf(), "",
            "loading", AttributeType.PARAM
        ),
        *getTagAttrs(
            V_INFINITE_SCROLL,
            getUiComponent(V_INFINITE_SCROLL, "elementui_${NlsBundle.language}/${V_INFINITE_SCROLL}.json")!!
        )
    )

    fun getTagHtml(tagName: String): String? {
        return getTagHtml(tagName, "elementui_${NlsBundle.language}/${tagName}.json")
    }

    private fun getUiComponent(tagName: String, filePath: String): ElementUIComponent? {
        if (uiComponentMap.containsKey(tagName)) {
            return uiComponentMap[tagName]
        }

        synchronized(this) {
            if (uiComponentMap.containsKey(tagName)) {
                return uiComponentMap[tagName]
            }

            val url = classloader.getResource(filePath)
            if (url == null) {
                uiComponentMap[tagName] = null
                return null
            } else {
                val jsonStr = url.readText(StandardCharsets.UTF_8)
                val uiComponent = ConvertMd.gson.fromJson(jsonStr, ElementUIComponent::class.java)
                uiComponentMap[tagName] = uiComponent
                return uiComponent
            }
        }
    }

    private fun getTagHtml(tagName: String, filePath: String): String? {
        if (tagHtmlMap.containsKey(tagName)) {
            return tagHtmlMap[tagName]
        }

        synchronized(this) {
            if (tagHtmlMap.containsKey(tagName)) {
                return tagHtmlMap[tagName]
            }

            val uiComponent = getUiComponent(tagName, filePath)
            if (uiComponent == null) {
                tagHtmlMap[tagName] = null
                return null
            }

            val content = convertToHtml(uiComponent)

            val html = """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 600px;
                            }
                        </style>
                        <body>
                            $content
                        </body>
                    """.trimIndent()
            tagHtmlMap[tagName] = html

            return html
        }

    }

    private fun convertToHtml(uiComponent: ElementUIComponent): String {
        val sb = StringBuilder()

        if (uiComponent.name == V_LOADING) {
            sb.append(NlsBundle.nls("loading.desc"))
        }

        var content = convertAttrs(uiComponent.attributes, "Attributes")
        sb.append(content)

        content = convertAttrs(uiComponent.shortcuts, "Shortcuts")
        sb.append(content)

        content = convertAttrs(uiComponent.options, "Options")
        sb.append(content)

        content = convertAttrs(uiComponent.props, "Properties")
        sb.append(content)

        content = convertSlots(uiComponent.slots)
        sb.append(content)

        content = convertEvents(uiComponent.events)
        sb.append(content)

        content = convertMethods(uiComponent.methods)
        sb.append(content)

        if (showDateFormatTagSet.contains(uiComponent.name)) {
            val html = getDateFormatHtml()
            sb.append(html)
        }

        return sb.toString()
    }

    private fun getDateFormatHtml(): String {
        val url = classloader.getResource("elementui/ri-qi-ge-shi_${NlsBundle.language}.html")!!
        return url.readText(StandardCharsets.UTF_8)
    }

    fun getTagAttrHtml(tagName: String, attrName: String, descriptor: ElementUIXmlAttributeDescriptor): String {
        var tagAttr = getUiTagAttr(tagName, attrName)
        if (tagAttr == null) {
            tagAttr = ElementUIComponentAttr()
            tagAttr.name = descriptor.name
            tagAttr.desc = descriptor.typeName ?: ""
            tagAttr.type = descriptor.typeName ?: ""
            tagAttr.optionValue = descriptor.rawAttributeValueHtml
            tagAttr.defaultValue = descriptor.defaultValue ?: ""
        }

        var content = convertAttrs(listOf(tagAttr), "Attributes")
        if (showDateFormatTagSet.contains(tagName) && showDateFormatAttrSet.contains(attrName)) {
            val html = getDateFormatHtml()
            content += html
        }

        val html = """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 600px;
                            }
                        </style>
                        <body>
                            $content
                        </body>
                    """.trimIndent()
        return html
    }

    fun getTagEventAttrHtml(tagName: String, attrName: String, descriptor: ElementUIXmlAttributeDescriptor): String {
        var event = getUiTagEventAttr(tagName, attrName)
        if (event == null) {
            event = ElementUIComponentEvent()
            event.name = descriptor.name
            event.desc = descriptor.typeName ?: ""
        }

        val content = convertEvents(listOf(event))

        val html = """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 600px;
                            }
                        </style>
                        <body>
                            $content
                        </body>
                    """.trimIndent()
        return html
    }


    private fun convertAttrs(attributes: List<ElementUIComponentAttr>, title: String): String {
        if (attributes.isEmpty()) return ""

        val sb = StringBuilder()
        sb.append("<h3>${title}</h3>")
        sb.append("<table>")
        sb.append(
            """
            <thead>
            <tr>
                <th style="width:80px">${NlsBundle.nls("attr.name")}</th>
                <th style="width:120px">${NlsBundle.nls("desc")}</th>
                <th style="width:40px">${NlsBundle.nls("attr.type")}</th>
                <th style="width:60px">${NlsBundle.nls("attr.optional")}</th>
                <th style="width:80px">${NlsBundle.nls("attr.default")}</th>
            </tr>
            </thead>
        """.trimIndent()
        )
        sb.append("<tbody>")

        val content = attributes.joinToString("") {
            """
                <tr>
                    <td>${it.name}</td>
                    <td>${it.desc}</td>
                    <td>${it.type}</td>
                    <td>${it.optionValue}</td>
                    <td>${it.defaultValue}</td>
                </tr>
            """.trimIndent()
        }
        sb.append(content)

        sb.append("</tbody>")
        sb.append("</table>")

        return sb.toString()
    }

    private fun convertSlots(slots: List<ElementUIComponentSlot>): String {
        if (slots.isEmpty()) return ""

        val sb = StringBuilder()
        sb.append("<h3>Slots</h3>")
        sb.append("<table>")
        sb.append(
            """
            <thead>
            <tr>
                <th style="width:120px">${NlsBundle.nls("slot.name")}</th>
                <th style="width:120px">${NlsBundle.nls("desc")}</th>
            </tr>
            </thead>
        """.trimIndent()
        )
        sb.append("<tbody>")

        val content = slots.joinToString("") {
            """
                <tr>
                    <td>${it.name}</td>
                    <td>${it.desc}</td>
                </tr>
            """.trimIndent()
        }
        sb.append(content)

        sb.append("</tbody>")
        sb.append("</table>")

        return sb.toString()
    }

    private fun convertEvents(events: List<ElementUIComponentEvent>): String {
        if (events.isEmpty()) return ""

        val sb = StringBuilder()
        sb.append("<h3>Events</h3>")
        sb.append("<table>")
        sb.append(
            """
            <thead>
            <tr>
                <th style="width:120px">${NlsBundle.nls("event.name")}</th>
                <th style="width:120px">${NlsBundle.nls("desc")}</th>
                <th style="width:120px">${NlsBundle.nls("event.param")}</th>
            </tr>
            </thead>
        """.trimIndent()
        )
        sb.append("<tbody>")

        val content = events.joinToString("") {
            """
                <tr>
                    <td>${it.name}</td>
                    <td>${it.desc}</td>
                    <td>${it.param}</td>
                </tr>
            """.trimIndent()
        }
        sb.append(content)

        sb.append("</tbody>")
        sb.append("</table>")

        return sb.toString()
    }

    private fun convertMethods(methods: List<ElementUIComponentMethod>): String {
        if (methods.isEmpty()) return ""

        val sb = StringBuilder()
        sb.append("<h3>Methods</h3>")
        sb.append("<table>")
        sb.append(
            """
            <thead>
            <tr>
                <th style="width:120px">${NlsBundle.nls("method.name")}</th>
                <th style="width:120px">${NlsBundle.nls("desc")}</th>
                <th style="width:120px">${NlsBundle.nls("method.param")}</th>
            </tr>
            </thead>
        """.trimIndent()
        )
        sb.append("<tbody>")

        val content = methods.joinToString("") {
            """
                <tr>
                    <td>${it.name}</td>
                    <td>${it.desc}</td>
                    <td>${it.param}</td>
                </tr>
            """.trimIndent()
        }
        sb.append(content)

        sb.append("</tbody>")
        sb.append("</table>")

        return sb.toString()
    }

    fun getTagAttr(tagName: String, attrName: String): ElementUIXmlAttributeDescriptor? {
        val descriptors = getTagAttrs(tagName)

        return descriptors.firstOrNull {
            it.name == attrName
        }
    }

    private fun getUiTagAttr(tagName: String, attrName: String): ElementUIComponentAttr? {
        val uiComponent = getUiComponent(tagName, "elementui_${NlsBundle.language}/${tagName}.json")
            ?: return null

        return uiComponent.attributes
            .firstOrNull { it.name == attrName }
    }

    private fun getUiTagEventAttr(tagName: String, attrName: String): ElementUIComponentEvent? {
        val uiComponent = getUiComponent(tagName, "elementui_${NlsBundle.language}/${tagName}.json")
            ?: return null

        return uiComponent.events
            .firstOrNull { it.name == attrName }
    }

    fun getTagAttrs(tagName: String): Array<ElementUIXmlAttributeDescriptor> {
        val uiComponent = getUiComponent(tagName, "elementui_${NlsBundle.language}/${tagName}.json")
            ?: return emptyArray()

        val tagAttrs = getTagAttrs(tagName, uiComponent)

        return arrayOf(*tagAttrs, *infiniteScrollAttrs, *loadingAttrs)
    }

    fun getVLoadingAttrHtml(): String {
        return getTagHtml(V_LOADING)!!
    }


    private fun getTagAttrs(
        tagName: String,
        uiComponent: ElementUIComponent,
    ): Array<ElementUIXmlAttributeDescriptor> {
        val descriptors = tagAttributeMap[tagName]
        if (descriptors != null) {
            return descriptors
        }

        synchronized(this) {
            var descriptorsTmp = tagAttributeMap[tagName]
            if (descriptorsTmp != null) {
                return descriptorsTmp
            }

            descriptorsTmp = resolveHtmlTagAttrs(uiComponent)

            tagAttributeMap[tagName] = descriptorsTmp

            return descriptorsTmp
        }
    }

    private fun resolveHtmlTagAttrs(uiComponent: ElementUIComponent): Array<ElementUIXmlAttributeDescriptor> {
        val list = mutableListOf<ElementUIXmlAttributeDescriptor>()

        var descriptors = uiComponent.attributes
            .map {
                ElementUIXmlAttributeDescriptor(
                    it.name, it.type + " " + it.desc, it.options.toTypedArray(), it.optionValue,
                    it.defaultValue, AttributeType.PARAM
                )
            }
        list.addAll(descriptors)

        descriptors = uiComponent.events
            .map {
                ElementUIXmlAttributeDescriptor(
                    it.name,
                    it.desc,
                    arrayOf(),
                    "",
                    it.param,
                    AttributeType.EVENT
                )
            }
        list.addAll(descriptors)

        return list.toTypedArray()
    }

}
