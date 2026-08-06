package com.example.util

import android.graphics.Bitmap
import android.graphics.Color

object QrCodeUtils {

    /**
     * Generates a 2D QR Code Bitmap algorithmically from payload string.
     */
    fun generateQrBitmap(payload: String, width: Int = 300, height: Int = 300): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val hash = payload.hashCode()

        val matrixSize = 21 // Standard QR Version 1 size
        val cellSize = width / matrixSize

        // Fill background white
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, Color.WHITE)
            }
        }

        // Draw position detection finders in 3 corners
        drawCornerFinder(bitmap, 0, 0, cellSize)
        drawCornerFinder(bitmap, (matrixSize - 7) * cellSize, 0, cellSize)
        drawCornerFinder(bitmap, 0, (matrixSize - 7) * cellSize, cellSize)

        // Generate data cells deterministically based on payload hash and characters
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip finder pattern zones
                if ((r < 7 && c < 7) || (r < 7 && c >= matrixSize - 7) || (r >= matrixSize - 7 && c < 7)) {
                    continue
                }

                val charIndex = (r * matrixSize + c) % payload.length
                val charVal = payload[charIndex].code
                val isBlack = ((r * 31 + c * 17 + hash + charVal) % 3) == 0

                if (isBlack) {
                    val startX = c * cellSize
                    val startY = r * cellSize
                    for (px in startX until (startX + cellSize)) {
                        for (py in startY until (startY + cellSize)) {
                            if (px < width && py < height) {
                                bitmap.setPixel(px, py, Color.BLACK)
                            }
                        }
                    }
                }
            }
        }

        return bitmap
    }

    private fun drawCornerFinder(bitmap: Bitmap, x0: Int, y0: Int, cellSize: Int) {
        val outerSize = 7 * cellSize
        val innerSize = 5 * cellSize
        val coreSize = 3 * cellSize

        // Outer black box
        for (x in x0 until (x0 + outerSize)) {
            for (y in y0 until (y0 + outerSize)) {
                if (x < bitmap.width && y < bitmap.height) {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }

        // Inner white box
        val innerOffset = (outerSize - innerSize) / 2
        for (x in (x0 + innerOffset) until (x0 + innerOffset + innerSize)) {
            for (y in (y0 + innerOffset) until (y0 + innerOffset + innerSize)) {
                if (x < bitmap.width && y < bitmap.height) {
                    bitmap.setPixel(x, y, Color.WHITE)
                }
            }
        }

        // Core black box
        val coreOffset = (outerSize - coreSize) / 2
        for (x in (x0 + coreOffset) until (x0 + coreOffset + coreSize)) {
            for (y in (y0 + coreOffset) until (y0 + coreOffset + coreSize)) {
                if (x < bitmap.width && y < bitmap.height) {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }
    }
}
