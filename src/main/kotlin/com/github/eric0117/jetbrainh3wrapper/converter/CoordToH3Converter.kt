package com.github.eric0117.jetbrainh3wrapper.converter

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

    override fun createPanel(): JPanel {
        val panel = JPanel(GridLayout(3, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel("Coord (lat, lng):"))
        panel.add(coordTextField)

        panel.add(JLabel("Resolution (0-15):"))
        panel.add(resolutionComboBox)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val convertButton = JButton("Convert to H3 Index")
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
                content.setResult(validationError, "")
                return
            }

            // H3 인덱스 변환
            val h3Index = content.h3Core.latLngToCellAddress(lat, lng, resolution)

            val result = StringBuilder()
            result.appendLine("Input:")
            result.appendLine("- Latitude: $lat")
            result.appendLine("- Longitude: $lng")
            result.appendLine("- Resolution: $resolution")
            result.appendLine("\nResult:")
            result.appendLine("- H3 Index: $h3Index")

            content.setResult(result.toString(), h3Index.toString())
        } catch (e: Exception) {
            content.setResult("Convert Error: ${e.message}", "")
        }
    }
}