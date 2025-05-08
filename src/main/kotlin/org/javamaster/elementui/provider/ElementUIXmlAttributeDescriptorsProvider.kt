package org.javamaster.elementui.provider

import com.intellij.psi.html.HtmlTag
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import org.javamaster.elementui.support.ElementUITagCacheHelper

/**
 * @author yudong
 */
class ElementUIXmlAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {

    override fun getAttributeDescriptors(xmlTag: XmlTag): Array<out XmlAttributeDescriptor> {
        val tagName = xmlTag.name

        if (xmlTag !is HtmlTag) {
            return XmlAttributeDescriptor.EMPTY
        }

        return ElementUITagCacheHelper.getTagAttrs(tagName)
    }

    override fun getAttributeDescriptor(s: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        return null
    }
}
