import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';
import 'package:haptic_feedback/src/haptics_type.dart';
import 'package:haptic_feedback/src/haptics_usage.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHapticFeedbackPlatform
    with MockPlatformInterfaceMixin
    implements HapticFeedbackPlatform {
  const MockHapticFeedbackPlatform();

  @override
  Future<bool> canVibrate() async {
    return true;
  }

  @override
  Future<void> vibrate(
    HapticsType type, {
    HapticsUsage? usage,
    bool useAndroidHapticConstants = false,
  }) async {}
}

class RecordingHapticFeedbackPlatform
    with MockPlatformInterfaceMixin
    implements HapticFeedbackPlatform {
  HapticsType? lastType;
  HapticsUsage? lastUsage;
  bool? lastUseAndroidHapticConstants;

  @override
  Future<bool> canVibrate() async {
    return true;
  }

  @override
  Future<void> vibrate(
    HapticsType type, {
    HapticsUsage? usage,
    bool useAndroidHapticConstants = false,
  }) async {
    lastType = type;
    lastUsage = usage;
    lastUseAndroidHapticConstants = useAndroidHapticConstants;
  }
}
