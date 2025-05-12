package org.javamaster.elementui.provider

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlTagNameProvider
import org.javamaster.elementui.service.ElementDetectService

/**
 * @author yudong
 */
class ElementUITagNameProvider : XmlTagNameProvider {

    override fun addTagNameVariants(list: MutableList<LookupElement>, xmlTag: XmlTag, s: String) {
        val elementDetectService = ElementDetectService.getInstance(xmlTag.project)
        if (elementDetectService.notExistsElement) {
            return
        }

        list.addAll(elementDetectService.elementTagLookupElements)
    }

}
