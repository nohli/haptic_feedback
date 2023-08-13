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
    vibratePattern(longArrayOf(75, 75, 75), intArrayOf(178, 0, 255))
  }

  private fun warning() {
    vibratePattern(longArrayOf(79, 119, 75), intArrayOf(227, 0, 178))
  }

  private fun error() {
    vibratePattern(longArrayOf( 75, 61, 79,57,75,57,97), intArrayOf(203,0, 200, 0,252,0,150))
  }

  private fun light() {
    vibratePattern(longArrayOf(79),intArrayOf(154) )
  }

  private fun medium() {
    vibratePattern(longArrayOf(79),intArrayOf(203) )
  }

  private fun heavy() {
    vibratePattern(longArrayOf(75),intArrayOf(252) )
  }

  private fun rigid() {
    vibratePattern(longArrayOf(48), intArrayOf(227))
  }

  private fun soft() {
    vibratePattern(longArrayOf(110), intArrayOf(178))
  }

  private fun selection() {
     vibratePattern(longArrayOf(57), intArrayOf(150))
  }

  private fun vibratePattern(lengths: LongArray, amplitudes: IntArray) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val effect = VibrationEffect.createWaveform(lengths, amplitudes, -1)
      vibrator.vibrate(effect)
    } else {
      vibrator.vibrate(lengths, -1)
    }
  }
}
