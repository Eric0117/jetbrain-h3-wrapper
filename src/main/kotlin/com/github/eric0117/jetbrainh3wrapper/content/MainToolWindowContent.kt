package com.github.eric0117.jetbrainh3wrapper.content

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.converter.*
import com.github.eric0117.jetbrainh3wrapper.util.ClipboardUtil
import com.github.eric0117.jetbrainh3wrapper.util.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.uber.h3core.H3Core
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

class MainToolWindowContent(private val project: Project) {
    // 공통 객체
    val h3Core: H3Core = H3Core.newInstance()

    // UI 구성요소
    private val mainPanel = JPanel(BorderLayout())
    private val tabbedPane = JBTabbedPane()

    // 유틸리티 객체를 public으로 변경하여 BaseConverter에서 접근 가능하게 함
    val clipboardUtil = ClipboardUtil()
    val browserUtil = BrowserUtil()

    // 변환기 컴포넌트
    private val coordToH3Converter = CoordToH3Converter(this)
    private val h3ToCoordConverter = H3ToCoordConverter(this)
    private val mapCoordinateSelector = MapCoordinateSelector(this)
    private val geoJsonCreator = GeoJsonCreator(this)

    // 변환기 리스트 (변환기 인덱스를 얻기 위해 사용)
    private val converters = listOf(
        coordToH3Converter,
        h3ToCoordConverter,
        mapCoordinateSelector,
        geoJsonCreator
    )

    init {
        setupUI()
    }

    private fun setupUI() {
        // 탭 추가
        tabbedPane.addTab(LanguageBundle.message("tab.coordToH3"), createConverterPanel(coordToH3Converter))
        tabbedPane.addTab(LanguageBundle.message("tab.h3ToCoord"), createConverterPanel(h3ToCoordConverter))
        tabbedPane.addTab(LanguageBundle.message("tab.mapMarker"), createConverterPanel(mapCoordinateSelector))
        tabbedPane.addTab(LanguageBundle.message("tab.geoJsonCreator"), createConverterPanel(geoJsonCreator))

        mainPanel.add(tabbedPane, BorderLayout.CENTER)

        // 정보 표시
        val infoLabel = JLabel(LanguageBundle.message("author"))
        mainPanel.add(infoLabel, BorderLayout.SOUTH)
    }

    private fun createConverterPanel(converter: BaseConverter): JPanel {
        val panel = JPanel(BorderLayout())
        panel.add(converter.createPanel(), BorderLayout.NORTH)
        panel.add(converter.resultPanel, BorderLayout.CENTER)
        return panel
    }

    fun getContent(): JComponent {
        return mainPanel
    }

    // 변환기의 인덱스 가져오기
    fun getConverterIndex(converter: BaseConverter): Int {
        return converters.indexOf(converter)
    }
}