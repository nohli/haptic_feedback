package io.achim.haptic_feedback

import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
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
    vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = flutterPluginBinding.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibratorManager.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      flutterPluginBinding.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
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
        val usage = Usage.fromArguments(call.arguments)
        vibratePattern(pattern, usage, result)
      } else {
        result.notImplemented()
      }
    }
  }

  private fun canVibrate(result: Result) {
    result.success(vibrator.hasVibrator())
  }

  /**
   * Checks if the device supports haptic primitives (API 30+).
   * Returns true only if all required primitives for the pattern are supported.
   */
  @RequiresApi(Build.VERSION_CODES.R)
  private fun supportsPrimitives(pattern: Pattern): Boolean {
    val requiredPrimitives = pattern.getPrimitives()
    return vibrator.areAllPrimitivesSupported(*requiredPrimitives)
  }

  private fun vibratePattern(pattern: Pattern, usage: Usage?, result: Result) {
    val shouldNotRepeat = -1

    try {
      // Try to use haptic primitives on API 30+ for better haptic feedback
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && supportsPrimitives(pattern)) {
        vibrateWithPrimitives(pattern, usage)
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()) {
        // Fall back to waveform with amplitude control
        val effect = VibrationEffect.createWaveform(pattern.lengths, pattern.amplitudes, shouldNotRepeat)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && usage != null) {
          vibrator.vibrate(effect, usage.toVibrationAttributes())
        } else {
          vibrator.vibrate(effect)
        }
      } else {
        // Legacy fallback for older devices
        // https://developer.android.com/reference/android/os/Vibrator#vibrate(long[],%20int)
        val leadingDelay = longArrayOf(0)
        val legacyPattern = leadingDelay + pattern.lengths
        @Suppress("DEPRECATION")
        vibrator.vibrate(legacyPattern, shouldNotRepeat)
      }
      result.success(null)
    } catch (e: Exception) {
      result.error("VIBRATION_ERROR", "Failed to vibrate", e.localizedMessage)
    }
  }

  /**
   * Vibrates using haptic primitives (API 30+) for more distinct and higher-quality haptic feedback.
   * This is especially beneficial for devices with advanced haptic hardware like Samsung with HD vibrations.
   */
  @RequiresApi(Build.VERSION_CODES.R)
  private fun vibrateWithPrimitives(pattern: Pattern, usage: Usage?) {
    val composition = VibrationEffect.startComposition()

    when (pattern) {
      Pattern.success -> {
        // Two ticks with increasing intensity - positive confirmation
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.5f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 1.0f, 50)
      }
      Pattern.warning -> {
        // A thud followed by a click - attention-getting
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.7f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.5f, 100)
      }
      Pattern.error -> {
        // Multiple quick clicks - error/failure indication
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 80)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.6f, 80)
      }
      Pattern.light -> {
        // Light tick - subtle feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.3f)
      }
      Pattern.medium -> {
        // Medium click - moderate feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.6f)
      }
      Pattern.heavy -> {
        // Heavy thud - strong feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
      }
      Pattern.rigid -> {
        // Sharp click - hard/rigid feel
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f)
      }
      Pattern.soft -> {
        // Gentle spin - soft/cushioned feel
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.4f)
      }
      Pattern.selection -> {
        // Light tick - UI selection feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.2f)
      }
    }

    val effect = composition.compose()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && usage != null) {
      vibrator.vibrate(effect, usage.toVibrationAttributes())
    } else {
      vibrator.vibrate(effect)
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
    selection(longArrayOf(57), intArrayOf(150));

    /**
     * Returns the haptic primitives required for this pattern.
     * Used to check device support before attempting to use primitives.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun getPrimitives(): IntArray {
      return when (this) {
        success -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_TICK)
        warning -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_THUD, VibrationEffect.Composition.PRIMITIVE_CLICK)
        error -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        light -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_TICK)
        medium -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        heavy -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_THUD)
        rigid -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        soft -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_SPIN)
        selection -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_TICK)
      }
    }
  }

  internal enum class Usage {
    alarm,
    communicationRequest,
    hardwareFeedback,
    media,
    notification,
    physicalEmulation,
    ringtone,
    touch,
    unknown;

    companion object {
      fun fromArguments(arguments: Any?): Usage? {
        val usageValue = (arguments as? Map<*, *>)?.get("usage") as? String ?: return null
        return entries.firstOrNull { it.name.equals(usageValue, ignoreCase = true) }
      }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun toVibrationAttributes(): VibrationAttributes {
      val usageConstant = when (this) {
        alarm -> VibrationAttributes.USAGE_ALARM
        communicationRequest -> VibrationAttributes.USAGE_COMMUNICATION_REQUEST
        hardwareFeedback -> VibrationAttributes.USAGE_HARDWARE_FEEDBACK
        media -> VibrationAttributes.USAGE_MEDIA
        notification -> VibrationAttributes.USAGE_NOTIFICATION
        physicalEmulation -> VibrationAttributes.USAGE_PHYSICAL_EMULATION
        ringtone -> VibrationAttributes.USAGE_RINGTONE
        touch -> VibrationAttributes.USAGE_TOUCH
        unknown -> VibrationAttributes.USAGE_UNKNOWN
      }
      return VibrationAttributes.Builder().setUsage(usageConstant).build()
    }
  }
}
