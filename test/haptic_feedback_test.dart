import 'package:flutter/foundation.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';
import 'package:haptic_feedback/src/haptics.dart';
import 'package:haptic_feedback/src/haptics_type.dart';
import 'package:haptic_feedback/src/haptics_usage.dart';

import 'support/mock_platforms.dart';

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

  test('canVibrate returns false on unsupported platforms', () async {
    debugDefaultTargetPlatformOverride = TargetPlatform.windows;
    addTearDown(() => debugDefaultTargetPlatformOverride = null);

    const fakePlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = fakePlatform;

    expect(await Haptics.canVibrate(), false);
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
    expect(recordingPlatform.lastUseAndroidHapticConstants, false);
  });

  test(
      'vibrate forwards useAndroidHapticConstants to the platform implementation',
      () async {
    final recordingPlatform = RecordingHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = recordingPlatform;

    await Haptics.vibrate(
      HapticsType.success,
      useAndroidHapticConstants: false,
    );

    expect(recordingPlatform.lastType, HapticsType.success);
    expect(recordingPlatform.lastUseAndroidHapticConstants, false);
  });
}
