package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.io.WKBWriter
import org.locationtech.jts.io.WKTWriter
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import javax.xml.bind.DatatypeConverter

class LatLngToPointConverter(content: MainToolWindowContent) : BaseConverter(content) {
    private val latLngToPointTextField = JTextField("37.4864760734468, 127.02473584702024")

    override fun createPanel(): JPanel {
        val panel = JPanel(GridLayout(2, 2, 5, 5))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        panel.add(JLabel("Coord (lat, lng):"))
        panel.add(latLngToPointTextField)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val convertButton = JButton("Convert to Point")
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
            val (lat, lng) = parseLatLng(latLngToPointTextField.text)

            // 값 범위 체크
            val validationError = validateCoordinates(lat, lng)
            if (validationError != null) {
                content.setResult(validationError, "")
                return
            }

            // WKT Point 형식으로 변환 (WKT는 lng,lat 순서를 사용)
            val point = content.geoFactory.createPoint(Coordinate(lng, lat))

            // WKT 형식으로 변환
            val wktWriter = WKTWriter()
            val pointWkt = wktWriter.write(point)

            // WKB 형식으로 변환 (바이너리)
            val wkbWriter = WKBWriter()
            val wkbBytes = wkbWriter.write(point)

            // 16진수 문자열로 변환
            val hexWkb = "0x" + DatatypeConverter.printHexBinary(wkbBytes)

            val result = StringBuilder()
            result.appendLine("Input:")
            result.appendLine("- Latitude: $lat")
            result.appendLine("- Longitude: $lng")
            result.appendLine("\nResult:")
            result.appendLine("- JTS Point: $point")
            result.appendLine("- WKT: $pointWkt")
            result.appendLine("- WKB (Hex): $hexWkb")

            content.setResult(result.toString(), hexWkb)
        } catch (e: Exception) {
            content.setResult("Convert Error: ${e.message}", "")
        }
    }
}