package com.wafeer.app.presentation.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wafeer.app.R
import com.wafeer.app.domain.model.RecurrentFrequency
import com.wafeer.app.presentation.MainActivity
import com.wafeer.app.presentation.util.font.format.symbolOnlyCurrencyFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import logcat.logcat
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_PERIOD_END = "budget_period_end_v131"
        const val CHANNEL_RECURRENT = "recurrent_expenses_v131"
        const val CHANNEL_CREDIT = "credit_expenses_v131"
        const val CHANNEL_APP_UPDATES = "app_updates_v133"
        const val CHANNEL_SPEND_DETECTED = "spend_detected_v133"

        const val SPEND_SAVED_TIMEOUT_MS = 6000L

        const val NOTIFICATION_ID_PERIOD_END = 1001
        const val NOTIFICATION_ID_RECURRENT = 1002
        const val NOTIFICATION_ID_CREDIT = 1003
        const val NOTIFICATION_ID_APP_UPDATE = 1004
        const val NOTIFICATION_ID_DAILY_75_PERCENT = 1005
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val periodEndChannel = NotificationChannel(
            CHANNEL_PERIOD_END,
            context.getString(R.string.notification_channel_period_end_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_period_end_description)
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        val recurrentChannel = NotificationChannel(
            CHANNEL_RECURRENT,
            context.getString(R.string.notification_channel_recurrent_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_recurrent_description)
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        val creditChannel = NotificationChannel(
            CHANNEL_CREDIT,
            context.getString(R.string.notification_channel_credit_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_credit_description)
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        val updatesChannel = NotificationChannel(
            CHANNEL_APP_UPDATES,
            if (isAr) "تحديثات وفير" else "Wafeer Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = if (isAr) "إشعارات توفر إصدارات وتحديثات جديدة لتطبيق وفير" else "Notifications for new Wafeer releases and updates"
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        val spendDetectedChannel = NotificationChannel(
            CHANNEL_SPEND_DETECTED,
            context.getString(R.string.notification_channel_spend_detected_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_spend_detected_description)
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(periodEndChannel)
        notificationManager.createNotificationChannel(recurrentChannel)
        notificationManager.createNotificationChannel(creditChannel)
        notificationManager.createNotificationChannel(updatesChannel)
        notificationManager.createNotificationChannel(spendDetectedChannel)
        logcat { "Notification channels created (v133)" }
    }

    fun canPostNotifications(channelId: String? = null): Boolean {
        if (!checkNotificationPermission()) return false
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return false
        if (channelId == null) return true
        val channel = manager.getNotificationChannelCompat(channelId) ?: return true
        return channel.importance != NotificationManagerCompat.IMPORTANCE_NONE
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            logcat { "Notification permission (Android 13+): $granted" }
            granted
        } else {
            logcat { "Notification permission: granted (pre-Android 13)" }
            true
        }
    }

    fun isNotificationPermissionGranted(): Boolean = checkNotificationPermission()

    fun showRichCustomNotification(
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        tag: String = if (com.wafeer.app.presentation.util.LocaleHelper.isArabic()) "وفير • الرقيب المالي الذكي" else "Wafeer • Smart Financial Assistant",
    ): Boolean {
        if (!checkNotificationPermission()) {
            logcat { "Cannot show notification: permission not granted" }
            return false
        }
        val managerCompat = NotificationManagerCompat.from(context)
        if (!managerCompat.areNotificationsEnabled()) {
            logcat { "Cannot show notification: notifications disabled in system settings" }
            return false
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val addExpenseIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATE_TO", "ADD_EXPENSE")
        }
        val addPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 2000,
            addExpenseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .bigText(message)
            .setSummaryText(tag)

        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFFA3CD51.toInt())
            .setContentTitle(title)
            .setContentText(message)
            .setSubText(tag)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(
                R.drawable.ic_notification,
                if (isAr) "تسجيل مصروف +" else "Log Expense +",
                addPendingIntent
            )
            .addAction(
                0,
                if (isAr) "عرض الميزانية" else "View Budget",
                mainPendingIntent
            )
            .build()

        managerCompat.notify(notificationId, notification)
        logcat { "Rich notification ($notificationId) sent successfully via $channelId" }
        return true
    }

    fun showPeriodEndNotification(remainingBudget: String, currency: String) {
        val hasPermission = checkNotificationPermission()
        if (!hasPermission) {
            logcat { "Cannot show notification - permission not granted" }
            return
        }

        val formattedAmount = formatAmount(remainingBudget, currency)
        val message = buildPeriodEndMessage(remainingBudget, formattedAmount)
        val title = context.getString(R.string.notification_period_end_title)
        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()

        showRichCustomNotification(
            notificationId = NOTIFICATION_ID_PERIOD_END,
            channelId = CHANNEL_PERIOD_END,
            title = title,
            message = message,
            tag = if (isAr) "وفير • تجديد الميزانية" else "Wafeer • Budget Renewal",
        )
    }

    fun showDaily75PercentAlert(): Boolean {
        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        return showRichCustomNotification(
            notificationId = NOTIFICATION_ID_DAILY_75_PERCENT,
            channelId = CHANNEL_PERIOD_END,
            title = if (isAr) "يوم هادئ وموفر، أم نسيت تدوين مصاريفك؟" else "A quiet, frugal day — or did you forget to log expenses?",
            message = if (isAr) {
                "انقضى 75% من يومك دون تسجيل أي حركة مالية. بلمسة واحدة دوّن مصاريفك وحافظ على انضباط ميزانيتك اليومية."
            } else {
                "75% of your day has passed without logging any transaction. Log your expenses with one tap to stay on top of your daily budget."
            },
            tag = if (isAr) "وفير • نشاط اليوم" else "Wafeer • Today's Activity",
        )
    }

    fun cancelDaily75PercentAlert() {
        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID_DAILY_75_PERCENT)
        } catch (e: Exception) {
            logcat { "Failed to cancel daily 75% alert: ${e.message}" }
        }
    }

    private fun buildPeriodEndMessage(remainingBudget: String, formattedAmount: String): String {
        val amount = remainingBudget.toDoubleOrNull() ?: 0.0
        return if (amount > 0) {
            context.getString(R.string.notification_period_end_message_positive, formattedAmount)
        } else if (amount < 0) {
            context.getString(
                R.string.notification_period_end_message_negative,
                formattedAmount
            )
        } else {
            context.getString(R.string.notification_period_end_message_neutral)
        }
    }

    fun recurrentNotificationId(transactionId: Long): Int = "recurrent_$transactionId".hashCode()

    private fun addRecurrentActions(
        builder: NotificationCompat.Builder,
        transactionId: Long,
        occurrenceDate: LocalDate,
        notificationId: Int,
    ) {
        builder
            .addAction(
                0,
                context.getString(R.string.mark_as_paid),
                recurrentActionIntent(
                    RecurrentNotificationActionReceiver.ACTION_MARK_PAID, transactionId, occurrenceDate, notificationId,
                ),
            )
            .addAction(
                0,
                context.getString(R.string.subscriptions_item_skip_short),
                recurrentActionIntent(
                    RecurrentNotificationActionReceiver.ACTION_SKIP, transactionId, occurrenceDate, notificationId,
                ),
            )
    }

    private fun recurrentActionIntent(
        action: String,
        transactionId: Long,
        occurrenceDate: LocalDate,
        notificationId: Int,
    ): PendingIntent {
        val intent = Intent(context, RecurrentNotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(RecurrentNotificationActionReceiver.EXTRA_TRANSACTION_ID, transactionId)
            putExtra(RecurrentNotificationActionReceiver.EXTRA_OCCURRENCE_EPOCH_DAY, occurrenceDate.toEpochDay())
            putExtra(RecurrentNotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getBroadcast(
            context,
            "$action$transactionId${occurrenceDate.toEpochDay()}$notificationId".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun formatAmount(amount: String, currency: String): String {
        val decimalValue = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        return symbolOnlyCurrencyFormat(currency).format(decimalValue)
    }

    fun showRecurrentExpenseNotification(
        amount: String,
        comment: String,
        currency: String,
        frequency: RecurrentFrequency,
        transactionId: Long? = null,
        occurrenceDate: LocalDate? = null,
    ) {
        val hasPermission = checkNotificationPermission()
        if (!hasPermission) {
            logcat { "Cannot show notification - permission not granted" }
            return
        }
        val notificationId = transactionId?.let { recurrentNotificationId(it) } ?: NOTIFICATION_ID_RECURRENT

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = formatAmount(amount, currency)
        val name = comment.ifBlank { context.getString(R.string.upcoming_recurrent_unnamed_expense) }
        val title = context.getString(R.string.notification_recurrent_due_today_title, name)
        val message = context.getString(
            when (frequency) {
                RecurrentFrequency.WEEKLY -> R.string.notification_recurrent_charged_weekly
                RecurrentFrequency.BIWEEKLY -> R.string.notification_recurrent_charged_biweekly
                RecurrentFrequency.MONTHLY -> R.string.notification_recurrent_charged_monthly
            },
            formattedAmount,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_RECURRENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFF2E7D32.toInt())
            .setSubText(context.getString(R.string.app_name))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if (transactionId != null && occurrenceDate != null) {
            addRecurrentActions(builder, transactionId, occurrenceDate, notificationId)
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun showUpcomingSubscriptionNotification(
        amount: String,
        comment: String,
        daysUntil: Long,
        currency: String,
        transactionId: Long? = null,
        occurrenceDate: LocalDate? = null,
    ) {
        val hasPermission = checkNotificationPermission()
        if (!hasPermission) {
            logcat { "Cannot show notification - permission not granted" }
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val daysText = when (daysUntil) {
            1L -> context.getString(R.string.notification_tomorrow)
            else -> context.getString(R.string.notification_in_days, daysUntil)
        }

        val formattedAmount = formatAmount(amount, currency)
        val title = context.getString(R.string.notification_upcoming_subscription_title)
        val message = if (comment.isNotBlank()) {
            context.getString(
                R.string.notification_upcoming_subscription_message_with_comment,
                comment,
                formattedAmount,
                daysText
            )
        } else {
            context.getString(
                R.string.notification_upcoming_subscription_message_without_comment,
                formattedAmount,
                daysText
            )
        }

        val notificationId = transactionId?.let { "upcoming_$it".hashCode() }
            ?: (NOTIFICATION_ID_RECURRENT + daysUntil.toInt())
        val builder = NotificationCompat.Builder(context, CHANNEL_RECURRENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFF2E7D32.toInt())
            .setSubText(context.getString(R.string.app_name))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if (transactionId != null && occurrenceDate != null) {
            addRecurrentActions(builder, transactionId, occurrenceDate, notificationId)
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        logcat { "Upcoming subscription notification shown: $message" }
    }

    fun showCreditCutoffNotification(
        totalAmount: String,
        dueDateText: String,
        currency: String
    ) {
        val hasPermission = checkNotificationPermission()
        if (!hasPermission) {
            logcat { "Cannot show credit notification - permission not granted" }
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = formatAmount(totalAmount, currency)
        val message = context.getString(
            R.string.notification_credit_cutoff_message,
            dueDateText,
            formattedAmount
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CREDIT)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFF2E7D32.toInt())
            .setSubText(context.getString(R.string.app_name))
            .setContentTitle(context.getString(R.string.notification_credit_cutoff_title))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CREDIT, notification)
    }

    fun showUpdateNotification(info: com.wafeer.app.domain.model.updater.AppUpdateInfo) {
        val hasPermission = checkNotificationPermission()
        if (!hasPermission) {
            logcat { "Cannot show update notification - permission not granted" }
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TRIGGER_UPDATE", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_APP_UPDATE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isAr = com.wafeer.app.presentation.util.LocaleHelper.isArabic()
        val title = if (isAr) "تحديث جديد لوفير: v${info.versionName}" else "New Wafeer Update: v${info.versionName}"
        val firstHighlight = info.mainFeatures.firstOrNull() ?: info.improvements.firstOrNull() ?: ""
        val cleanSummary = com.wafeer.app.presentation.util.MarkdownParser.stripMarkdown(firstHighlight)
        val message = if (cleanSummary.isNotBlank()) {
            if (isAr) {
                "$cleanSummary\nاضغط للتحميل والتثبيت المباشر."
            } else {
                "$cleanSummary\nTap to download and install directly."
            }
        } else {
            if (isAr) {
                "يتوفر إصدار جديد بتحسينات ومميزات ذكية. اضغط للتحميل والتثبيت."
            } else {
                "A new release with smart features and improvements is available. Tap to download and install."
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_APP_UPDATES)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(0xFF749F27.toInt())
            .setSubText(context.getString(R.string.app_name))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_APP_UPDATE, notification)
        logcat { "Update notification shown for version ${info.versionName}" }
    }

    fun spendDetectedNotificationId(sourceLabel: String, amount: String): Int =
        "spend_${sourceLabel}_$amount".hashCode()

    fun showSpendDetectedNotification(
        amount: BigDecimal,
        currency: String,
        sourceLabel: String,
    ) {
        if (!canPostNotifications(CHANNEL_SPEND_DETECTED)) return

        val plainAmount = amount.toPlainString()
        val notificationId = spendDetectedNotificationId(sourceLabel, plainAmount)
        val formattedAmount = formatAmount(plainAmount, currency)
        val message = context.getString(R.string.notification_scan_detected_message, formattedAmount)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            action = com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.ACTION_QUICK_ADD
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_AMOUNT, plainAmount)
            putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val quickSaveIntent = Intent(context, com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver::class.java).apply {
            action = com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.ACTION_QUICK_SAVE
            putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_AMOUNT, plainAmount)
            putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_SOURCE_LABEL, sourceLabel)
            putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val quickSavePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1,
            quickSaveIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SPEND_DETECTED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(sourceLabel)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(0, context.getString(R.string.notification_scan_action_minus_it), openPendingIntent)
            .addAction(0, context.getString(R.string.notification_scan_action_quick_save), quickSavePendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
        logcat { "Spend detected notification shown for $sourceLabel" }
    }

    fun showSpendSavedNotification(
        notificationId: Int,
        amount: String,
        currency: String,
        transactionId: Long,
        queuedForNextPeriod: Boolean,
    ) {
        if (!canPostNotifications(CHANNEL_SPEND_DETECTED)) {
            cancelSpendNotification(notificationId)
            return
        }

        val formattedAmount = formatAmount(amount, currency)
        val message = if (queuedForNextPeriod) {
            context.getString(R.string.notification_scan_queued_message, formattedAmount)
        } else {
            context.getString(R.string.notification_scan_saved_message, formattedAmount)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SPEND_DETECTED)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_scan_saved_title))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setTimeoutAfter(SPEND_SAVED_TIMEOUT_MS)
            .setCategory(NotificationCompat.CATEGORY_STATUS)

        if (transactionId > 0L) {
            val undoIntent = Intent(context, com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver::class.java).apply {
                action = com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.ACTION_UNDO
                putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_TRANSACTION_ID, transactionId)
                putExtra(com.wafeer.app.presentation.notification.scan.ExpenseScanActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            }
            builder.addAction(
                0,
                context.getString(R.string.notification_scan_action_undo),
                PendingIntent.getBroadcast(
                    context,
                    notificationId + 2,
                    undoIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun cancelSpendNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
