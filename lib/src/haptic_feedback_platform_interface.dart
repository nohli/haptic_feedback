import 'package:plugin_platform_interface/plugin_platform_interface.dart'
    show PlatformInterface;

import 'haptic_feedback_method_channel.dart';
import 'haptics_type.dart';
import 'haptics_usage.dart';

/// The interface for using the haptic feedback plugin.
abstract class HapticFeedbackPlatform extends PlatformInterface {
  /// Constructs a HapticFeedbackPlatform.
  HapticFeedbackPlatform() : super(token: _token);

  static final Object _token = Object();

  static HapticFeedbackPlatform _instance = MethodChannelHapticFeedback();

  /// The default instance of [HapticFeedbackPlatform] to use.
  ///
  /// Defaults to [MethodChannelHapticFeedback].
  static HapticFeedbackPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HapticFeedbackPlatform] when
  /// they register themselves.
  static set instance(HapticFeedbackPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Checks if the device is capable of performing haptic feedback.
  Future<bool> canVibrate() async {
    throw UnsupportedError(
      'Use the implementation method of MethodChannelHapticFeedback.',
    );
  }

  /// Performs haptic feedback on the device.
  Future<void> vibrate(HapticsType type, {HapticsUsage? usage}) {
    throw UnsupportedError(
      'Use the implementation method of MethodChannelHapticFeedback.',
    );
  }
}
