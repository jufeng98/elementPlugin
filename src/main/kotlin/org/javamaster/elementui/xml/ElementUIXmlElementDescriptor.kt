package org.javamaster.elementui.xml

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl
import com.intellij.psi.impl.source.xml.XmlDocumentImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.util.containers.ContainerUtil
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlElementsGroup
import com.intellij.xml.XmlNSDescriptor
import com.intellij.xml.impl.XmlElementDescriptorEx
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor
import org.jetbrains.annotations.NonNls

/**
 * @author yudong
 */
class ElementUIXmlElementDescriptor(
    xmlElementDescriptor: XmlElementDescriptor?,
    xmlNSDescriptor: XmlNSDescriptor?,
    private val name: String,
    project: Project,
) :
    AnyXmlElementDescriptor(xmlElementDescriptor, xmlNSDescriptor), XmlElementDescriptorEx {

    private val dumbService = project.getService(DumbService::class.java)

    override fun getQualifiedName(): String {
        return name
    }

    override fun getDefaultName(): String {
        return name
    }

    override fun getElementsDescriptors(context: XmlTag): Array<XmlElementDescriptor> {
        val xmlDocument = PsiTreeUtil.getParentOfType(context, XmlDocumentImpl::class.java) ?: return emptyArray()

        return xmlDocument.rootTagNSDescriptor.getRootElementsDescriptors(xmlDocument)
    }

    override fun getElementDescriptor(tag: XmlTag, contextTag: XmlTag): XmlElementDescriptor? {
        val parent = contextTag.parentTag ?: return null

        return parent.getNSDescriptor(tag.namespace, true)?.getElementDescriptor(tag)
    }

    override fun getAttributesDescriptors(context: XmlTag?): Array<XmlAttributeDescriptor> {
        if (dumbService.isDumb) {
            return emptyArray()
        }

        return HtmlNSDescriptorImpl.getCommonAttributeDescriptors(context)
    }

    override fun getAttributeDescriptor(attr: XmlAttribute): XmlAttributeDescriptor? {
        return getAttributeDescriptor(attr.name, attr.parent)
    }

    override fun getAttributeDescriptor(attributeName: @NonNls String, context: XmlTag?): XmlAttributeDescriptor? {
        return ContainerUtil.find(getAttributesDescriptors(context)) {
            attributeName == it.name
        }
    }

    override fun getNSDescriptor(): XmlNSDescriptor? {
        return null
    }

    override fun getTopGroup(): XmlElementsGroup? {
        return null
    }

    override fun getContentType(): Int {
        return CONTENT_TYPE_ANY
    }

    override fun getDefaultValue(): String? {
        return null
    }

    override fun getDeclaration(): PsiElement? {
        return null
    }

    override fun getName(context: PsiElement): String {
        return name
    }

    override fun getName(): String {
        return name
    }

    override fun init(element: PsiElement) {
    }

}
