package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.intellij.ui.jcef.JBCefJSQuery
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * OpenStreetMap을 이용한 좌표 선택기
 * 지도에서 클릭하면 해당 위치의 위도/경도를 가져올 수 있습니다.
 */
class MapCoordinateSelector(content: MainToolWindowContent) : BaseConverter(content) {
    // UI 컴포넌트
    private val browser = JBCefBrowser()
    private val coordinateField = JTextField("")
    private val statusLabel = JLabel(LanguageBundle.message("label.mapMarker"))

    // 자바스크립트 통신을 위한 쿼리 객체
    private val jsQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

    // 리스너 참조 변수
    private lateinit var coordinateListener: DocumentListener

    // 마지막으로 설정된 좌표값
    private var lastLat: Double = 37.526886
    private var lastLng: Double = 126.966532

    override fun createPanel(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // 상단 상태 라벨
        val labelPanel = JPanel(BorderLayout())
        statusLabel.horizontalAlignment = SwingConstants.CENTER
        statusLabel.border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
        labelPanel.add(statusLabel, BorderLayout.CENTER)

        // 입력 패널
        val inputPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        inputPanel.add(JLabel(LanguageBundle.message("label.coord")))
        inputPanel.add(coordinateField)

        // 브라우저 패널
        val browserPanel = JPanel(BorderLayout())
        browser.component.preferredSize = Dimension(600, 400)
        browserPanel.add(browser.component, BorderLayout.CENTER)

        // HTML 지도 로드
        loadMap()

        // 실시간 입력 리스너 설정
        setupInputListener()

        // 전체 패널 레이아웃
        val topPanel = JPanel(BorderLayout())
        topPanel.add(labelPanel, BorderLayout.NORTH)
        topPanel.add(inputPanel, BorderLayout.CENTER)

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(browserPanel, BorderLayout.CENTER)

        return panel
    }

    /**
     * 맵 HTML 로드 및 자바스크립트 통신 설정
     */
    private fun loadMap() {
        val iconUrl = javaClass.getResource("/asset/map_pin.svg")
        // SVG 파일 내용 읽기
        val svgContent = iconUrl?.openStream()?.bufferedReader()?.use { it.readText() } ?: ""

        // SVG를 Base64로 인코딩
        val base64Icon = if (svgContent.isNotEmpty()) {
            Base64.getEncoder().encodeToString(svgContent.toByteArray())
        } else {
            ""
        }

        // 지도 HTML 생성
        val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>OSM Map</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            
            <!-- Leaflet CSS -->
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            
            <style>
                html, body, #map {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            
            <!-- Leaflet JavaScript -->
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            
            <script>
                // 지도 초기화
                var iconData = "$base64Icon";
                var map = L.map('map').setView([37.526886,126.966532], 15);
                
                
                // 타일 레이어 추가 (OpenStreetMap)
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                }).addTo(map);
                
                var customIcon = L.icon({
                    iconUrl: iconData ? 'data:image/svg+xml;base64,' + iconData : 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
                    iconSize: [32, 32],  // 이미지 크기에 맞게 조정
                    iconAnchor: [16, 32], // 이미지의 "바닥" 부분이 좌표에 위치하도록 조정
                    popupAnchor: [0, -32] // 팝업이 적절한 위치에 표시되도록 조정
                });
                
                var marker = null;
                
                // 지도 클릭 이벤트 리스너
                map.on('click', function(e) {
                    var lat = e.latlng.lat;
                    var lng = e.latlng.lng;
                    
                    // 마커 업데이트
                    updateMarker(lat, lng);
                    
                    // 좌표 값을 자바로 전달
                    var coordText = lat.toFixed(10) + ',' + lng.toFixed(10);
                    ${jsQuery.inject("coordText")}
                });
                
                // 마커 업데이트 함수
                function updateMarker(lat, lng) {
                    // 기존 마커 제거
                    if (marker) {
                        map.removeLayer(marker);
                    }
                    
                    // 새 마커 추가
                    marker = L.marker([lat, lng], {icon: customIcon}).addTo(map);
                    marker.bindPopup('${LanguageBundle.message("label.lat")}: ' + lat.toFixed(10) + '<br>${LanguageBundle.message("label.lng")}: ' + lng.toFixed(10)).openPopup();
                }
                
                // 외부(자바)에서 호출할 함수: 마커 설정 및 지도 중심 이동
                function setMarkerAndCenter(lat, lng) {
                    updateMarker(lat, lng);
                    map.setView([lat, lng], map.getZoom());
                }
                
                // 오직 마커만 업데이트하는 함수
                function setMarkerOnly(lat, lng) {
                    updateMarker(lat, lng);
                }
            </script>
        </body>
        </html>
        """.trimIndent()

        // 브라우저에 HTML 로드
        browser.loadHTML(html)

        // 자바스크립트 -> 자바 콜백 설정
        jsQuery.addHandler { value ->
            try {
                // 좌표 필드 업데이트 (리스너 일시 제거)
                coordinateField.document.removeDocumentListener(coordinateListener)
                coordinateField.text = value
                coordinateField.document.addDocumentListener(coordinateListener)

                // 상태 라벨 업데이트
                updateStatusLabel(value)

                // 좌표 파싱 및 저장
                val (lat, lng) = parseLatLng(value)
                lastLat = lat
                lastLng = lng
                content.setResult("${lastLat},${lastLng}", "${lastLat},${lastLng}")
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * 입력 필드의 실시간 리스너 설정
     */
    private fun setupInputListener() {
        coordinateListener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = updateMapFromInput()
            override fun removeUpdate(e: DocumentEvent?) = updateMapFromInput()
            override fun changedUpdate(e: DocumentEvent?) = updateMapFromInput()
        }

        // 리스너 등록
        coordinateField.document.addDocumentListener(coordinateListener)
    }

    /**
     * 입력 필드 값으로 지도 업데이트
     */
    private fun updateMapFromInput() {
        try {
            val text = coordinateField.text
            if (text.isBlank()) return

            // 좌표 파싱 및 유효성 검사
            val (lat, lng) = parseLatLng(text)
            val error = validateCoordinates(lat, lng)

            if (error != null) {
                content.setResult(error, "")
                // 유효하지 않은 좌표면 상태 라벨만 업데이트
                statusLabel.text = error
                return
            }

            // 좌표 유효하면 마커 업데이트 및 상태 라벨 갱신
            lastLat = lat
            lastLng = lng
            updateStatusLabel(text)

            content.setResult("${lastLat},${lastLng}", "${lastLat},${lastLng}")

            // 자바스크립트를 통해 지도 마커 업데이트 (중심 이동 없음)
            browser.cefBrowser.executeJavaScript(
                "setMarkerOnly($lat, $lng);",
                browser.cefBrowser.url, 0
            )
        } catch (e: Exception) {
            e.message?.let { content.setResult(it, "") }
            // 입력값 파싱 실패는 무시 (아직 입력 중일 수 있음)
        }
    }

    /**
     * 상태 라벨 업데이트
     */
    private fun updateStatusLabel(coordText: String) {
        try {
            val (lat, lng) = parseLatLng(coordText)
            statusLabel.text = String.format(LanguageBundle.message("result.mapMarker"), lat, lng)
        } catch (e: Exception) {
            statusLabel.text = LanguageBundle.message("error.mapMarkerCoord")
        }
    }

    override fun convert() {

    }
}