import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'haptic_feedback_platform_interface.dart';
import 'haptics_type.dart';
import 'haptics_usage.dart';

/// An implementation of [HapticFeedbackPlatform] that uses method channels.
class MethodChannelHapticFeedback extends HapticFeedbackPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  static const methodChannel = MethodChannel('haptic_feedback');

  @override
  Future<bool> canVibrate() async {
    return await methodChannel.invokeMethod<bool>('canVibrate') ?? false;
  }

  @override
  Future<void> vibrate(HapticsType type, {HapticsUsage? usage}) async {
    final arguments =
        usage == null ? null : <String, dynamic>{'usage': usage.name};
    return await methodChannel.invokeMethod(type.name, arguments);
  }
}
