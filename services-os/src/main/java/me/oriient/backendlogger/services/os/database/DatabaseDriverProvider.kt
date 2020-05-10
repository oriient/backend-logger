package me.oriient.backendlogger.services.os.database

import com.squareup.sqldelight.db.SqlDriver

interface DatabaseDriverProvider {
    val driver: SqlDriver
}