package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel

import java.util.regex.Pattern
import javax.swing.JButton
import javax.swing.JTextArea

abstract class BaseConverter(protected val content: MainToolWindowContent) {
    // 결과 영역 컴포넌트
    private val resultTextArea = JTextArea(10, 30)
    private var lastResult: String = ""
    open val resultPanel = JPanel(BorderLayout())

    init {
        setupResultPanel()
    }

    private fun setupResultPanel() {
        // 결과 영역 설정
        resultTextArea.isEditable = false
        val scrollPane = JBScrollPane(resultTextArea)

        // 버튼 패널
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

        // 결과 복사 버튼
        val copyButton = JButton(LanguageBundle.message("button.copyToClipboard"))
        copyButton.addActionListener {
            content.clipboardUtil.copyToClipboard(lastResult, resultPanel)
        }

        // 웹 브라우저 열기 버튼
        val webButton = JButton(LanguageBundle.message("button.viewInWeb"))
        webButton.addActionListener {
            content.browserUtil.openInBrowser(getConverterIndex(), lastResult, resultPanel)
        }

        // 구현 클래스에서 웹 버튼 표시 여부를 결정
        webButton.isVisible = showWebButton()

        buttonPanel.add(webButton)
        buttonPanel.add(copyButton)

        resultPanel.add(scrollPane, BorderLayout.CENTER)
        resultPanel.add(buttonPanel, BorderLayout.SOUTH)
    }

    // 결과 설정 메서드
    protected fun setResult(text: String, result: String) {
        resultTextArea.text = text
        lastResult = result
    }

    // 공통 변환기 로직
    abstract fun createPanel(): JPanel
    abstract fun convert()

    // 웹 버튼 표시 여부 (기본값은 true, 필요시 오버라이드)
    protected open fun showWebButton(): Boolean = true

    // 변환기의 인덱스 반환 (웹 브라우저 열기에 사용)
    protected abstract fun getConverterIndex(): Int

    // 공통 유틸리티 메서드
    protected fun parseLatLng(input: String): Pair<Double, Double> {
        // lat,lng 패턴 파악 (콤마 전후 공백 허용)
        val pattern = Pattern.compile("\\s*([-+]?\\d*\\.?\\d+)\\s*,\\s*([-+]?\\d*\\.?\\d+)\\s*")
        val matcher = pattern.matcher(input)

        if (matcher.matches()) {
            val lat = matcher.group(1).toDouble()
            val lng = matcher.group(2).toDouble()
            return Pair(lat, lng)
        } else {
            throw IllegalArgumentException(LanguageBundle.message("error.invalidCoord"))
        }
    }

    // 좌표 유효성 검사
    protected fun validateCoordinates(lat: Double, lng: Double): String? {
        if (lat < -90 || lat > 90) {
            return LanguageBundle.message("error.invalidLat")
        }

        if (lng < -180 || lng > 180) {
            return LanguageBundle.message("error.invalidLng")
        }

        return null
    }
}