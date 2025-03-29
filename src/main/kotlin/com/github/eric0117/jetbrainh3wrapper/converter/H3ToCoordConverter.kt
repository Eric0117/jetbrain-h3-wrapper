package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*

class H3ToCoordConverter(content: MainToolWindowContent) : BaseConverter(content) {
    private val h3IndexTextField = JTextField("8830e1ca25fffff")

    override fun getConverterIndex(): Int {
        return content.getConverterIndex(this)
    }

    override fun showClipboardCopyButton(): Boolean {
        return true
    }

    override fun showWebButton(): Boolean {
        return true
    }

    override fun createPanel(): JPanel {
        val panel = JPanel(GridLayout(2, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel(LanguageBundle.message("label.h3Index")))
        panel.add(h3IndexTextField)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val convertButton = JButton(LanguageBundle.message("button.convertToCoord"))
        convertButton.addActionListener {
            convert()
        }
        buttonPanel.add(convertButton)

        panel.add(JLabel(""))
        panel.add(buttonPanel)

        return panel
    }

    override fun convert() {
        try {
            val h3Index = h3IndexTextField.text

            // H3 인덱스 유효성 확인
            if (!content.h3Core.isValidCell(h3Index)) {
                setResult(LanguageBundle.message("error.invalidH3Index"), "")
                return
            }

            // 좌표 변환
            val geoCoord = content.h3Core.cellToLatLng(h3Index)

            val lat = "%.10f".format(geoCoord.lat)
            val lng = "%.10f".format(geoCoord.lng)

            val result = StringBuilder()
            result.appendLine(LanguageBundle.message("result.inputText"))
            result.appendLine(LanguageBundle.message("result.h3Index", h3Index))
            result.appendLine("\n${LanguageBundle.message("result.result")}")
            result.appendLine(LanguageBundle.message("result.coord", lat, lng))


            setResult(result.toString(), "$lat,$lng")
        } catch (e: Exception) {
            setResult("${LanguageBundle.message("error.convertError")} ${e.message}", "")
        }
    }
}