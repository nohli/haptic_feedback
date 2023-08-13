import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelHapticFeedback platform = MethodChannelHapticFeedback();
  const MethodChannel channel = MethodChannel('haptic_feedback');

  setUp(() {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
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
}
