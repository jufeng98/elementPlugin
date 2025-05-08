package org.javamaster.elementui.convert

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTable
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownTableRow
import org.javamaster.elementui.support.*
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author yudong
 */
object ConvertMd {
    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()

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
        val path = Paths.get("D:\\opsouce_project\\element\\examples\\docs\\${area}")

        for (file in path.toFile().listFiles()!!) {
            val virtualFile = VfsUtil.findFileByIoFile(file, true)
            val psiFile = PsiUtilCore.getPsiFile(project, virtualFile!!)
            val tables = PsiTreeUtil.findChildrenOfType(psiFile, MarkdownTable::class.java)

            val map = tables.groupBy {
                val header = PsiTreeUtil.getPrevSiblingOfType(it, MarkdownHeader::class.java)
                val text = header!!.contentElement!!.text.trim()
                if (text.contains("Breadcrumb Item")) {
                    return@groupBy "el-breadcrumb-item"
                } else if (text.contains("Carousel-Item")) {
                    return@groupBy "el-carousel-item"
                } else if (text.contains("CascaderPanel")) {
                    return@groupBy "el-cascader-panel"
                } else if (text.contains("Checkbox-group")) {
                    return@groupBy "el-checkbox-group"
                } else if (text.contains("Checkbox-button")) {
                    return@groupBy "el-checkbox-button"
                } else if (text.contains("Collapse Item")) {
                    return@groupBy "el-collapse-item"
                } else if (text.contains("Header")) {
                    return@groupBy "el-header"
                } else if (text.contains("Aside")) {
                    return@groupBy "el-aside"
                } else if (text.contains("Footer")) {
                    return@groupBy "el-footer"
                } else if (text.contains("Descriptions Item")) {
                    return@groupBy "el-descriptions-item"
                } else if (text.contains("Dropdown Menu Item")) {
                    return@groupBy "el-dropdown-item"
                } else if (text.contains("Form-Item")) {
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
                } else if (text.contains("Menu-Group")) {
                    return@groupBy "el-menu-item-group"
                } else if (text.contains("Radio-group")) {
                    return@groupBy "el-radio-group"
                } else if (text.contains("Radio-button")) {
                    return@groupBy "el-radio-button"
                } else if (text.contains("Option Group")) {
                    return@groupBy "el-option-group"
                } else if (text.contains("Option Attributes")) {
                    return@groupBy "el-option"
                } else if (text.contains("Skeleton Item")) {
                    return@groupBy "el-skeleton-item"
                } else if (text.contains("Table-column")) {
                    return@groupBy "el-table-column"
                } else if (text.contains("Tab-pane")) {
                    return@groupBy "el-tab-pane"
                } else if (text.contains("Time Select")) {
                    return@groupBy "el-time-select"
                } else if (text.contains("Timeline-item")) {
                    return@groupBy "el-timeline-item"
                } else {
                    val nameWithoutExtension = file.nameWithoutExtension
                    if (nameWithoutExtension == "loading") {
                        return@groupBy "v-loading"
                    } else if (nameWithoutExtension == "infiniteScroll") {
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

        val uiComponent = ElementUIComponent()
        uiComponent.name = "el-main"
        saveComp(uiComponent, lang)

        uiComponent.name = "el-button-group"
        saveComp(uiComponent, lang)

        uiComponent.name = "el-dropdown-menu"
        saveComp(uiComponent, lang)

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

            if (text.contains("attribute", ignoreCase = true)) {
                val attrs = table.getRows(false)
                    .map {
                        initAttr(it, ElementUIComponentAttr::class.java)
                    }.flatten()
                uiComponent.attributes = attrs
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
            } else if (text.contains("slot", ignoreCase = true)) {
                val slots = table.getRows(false)
                    .map {
                        val slot = ElementUIComponentSlot()
                        slot.name = it.getCell(0)!!.text.trim()
                        slot.desc = it.getCell(1)!!.text.trim()
                        slot
                    }
                uiComponent.slots = slots
            } else if (text.contains("event", ignoreCase = true)) {
                val events = table.getRows(false)
                    .map {
                        val event = ElementUIComponentEvent()
                        initEvent(event, it, "@")
                        event
                    }
                uiComponent.events = events
            } else if (text.contains("method", ignoreCase = true)) {
                val methods = table.getRows(false)
                    .map {
                        val event = ElementUIComponentMethod()
                        initEvent(event, it, "")
                        event
                    }
                uiComponent.methods = methods
            }
        }
    }

    private fun saveComp(uiComponent: ElementUIComponent, lang: String) {
        val outPath = Paths.get("D:\\my_opensource_project\\elementPlugin\\src\\main\\resources\\elementui_${lang}")
        val outFile = File(outPath.toFile(), uiComponent.name + ".json")
        Files.writeString(outFile.toPath(), gson.toJson(uiComponent))
    }

    private fun initAttr(it: MarkdownTableRow, clz: Class<out ElementUIComponentAttr>): List<ElementUIComponentAttr> {
        val name = it.getCell(0)!!.text.trim()

        val names = name.split("/")

        return names.map { innerIt ->
            val attr = clz.getDeclaredConstructor().newInstance()
            attr.name = innerIt.trim()
            attr.desc = it.getCell(1)!!.text.trim()
            attr.type = it.getCell(2)!!.text.trim()
            attr.optionValue = it.getCell(3)!!.text.trim()
            attr.defaultValue = it.getCell(4)!!.text.trim()

            val split = attr.optionValue.split("/")
            if (split.size > 1) {
                attr.options = split.map { s -> s.trim() }
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

        if (event.desc.contains("https://")) {
            event.desc = markdownToHtml(event.desc)
        }
    }

}