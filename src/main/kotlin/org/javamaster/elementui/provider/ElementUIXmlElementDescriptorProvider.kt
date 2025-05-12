package org.javamaster.elementui.provider

import com.intellij.psi.html.HtmlTag
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.javamaster.elementui.service.ElementDetectService
import org.javamaster.elementui.service.ElementTagCacheService
import org.javamaster.elementui.xml.ElementUIXmlElementDescriptor

/**
 * @author yudong
 */
class ElementUIXmlElementDescriptorProvider : XmlElementDescriptorProvider {

    override fun getDescriptor(xmlTag: XmlTag): XmlElementDescriptor? {
        val elementDetectService = ElementDetectService.getInstance(xmlTag.project)
        if (elementDetectService.notExistsElement) {
            return null
        }

        if (xmlTag !is HtmlTag) {
            return null
        }

        val project = xmlTag.project
        val tagName = xmlTag.name

        ElementTagCacheService.getInstance(project).getTagHtml(tagName) ?: return null

        val nsDescriptor = xmlTag.getNSDescriptor(xmlTag.namespace, false)

        val descriptor = nsDescriptor?.getElementDescriptor(xmlTag)

        return ElementUIXmlElementDescriptor(descriptor, nsDescriptor, tagName, project)
    }

}
