package com.github.eric0117.jetbrainh3wrapper.converter

import com.github.eric0117.jetbrainh3wrapper.LanguageBundle
import com.github.eric0117.jetbrainh3wrapper.content.MainToolWindowContent
import javax.swing.JPanel

import java.util.regex.Pattern

abstract class BaseConverter(protected val content: MainToolWindowContent) {
    // 공통 변환기 로직
    abstract fun createPanel(): JPanel
    abstract fun convert()

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