package com.example

import com.example.domain.ime.ImeActivationManager
import com.example.domain.ime.KeyboardActivationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImeActivationTest {

    @Test
    fun testActivationStateEnumCompleteness() {
        val states = KeyboardActivationState.values()
        assertTrue(states.contains(KeyboardActivationState.NOT_ENABLED))
        assertTrue(states.contains(KeyboardActivationState.ENABLED_NOT_DEFAULT))
        assertTrue(states.contains(KeyboardActivationState.DEFAULT))
        assertTrue(states.contains(KeyboardActivationState.SYSTEM_PROBLEM))
        assertTrue(states.contains(KeyboardActivationState.UNKNOWN))
    }

    @Test
    fun testImeServiceClassConstant() {
        assertEquals("com.example.ime.ClipbordIME", ImeActivationManager.IME_SERVICE_CLASS)
    }
}
