package com.example.haptic_feedback

import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class HapticFeedbackPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var vibrator: Vibrator

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "haptic_feedback")
    channel.setMethodCallHandler(this)
    vibrator = flutterPluginBinding.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "canVibrate" -> {
        result.success(canVibrate())
        return
      }
      "success" -> success()
      "warning" -> warning()
      "error" -> error()
      "light" -> light()
      "medium" -> medium()
      "heavy" -> heavy()
      "rigid" -> rigid()
      "soft" -> soft()
      "selection" -> selection()
      else -> {
        result.notImplemented()
        return
      }
    }
    result.success(true)
  }

  private fun canVibrate(): Boolean {
    return vibrator.hasVibrator()
  }

  private fun success() {
    vibratePattern(longArrayOf(0, 50, 10, 50), intArrayOf(0, 255, 0, 255))
  }

  private fun warning() {
    vibratePattern(longArrayOf(0, 30, 20, 30), intArrayOf(0, 255, 0, 255))
  }

  private fun error() {
    vibratePattern(longArrayOf(0, 100, 30, 100), intArrayOf(0, 255, 0, 255))
  }

  private fun light() {
    vibratePattern(longArrayOf(0, 10))
  }

  private fun medium() {
    vibratePattern(longArrayOf(0, 20))
  }

  private fun heavy() {
    vibratePattern(longArrayOf(0, 30))
  }

  private fun rigid() {
    vibratePattern(longArrayOf(0, 20, 10, 20, 10, 20), intArrayOf(0, 255, 0, 255, 0, 255))
  }

  private fun soft() {
    vibratePattern(longArrayOf(0, 15, 10, 15), intArrayOf(0, 127, 0, 127))  // Reduced amplitude for soft
  }

  private fun selection() {
     vibratePattern(longArrayOf(0, 10, 5, 10, 5, 10), intArrayOf(0, 255, 0, 255, 0, 255))
  }

  private fun vibratePattern(pattern: LongArray, amplitudes: IntArray? = null) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && amplitudes != null) {
      val effect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
      vibrator.vibrate(effect)
    } else {
      vibrator.vibrate(pattern, -1)
    }
  }
}
