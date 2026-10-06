package com.streak.app

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var daysText: TextView
    private lateinit var checkButton: Button
    private lateinit var bestText: TextView
    private lateinit var totalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("streak", MODE_PRIVATE)
        if (!prefs.contains("start")) {
            prefs.edit().putString("start", LocalDate.now().toString()).apply()
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 70, 48, 40)
            gravity = Gravity.CENTER_HORIZONTAL
        }
        fun label(value: String, size: Float) = TextView(this).apply {
            text = value
            textSize = size
            gravity = Gravity.CENTER
        }

        root.addView(label("STREAK", 18f))
        root.addView(label("One day at a time.", 15f))
        daysText = label("", 64f)
        root.addView(daysText)
        root.addView(label("DAYS FREE", 16f))

        checkButton = Button(this).apply {
            text = "✓  I stayed free today"
            setOnClickListener { checkIn() }
        }
        root.addView(checkButton)

        bestText = label("", 18f)
        totalText = label("", 18f)
        root.addView(bestText)
        root.addView(totalText)
        root.addView(label("\nMilestones  •  1  •  3  •  7  •  14  •  30  •  60  •  90  •  365", 16f))

        root.addView(Button(this).apply {
            text = "🔔 Daily reminder: 8:30 AM"
            setOnClickListener {
                schedule(this@MainActivity)
                Toast.makeText(context, "Reminder set for 8:30 AM", Toast.LENGTH_SHORT).show()
            }
        })

        root.addView(Button(this).apply {
            text = "Reset streak"
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Start a new streak?")
                    .setMessage("Best streak and check-ins are kept.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Reset") { _, _ -> resetStreak() }
                    .show()
            }
        })

        setContentView(root)
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 7)
        }
        schedule(this)
        refresh()
    }

    private fun currentDays(): Int {
        val start = prefs.getString("start", LocalDate.now().toString()) ?: LocalDate.now().toString()
        return ChronoUnit.DAYS.between(LocalDate.parse(start), LocalDate.now()).toInt().coerceAtLeast(0)
    }

    private fun refresh() {
        val d = currentDays()
        daysText.text = d.toString()
        bestText.text = "🏆 Best streak   " + max(prefs.getInt("best", 0), d) + " days"
        totalText.text = "✓ Total check-ins   " + prefs.getInt("total", 0)
        val canCheck = prefs.getString("last", "") != LocalDate.now().toString()
        checkButton.isEnabled = canCheck
        checkButton.text = if (canCheck) "✓  I stayed free today" else "✓  Checked in today"
    }

    private fun checkIn() {
        prefs.edit()
            .putString("last", LocalDate.now().toString())
            .putInt("total", prefs.getInt("total", 0) + 1)
            .putInt("best", max(prefs.getInt("best", 0), currentDays()))
            .apply()
        refresh()
    }

    private fun resetStreak() {
        prefs.edit()
            .putInt("best", max(prefs.getInt("best", 0), currentDays()))
            .putString("start", LocalDate.now().toString())
            .remove("last")
            .apply()
        refresh()
    }

    companion object {
        fun schedule(context: Context) {
            val alarm = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, 830, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val next = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, next.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
        }
    }
}
