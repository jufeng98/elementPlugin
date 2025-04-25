package org.javamaster.elementui.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.ide.highlighter.HtmlFileHighlighter
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.PsiFile

class ElementUIVueTemplateContextType : TemplateContextType("ElementUI") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return inContext(templateActionContext.file)
    }

    private fun inContext(file: PsiFile): Boolean {
        return file.language.baseLanguage is HTMLLanguage
    }

    override fun createHighlighter(): SyntaxHighlighter {
        return HtmlFileHighlighter()
    }
}
