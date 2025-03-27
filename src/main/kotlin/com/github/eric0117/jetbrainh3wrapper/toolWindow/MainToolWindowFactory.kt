package com.github.eric0117.jetbrainh3wrapper.toolWindow

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainToolWindowContent = MainToolWindowContent(project)
        val content = ContentFactory.getInstance().createContent(mainToolWindowContent.getContent(), "", false)
        val icon = IconLoader.getIcon("/META-INF/pluginIcon.svg", MainToolWindowFactory::class.java)
        toolWindow.setIcon(icon)
        toolWindow.contentManager.addContent(content)
    }
}
