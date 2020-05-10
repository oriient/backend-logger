package me.oriient.backendlogger

import androidx.test.platform.app.InstrumentationRegistry
import me.oriient.backendlogger.di.DI
import org.junit.Test

class BasicTests {

    init {
        setContext()
    }

    @Test
    fun initDI() {
        Class.forName("me.oriient.backendlogger.di.DIImpl").newInstance() as DI
    }

    private fun setContext() {
        val clazz = Class.forName("me.oriient.backendlogger.services.android.ContextProviderKt")
        val field = clazz.declaredFields[0]
        field.isAccessible = true
        field.set(clazz, InstrumentationRegistry.getInstrumentation().context)
    }

}
