package org.javamaster.elementui.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.psi.xml.XmlElement
import com.intellij.xml.impl.BasicXmlAttributeDescriptor
import com.intellij.xml.impl.XmlAttributeDescriptorEx
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.support.ElementUIIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * @author yudong
 */
class ElementUIXmlAttributeDescriptor(
    private val attributeName: String,
    private val typeName: String?,
    private val attributeValues: Array<String>,
    val rawAttributeValueHtml: String,
    private val defaultValue: String?,
    val attributeType: AttributeType,
) :
    BasicXmlAttributeDescriptor(), XmlAttributeDescriptorEx, PsiPresentableMetaData {

    override fun getDeclaration(): PsiElement? {
        return null
    }

    override fun getName(): String {
        return attributeName
    }

    override fun init(element: PsiElement) {
    }

    override fun isRequired(): Boolean {
        return false
    }

    override fun hasIdType(): Boolean {
        return false
    }

    override fun hasIdRefType(): Boolean {
        return false
    }

    override fun isEnumerated(): Boolean {
        return false
    }

    override fun isFixed(): Boolean {
        return false
    }

    override fun getDefaultValue(): String? {
        return defaultValue
    }

    override fun getEnumeratedValues(): Array<String> {
        return attributeValues
    }

    override fun getEnumeratedValueDeclaration(xmlElement: XmlElement, value: String): PsiElement {
        return xmlElement
    }

    override fun handleTargetRename(newTargetName: @NonNls String): String {
        return newTargetName
    }

    override fun getTypeName(): String? {
        return typeName
    }

    override fun getIcon(): Icon {
        return ElementUIIcons.FILE
    }
}
