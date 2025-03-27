package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*

class H3ToCoordConverter(content: MainToolWindowContent) : BaseConverter(content) {
    private val h3IndexTextField = JTextField("8830e1ca25fffff")

    override fun createPanel(): JPanel {
        val panel = JPanel(GridLayout(2, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel("H3 Index:"))
        panel.add(h3IndexTextField)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val convertButton = JButton("Convert to Coord")
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
                content.setResult("Error: Invalid H3 index.", "")
                return
            }

            // 좌표 변환
            val geoCoord = content.h3Core.cellToLatLng(h3Index)
            val resolution = content.h3Core.getResolution(h3Index)

            val result = StringBuilder()
            result.appendLine("Input:")
            result.appendLine("- H3 Index: $h3Index")
            result.appendLine("\nResult:")
            result.appendLine("- Coord: ${geoCoord.lat},${geoCoord.lng}")

            content.setResult(result.toString(), "${geoCoord.lat},${geoCoord.lng}")
        } catch (e: Exception) {
            content.setResult("Convert Error: ${e.message}", "")
        }
    }
}