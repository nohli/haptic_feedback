package io.achim.haptic_feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class HapticFeedbackPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var vibrator: Vibrator

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "haptic_feedback")
    channel.setMethodCallHandler(this)
    vibrator = flutterPluginBinding.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "canVibrate") {
      canVibrate(result)
    } else {
      val pattern = Pattern.values().find { it.name == call.method }
      if (pattern != null) {
        vibratePattern(pattern, result)
      } else {
        result.notImplemented()
      }
    }
  }

  private fun canVibrate(result: Result) {
    result.success(vibrator.hasVibrator())
  }

  private fun vibratePattern(pattern: Pattern, result: Result) {
    try {
      if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()) {
        val effect = VibrationEffect.createWaveform(pattern.lengths, pattern.amplitudes, -1)
        vibrator.vibrate(effect)
      } else {
        vibrator.vibrate(pattern.lengths, -1)
      }
      result.success(null)
    } catch (e: Exception) {
      result.error("VIBRATION_ERROR", "Failed to vibrate", e.localizedMessage)
    }
  }



  private enum class Pattern(val lengths: LongArray, val amplitudes: IntArray) {
    success(longArrayOf(64, 64, 64), intArrayOf(149, 0, 159)),
    warning(longArrayOf(64, 100, 64), intArrayOf(192, 0, 151)),
    error(longArrayOf(64, 48, 64, 48, 64, 48, 80), intArrayOf(173, 0, 173, 0, 214, 0, 132)),
    light(longArrayOf(64), intArrayOf(129)),
    medium(longArrayOf(64), intArrayOf(170)),
    heavy(longArrayOf(64), intArrayOf(214)),
    rigid(longArrayOf(40), intArrayOf(193)),
    soft(longArrayOf(96), intArrayOf(151)),
    selection(longArrayOf(48), intArrayOf(129));
  }

}
