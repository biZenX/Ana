package com.wafeer.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.wafeer.app.domain.usecase.BackfillOrphanedPeriodsUseCase
import com.wafeer.app.domain.usecase.FoldCarryIntoTotalBudgetUseCase
import com.wafeer.app.wearsync.PhoneWearMessageListener
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import javax.inject.Inject

@HiltAndroidApp
class WafeerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var phoneWearMessageListener: PhoneWearMessageListener

    @Inject
    lateinit var backfillOrphanedPeriodsUseCase: BackfillOrphanedPeriodsUseCase

    @Inject
    lateinit var foldCarryIntoTotalBudgetUseCase: FoldCarryIntoTotalBudgetUseCase

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

        phoneWearMessageListener.start()

        CoroutineScope(Dispatchers.IO).launch {
            com.wafeer.app.data.updater.AppUpdateManager.cleanOldUpdateApks(this@WafeerApplication)
            backfillOrphanedPeriodsUseCase()
            foldCarryIntoTotalBudgetUseCase()
        }

//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//            override fun onActivityPaused(activity: Activity) {
// 				ExtendWidgetReceiver.requestUpdateData(activity.applicationContext)
// 				MinimalWidgetReceiver.requestUpdateData(activity.applicationContext)
//            }
//        })
    }
}
