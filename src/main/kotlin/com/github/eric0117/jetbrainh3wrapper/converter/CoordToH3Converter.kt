package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.intellij.openapi.ui.ComboBox
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*

class CoordToH3Converter(content: MainToolWindowContent) : BaseConverter(content) {
    private val coordTextField = JTextField("37.4864760734468, 127.02473584702024")
    private val resolutionComboBox = ComboBox(Array(16) { (it).toString() }).apply {
        selectedIndex = 8
    }

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
        val panel = JPanel(GridLayout(3, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel(LanguageBundle.message("label.coord")))
        panel.add(coordTextField)

        panel.add(JLabel(LanguageBundle.message("label.resolution")))
        panel.add(resolutionComboBox)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val convertButton = JButton(LanguageBundle.message("button.convertToH3"))
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
            // 좌표 파싱 (lat,lng 형식)
            val (lat, lng) = parseLatLng(coordTextField.text)
            val resolution = resolutionComboBox.selectedItem?.toString()?.toInt() ?: 8

            // 값 범위 체크
            val validationError = validateCoordinates(lat, lng)
            if (validationError != null) {
                setResult(validationError, "")
                return
            }

            // H3 인덱스 변환
            val h3Index = content.h3Core.latLngToCellAddress(lat, lng, resolution)

            val result = StringBuilder()
            result.appendLine(LanguageBundle.message("result.inputText"))
            result.appendLine(LanguageBundle.message("result.lat", lat))
            result.appendLine(LanguageBundle.message("result.lng", lng))
            result.appendLine(LanguageBundle.message("result.resolution", resolution))
            result.appendLine("\n${LanguageBundle.message("result.result")}")
            result.appendLine(LanguageBundle.message("result.h3Index", h3Index))

            setResult(result.toString(), h3Index.toString())
        } catch (e: Exception) {
            setResult("${LanguageBundle.message("error.convertError")} ${e.message}", "")
        }
    }
}