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
    private val resultTextArea = JTextArea(10, 30)
    private var lastResult: String = ""

    // 유틸리티 객체
    private val clipboardUtil = ClipboardUtil()
    private val browserUtil = BrowserUtil()

    // 변환기 컴포넌트
    private val coordToH3Converter = CoordToH3Converter(this)
    private val h3ToCoordConverter = H3ToCoordConverter(this)
    private val mapCoordinateSelector = MapCoordinateSelector(this)

    init {
        setupUI()
    }

    private fun setupUI() {
        // 탭 추가
        tabbedPane.addTab(LanguageBundle.message("tab.coordToH3"), coordToH3Converter.createPanel())
        tabbedPane.addTab(LanguageBundle.message("tab.h3ToCoord"), h3ToCoordConverter.createPanel())
        tabbedPane.addTab(LanguageBundle.message("tab.mapMarker"), mapCoordinateSelector.createPanel())

        // 결과 영역
        resultTextArea.isEditable = false
        val scrollPane = JBScrollPane(resultTextArea)

        // 버튼 패널
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

        // 결과 복사 버튼
        val copyButton = JButton(LanguageBundle.message("button.copyToClipboard"))
        copyButton.addActionListener {
            clipboardUtil.copyToClipboard(lastResult, mainPanel)
        }

        // 웹 브라우저 열기 버튼
        val webButton = JButton(LanguageBundle.message("button.viewInWeb"))
        webButton.addActionListener {
            browserUtil.openInBrowser(tabbedPane.selectedIndex, lastResult, mainPanel)
        }

        // 웹 버튼 표시 제어를 위한 탭 변경 리스너
        tabbedPane.addChangeListener {
            val selectedIndex = tabbedPane.selectedIndex
            // 좌표 -> Point (2번 탭) 또는 Point -> 좌표 (3번 탭)인 경우 웹 버튼 숨김
            webButton.isVisible = !(selectedIndex == 2 || selectedIndex == 3)
        }

        buttonPanel.add(webButton)
        buttonPanel.add(copyButton)

        // 메인 패널 구성
        val resultPanel = JPanel(BorderLayout())
        resultPanel.add(scrollPane, BorderLayout.CENTER)
        resultPanel.add(buttonPanel, BorderLayout.SOUTH)

        mainPanel.add(tabbedPane, BorderLayout.NORTH)
        mainPanel.add(resultPanel, BorderLayout.CENTER)

        // 정보 표시
        val infoLabel = JLabel(LanguageBundle.message("author"))
        mainPanel.add(infoLabel, BorderLayout.SOUTH)
    }

    fun getContent(): JComponent {
        return mainPanel
    }

    // 결과 설정 메서드
    fun setResult(text: String, result: String) {
        resultTextArea.text = text
        lastResult = result
    }
}