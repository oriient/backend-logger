package me.oriient.backendlogger.services.android.instancecreator

import me.oriient.backendlogger.services.android.context
import me.oriient.backendlogger.services.os.instancecreator.InstanceCreator

@Suppress("unused")
internal class InstanceCreatorImpl:
    InstanceCreator {
    override fun createInstance(name: String, vararg constructorArgs: Any): Any {
        return Class.forName(name, false, context.classLoader).constructors.first().newInstance(*constructorArgs)
    }
}