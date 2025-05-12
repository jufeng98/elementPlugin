package org.javamaster.elementui.provider

import com.intellij.psi.html.HtmlTag
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import org.javamaster.elementui.service.ElementDetectService
import org.javamaster.elementui.service.ElementTagCacheService

/**
 * @author yudong
 */
class ElementUIXmlAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {

    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<out XmlAttributeDescriptor> {
        val elementDetectService = ElementDetectService.getInstance(xmlTag.project)
        if (elementDetectService.notExistsElement) {
            return emptyArray()
        }

        if (xmlTag !is HtmlTag) {
            return XmlAttributeDescriptor.EMPTY
        }

        return ElementTagCacheService.getInstance(xmlTag.project).getTagAttrs(xmlTag.name)
    }

    override fun getAttributeDescriptor(s: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        val elementDetectService = ElementDetectService.getInstance(xmlTag.project)
        if (elementDetectService.notExistsElement) {
            return null
        }

        if (xmlTag !is HtmlTag) {
            return null
        }

        return ElementTagCacheService.getInstance(xmlTag.project).getTagAttr(xmlTag.name, s)
    }
}
