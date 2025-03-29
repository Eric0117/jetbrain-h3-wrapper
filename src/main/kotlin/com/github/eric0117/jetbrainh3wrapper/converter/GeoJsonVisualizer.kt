package com.github.eric0117.jetbrainh3wrapper.converter

import ai.grazie.utils.json.JSONObject
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.awt.Dimension
import java.awt.BorderLayout

class GeoJsonVisualizer(content: MainToolWindowContent) : BaseConverter(content) {
    private val browser: JBCefBrowser = JBCefBrowser()
    private val statusLabel = JLabel(LanguageBundle.message("geojson.status.ready"))
    private val inputTextArea = JTextArea(8, 50)

    // 자바스크립트 통신을 위한 쿼리 객체
    private val jsQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

    override fun getConverterIndex(): Int {
        return content.getConverterIndex(this)
    }

    override fun showResultPanel(): Boolean {
        return false
    }

    override fun showClipboardCopyButton(): Boolean {
        return false
    }

    override fun showWebButton(): Boolean {
        return false
    }

    override fun createPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        // 입력 패널
        val inputPanel = JPanel(BorderLayout())
        val inputLabel = JLabel(LanguageBundle.message("label.geoJsonInput"))

        // 입력 텍스트 영역 설정
        inputTextArea.lineWrap = true
        inputTextArea.wrapStyleWord = true
        inputTextArea.text = ""
        val inputScrollPane = JBScrollPane(inputTextArea)

        // 입력 변경 리스너 - 텍스트 변경시 실시간 렌더링
        inputTextArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) { debounceRender() }
            override fun removeUpdate(e: DocumentEvent) { debounceRender() }
            override fun changedUpdate(e: DocumentEvent) { debounceRender() }
        })

        // 입력 패널 구성
        inputPanel.add(inputLabel, BorderLayout.NORTH)
        inputPanel.add(inputScrollPane, BorderLayout.CENTER)

        // 맵 패널
        val browserPanel = JPanel(BorderLayout())
        browser.component.preferredSize = Dimension(600, 600)
        browserPanel.add(browser.component, BorderLayout.CENTER)

        // 전체 패널을 수직으로 분할
        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, browserPanel)
        splitPane.resizeWeight = 0.3  // 입력 패널이 30% 차지

        // 전체 패널 구성
        panel.add(splitPane, BorderLayout.CENTER)

        // 맵 초기화
        initializeMap()

        return panel
    }

    private fun initializeMap() {
        // HTML 콘텐츠 로드 (맵과 GeoJSON 렌더링 기능 포함)
        browser.loadHTML(getMapHtml())

        // 페이지 로드 완료 후 JavaScript 브릿지 설정
        setupJavascriptBridge()
    }

    private fun setupJavascriptBridge() {
        try {
            // 자바스크립트에서 호출 가능한 메서드 등록
            jsQuery.addHandler { message ->
                handleJSMessage(message)
                JBCefJSQuery.Response("success")
            }

            // 브라우저에 핸들러 등록
            val jsCode = """
                window.sendMessageToKotlin = function(message) {
                    ${jsQuery.inject("message")};
                };
            """.trimIndent()

            browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
        } catch (e: Exception) {
            updateStatus(LanguageBundle.message("error.bridgeError"))
        }
    }

    // 디바운싱을 위한 변수
    private var debounceTimer: Timer? = null
    private val DEBOUNCE_DELAY = 300 // 밀리초

    // 디바운싱 적용 렌더링
    private fun debounceRender() {
        debounceTimer?.stop()
        debounceTimer = Timer(DEBOUNCE_DELAY) { render() }
        debounceTimer?.isRepeats = false
        debounceTimer?.start()
    }

    // GeoJSON 렌더링
    private fun render() {
        try {
            val geoJsonText = inputTextArea.text.trim()
            if (geoJsonText.isEmpty()) {
                // 텍스트가 비어있으면 맵 초기화
                clearGeoJson()
                updateStatus(LanguageBundle.message("geojson.status.noData"))
                return
            }

            // GeoJSON 문법 검증
            validateGeoJson(geoJsonText)

            // GeoJSON 렌더링
            val jsCode = """
                try {
                    renderGeoJson(${geoJsonText});
                    window.sendMessageToKotlin("Rendered GeoJSON successfully");
                } catch(e) {
                    window.sendMessageToKotlin("Error: " + e.message);
                }
            """.trimIndent()

            browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
            updateStatus(LanguageBundle.message("geojson.status.rendering"))
        } catch (e: Exception) {
            clearGeoJson()
            updateStatus(LanguageBundle.message("error.invalidJson") + ": " + e.message)
        }
    }

    private fun clearGeoJson() {
        browser.cefBrowser.executeJavaScript("clearGeoJson();", browser.cefBrowser.url, 0)
    }

    // GeoJSON 유효성 검사
    private fun validateGeoJson(json: String) {
        try {
            JSONObject(json)

        } catch (e: Exception) {
            clearGeoJson()
            throw IllegalArgumentException(LanguageBundle.message("error.invalidJson"))
        }
    }

    private fun handleJSMessage(message: String) {
        if (message.startsWith("Error:")) {
            updateStatus(message)
        } else {
            updateStatus(LanguageBundle.message("geojson.status.rendered"))
            // 렌더링된 GeoJSON 데이터를 결과에 설정
            setResult(inputTextArea.text, inputTextArea.text)
        }
    }

    private fun updateStatus(message: String) {
        statusLabel.text = message
    }

    override fun convert() {
        render()
    }

    private fun getMapHtml(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <title>GeoJSON Renderer</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <style>
                    html, body, #map {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <script>           
                    // 맵 초기화
                    var map = L.map('map').setView([37.5665, 126.9780], 10); // 서울 중심
                    
                    // 베이스 맵 레이어
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    }).addTo(map);
                    
                    // GeoJSON 레이어
                    var geoJsonLayer = null;
                    
                    // GeoJSON 렌더링 함수
                    function renderGeoJson(geoJson) {
                        // 기존 GeoJSON 레이어 제거
                        clearGeoJson();
                        
                        // 새 GeoJSON 레이어 추가
                        geoJsonLayer = L.geoJSON(geoJson, {
                            style: function(feature) {
                                return {
                                    color: '#3388ff',
                                    weight: 3,
                                    opacity: 0.8,
                                    fillColor: '#3388ff',
                                    fillOpacity: 0.2
                                };
                            },
                            pointToLayer: function(feature, latlng) {
                                return L.circleMarker(latlng, {
                                    radius: 8,
                                    fillColor: '#ff7800',
                                    color: '#000',
                                    weight: 1,
                                    opacity: 1,
                                    fillOpacity: 0.8
                                });
                            },
                            onEachFeature: function(feature, layer) {
                                if (feature.properties) {
                                    var popupContent = '<table>';
                                    for (var p in feature.properties) {
                                        popupContent += '<tr><td><b>' + p + '</b></td><td>' + feature.properties[p] + '</td></tr>';
                                    }
                                    popupContent += '</table>';
                                    layer.bindPopup(popupContent);
                                }
                            }
                        }).addTo(map);
                        
                        // 자동으로 줌 조정
                        if (geoJsonLayer.getBounds().isValid()) {
                            map.fitBounds(geoJsonLayer.getBounds(), { padding: [30, 30] });
                        }
                    }
                    
                    // GeoJSON 레이어 제거 함수
                    function clearGeoJson() {
                        if (geoJsonLayer) {
                            map.removeLayer(geoJsonLayer);
                            geoJsonLayer = null;
                        }
                    }

                </script>
            </body>
            </html>
        """.trimIndent()
    }
}