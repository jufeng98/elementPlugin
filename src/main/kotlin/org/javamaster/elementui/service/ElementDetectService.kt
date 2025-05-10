package org.javamaster.elementui.service

import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.readText
import org.javamaster.elementui.nls.NlsBundle
import org.javamaster.elementui.support.ElementUIIcons

@Service(Service.Level.PROJECT)
class ElementDetectService(private val project: Project) {
    private val ui = "elementui"
    private val plus = "elementplus"

    private val elementPlus by lazy {
        detectElementPlus()
    }

    val elementName by lazy {
        if (elementPlus) {
            "Element Plus(2.9.10)"
        } else {
            "Element UI(2.15.14)"
        }
    }

    val infiniteScrollName by lazy {
        if (elementPlus) {
            "infinite-scroll"
        } else {
            "infiniteScroll"
        }
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

    private fun detectElementPlus(): Boolean {
        val packageJsonFileManager = PackageJsonFileManager.getInstance(project)
        
        val validPackageJsonFiles = packageJsonFileManager.validPackageJsonFiles

        return validPackageJsonFiles.any {
            val content = it.readText()

            return content.contains("element-plus")
        }
    }

    companion object {
        fun getInstance(project: Project): ElementDetectService {
            return project.getService(ElementDetectService::class.java)
        }
    }
}