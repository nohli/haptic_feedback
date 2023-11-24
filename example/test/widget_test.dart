// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
import 'package:haptic_feedback_example/main.dart';

void main() {
  testWidgets(
    'Haptics types are displayed in example',
    (WidgetTester tester) async {
      // Set the size of the testing window.
      tester.view.physicalSize = const Size(1080, 1920);
      tester.view.devicePixelRatio = 1;

      // Build our app and trigger a frame.
      await tester.pumpWidget(const MyApp());

      // Verify that it shows the last haptics type.
      expect(
        find.byWidgetPredicate(
          (Widget widget) {
            return widget is Text &&
                widget.data!.startsWith(HapticsType.values.last.name);
          },
        ),
        findsOneWidget,
      );
    },
  );
}
