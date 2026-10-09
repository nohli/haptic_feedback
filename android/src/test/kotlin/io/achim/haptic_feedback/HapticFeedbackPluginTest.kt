package io.achim.haptic_feedback

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.Window
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [24, 26, 30, 31, 33, 36])
class HapticFeedbackPluginTest {
  private lateinit var plugin: HapticFeedbackPlugin
  private lateinit var vibrator: Vibrator
  private lateinit var result: MethodChannel.Result

  @Before
  fun setUp() {
    plugin = HapticFeedbackPlugin()
    vibrator = mock(Vibrator::class.java)
    result = mock(MethodChannel.Result::class.java)
    val context = spy(RuntimeEnvironment.getApplication())
    if (Build.VERSION.SDK_INT >= 31) {
      val manager = mock(VibratorManager::class.java)
      `when`(manager.defaultVibrator).thenReturn(vibrator)
      doReturn(manager).`when`(context).getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
    } else {
      doReturn(vibrator).`when`(context).getSystemService(Context.VIBRATOR_SERVICE)
    }
    val binding = mock(FlutterPlugin.FlutterPluginBinding::class.java)
    `when`(binding.applicationContext).thenReturn(context)
    `when`(binding.binaryMessenger).thenReturn(mock(BinaryMessenger::class.java))
    plugin.onAttachedToEngine(binding)
  }

  private fun attachView(): View {
    val view = mock(View::class.java)
    val window = mock(Window::class.java)
    val activity = mock(Activity::class.java)
    val binding = mock(ActivityPluginBinding::class.java)
    `when`(window.decorView).thenReturn(view)
    `when`(activity.window).thenReturn(window)
    `when`(binding.activity).thenReturn(activity)
    plugin.onAttachedToActivity(binding)
    return view
  }

  @Test
  fun reportsHardwareCapability() {
    `when`(vibrator.hasVibrator()).thenReturn(true)
    plugin.onMethodCall(MethodCall("canVibrate", null), result)
    verify(result).success(true)
  }

  @Test
  fun prepareIsANoOp() {
    plugin.onMethodCall(MethodCall("prepare", "light"), result)
    verify(result).success(null)
    verifyNoInteractions(vibrator)
  }

  @Test
  fun unknownMethodsAreNotImplemented() {
    plugin.onMethodCall(MethodCall("unknown", null), result)
    verify(result).notImplemented()
    verifyNoInteractions(vibrator)
  }

  @Test
  @Config(sdk = [26, 30, 33, 36])
  fun waveformUsesAmplitudeControlWhenAvailable() {
    `when`(vibrator.hasAmplitudeControl()).thenReturn(true)
    plugin.onMethodCall(MethodCall("success", null), result)
    val effect = VibrationEffect.createWaveform(longArrayOf(55, 55, 53), intArrayOf(178, 0, 255), -1)
    verify(vibrator).vibrate(effect)
    verify(result).success(null)
  }

  @Test
  @Config(sdk = [31, 33, 36])
  fun softUsesSpinOnNewerApis() {
    `when`(vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_SPIN)).thenReturn(true)
    plugin.onMethodCall(MethodCall("soft", null), result)
    val effect = VibrationEffect.startComposition()
      .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.7f).compose()
    verify(vibrator).vibrate(effect)
    verify(result).success(null)
  }

  @Test
  @Config(sdk = [30, 33, 36])
  fun nativeFeedbackDoesNotBypassDisabledFeedback() {
    val view = attachView()
    // A false return can mean the user or view has disabled feedback.
    `when`(view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)).thenReturn(false)
    plugin.onMethodCall(MethodCall("success", mapOf("useAndroidHapticConstants" to true)), result)
    verify(view).performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    verifyNoInteractions(vibrator)
    verify(result).success(null)
  }

  @Test
  fun detachedActivityFallsBackToVibrator() {
    val view = attachView()
    plugin.onDetachedFromActivityForConfigChanges()
    plugin.onMethodCall(MethodCall("light", mapOf("useAndroidHapticConstants" to true)), result)
    verifyNoInteractions(view)
    verify(result).success(null)
  }

  @Test
  fun vibrationErrorsAreReturnedToDart() {
    if (Build.VERSION.SDK_INT >= 26) {
      `when`(vibrator.hasAmplitudeControl()).thenReturn(true)
      doThrow(SecurityException("Access denied")).`when`(vibrator).vibrate(any(VibrationEffect::class.java))
    } else {
      @Suppress("DEPRECATION")
      doThrow(SecurityException("Access denied")).`when`(vibrator).vibrate(any(LongArray::class.java), eq(-1))
    }
    plugin.onMethodCall(MethodCall("light", null), result)
    verify(result).error("VIBRATION_ERROR", "Failed to vibrate", "Access denied")
    verifyNoMoreInteractions(result)
  }
}
