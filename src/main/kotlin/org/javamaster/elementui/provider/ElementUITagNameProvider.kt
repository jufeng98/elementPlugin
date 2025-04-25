package org.javamaster.elementui.provider

import com.intellij.codeInsight.completion.XmlTagInsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlTagNameProvider

/**
 * @author yudong
 */
class ElementUITagNameProvider : XmlTagNameProvider {

    private val tagNames by lazy {
        val url = this::class.java.classLoader.getResource("elementuiDoc/el-alert.html")!!
        val files = VfsUtil.findFileByURL(url)!!.parent.children

        files.map {
            LookupElementBuilder.create(it.nameWithoutExtension).withInsertHandler(XmlTagInsertHandler.INSTANCE)
        }

    }

    override fun addTagNameVariants(list: MutableList<LookupElement>, xmlTag: XmlTag, s: String) {
        list.addAll(tagNames)
    }
}
