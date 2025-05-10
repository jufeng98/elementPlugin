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
        if (xmlTag !is HtmlTag) {
            return XmlAttributeDescriptor.EMPTY
        }

        return ElementUITagCacheHelper.getTagAttrs(xmlTag.name, xmlTag.project)
    }

    override fun getAttributeDescriptor(s: String, xmlTag: XmlTag): XmlAttributeDescriptor? {
        if (xmlTag !is HtmlTag) {
            return null
        }

        return ElementUITagCacheHelper.getTagAttr(xmlTag.name, s, xmlTag.project)
    }
}
