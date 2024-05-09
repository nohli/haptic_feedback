import 'dart:io';

import 'package:haptic_feedback/haptic_feedback.dart';

import 'haptic_feedback_platform_interface.dart';

/// A class that exposes the haptic feedback functionality.
class Haptics {
  const Haptics._();

  /// Checks if the device is capable of performing haptic feedback.
  ///
  /// - On Android: Haptic feedback is generally available,
  /// but actual support can vary between devices and versions.
  /// - On iOS: Haptic feedback is available on iPhone 7 and later models.
  static Future<bool> canVibrate() {
    if (Platform.isAndroid || Platform.isIOS) {
      return HapticFeedbackPlatform.instance.canVibrate();
    } else {
      return Future.sync(() => false);
    }
  }

  /// Performs haptic feedback of [HapticsType] on the device.
  static Future<void> vibrate(HapticsType type) {
    if (Platform.isAndroid || Platform.isIOS) {
      return HapticFeedbackPlatform.instance.vibrate(type);
    } else {
      return Future.sync(() => {});
    }
  }
}
