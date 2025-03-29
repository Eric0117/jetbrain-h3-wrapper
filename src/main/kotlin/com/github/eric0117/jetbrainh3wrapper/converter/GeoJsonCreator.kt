package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import javax.swing.*
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.BorderLayout


class GeoJsonCreator(content: MainToolWindowContent) : BaseConverter(content) {
    private val browser: JBCefBrowser = JBCefBrowser()
    private val statusLabel = JLabel(LanguageBundle.message("map.status.ready"))

    // 자바스크립트 통신을 위한 쿼리 객체
    private val jsQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

    override fun getConverterIndex(): Int {
        return content.getConverterIndex(this)
    }

    override fun showClipboardCopyButton(): Boolean {
        return true
    }

    override fun showWebButton(): Boolean {
        return false
    }

    override fun createPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        // 버튼 패널
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))

        val drawButton = JButton(LanguageBundle.message("button.drawGeoJson"))
        drawButton.addActionListener {
            convert()
        }

        val clearButton = JButton(LanguageBundle.message("button.clearMap"))
        clearButton.addActionListener {
            clearMap()
        }

        buttonPanel.add(drawButton)
        buttonPanel.add(clearButton)
        buttonPanel.add(statusLabel)

        // 맵 패널
        val browserPanel = JPanel(BorderLayout())
        browser.component.preferredSize = Dimension(600, 400)
        browserPanel.add(browser.component, BorderLayout.CENTER)

        // 전체 패널 구성
        panel.add(buttonPanel, BorderLayout.NORTH)
        panel.add(browserPanel, BorderLayout.CENTER)

        // 맵 초기화
        initializeMap()

        return panel
    }

    private fun initializeMap() {
        // HTML 콘텐츠 로드 (맵과 폴리곤 그리기 기능 포함)
        browser.loadHTML(getMapHtml())

        // 페이지 로드 완료 후 JavaScript 브릿지 설정
        setupJavascriptBridge()
    }

    private fun setupJavascriptBridge() {
        try {
            // 자바스크립트에서 호출 가능한 메서드 등록
            jsQuery.addHandler { geoJsonData ->
                handleGeoJsonData(geoJsonData)
                JBCefJSQuery.Response("success")
            }

            // 브라우저에 핸들러 등록
            val jsCode = """
                window.sendGeoJsonToKotlin = function(geoJsonData) {
                    ${jsQuery.inject("geoJsonData")};
                };
            """.trimIndent()

            browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
        } catch (e: Exception) {
            updateStatus(LanguageBundle.message("error.bridgeError"))
        }
    }

    private fun handleGeoJsonData(geoJsonData: String) {
        try {
            setResult(geoJsonData, geoJsonData)
            updateStatus(LanguageBundle.message("map.status.geoJsonReceived"))
        } catch (e: Exception) {
            updateStatus(LanguageBundle.message("error.processing") + ": " + e.message)
        }
    }

    private fun updateStatus(message: String) {
        statusLabel.text = message
    }

    private fun clearMap() {
        browser.cefBrowser.executeJavaScript("if (window.clearAllLayers) window.clearAllLayers();", browser.cefBrowser.url, 0)
        updateStatus(LanguageBundle.message("map.status.cleared"))
        setResult("", "")
    }

    override fun convert() {
        val jsCode = """
            if (window.getGeoJsonData) {
                window.getGeoJsonData();
            } else {
                console.error("getGeoJsonData function is not defined");
            }
        """.trimIndent()

        browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

    private fun getMapHtml(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <title>Map with Drawing Tools</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet-draw@1.0.4/dist/leaflet.draw.css" />
                <style>
                    html, body, #map {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                    }
                    .info-panel {
                        padding: 6px 8px;
                        background: white;
                        border-radius: 5px;
                        box-shadow: 0 0 15px rgba(0,0,0,0.2);
                        position: absolute;
                        bottom: 10px;
                        right: 10px;
                        z-index: 1000;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <script src="https://unpkg.com/leaflet-draw@1.0.4/dist/leaflet.draw.js"></script>
                <script>           
                    // 맵 초기화
                    var map = L.map('map').setView([37.5665, 126.9780], 10); // 서울 중심
                    
                    // 베이스 맵 레이어
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    }).addTo(map);
                    
                    // FeatureGroup은 그려진 아이템을 저장
                    var drawnItems = new L.FeatureGroup();
                    map.addLayer(drawnItems);
                    
                    // 그리기 컨트롤 초기화
                    var drawControl = new L.Control.Draw({
                        edit: {
                            featureGroup: drawnItems,
                            poly: {
                                allowIntersection: false
                            }
                        },
                        draw: {
                            polygon: {
                                allowIntersection: false,
                                showArea: true
                            },
                            polyline: true,
                            rectangle: true,
                            circle: false,
                            marker: true,
                            circlemarker: false
                        }
                    });
                    map.addControl(drawControl);
                    
                    // 그리기 이벤트 처리
                    map.on(L.Draw.Event.CREATED, function (event) {
                        var layer = event.layer;
                        drawnItems.addLayer(layer);
                        
                        var geoJson = drawnItems.toGeoJSON();
                        var geoJsonString = JSON.stringify(geoJson, null, 2);
                        ${jsQuery.inject("geoJsonString")}
                        if (window.sendGeoJsonToKotlin) {
                            window.sendGeoJsonToKotlin(geoJsonString);
                        }
                    });
                    
                    map.on(L.Draw.Event.DELETED, function (event) {
                        ${jsQuery.inject("")}
                    });

                    // GeoJSON 데이터 가져오기 및 Kotlin으로 전송
                    window.getGeoJsonData = function() {
                        var geoJson = drawnItems.toGeoJSON();
                        var geoJsonString = JSON.stringify(geoJson, null, 2);
                        ${jsQuery.inject("geoJsonString")}
                        if (window.sendGeoJsonToKotlin) {
                            window.sendGeoJsonToKotlin(geoJsonString);
                        }
                    };
                    
                    // 모든 레이어 지우기
                    window.clearAllLayers = function() {
                        drawnItems.clearLayers();
                    };

                </script>
            </body>
            </html>
        """.trimIndent()
    }
}
