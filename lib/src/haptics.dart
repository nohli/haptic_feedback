import 'package:haptic_feedback/haptic_feedback.dart';

import 'haptic_feedback_platform_interface.dart';
import 'is_platform_supported.dart';

/// A class that exposes the haptic feedback functionality.
class Haptics {
  const Haptics._();

  /// Checks if the device is capable of performing haptic feedback.
  ///
  /// - On iOS: Haptic feedback is available on iPhone 7 and later models.
  /// - On Android: Haptic feedback is generally available,
  /// but actual support can vary between devices and versions.
  static Future<bool> canVibrate() async {
    if (!isPlatformSupported) {
      return false;
    }

    return HapticFeedbackPlatform.instance.canVibrate();
  }

  /// Performs haptic feedback of [HapticsType] on the device.
  /// Performs nothing if the platform is not supported.
  ///
  /// Provide [usage] to influence how Android routes the vibration when running
  /// on API level 33 or later (for example, `HapticsUsage.media` for breathing
  /// exercises). The value is ignored on older Android versions and on iOS.
  ///
  /// On Android, set [useAndroidHapticConstants] to `true` to
  /// use the platform `HapticFeedbackConstants` (e.g., `CONFIRM`, `REJECT`,
  /// `VIRTUAL_KEY`) when available. Set to `false` to use custom vibration
  /// primitives instead, which more closely mirror the iOS patterns.
  static Future<void> vibrate(
    HapticsType type, {
    HapticsUsage? usage,
    bool useAndroidHapticConstants = false,
  }) async {
    if (!isPlatformSupported) {
      return;
    }

    return HapticFeedbackPlatform.instance.vibrate(
      type,
      usage: usage,
      useAndroidHapticConstants: useAndroidHapticConstants,
    );
  }
}
