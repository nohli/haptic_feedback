import 'package:haptic_feedback/haptic_feedback.dart';

import 'haptic_feedback_platform_interface.dart';

///
class Haptics {
  /// A class that exposes the haptic feedback functionality.
  const Haptics();

  /// Checks if the device is capable of performing haptic feedback.
  /// Haptic feedack is available on iPhones >= 7 and Android devices.
  static Future<bool> get canVibrate {
    return HapticFeedbackPlatform.instance.canVibrate();
  }

  /// Performs haptic feedback on the device.
  static Future<void> vibrate(HapticsType type) {
    return HapticFeedbackPlatform.instance.vibrate(type);
  }
}
