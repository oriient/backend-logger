package me.oriient.backendlogger.services.android.instancecreator

import android.util.Log
import me.oriient.backendlogger.services.android.context
import me.oriient.backendlogger.services.os.instancecreator.InstanceCreator
import java.lang.reflect.InvocationTargetException

private const val TAG = "InstanceCreator"

@Suppress("unused")
internal class InstanceCreatorImpl:
    InstanceCreator {
    override fun createInstance(name: String, vararg constructorArgs: Any): Any {
        try {
            return Class.forName(name, false, context.classLoader).constructors.first()
                .newInstance(*constructorArgs)
        } catch (e: InvocationTargetException) {
            Log.e(TAG, "createInstance: cause: ${e.cause}, target: ${e.targetException}, message: ${e.message}", e)
            throw e
        }
    }
}