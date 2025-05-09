package org.javamaster.elementui.provider

import com.intellij.psi.html.HtmlTag
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.javamaster.elementui.support.ElementUITagCacheHelper
import org.javamaster.elementui.xml.ElementUIXmlElementDescriptor

/**
 * @author yudong
 */
class ElementUIXmlElementDescriptorProvider : XmlElementDescriptorProvider {

    override fun getDescriptor(xmlTag: XmlTag): XmlElementDescriptor? {
        if (xmlTag !is HtmlTag) {
            return null
        }

        ElementUITagCacheHelper.getTagHtml(xmlTag.name, xmlTag.project) ?: return null

        val nsDescriptor = xmlTag.getNSDescriptor(xmlTag.namespace, false)

        val descriptor = nsDescriptor?.getElementDescriptor(xmlTag)

        return ElementUIXmlElementDescriptor(descriptor, nsDescriptor, xmlTag.name, xmlTag.project)
    }

}
