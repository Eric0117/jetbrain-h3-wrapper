package com.github.eric0117.jetbrainh3wrapper.util

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JComponent
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

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

        // 스크롤 가능한 텍스트 영역이 있는 커스텀 다이얼로그
        val textArea = JTextArea(10, 50)
        textArea.text = text
        textArea.isEditable = false
        textArea.lineWrap = true
        textArea.wrapStyleWord = true

        val scrollPane = JScrollPane(textArea)

        JOptionPane.showMessageDialog(
            parentComponent,
            scrollPane,
            LanguageBundle.message("message.clipboard.complete"),
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}