package org.javamaster.elementui.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFileManager

abstract class TestAction : AnAction() {
    private val sandBox by lazy {
        inSandbox()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = sandBox
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (!sandBox) {
            return
        }

        wash(e)

        val virtualFileManager = VirtualFileManager.getInstance()

        try {
            val clz = virtualFileManager::class.java
            val method = clz.getMethod("asyncRefresh")
            method.isAccessible = true
            method.invoke(virtualFileManager)
        } catch (e: NoSuchMethodException) {
            System.err.println(e.message)
        }
    }

    private fun inSandbox(): Boolean {
        val path = System.getProperty("idea.log.path")
        return path != null && path.contains("sandbox")
    }

    abstract fun wash(e: AnActionEvent)
}
