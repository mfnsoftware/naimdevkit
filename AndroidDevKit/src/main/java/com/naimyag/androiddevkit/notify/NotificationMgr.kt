package com.naimyag.androiddevkit.notify

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.push.HmsMessaging
import com.naimyag.androiddevkit.prefs.UpCatcher
import com.naimyag.androiddevkit.utils.ILog
import me.leolin.shortcutbadger.ShortcutBadger

@Keep
object NotificationMgr : ILog {

    private const val LAST_TOKEN = "lastToken"
    private const val BADGE_COUNT = "badgeCount"

    var availableMS = NS_ENUM.GMS
    private var broadcastReceiver: BroadcastReceiver? = null

    @SuppressLint("HardwareIds")
    fun initMS(appContext: Context, appKeyCountly: String, isTestMode: Boolean) {

        if (isGmsAvailable(appContext)) {
            availableMS = NS_ENUM.GMS
        } else if (isHmsAvailable(appContext)) {
            availableMS = NS_ENUM.HMS
        }

        if (availableMS == NS_ENUM.GMS) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    printEx("Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                task.result?.let {
                    // Get new Instance ID token
                    val token: String = it
                    printLog("FirebaseInstanceId.getInstance() onComplete() token received")
                    onTokenRefresh(token, NS_ENUM.GMS)
                } ?: run {
                    printEx("FirebaseInstanceId.getInstance() getInstanceId failed", task.exception)
                }

            })

        } else {
            HmsMessaging.getInstance(appContext).turnOnPush()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        printLog("HmsMessaging.turnOnPush.onCompleted isSuccessful: true!")
                    } else {
                        printEx("HmsMessaging.turnOnPush.onCompleted isSuccessful: false!")
                    }
                }

            object : Thread() {
                override fun run() {
                    super.run()
                    try {
                        val appId = AGConnectServicesConfig.fromContext(appContext)
                            .getString("client/app_id")

                        appId?.let {
                            val token =
                                HmsInstanceId.getInstance(appContext).getToken(it, "HCM")
                            if (!TextUtils.isEmpty(token)) {
                                printLog("HmsInstanceId.getInstance() getToken() token received")
                                onTokenRefresh(token, NS_ENUM.HMS)
                            } else {
                                printEx(
                                    "HmsInstanceId.getInstance() getToken() token is Empty!",
                                    null
                                )
                            }
                        } ?: kotlin.run {
                            printEx("HmsInstanceId.getInstance() getToken() appId is Empty!")
                        }

                    } catch (e: Exception) {
                        printEx("HmsInstanceId.getInstance() getToken() ex:", e)
                    }
                }
            }.start()
        }
    }

    fun registerForegroundNotifications(
        context: Context,
        onNotifyReceived: (message: String, menuId: String?, parametre: String?) -> Unit
    ) {
        if (broadcastReceiver != null) {
            try {
                context.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {
            }
            broadcastReceiver = null
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val bundle = intent.getBundleExtra("notification")

                bundle?.getString("message")?.let {
                    onNotifyReceived(
                        it,
                        bundle.getString("menuID"),
                        bundle.getString("parametre")
                    )
                }
            }
        }

        val intentFilter = IntentFilter("ACTION_STRING_ACTIVITY")
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun onPause(context: Context) {
        try {
            if (broadcastReceiver != null)
                context.unregisterReceiver(broadcastReceiver)
        } catch (ex: IllegalArgumentException) {
            //printEx("onPause() ex:", ex)
        }
    }

    fun onStop(context: Context) {
        try {
            if (broadcastReceiver != null)
                context.unregisterReceiver(broadcastReceiver)
        } catch (ex: IllegalArgumentException) {
            //printEx("onStop() ex:", ex)
        }
    }

    fun onTokenRefresh(token: String?, fromMS: NS_ENUM) {
        token?.let {
            if (availableMS == fromMS) {
                if (!TextUtils.isEmpty(it)) {
                    printLog("onNewToken() token received")
                    putLastToken(it)
                } else {
                    printEx("onNewToken() token is Empty!")
                }
            } else {
                printLog("onNewToken() wrong service, token not sended.")
            }
        }
    }

    private fun putLastToken(token: String?) {
        printLog("->putLastToken()")
        token?.let {
            UpCatcher.getInstance().putString(LAST_TOKEN, it)
        }
    }

    fun getLastToken(): String {
        UpCatcher.getInstance().getString(LAST_TOKEN, "")?.let {
            return it
        } ?: kotlin.run {
            return ""
        }
    }

    fun notificationReceived(
        context: Context,
        type: NS_ENUM,
        remoteMessage: Any,
        toActivityIntent: Intent,
        notifySmallIcon: Int
    ) {
        var message: Any? = null
        var title: String? = null
        var body: String? = null
        var menuId: String? = null
        var parametre: String? = null
        var badgeCount: Int? = null

        when (type) {
            NS_ENUM.GMS -> {
                message = (remoteMessage as RemoteMessage)
                title = message.data["title"]
                body = message.notification?.body
                if (body == null) {
                    body = message.data["message"]
                }
                menuId = message.data["Menu"]
                parametre = remoteMessage.data["Parametre"]

                try {
                    badgeCount = message.data["badge"]?.toInt()
                } catch (e: Exception) {
                }
                if (badgeCount == null) {
                    badgeCount = message.notification?.notificationCount
                }

            }
            NS_ENUM.HMS -> {
                message = (remoteMessage as com.huawei.hms.push.RemoteMessage)
                title = message.dataOfMap["title"]
                body = message.notification?.body
                if (body == null) {
                    body = message.dataOfMap["message"]
                }
                menuId = message.dataOfMap["Menu"]
                parametre = message.dataOfMap["Parametre"]

                try {
                    badgeCount = message.dataOfMap["badge"]?.toInt()
                } catch (e: Exception) {
                }
                if (badgeCount == null) {
                    badgeCount = message.notification?.badgeNumber
                }

            }
        }

        if (body.isNullOrEmpty()) {
            badgeCount?.let {
                updateBadgeCount(context, it)
            }
            return
        }

        body.let {
            val bundle = Bundle()

            if (menuId != null)
                bundle.putString("menuID", menuId)
            if (parametre != null)
                bundle.putString("parametre", parametre)

            bundle.putString("message", it)

            toActivityIntent.putExtra("notification", bundle)
        }

        toActivityIntent.apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            `package` = context.applicationContext.packageName
        }

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                toActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                toActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val builder = NotificationCompat.Builder(context, "Default")
            .setSmallIcon(notifySmallIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setColor(context.resources.getColor(android.R.color.white))
            .setContentIntent(pendingIntent)

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            try {
                val channels = manager.notificationChannels
                printLog("notifyChannels: $channels")
            } catch (e: Exception) {
                printEx("notifyChannels: ex:", e)
            }

            val channel = NotificationChannel(
                "Default",
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        badgeCount?.let { updateBadgeCount(context, it) }

        val notifyId = (System.currentTimeMillis() % 10000).toInt()

        manager.notify(notifyId, builder.build())

        body.let {
            val bundle = Bundle()

            if (menuId != null)
                bundle.putString("menuID", menuId)
            if (parametre != null)
                bundle.putString("parametre", parametre)

            bundle.putString("message", it)

            val mIntent = Intent()
            mIntent.action = "ACTION_STRING_ACTIVITY"
            mIntent.putExtra("notification", bundle)
            context.sendBroadcast(mIntent)
        }

    }

    fun updateBadgeCount(context: Context, count: Int) {
        printLog("->updateBadgeCount()")
        UpCatcher.getInstance().putInt(BADGE_COUNT, count)
        ShortcutBadger.applyCount(context, count)
    }

    fun notifyClickIntentCheck(
        context: Context,
        intent: Intent,
        goToMenuClick: ((menuId: String?) -> Unit)? = null
    ) {
        val bundle = intent.getBundleExtra("notification")

        bundle?.getString("message")?.let { msg ->

            bundle.getString("menuID")?.let { menuId ->
                goToMenuClick?.let { it(menuId) }
                /*
                AppUtils.showDialog(context, "Bildirim", msg, true, "Git") {
                    if (it) {
                        goToMenuClick?.let { it(menuId) }
                    } else {
                        goToMenuClick?.let { it(null) }
                    }
                }
                */
            } ?: kotlin.run {
                goToMenuClick?.let { it("100") }
                /*
                AppUtils.showDialog(context, "Bildirim", msg, true) {
                    goToMenuClick?.let { it(null) }
                }
                */
            }
        } ?: kotlin.run {
            goToMenuClick?.let { it(null) }
        }
        //updateBadgeCount(context, 0)
    }

    private fun isHmsAvailable(context: Context?): Boolean {
        context?.let {
            val result =
                HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            printLog("isHmsAvailable() return ${result == ConnectionResult.SUCCESS}")
            return result == ConnectionResult.SUCCESS
        } ?: kotlin.run {
            return false
        }
    }

    private fun isGmsAvailable(context: Context?): Boolean {
        context?.let {
            val result =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            printLog("isGmsAvailable() return ${result == com.google.android.gms.common.ConnectionResult.SUCCESS}")
            return result == com.google.android.gms.common.ConnectionResult.SUCCESS
        } ?: kotlin.run {
            return false
        }
    }

    override fun getmSimpleName(): String? {
        return javaClass.simpleName
    }
}