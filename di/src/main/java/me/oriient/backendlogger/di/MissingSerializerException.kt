package me.oriient.backendlogger.di


private const val TAG = "MissingSerializerException"

class MissingSerializerException: Exception("No serializer found. Please add a serializer dependency.")