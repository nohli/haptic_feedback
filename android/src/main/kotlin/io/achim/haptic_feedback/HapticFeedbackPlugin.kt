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
   *
   * Primitive selection rationale (matching Apple's HIG patterns from docs/patterns.md):
   * - PRIMITIVE_TICK: Short, subtle feedback (~30-50ms feel). Used for light/selection (low intensity single pulses).
   * - PRIMITIVE_CLICK: Sharp, distinct feedback (~50ms feel). Used for success/warning (multi-pulse confirmations) and medium/rigid (moderate-strong single pulses).
   * - PRIMITIVE_THUD: Deep, heavy feedback (~50-80ms feel). Used for heavy (max intensity) and error (accented pulse in multi-pulse pattern).
   * - PRIMITIVE_SPIN (API 31+): Longer, softer feedback (~80ms feel). Used for soft (longer duration, moderate intensity).
   */
  @RequiresApi(Build.VERSION_CODES.R)
  private fun vibrateWithPrimitives(pattern: Pattern, usage: Usage?) {
    val composition = VibrationEffect.startComposition()

    when (pattern) {
      Pattern.success -> {
        // Two pulses with increasing intensity - positive confirmation (55ms pause between)
        // Using CLICK for distinct "confirmation" feel matching iOS notification feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.7f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 55)
      }
      Pattern.warning -> {
        // Two pulses with decreasing intensity - attention-getting (91ms pause between)
        // Using CLICK for noticeable but not alarming feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.9f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.7f, 91)
      }
      Pattern.error -> {
        // Four quick pulses - error/failure indication with accented third pulse
        // Using CLICK for sharp feedback, with THUD for the strongest pulse to emphasize error
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f, 45)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 43)
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.6f, 41)
      }
      Pattern.light -> {
        // Light single pulse - subtle feedback
        // Using TICK for its light, unobtrusive character
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f)
      }
      Pattern.medium -> {
        // Medium single pulse - moderate feedback
        // Using CLICK for noticeable but not heavy feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f)
      }
      Pattern.heavy -> {
        // Heavy single pulse - strong feedback
        // Using THUD for maximum impact and deep feel
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
      }
      Pattern.rigid -> {
        // Sharp, crisp single pulse - hard/rigid feel
        // Using CLICK at high intensity for sharp, defined feedback
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.9f)
      }
      Pattern.soft -> {
        // Gentle, longer single pulse - soft/cushioned feel
        // Using SPIN (API 31+) for its longer, softer character; fallback to low-intensity TICK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.7f)
        } else {
          composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.7f)
        }
      }
      Pattern.selection -> {
        // Light single pulse - UI selection feedback
        // Using TICK at low intensity for subtle selection confirmation
        composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f)
      }
    }

    val effect = composition.compose()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && usage != null) {
      vibrator.vibrate(effect, usage.toVibrationAttributes())
    } else {
      vibrator.vibrate(effect)
    }
  }

  /**
   * Waveform patterns matching Apple's iOS haptic timings from docs/patterns.md.
   * Format: lengths = [duration1, pause1, duration2, pause2, ...], amplitudes = [intensity1, 0, intensity2, 0, ...]
   * Intensity values are normalized (0.0-1.0) * 255.
   */
  private enum class Pattern(val lengths: LongArray, val amplitudes: IntArray) {
    // success: 55ms @ 0.7, 55ms pause, 53ms @ 1.0
    success(longArrayOf(55, 55, 53), intArrayOf(178, 0, 255)),
    // warning: 55ms @ 0.9, 91ms pause, 55ms @ 0.7
    warning(longArrayOf(55, 91, 55), intArrayOf(229, 0, 178)),
    // error: 51ms @ 0.8, 45ms pause, 55ms @ 0.8, 43ms pause, 55ms @ 1.0, 41ms pause, 68ms @ 0.6
    error(longArrayOf(51, 45, 55, 43, 55, 41, 68), intArrayOf(204, 0, 204, 0, 255, 0, 153)),
    // light: 55ms @ 0.6
    light(longArrayOf(55), intArrayOf(153)),
    // medium: 51ms @ 0.8
    medium(longArrayOf(51), intArrayOf(204)),
    // heavy: 55ms @ 1.0
    heavy(longArrayOf(55), intArrayOf(255)),
    // rigid: 34ms @ 0.9
    rigid(longArrayOf(34), intArrayOf(229)),
    // soft: 82ms @ 0.7
    soft(longArrayOf(82), intArrayOf(178)),
    // selection: 41ms @ 0.6
    selection(longArrayOf(41), intArrayOf(153));

    /**
     * Returns the haptic primitives required for this pattern.
     * Used to check device support before attempting to use primitives.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun getPrimitives(): IntArray {
      return when (this) {
        success -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        warning -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        error -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK, VibrationEffect.Composition.PRIMITIVE_THUD)
        light -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_TICK)
        medium -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        heavy -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_THUD)
        rigid -> intArrayOf(VibrationEffect.Composition.PRIMITIVE_CLICK)
        soft -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          intArrayOf(VibrationEffect.Composition.PRIMITIVE_SPIN)
        } else {
          intArrayOf(VibrationEffect.Composition.PRIMITIVE_TICK)
        }
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
