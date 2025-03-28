package com.github.eric0117.jetbrainh3wrapper.util

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JComponent
import javax.swing.JOptionPane

class ClipboardUtil {
    fun copyToClipboard(text: String, parentComponent: JComponent) {
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(
                parentComponent,
                LanguageBundle.message("message.clipboard.dialog1"),
                LanguageBundle.message("label.error"),
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
            LanguageBundle.message("message.clipboard.dialog2", text),
            LanguageBundle.message("message.clipboard.complete"),
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}