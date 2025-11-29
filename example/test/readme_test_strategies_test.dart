import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';

import 'support/mock_platforms.dart';

void main() {
  final initialPlatform = HapticFeedbackPlatform.instance;

  tearDown(() {
    HapticFeedbackPlatform.instance = initialPlatform;
  });

  TestWidgetsFlutterBinding.ensureInitialized();

  test('overriding target platform in tests is cleaned up', () async {
    addTearDown(() {
      expect(debugDefaultTargetPlatformOverride, isNull);
    });
    addTearDown(() => debugDefaultTargetPlatformOverride = null);
    debugDefaultTargetPlatformOverride = TargetPlatform.iOS;
    expect(debugDefaultTargetPlatformOverride, TargetPlatform.iOS);

    HapticFeedbackPlatform.instance = const MockHapticFeedbackPlatform();

    expect(await Haptics.canVibrate(), isTrue);
  });

  test('target platform override is reset after previous test', () {
    expect(debugDefaultTargetPlatformOverride, isNull);
  });

  test('mocking the platform interface avoids channel calls', () async {
    const mockPlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = mockPlatform;

    expect(await Haptics.canVibrate(), isTrue);

    await Haptics.vibrate(
      HapticsType.success,
      usage: HapticsUsage.notification,
      useAndroidHapticConstants: true,
    );
  });

  test('mocking the method channel keeps MethodChannelHapticFeedback usable',
      () async {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    const channel = MethodChannelHapticFeedback.methodChannel;
    MethodCall? lastCall;
    Map<dynamic, dynamic>? lastArguments;

    binding.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall call) async {
        lastCall = call;
        lastArguments = call.arguments as Map<dynamic, dynamic>?;
        if (call.method == 'canVibrate') return true;
        return null;
      },
    );
    addTearDown(
      () => binding.defaultBinaryMessenger.setMockMethodCallHandler(
        channel,
        null,
      ),
    );

    final platform = MethodChannelHapticFeedback();

    expect(await platform.canVibrate(), isTrue);
    await platform.vibrate(
      HapticsType.success,
      usage: HapticsUsage.media,
      useAndroidHapticConstants: true,
    );

    expect(lastCall?.method, 'success');
    expect(
      lastArguments,
      {'usage': 'media', 'useAndroidHapticConstants': true},
    );
  });
}
