package org.javamaster.elementui.convert

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTableRow
import org.javamaster.elementui.convert.ConvertMd.gson
import org.javamaster.elementui.support.*
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

/**
 * @author yudong
 */
object ConvertPlusMd {

    private val converter: Any
    private val method: Method

    init {
        val clz = Class.forName("com.intellij.markdown.utils.MarkdownToHtmlConverter")
        val constructor = clz.constructors[0]
        constructor.isAccessible = true
        converter = constructor.newInstance(CommonMarkFlavourDescriptor())
        method = clz.getDeclaredMethod("convertMarkdownToHtml", String::class.java, String::class.java)
        method.isAccessible = true
    }

    private fun markdownToHtml(markdown: String): String {
        val s = method.invoke(converter, markdown, null) as String
        return s.replace("<body>", "").replace("</body>", "")
    }

    fun wash(project: Project, area: String, lang: String) {
        val path = Paths.get("D:\\opsouce_project\\element-plus\\docs\\${area}\\component")

        for (file in path.toFile().listFiles()!!) {
//            if (file.nameWithoutExtension != "input") {
//                continue
//            }

            val virtualFile = VfsUtil.findFileByIoFile(file, true)
            val psiFile = PsiUtilCore.getPsiFile(project, virtualFile!!)
            val tables = PsiTreeUtil.findChildrenOfType(psiFile, MarkdownTable::class.java)

            val map = tables.groupBy {
                val header = PsiTreeUtil.getPrevSiblingOfType(it, MarkdownHeader::class.java)
                val text = header!!.contentElement!!.text.trim()
                if (text.contains("BreadcrumbItem")) {
                    return@groupBy "el-breadcrumb-item"
                } else if (text.contains("AnchorLink")) {
                    return@groupBy "el-anchor-link"
                } else if (text.contains("ButtonGroup")) {
                    return@groupBy "el-button-group"
                } else if (text.contains("Carousel-Item")) {
                    return@groupBy "el-carousel-item"
                } else if (text.contains("CascaderPanel")) {
                    return@groupBy "el-cascader-panel"
                } else if (text.contains("CheckboxGroup")) {
                    return@groupBy "el-checkbox-group"
                } else if (text.contains("CheckboxButton")) {
                    return@groupBy "el-checkbox-button"
                } else if (text.contains("Collapse Item")) {
                    return@groupBy "el-collapse-item"
                } else if (text.contains("Header")) {
                    return@groupBy "el-header"
                } else if (text.contains("Aside")) {
                    return@groupBy "el-aside"
                } else if (text.contains("Footer")) {
                    return@groupBy "el-footer"
                } else if (text.contains("Main")) {
                    return@groupBy "el-main"
                } else if (text.contains("DescriptionsItem")) {
                    return@groupBy "el-descriptions-item"
                } else if (text.contains("Dropdown-Menu")) {
                    return@groupBy "el-dropdown-menu"
                } else if (text.contains("Dropdown-Item")) {
                    return@groupBy "el-dropdown-item"
                } else if (text.contains("Image Viewer")) {
                    return@groupBy "el-image-viewer"
                } else if (text.contains("FormItem")) {
                    return@groupBy "el-form-item"
                } else if (text.contains("Autocomplete")) {
                    return@groupBy "el-autocomplete"
                } else if (text.contains("Row")) {
                    return@groupBy "el-row"
                } else if (text.contains("Col")) {
                    return@groupBy "el-col"
                } else if (text.contains("SubMenu")) {
                    return@groupBy "el-submenu"
                } else if (text.contains("Menu-Item")) {
                    return@groupBy "el-menu-item"
                } else if (text.contains("Menu-Item-Group")) {
                    return@groupBy "el-menu-item-group"
                } else if (text.contains("RadioGroup")) {
                    return@groupBy "el-radio-group"
                } else if (text.contains("RadioButton")) {
                    return@groupBy "el-radio-button"
                } else if (text.contains("Option Group")) {
                    return@groupBy "el-option-group"
                } else if (text.contains("Option Attributes") || text.contains("Option Slots")) {
                    return@groupBy "el-option"
                } else if (text.contains("SkeletonItem")) {
                    return@groupBy "el-skeleton-item"
                } else if (text.contains("Table-column")) {
                    return@groupBy "el-table-column"
                } else if (text.contains("Tab-pane")) {
                    return@groupBy "el-tab-pane"
                } else if (text.contains("Tab-nav")) {
                    return@groupBy "el-tab-nav"
                } else if (text.contains("CheckTag")) {
                    return@groupBy "el-check-tag"
                } else if (text.contains("TourStep")) {
                    return@groupBy "el-tour-step"
                } else if (text.contains("Transfer Panel")) {
                    return@groupBy "el-transfer-panel"
                } else if (text.contains("Timeline-Item")) {
                    return@groupBy "el-timeline-item"
                } else if (text.contains("Countdown")) {
                    return@groupBy "el-countdown"
                } else if (text.contains("Step ")) {
                    return@groupBy "el-step"
                } else {
                    val nameWithoutExtension = file.nameWithoutExtension
                    if (nameWithoutExtension == "loading") {
                        return@groupBy "v-loading"
                    } else if (nameWithoutExtension == "infinite-scroll") {
                        return@groupBy "v-infinite-scroll"
                    }

                    return@groupBy "el-$nameWithoutExtension"
                }
            }

            map.forEach { entry ->
                val fileName = entry.key
                val tablesTmp = entry.value

                val uiComponent = initComp(tablesTmp, fileName)

                saveComp(uiComponent, lang)
            }

        }

        VirtualFileManager.getInstance().asyncRefresh()
    }

