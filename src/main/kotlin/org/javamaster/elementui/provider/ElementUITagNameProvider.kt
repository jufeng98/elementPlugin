package org.javamaster.elementui.provider

import com.intellij.codeInsight.completion.XmlTagInsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlTagNameProvider
import org.javamaster.elementui.nls.NlsBundle
import org.javamaster.elementui.service.ElementDetectService

/**
 * @author yudong
 */
class ElementUITagNameProvider : XmlTagNameProvider {
    private lateinit var elementDetectService: ElementDetectService

    private val elementTagLookupElements by lazy {
        val dirPrefix = elementDetectService.dirPrefix
        val icon = elementDetectService.icon
        val elementName = elementDetectService.elementName

        val url = this::class.java.classLoader.getResource("${dirPrefix}_${NlsBundle.language}/el-alert.json")!!
        val files = VfsUtil.findFileByURL(url)!!.parent.children

        files.map {
            LookupElementBuilder.create(it.nameWithoutExtension)
                .withInsertHandler(XmlTagInsertHandler.INSTANCE)
                .withTypeText(elementName)
                .withIcon(icon)
        }
    }

    override fun addTagNameVariants(list: MutableList<LookupElement>, xmlTag: XmlTag, s: String) {
        elementDetectService = ElementDetectService.getInstance(xmlTag.project)

        list.addAll(elementTagLookupElements)
    }
}
