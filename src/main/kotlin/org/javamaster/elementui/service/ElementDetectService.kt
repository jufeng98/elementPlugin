package org.javamaster.elementui.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.javamaster.elementui.nls.NlsBundle
import org.javamaster.elementui.support.ElementUIIcons
import java.io.File
import java.nio.file.Files

@Service(Service.Level.PROJECT)
class ElementDetectService(private val project: Project) {
    private val ui = "elementui"
    private val plus = "elementplus"

    private val elementPlus by lazy {
        detectElementFramework()
    }

    val icon by lazy {
        if (elementPlus) {
            ElementUIIcons.FILE_PLUS
        } else {
            ElementUIIcons.FILE
        }
    }

    val dirPrefix by lazy {
        if (elementPlus) {
            plus
        } else {
            ui
        }
    }

    fun getUrl(name: String): String {
        return if (elementPlus) {
            "https://cn.element-plus.org/${NlsBundle.region}/component/${name}.html"
        } else {
            "https://element.eleme.cn/#/${NlsBundle.region}/component/${name}"
        }
    }

    private fun detectElementFramework(): Boolean {
        val basePath = project.basePath ?: return false

        val file = File(basePath, "package.json")
        if (!file.exists()) {
            return false
        }

        val content = Files.readString(file.toPath())

        return content.contains("element-plus")
    }

    companion object {
        fun getInstance(project: Project): ElementDetectService {
            return project.getService(ElementDetectService::class.java)
        }
    }
}