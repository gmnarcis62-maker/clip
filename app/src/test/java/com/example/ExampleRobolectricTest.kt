package com.example

import android.content.Context
import android.view.inputmethod.EditorInfo
import androidx.test.core.app.ApplicationProvider
import com.example.ime.ClipbordIME
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("مرسانا", appName)
    }

    @Test
    fun `test ClipbordIME lifecycle and input view creation`() {
        val serviceController = Robolectric.buildService(ClipbordIME::class.java)
        val ime = serviceController.create().get()
        assertNotNull(ime)

        val inputView = ime.onCreateInputView()
        assertNotNull(inputView)

        val editorInfo = EditorInfo()
        ime.onStartInputView(editorInfo, false)
        ime.onFinishInputView(false)
        serviceController.destroy()
    }
}
