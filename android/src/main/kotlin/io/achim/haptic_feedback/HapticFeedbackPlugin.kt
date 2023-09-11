package io.achim.haptic_feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
    success(longArrayOf(75, 75, 75), intArrayOf(178, 0, 255)),
    warning(longArrayOf(79, 119, 75), intArrayOf(227, 0, 178)),
    error(longArrayOf(75, 61, 79, 57, 75, 57, 97), intArrayOf(203, 0, 200, 0, 252, 0, 150)),
    light(longArrayOf(79), intArrayOf(154)),
    medium(longArrayOf(79), intArrayOf(203)),
    heavy(longArrayOf(75), intArrayOf(252)),
    rigid(longArrayOf(48), intArrayOf(227)),
    soft(longArrayOf(110), intArrayOf(178)),
    selection(longArrayOf(57), intArrayOf(150))
  }
}
