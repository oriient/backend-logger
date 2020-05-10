package me.oriient.backendlogger.services.os.instancecreator

interface InstanceCreator {
    fun createInstance(name: String, vararg constructorArgs: Any): Any
}