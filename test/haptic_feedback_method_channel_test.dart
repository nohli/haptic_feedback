import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptics_type.dart';
import 'package:haptic_feedback/src/haptics_usage.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelHapticFeedback platform = MethodChannelHapticFeedback();
  const MethodChannel channel = MethodChannel('haptic_feedback');

  MethodCall? lastMethodCall;

  setUp(() {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        lastMethodCall = methodCall;
        return true;
      },
    );
  });

  tearDown(() {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('canVibrate', () async {
    expect(await platform.canVibrate(), true);
  });

  test('vibrate forwards usage argument when provided', () async {
    await platform.vibrate(
      HapticsType.success,
      usage: HapticsUsage.media,
    );

    expect(lastMethodCall?.method, 'success');
    expect(lastMethodCall?.arguments, {'usage': 'media', 'useNativeHaptics': true});
  });

  test('vibrate includes useNativeHaptics in arguments', () async {
    await platform.vibrate(HapticsType.light);

    expect(lastMethodCall?.method, 'light');
    expect(lastMethodCall?.arguments, {'useNativeHaptics': true});
  });

  test('vibrate forwards useNativeHaptics false', () async {
    await platform.vibrate(HapticsType.heavy, useNativeHaptics: false);

    expect(lastMethodCall?.method, 'heavy');
    expect(lastMethodCall?.arguments, {'useNativeHaptics': false});
  });
}
