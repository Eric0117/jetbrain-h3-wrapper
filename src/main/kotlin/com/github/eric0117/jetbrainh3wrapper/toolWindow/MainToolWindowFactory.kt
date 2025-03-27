package com.github.eric0117.jetbrainh3wrapper.toolWindow

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.intellij.openapi.project.Project
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.uber.h3core.H3Core
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*
import java.awt.GridLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.regex.Pattern
import org.locationtech.jts.geom.*
import org.locationtech.jts.io.WKBWriter
import org.locationtech.jts.io.WKBReader
import org.locationtech.jts.io.WKTWriter
import org.locationtech.jts.io.WKTReader
import javax.xml.bind.DatatypeConverter

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainToolWindowContent = MainToolWindowContent(project)
        val content = ContentFactory.getInstance().createContent(mainToolWindowContent.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
