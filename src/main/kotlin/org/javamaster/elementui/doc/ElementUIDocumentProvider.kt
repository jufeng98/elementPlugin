package org.javamaster.elementui.doc

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.xml.XmlAttribute
import com.intellij.util.SmartList
import org.javamaster.elementui.enums.AttributeType
import org.javamaster.elementui.support.ElementUITagCacheHelper

/**
 * @author yudong
 */
class ElementUIDocumentProvider : AbstractDocumentationProvider() {

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String> {
        val parent = element?.parent
        val name = if (element is XmlAttribute && parent is HtmlTag) {
            val name = element.name
            when (name) {
                ElementUITagCacheHelper.V_LOADING -> {
                    "loading"
                }

                ElementUITagCacheHelper.INFINITE_SCROLL -> {
                    "infiniteScroll"
                }

                else -> {
                    null
                }
            }
        } else if (element is HtmlTag) {
            element.name.replace("el-", "")
        } else {
            null
        }

        name ?: return emptyList()

        return SmartList("https://element.eleme.cn/#/zh-CN/component/$name")
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (element is HtmlTag) {
            return ElementUITagCacheHelper.getTagHtml(element.name)
        }

        val parent = element.parent
        if (element is XmlAttribute && parent is HtmlTag) {
            val tagName = parent.name
            val project = element.project
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

            if (attrName == ElementUITagCacheHelper.V_LOADING) {
                return ElementUITagCacheHelper.getTagHtmlMock(attrName)
            }

            val descriptor = ElementUITagCacheHelper.getTagAttr(tagName, attrName, project) ?: return null

            return when (descriptor.attributeType) {
                AttributeType.PARAM -> {
                    """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 500px;
                            }
                        </style>
                        <body>
                            <table>
                                <thead>
                                <tr>
                                    <th>参数</th>
                                    <th>说明及类型</th>
                                    <th>可选值</th>
                                    <th>默认值</th>
                                </tr>
                                </thead>   
                                <tbody>
                                    <td>${descriptor.name}</td>
                                    <td>${descriptor.typeName}</td>
                                    <td>${descriptor.rawAttributeValueHtml}</td>
                                    <td>${descriptor.defaultValue ?: "—"}</td>                        
                                </tbody>
                            </table>
                        </body>
                    """.trimIndent()
                }

                AttributeType.EVENT -> {
                    """
                        <!DOCTYPE html>                
                        <style>
                            table {
                                width: 500px;
                            }
                        </style>
                        <body>
                            <table>
                                <thead>
                                <tr>
                                    <th>事件名称</th>
                                    <th>说明</th>
                                    <th>回调参数</th>
                                </tr>
                                </thead>   
                                <tbody>
                                    <td>${descriptor.name}</td>
                                    <td>${descriptor.typeName}</td>
                                    <td>${descriptor.defaultValue ?: "—"}</td>                        
                                </tbody>
                            </table>
                        </body>
                    """.trimIndent()
                }
            }
        }

        return null
    }

}
