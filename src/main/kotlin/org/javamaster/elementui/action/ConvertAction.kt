package org.javamaster.elementui.action

import com.intellij.openapi.actionSystem.AnActionEvent
import org.javamaster.elementui.convert.ConvertMd

/**
 * @author yudong
 */
class ConvertAction : TestAction() {

    override fun wash(e: AnActionEvent) {
        ConvertMd.wash(e.project!!, "zh-CN", "zh")
        ConvertMd.wash(e.project!!, "en-US", "en")
    }

}
