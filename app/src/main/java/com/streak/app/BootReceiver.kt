package com.streak.app
import android.content.*
class BootReceiver:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){MainActivity.schedule(c)}}