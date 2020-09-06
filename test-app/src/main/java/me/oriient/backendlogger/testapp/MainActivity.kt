package me.oriient.backendlogger.testapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import me.oriient.backendlogger.BackendLogger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.oriient.backendlogger.Scheduled
import me.oriient.backendlogger.Online

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    private lateinit var backendLogger: BackendLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var c = 1
        val message = mutableMapOf<String, Any>()

//        message["message"] = "Test message number $c"
//        backendLogger.sendMessage(message)
//        c++
//        message["message"] = "Test message number $c"
//        backendLogger.sendMessage(message)

        fab.setOnClickListener {

            if (!::backendLogger.isInitialized) {
                backendLogger = BackendLogger(BuildConfig.TEST_URL) {
                    sizeLimit = 100
                    retries = 3
                    state = Online()
                }
            }

            var map = hashMapOf<String, Any>()

            map["couter"] = logCounter
            map["string"] = "abc 1"
            map["bool1"] = false
            map["bool2"] = true
            map["float"] = 1.0f
            map["int"] = 5

            val map1 = map

            logCounter++

            map = hashMapOf()
            map["couter"] = logCounter
            map["string"] = "abc 2"
            map["bool1"] = false
            map["bool2"] = true
            map["float"] = 1.0f
            map["int"] = 5

            val map2 = map

            logCounter++

            backendLogger.sendMessage(map1)
            backendLogger.sendMessage(map2)

            //backendLogger.configure { state = Online() }
        }
    }

    var logCounter = 1

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
