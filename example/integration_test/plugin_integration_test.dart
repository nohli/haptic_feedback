// This is a basic Flutter integration test.
//
// Since integration tests run in a full Flutter application, they can interact
// with the host side of a plugin implementation, unlike Dart unit tests.
//
// For more information about Flutter integration tests, please see
// https://docs.flutter.dev/cookbook/testing/integration/introduction

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  final binding = IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  final isIos = defaultTargetPlatform == TargetPlatform.iOS;
  if (isIos) {
    // XCTest requests semantics before the test body, so establish that
    // baseline before Flutter's leak check.
    binding.platformDispatcher.semanticsEnabledTestValue = true;
  }

  testWidgets('canVibrate reflects host capability', (
    WidgetTester tester,
  ) async {
    final canVibrate = await Haptics.canVibrate();

    expect(canVibrate, isA<bool>());
  }, semanticsEnabled: !isIos);

  testWidgets('prepare and vibrate complete for every type', (
    WidgetTester tester,
  ) async {
    for (final type in HapticsType.values) {
      await expectLater(Haptics.prepare(type), completes);
      await expectLater(Haptics.vibrate(type), completes);
    }
  }, semanticsEnabled: !isIos);

  testWidgets(
    'prepare rejects invalid types on iOS',
    (WidgetTester tester) async {
      const methodChannel = MethodChannel('haptic_feedback');
      const invalidArguments = <(Object?, Object?)>[
        (null, null),
        ('unknown', 'unknown'),
      ];

      for (final (arguments, expectedDetails) in invalidArguments) {
        await expectLater(
          methodChannel.invokeMethod<void>('prepare', arguments),
          throwsA(
            isA<PlatformException>()
                .having((error) => error.code, 'code', 'invalid_arguments')
                .having(
                  (error) => error.message,
                  'message',
                  'Invalid or missing haptic type',
                )
                .having((error) => error.details, 'details', expectedDetails),
          ),
        );
      }
    },
    skip: !isIos,
    semanticsEnabled: !isIos,
  );
}
