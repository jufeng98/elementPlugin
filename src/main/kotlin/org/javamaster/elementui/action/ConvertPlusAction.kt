package org.javamaster.elementui.action

import com.intellij.openapi.actionSystem.AnActionEvent
import org.javamaster.elementui.convert.ConvertPlusMd

/**
 * @author yudong
 */
class ConvertPlusAction : TestAction() {

    override fun wash(e: AnActionEvent) {
        ConvertPlusMd.wash(e.project!!, "zh-CN", "zh")
        ConvertPlusMd.wash(e.project!!, "en-US", "en")
    }

}