    private fun initComp(tables: List<MarkdownTable>, fileName: String): ElementUIComponent {
        val uiComponent = ElementUIComponent()
        uiComponent.name = fileName

        initUiComponent(tables, uiComponent)

        return uiComponent
    }

    @Suppress("UNCHECKED_CAST")
    private fun initUiComponent(
        tables: List<MarkdownTable>,
        uiComponent: ElementUIComponent,
    ) {
        for (table in tables) {
            val header = PsiTreeUtil.getPrevSiblingOfType(table, MarkdownHeader::class.java)
            val text = header!!.contentElement!!.text.trim()

            if (text.contains("attribute", ignoreCase = true)
                || text.contains("属性")
                || text.contains("Directives")
                || text.contains("指令")
            ) {
                val attrs = table.getRows(false)
                    .map {
                        initAttr(it, ElementUIComponentAttr::class.java)
                    }.flatten()
                uiComponent.attributes = attrs
            } else if (text.contains("slot", ignoreCase = true) || text.contains("插槽")) {
                val slots = table.getRows(false)
                    .map {
                        val slot = ElementUIComponentSlot()
                        slot.name = it.getCell(0)!!.text.trim()
                        slot.desc = it.getCell(1)!!.text.trim()
                        slot
                    }
                uiComponent.slots = slots
            } else if (text.contains("event", ignoreCase = true) || text.contains("事件")) {
                val events = table.getRows(false)
                    .map {
                        val event = ElementUIComponentEvent()
                        initEvent(event, it, "@")
                        event
                    }
                uiComponent.events = events
            } else if (text.contains("method", ignoreCase = true)
                || text.contains("expose", ignoreCase = true)
                || text.contains("暴露")
                || text.contains("方法")
            ) {
                val methods = table.getRows(false)
                    .map {
                        val event = ElementUIComponentMethod()
                        initEvent(event, it, "")
                        event
                    }
                uiComponent.methods = methods
            } else if (text.contains("prop", ignoreCase = true)) {
                val props = table.getRows(false)
                    .map {
                        initAttr(it, ElementUIComponentProp::class.java)
                    }.flatten()
                uiComponent.props = props as List<ElementUIComponentProp>
            } else if (text.contains("option", ignoreCase = true)) {
                val options = table.getRows(false)
                    .map {
                        initAttr(it, ElementUIComponentOption::class.java)
                    }.flatten()
                uiComponent.options = options as List<ElementUIComponentOption>
            } else if (text.contains("shortcut", ignoreCase = true)) {
                val shortcuts = table.getRows(false)
                    .map {
                        initAttr(it, ElementUIComponentShortcut::class.java)
                    }.flatten()
                uiComponent.shortcuts = shortcuts as List<ElementUIComponentShortcut>
            }
        }
    }

    private fun saveComp(uiComponent: ElementUIComponent, lang: String) {
        val outPath = Paths.get("D:\\my_opensource_project\\elementPlugin\\src\\main\\resources\\elementplus_${lang}")
        val outFile = File(outPath.toFile(), uiComponent.name + ".json")
        Files.writeString(outFile.toPath(), gson.toJson(uiComponent))
    }

    private val pattern = Pattern.compile("[a-zA-Z]+")

    private fun extractAlpha(input: String): String {
        val matcher = pattern.matcher(input)
        val result = StringBuilder()
        while (matcher.find()) {
            result.append(matcher.group())
        }

        return result.toString()
    }

    private fun initAttr(it: MarkdownTableRow, clz: Class<out ElementUIComponentAttr>): List<ElementUIComponentAttr> {
        val name = it.getCell(0)!!.text.trim()

        val names = name.split("/")

        return names.map { innerIt ->
            val attr = clz.getDeclaredConstructor().newInstance()

            attr.name = extractAlpha(innerIt)

            attr.desc = it.getCell(1)!!.text.trim().replace("`", "")

            val tmpStr = it.getCell(2)!!.text.trim()
            val strs = tmpStr.split("`")

            attr.type = strs[0].replace("^[", "").replace("]", "")

            if (strs.size > 1) {
                attr.optionValue = strs[1].replace("'", "").replace("\\", "")
            }

            attr.defaultValue = it.getCell(3)?.text?.trim()?.replace("`", "")?.replace("'", "") ?: ""

            if (!attr.optionValue.contains("=>") && !attr.type.contains("object")) {
                val split = attr.optionValue.split("|")
                if (split.size > 1) {
                    attr.options = split.map { s -> s.trim() }.filter { it.isNotEmpty() }
                }
            }

            if (attr.optionValue.contains("ri-qi-ge-shi") || attr.optionValue.contains("date-formats")) {
                attr.optionValue = markdownToHtml(attr.optionValue)
                attr.options = mutableListOf()
            } else if (attr.optionValue.contains("https://")) {
                attr.optionValue = markdownToHtml(attr.optionValue)
                attr.options = mutableListOf()
            } else if (attr.desc.contains("https://")) {
                attr.desc = markdownToHtml(attr.desc)
            } else if (attr.defaultValue.contains("https://")) {
                attr.defaultValue = markdownToHtml(attr.defaultValue)
            }

            attr
        }
    }

    private fun initEvent(event: ElementUIComponentEvent, it: MarkdownTableRow, s: String) {
        event.name = s + it.getCell(0)!!.text.trim()
        event.desc = it.getCell(1)!!.text.trim()
        event.param = it.getCell(2)?.text?.trim() ?: ""
        event.param = event.param.replace("^[", "").replace("]", "")
            .replace("`", "").replace("\\", "").replace("'", "")

        if (event.desc.contains("https://")) {
            event.desc = markdownToHtml(event.desc)
        }
    }

}