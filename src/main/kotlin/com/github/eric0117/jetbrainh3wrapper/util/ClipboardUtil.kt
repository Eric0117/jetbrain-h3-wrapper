package com.github.eric0117.jetbrainh3wrapper.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JComponent
import javax.swing.JOptionPane

class ClipboardUtil {
    fun copyToClipboard(text: String, parentComponent: JComponent) {
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "No result to copy",
                "Error",
                JOptionPane.WARNING_MESSAGE
            )
            return
        }

        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)

        // 사용자에게 복사 완료 알림
        JOptionPane.showMessageDialog(
            parentComponent,
            "Copied to clipboard: $text",
            "Copy complete",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}