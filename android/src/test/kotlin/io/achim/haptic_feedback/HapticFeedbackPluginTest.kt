package io.achim.haptic_feedback

import android.content.Context
import android.os.Vibrator
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.Test
import org.mockito.Mockito

/*
 * This demonstrates a simple unit test of the Kotlin portion of this plugin's implementation.
 *
 * Once you have built the plugin's example app, you can run these tests from the command
 * line by running `./gradlew testDebugUnitTest` in the `example/android/` directory, or
 * you can run them directly from IDEs that support JUnit such as Android Studio.
 */

internal class HapticFeedbackPluginTest {
  @Test
  fun onMethodCall_getPlatformVersion_returnsExpectedValue() {
    val plugin = HapticFeedbackPlugin()

    // Mock the FlutterPluginBinding and its dependencies
    val mockBinding = Mockito.mock(FlutterPlugin.FlutterPluginBinding::class.java)
    val mockBinaryMessenger = Mockito.mock(BinaryMessenger::class.java)
    val mockContext = Mockito.mock(Context::class.java)
    val mockVibrator = Mockito.mock(Vibrator::class.java)

    Mockito.`when`(mockBinding.binaryMessenger).thenReturn(mockBinaryMessenger)
    Mockito.`when`(mockBinding.applicationContext).thenReturn(mockContext)
    Mockito.`when`(mockContext.getSystemService(Context.VIBRATOR_SERVICE)).thenReturn(mockVibrator)
    Mockito.`when`(mockVibrator.hasVibrator()).thenReturn(true)

    // Initialize the plugin
    plugin.onAttachedToEngine(mockBinding)

    val call = MethodCall("canVibrate", null)
    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(call, mockResult)

    Mockito.verify(mockResult).success(true)
  }
}
