package org.javamaster.elementui.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.vuejs.lang.expr.highlighting.VueJSSyntaxHighlighter
import org.jetbrains.vuejs.lang.html.VueFile

/**
 * @author yudong
 */
class ElementUIOtherTemplateContextType : TemplateContextType("InVueJs") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        val offset = templateActionContext.startOffset
        return file is VueFile && PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(JavascriptLanguage.INSTANCE)
    }

    override fun createHighlighter(): SyntaxHighlighter {
        return VueJSSyntaxHighlighter()
    }

}
