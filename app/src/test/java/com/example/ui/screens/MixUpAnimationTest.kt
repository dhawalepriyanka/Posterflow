package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MixUpAnimationTest {
    @Test
    fun `mix up enters safely then settles at full opacity`() {
        val start = mixUpFrameAt(0f)
        val entranceEnd = mixUpFrameAt(0.72f)
        val settled = mixUpFrameAt(1.44f)

        assertEquals(0f, start.alpha, 0.001f)
        assertEquals(0.82f, start.scale, 0.001f)
        assertTrue(start.translationXFraction + start.scale / 2f <= 0.5f)
        assertEquals(1f, entranceEnd.alpha, 0.001f)
        assertEquals(0.94f, entranceEnd.scale, 0.001f)
        assertEquals(1f, settled.alpha, 0.001f)
        assertEquals(1f, settled.scale, 0.001f)
        assertEquals(0f, settled.rotation, 0.001f)
        assertEquals(0f, settled.translationXFraction, 0.001f)
        assertEquals(0f, settled.translationYFraction, 0.001f)
    }

    @Test
    fun `mix up resting motion remains subtle`() {
        val frame = mixUpFrameAt(8f)

        assertEquals(1f, frame.alpha, 0.001f)
        assertTrue(frame.scale in 0.988f..1.012f)
        assertTrue(frame.rotation in -0.35f..0.35f)
        assertTrue(frame.translationXFraction in -0.004f..0.004f)
        assertTrue(frame.translationYFraction in -0.003f..0.003f)
    }
}
