package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.WKBReader
import org.locationtech.jts.io.WKTReader
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import javax.xml.bind.DatatypeConverter

class PointToLatLngConverter(content: MainToolWindowContent) : BaseConverter(content) {
    private val pointToLatLngTextField = JTextField("0x0000000001405FC19545A97F694042BE44D914DEC9")

    override fun createPanel(): JPanel {
        val panel = JPanel(GridLayout(2, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel("WKT Point:"))
        panel.add(pointToLatLngTextField)

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
            val input = pointToLatLngTextField.text.trim()

            // JTS 라이브러리를 사용하여 Point 파싱
            val point: Point

            // 입력이 WKT 형식인지 WKB 16진수 형식인지 확인
            if (input.startsWith("POINT", ignoreCase = true)) {
                // WKT 형식 처리
                val wktReader = WKTReader()
                point = wktReader.read(input) as Point
            } else if (input.startsWith("0x", ignoreCase = true)) {
                // WKB 16진수 형식 처리
                val wkbReader = WKBReader()
                val hexString = input.substring(2) // "0x" 제거
                val wkbBytes = DatatypeConverter.parseHexBinary(hexString)
                point = wkbReader.read(wkbBytes) as Point
            } else {
                content.setResult("Error: Unsupported format. Please use WKT(POINT(longitude latitude)) or WKB(0x...) format.", "")
                return
            }

            // Point에서 좌표 추출 (x=경도, y=위도)
            val lng = point.x
            val lat = point.y

            // 값 범위 체크
            val validationError = validateCoordinates(lat, lng)
            if (validationError != null) {
                content.setResult(validationError, "")
                return
            }

            // 결과 출력
            val result = StringBuilder()
            result.appendLine("Input:")
            result.appendLine("- Point: $input")
            result.appendLine("\nResult:")
            result.appendLine("- Coord: $lat,$lng")

            content.setResult(result.toString(), "$lat,$lng")
        } catch (e: Exception) {
            content.setResult("Convert Error: ${e.message}", "")
            e.printStackTrace()
        }
    }
}