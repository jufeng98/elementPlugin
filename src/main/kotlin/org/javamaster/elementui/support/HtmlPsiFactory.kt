package org.javamaster.elementui.support

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.source.html.HtmlFileImpl

/**
 * @author yudong
 */
object HtmlPsiFactory {

    fun createDummyFile(project: Project, content: String): HtmlFileImpl {
        val fileType = HtmlFileType.INSTANCE
        val fileName = "dummy." + fileType.defaultExtension
        return PsiFileFactory.getInstance(project)
            .createFileFromText(fileName, fileType, content, System.currentTimeMillis(), false) as HtmlFileImpl
    }

}
