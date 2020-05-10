package me.oriient.backendlogger.services.android.database

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.oriient.backendlogger.services.android.context
import me.oriient.backendlogger.services.database.BackendLoggerDatabase
import me.oriient.backendlogger.services.os.database.DatabaseDriverProvider

@Suppress("unused")
internal class DatabaseDriverProviderImpl:
    DatabaseDriverProvider {
        override val driver: SqlDriver = AndroidSqliteDriver(BackendLoggerDatabase.Schema, context, "me.oriient.backendlogger.database")
    }