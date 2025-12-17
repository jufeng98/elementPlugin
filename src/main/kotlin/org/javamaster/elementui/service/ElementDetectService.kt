package org.javamaster.elementui.service

import com.intellij.codeInsight.completion.XmlTagInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.readText
import org.javamaster.elementui.nls.NlsBundle
import org.javamaster.elementui.support.ElementUIIcons

@Service(Service.Level.PROJECT)
class ElementDetectService(private val project: Project) {
    private val ui = "elementui"
    private val plus = "elementplus"

    private val elementFlag by lazy {
        detectElementPlus()
    }

    private val elementPlus by lazy {
        elementFlag!!
    }

    val notExistsElement by lazy {
        elementFlag == null
    }

    val elementName by lazy {
        if (elementPlus) {
            "Element Plus(2.12.0)"
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

    val elementTagLookupElements by lazy {
        val dirPrefix = dirPrefix
        val icon = icon
        val elementName = elementName

        val url = this::class.java.classLoader.getResource("${dirPrefix}_${NlsBundle.language}/el-alert.json")!!
        val files = VfsUtil.findFileByURL(url)!!.parent.children

        files.map {
            LookupElementBuilder.create(it.nameWithoutExtension)
                .withInsertHandler(XmlTagInsertHandler.INSTANCE)
                .withTypeText(elementName)
                .withIcon(icon)
        }
    }

    private fun detectElementPlus(): Boolean? {
        val packageJsonFileManager = PackageJsonFileManager.getInstance(project)

        val validPackageJsonFiles = packageJsonFileManager.validPackageJsonFiles
        if (validPackageJsonFiles.isEmpty()) {
            return null
        }

        for (validPackageJsonFile in validPackageJsonFiles) {
            val content = validPackageJsonFile.readText()

            if (content.contains("element-plus")) {
                return true
            } else if (content.contains("element-ui")) {
                return false
            }
        }

        return null
    }

    companion object {
        fun getInstance(project: Project): ElementDetectService {
            return project.getService(ElementDetectService::class.java)
        }
    }
}