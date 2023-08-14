import 'package:haptic_feedback/haptic_feedback.dart';

import 'haptic_feedback_platform_interface.dart';

///
class Haptics {
  /// A class that exposes the haptic feedback functionality.
  const Haptics();

  /// Checks if the device is capable of performing haptic feedback.
  ///
  /// - On Android: Haptic feedback is generally available,
  /// but actual support can vary between devices and versions.
  /// - On iOS: Haptic feedback is available on iPhone 7 and later models.
  static Future<bool> get canVibrate {
    return HapticFeedbackPlatform.instance.canVibrate();
  }

  /// Performs haptic feedback of [HapticsType] on the device.
  static Future<void> vibrate(HapticsType type) {
    return HapticFeedbackPlatform.instance.vibrate(type);
  }
}
