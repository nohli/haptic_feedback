import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';
import 'package:haptic_feedback/src/haptics.dart';
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
  Future<void> vibrate(HapticsType type, {HapticsUsage? usage}) async {}
}

class RecordingHapticFeedbackPlatform
    with MockPlatformInterfaceMixin
    implements HapticFeedbackPlatform {
  HapticsType? lastType;
  HapticsUsage? lastUsage;

  @override
  Future<bool> canVibrate() async {
    return true;
  }

  @override
  Future<void> vibrate(HapticsType type, {HapticsUsage? usage}) async {
    lastType = type;
    lastUsage = usage;
  }
}

void main() {
  final initialPlatform = HapticFeedbackPlatform.instance;
  tearDown(() {
    HapticFeedbackPlatform.instance = initialPlatform;
  });

  test('$MethodChannelHapticFeedback is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHapticFeedback>());
  });

  test('canVibrate', () async {
    const fakePlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = fakePlatform;

    expect(await Haptics.canVibrate(), true);
  });

  test('vibrate forwards usage to the platform implementation', () async {
    final recordingPlatform = RecordingHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = recordingPlatform;

    await Haptics.vibrate(
      HapticsType.warning,
      usage: HapticsUsage.media,
    );

    expect(recordingPlatform.lastType, HapticsType.warning);
    expect(recordingPlatform.lastUsage, HapticsUsage.media);
  });
}
