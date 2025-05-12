package org.javamaster.elementui.doc

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.xml.XmlAttribute
import com.intellij.util.SmartList
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.service.ElementDetectService
import org.javamaster.elementui.service.ElementTagCacheService

/**
 * @author yudong
 */
class ElementUIDocumentProvider : AbstractDocumentationProvider() {

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String> {
        element ?: return emptyList()

        val parent = element.parent
        val project = element.project

        val elementDetectService = ElementDetectService.getInstance(project)
        if (elementDetectService.notExistsElement) {
            return emptyList()
        }

        val name = if (element is XmlAttribute && parent is HtmlTag) {
            val name = element.name
            when (name) {
                ElementTagCacheService.V_LOADING -> {
                    "loading"
                }

                ElementTagCacheService.V_INFINITE_SCROLL -> {
                    elementDetectService.infiniteScrollName
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
                "tab-nav" -> "tab"
                "breadcrumb-item" -> "breadcrumb"
                "time-select" -> "time-picker"
                "timeline-item" -> "timeline"
                "step" -> "steps"
                "check-tag" -> "check"
                else -> str
            }
        } else {
            null
        }

        name ?: return emptyList()

        val url = elementDetectService.getUrl(name)

        return SmartList(url)
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        element ?: return null

        val project = element.project

        val elementDetectService = ElementDetectService.getInstance(project)
        if (elementDetectService.notExistsElement) {
            return null
        }

        if (element is HtmlTag) {
            return ElementTagCacheService.getInstance(element.project).getTagHtml(element.name)
        }

        val parent = element.parent
        if (element is XmlAttribute && parent is HtmlTag) {
            val tagName = parent.name
            val name = element.name

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

            val elementTagCacheService = ElementTagCacheService.getInstance(project)

            if (attrName == ElementTagCacheService.V_LOADING) {
                return elementTagCacheService.getVLoadingAttrHtml()
            }

            val descriptor = elementTagCacheService.getTagAttr(tagName, attrName) ?: return null

            return when (descriptor.attributeType) {
                AttributeType.PARAM -> {
                    elementTagCacheService.getTagAttrHtml(tagName, attrName, descriptor)
                }

                AttributeType.EVENT -> {
                    elementTagCacheService.getTagEventAttrHtml(tagName, attrName, descriptor)
                }
            }
        }

        return null
    }

}
