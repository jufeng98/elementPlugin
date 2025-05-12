package org.javamaster.elementui.action

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId
import kotlin.io.path.pathString

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
    }

    private fun inSandbox(): Boolean {
        val pluginManager = PluginManager.getInstance()
        val plugin = pluginManager.findEnabledPlugin(PluginId.findId("org.javamaster.elementui")!!)!!
        val pluginPath = plugin.pluginPath.pathString
        return pluginPath.contains("idea-sandbox")
    }

    abstract fun wash(e: AnActionEvent)
}
