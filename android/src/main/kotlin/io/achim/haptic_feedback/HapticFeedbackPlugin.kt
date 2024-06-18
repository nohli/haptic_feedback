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
    success(Helper.preComputeLengths(longArrayOf(80, 80, 80)), Helper.preComputeAmplitudes(intArrayOf(272, 0, 290))),
    warning(Helper.preComputeLengths(longArrayOf(80, 125, 80)), Helper.preComputeAmplitudes(intArrayOf(350, 0, 275))),
    error(Helper.preComputeLengths(longArrayOf(80, 60, 80, 60, 80, 60, 100)), Helper.preComputeAmplitudes(intArrayOf(315, 0, 315, 0, 390, 0, 240))),
    light(Helper.preComputeLengths(longArrayOf(80)), Helper.preComputeAmplitudes(intArrayOf(235))),
    medium(Helper.preComputeLengths(longArrayOf(80)), Helper.preComputeAmplitudes(intArrayOf(310))),
    heavy(Helper.preComputeLengths(longArrayOf(80)), Helper.preComputeAmplitudes(intArrayOf(390))),
    rigid(Helper.preComputeLengths(longArrayOf(50)), Helper.preComputeAmplitudes(intArrayOf(352))),
    soft(Helper.preComputeLengths(longArrayOf(120)), Helper.preComputeAmplitudes(intArrayOf(275))),
    selection(Helper.preComputeLengths(longArrayOf(60)), Helper.preComputeAmplitudes(intArrayOf(235)));

  }

  class Helper {
    companion object {
      private const val timeMultiplier = 0.8
      private const val amplitudeMultiplier = 0.55

      fun preComputeLengths(lengths: LongArray): LongArray {
        return lengths.map { (it * timeMultiplier).toLong() }.toLongArray()
      }

      fun preComputeAmplitudes(amplitudes: IntArray): IntArray {
        return amplitudes.map { (it * amplitudeMultiplier).toInt() }.toIntArray()
      }

    }
  }

}
