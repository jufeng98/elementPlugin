package org.javamaster.elementui.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.vuejs.lang.html.VueFile

/**
 * @author yudong
 */
class ElementUIVueTemplateContextType : TemplateContextType("InVue") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        val offset = templateActionContext.startOffset
        return file is VueFile && PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(HTMLLanguage.INSTANCE)
    }


    override fun createHighlighter(): SyntaxHighlighter {
        return HtmlFileHighlighter()
    }

}
