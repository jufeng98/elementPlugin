package org.javamaster.elementui.convert

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author yudong
 */
class ConvertAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        ConvertMd.wash(e.project!!, "zh-CN", "zh")
        ConvertMd.wash(e.project!!, "en-US", "en")
    }

}
