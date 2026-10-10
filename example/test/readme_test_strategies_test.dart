import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';

class FakeHapticFeedbackPlatform extends HapticFeedbackPlatform {
  @override
  Future<bool> canVibrate() async => true;

  @override
  Future<void> vibrate(
    HapticsType type, {
    HapticsUsage? usage,
    bool useAndroidHapticConstants = false,
  }) async {}
}

void main() {
  final initialPlatform = HapticFeedbackPlatform.instance;

  tearDown(() {
    HapticFeedbackPlatform.instance = initialPlatform;
  });

  TestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('widget-test platform overrides are restored in the body', (
    tester,
  ) async {
    final originalTarget = debugDefaultTargetPlatformOverride;
    debugDefaultTargetPlatformOverride = TargetPlatform.iOS;
    try {
      expect(debugDefaultTargetPlatformOverride, TargetPlatform.iOS);
      HapticFeedbackPlatform.instance = FakeHapticFeedbackPlatform();
      expect(await Haptics.canVibrate(), isTrue);
    } finally {
      debugDefaultTargetPlatformOverride = originalTarget;
    }
  });

  test('target platform override is reset after previous test', () {
    expect(debugDefaultTargetPlatformOverride, isNull);
  });

  test('README handling catches vibration and registration failures', () async {
    const channel = MethodChannelHapticFeedback.methodChannel;
    final messenger =
        TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;
    final originalTarget = debugDefaultTargetPlatformOverride;
    debugDefaultTargetPlatformOverride = TargetPlatform.android;
    HapticFeedbackPlatform.instance = MethodChannelHapticFeedback();
    try {
      for (final failure in <Exception>[
        PlatformException(code: 'VIBRATION_ERROR'),
        MissingPluginException('Missing registration'),
      ]) {
        messenger.setMockMethodCallHandler(channel, (_) async => throw failure);
        Exception? handled;
        try {
          await Haptics.vibrate(HapticsType.success);
        } on PlatformException catch (error) {
          handled = error;
        } on MissingPluginException catch (error) {
          handled = error;
        }
        expect(handled, isA<Exception>());
        expect(handled.runtimeType, failure.runtimeType);
      }
    } finally {
      messenger.setMockMethodCallHandler(channel, null);
      debugDefaultTargetPlatformOverride = originalTarget;
    }
  }, skip: kIsWeb);

  test('mocking the platform interface avoids channel calls', () async {
    final mockPlatform = FakeHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = mockPlatform;

    expect(await Haptics.canVibrate(), isTrue);

    await Haptics.vibrate(
      HapticsType.success,
      usage: HapticsUsage.notification,
      useAndroidHapticConstants: true,
    );
  });

  test(
    'mocking the method channel keeps MethodChannelHapticFeedback usable',
    () async {
      final binding = TestDefaultBinaryMessengerBinding.instance;
      const channel = MethodChannelHapticFeedback.methodChannel;
      MethodCall? lastCall;
      Map<dynamic, dynamic>? lastArguments;

      binding.defaultBinaryMessenger.setMockMethodCallHandler(channel, (
        MethodCall call,
      ) async {
        lastCall = call;
        lastArguments = call.arguments as Map<dynamic, dynamic>?;
        if (call.method == 'canVibrate') return true;
        return null;
      });
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
      expect(lastArguments, {
        'usage': 'media',
        'useAndroidHapticConstants': true,
      });
    },
  );
}
