package com.github.eric0117.jetbrainh3wrapper.util

import com.intellij.ide.BrowserUtil
import javax.swing.JComponent
import javax.swing.JOptionPane

class BrowserUtil {
    fun openInBrowser(selectedTab: Int, lastResult: String, parentComponent: JComponent) {
        try {
            if (lastResult.isEmpty()) {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Please generate a result first.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
                )
                return
            }

            val url = when (selectedTab) {
                0 -> "https://h3geo.org/#hex=$lastResult" // 좌표→H3 탭
                1 -> "https://www.google.com/search?q=$lastResult" // H3→좌표 탭
                // 2, 3번 탭(좌표→Point, Point→좌표)은 웹 버튼이 숨겨져 있으므로 실행되지 않음
                else -> throw IllegalArgumentException("Unknown tab index: $selectedTab")
            }

            BrowserUtil.browse(url)
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Cannot open browser: ${e.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
}