package org.javamaster.elementui.convert

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author yudong
 */
class ConvertPlusAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        ConvertPlusMd.wash(e.project!!, "zh-CN", "zh")
        ConvertPlusMd.wash(e.project!!, "en-US", "en")
    }

}
