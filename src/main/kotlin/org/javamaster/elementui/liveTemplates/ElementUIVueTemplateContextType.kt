package org.javamaster.elementui.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.vuejs.lang.html.VueLanguage

/**
 * @author yudong
 */
class ElementUIVueTemplateContextType : TemplateContextType("InVue") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        val offset = templateActionContext.startOffset
        return PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(VueLanguage.INSTANCE)
    }


    override fun createHighlighter(): SyntaxHighlighter {
        return HtmlFileHighlighter()
    }

}
