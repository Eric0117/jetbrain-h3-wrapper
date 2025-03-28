package com.github.eric0117.jetbrainh3wrapper.util

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.intellij.ide.BrowserUtil
import javax.swing.JComponent
import javax.swing.JOptionPane

class BrowserUtil {
    fun openInBrowser(selectedTab: Int, lastResult: String, parentComponent: JComponent) {
        try {
            if (lastResult.isEmpty()) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    LanguageBundle.message("message.browser.dialog1"),
                    LanguageBundle.message("label.error"),
                    JOptionPane.WARNING_MESSAGE
                )
                return
            }

            val url = when (selectedTab) {
                0 -> "https://h3geo.org/#hex=$lastResult" // 좌표→H3 탭
                1 -> "https://www.google.com/search?q=$lastResult" // H3→좌표 탭
                else -> throw IllegalArgumentException(LanguageBundle.message("error.unknownTabIndex", selectedTab))
            }

            BrowserUtil.browse(url)
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                parentComponent,
                LanguageBundle.message("message.browser.dialog2", e.message ?: ""),
                LanguageBundle.message("label.error"),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
}