import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptic_feedback_platform_interface.dart';
import 'package:haptic_feedback/src/haptics.dart';
import 'package:haptic_feedback/src/haptics_type.dart';
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
  Future<void> vibrate(HapticsType type) async {}
}

void main() {
  final initialPlatform = HapticFeedbackPlatform.instance;

  test('$MethodChannelHapticFeedback is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHapticFeedback>());
  });

  test('canVibrate returns false on unsupported platforms', () async {
    const fakePlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = fakePlatform;

    // On desktop platforms (where tests typically run), canVibrate should
    // return false because only Android and iOS are supported.
    // This allows UI components using haptics to be tested without mocking.
    expect(await Haptics.canVibrate(), false);
  });

  test('vibrate does nothing on unsupported platforms', () async {
    const fakePlatform = MockHapticFeedbackPlatform();
    HapticFeedbackPlatform.instance = fakePlatform;

    // Should complete without error on unsupported platforms
    await Haptics.vibrate(HapticsType.selection);
  });
}
