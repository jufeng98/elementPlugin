package org.javamaster.elementui.doc

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.xml.XmlAttribute
import com.intellij.util.SmartList
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.service.ElementDetectService
import org.javamaster.elementui.support.ElementUITagCacheHelper

/**
 * @author yudong
 */
class ElementUIDocumentProvider : AbstractDocumentationProvider() {

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String> {
        val parent = element?.parent ?: return emptyList()

        val name = if (element is XmlAttribute && parent is HtmlTag) {
            val name = element.name
            when (name) {
                ElementUITagCacheHelper.V_LOADING -> {
                    "loading"
                }

                ElementUITagCacheHelper.V_INFINITE_SCROLL -> {
                    "infiniteScroll"
                }

                else -> {
                    null
                }
            }
        } else if (element is HtmlTag) {
            when (val str = element.name.replace("el-", "")) {
                "form-item" -> "form"
                "table-column" -> "table"
                "option" -> "select"
                "radio-button" -> "radio"
                "row" -> "layout"
                "col" -> "layout"
                "radio-group" -> "radio"
                "option-group" -> "select"
                "main" -> "container"
                "aside" -> "container"
                "header" -> "container"
                "footer" -> "container"
                "autocomplete" -> "input"
                "input-number" -> "input"
                "cascader-panel" -> "cascader"
                "carousel-item" -> "carousel"
                "checkbox-button" -> "checkbox"
                "checkbox-group" -> "checkbox"
                "descriptions-item" -> "descriptions"
                "dropdown-item" -> "dropdown"
                "dropdown-menu" -> "dropdown"
                "menu-item" -> "menu"
                "menu-item-group" -> "menu"
                "submenu" -> "menu"
                "skeleton-item" -> "skeleton"
                "tab-pane" -> "tab"
                "breadcrumb-item" -> "breadcrumb"
                "time-select" -> "time-picker"
                "timeline-item" -> "timeline"
                else -> str
            }
        } else {
            null
        }

        name ?: return emptyList()

        val project = element.project
        val elementDetectService = ElementDetectService.getInstance(project)

        val url = elementDetectService.getUrl(name)

        return SmartList(url)
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element is HtmlTag) {
            return ElementUITagCacheHelper.getTagHtml(element.name, element.project)
        }

        val parent = element?.parent
        if (element is XmlAttribute && parent is HtmlTag) {
            val tagName = parent.name
            val name = element.name
            val project = element.project

            val idx = name.indexOf(':')
            val startIndex = idx + 1
            if (name.length == startIndex) {
                return null
            }

            val split = name.split(':')
            val attrName = if (split.size == 1) {
                split[0]
            } else {
                val direction = split[0]
                val type = split[1].split('.')[0]

                if (direction == "v-on") {
                    "@$type"
                } else {
                    type
                }
            }

            if (attrName == ElementUITagCacheHelper.V_LOADING) {
                return ElementUITagCacheHelper.getVLoadingAttrHtml(project)
            }

            val descriptor = ElementUITagCacheHelper.getTagAttr(tagName, attrName, project) ?: return null

            return when (descriptor.attributeType) {
                AttributeType.PARAM -> {
                    ElementUITagCacheHelper.getTagAttrHtml(tagName, attrName, descriptor, project)
                }

                AttributeType.EVENT -> {
                    ElementUITagCacheHelper.getTagEventAttrHtml(tagName, attrName, descriptor, project)
                }
            }
        }

        return null
    }

}
