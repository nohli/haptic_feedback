package io.achim.haptic_feedback

import android.os.Vibrator
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito

internal class HapticFeedbackPluginTest {
  @Test
  fun canVibrate_usesInjectedVibrator() {
    val plugin = HapticFeedbackPlugin()
    val vibrator: Vibrator = Mockito.mock(Vibrator::class.java)
    Mockito.`when`(vibrator.hasVibrator()).thenReturn(true)
    plugin.setVibratorForTesting(vibrator)

    val call = MethodCall("canVibrate", null)
    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)

    plugin.onMethodCall(call, mockResult)

    Mockito.verify(mockResult).success(true)
  }

  @Test
  fun usageFromArguments_returnsNullWhenMissing() {
    val usage = HapticFeedbackPlugin.Usage.fromArguments(null)

    assertNull(usage)
  }

  @Test
  fun usageFromArguments_matchesCaseInsensitively() {
    val usage = HapticFeedbackPlugin.Usage.fromArguments(mapOf("usage" to "MeDiA"))

    assertEquals(HapticFeedbackPlugin.Usage.media, usage)
  }
}
