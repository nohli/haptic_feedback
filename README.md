# haptic_feedback

A haptic feedback plugin for both iOS and Android.

While it utilizes [standard iOS haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS), it aims to emulate these same haptic patterns on Android for a consistent experience across platforms.

`haptic_feedback` requires Flutter 3.47 or newer and Android API 24 or newer when targeting Android.
iOS builds require iOS 15 or newer.
Android builds require Java 17 or newer and AGP 9 with built-in Kotlin support. The plugin does not
apply or bundle the external Kotlin Gradle Plugin. Existing apps must migrate
their Gradle configuration and remove any `android.builtInKotlin=false` opt-out;
updating Flutter alone does not migrate the app. See Flutter's
[AGP 9 migration guide](https://docs.flutter.dev/release/breaking-changes/migrate-to-built-in-kotlin/for-app-developers).

For a guide on when to use each haptic type, see [Missing haptic types, and which to use when](https://flutterpro.design/details/haptic-feedback) by Kamran.

## Getting Started

### 1. Add the dependency

```shell
flutter pub add haptic_feedback
```

### 2. Use the plugin

```dart
import 'package:haptic_feedback/haptic_feedback.dart';

final canVibrate = await Haptics.canVibrate();

// Call shortly before an expected haptic to reduce initial latency on iOS.
await Haptics.prepare(HapticsType.success);
await Haptics.vibrate(HapticsType.success);
await Haptics.vibrate(HapticsType.warning);
await Haptics.vibrate(HapticsType.error);

await Haptics.vibrate(HapticsType.light);
await Haptics.vibrate(HapticsType.medium);
await Haptics.vibrate(HapticsType.heavy);

await Haptics.vibrate(HapticsType.rigid);
await Haptics.vibrate(HapticsType.soft);

await Haptics.vibrate(HapticsType.selection);
```

`Haptics.prepare()` warms the matching iOS feedback generator. Call it shortly
before the expected event, then trigger the same `HapticsType` with
`Haptics.vibrate()`. It is a no-op on Android and unsupported platforms.

`canVibrate()` reports hardware capability. A `true` result does not guarantee
that feedback will play: user settings, device state, and hardware support can
suppress it. On web and desktop, it returns `false`, and `prepare()` and
`vibrate()` do nothing.

Android vibration exceptions are returned as `PlatformException` with code
`VIBRATION_ERROR`. Missing plugin registration can throw `MissingPluginException`.
You can handle these failures when needed (exception classes are from
`package:flutter/services.dart`):

```dart
try {
  await Haptics.vibrate(HapticsType.success);
} on PlatformException catch (e) {
  // Handle or log as needed
}
```

## Android-specific options

```dart
// Use native Android haptic constants (default: false)
// When true, uses HapticFeedbackConstants like CONFIRM, REJECT, etc.
// When false, uses custom vibration primitives that are more aligned
// with iOS haptics
await Haptics.vibrate(
  HapticsType.success,
  useAndroidHapticConstants: false,  // default
);

// On Android 13+, you can hint how the system should treat this vibration
// (alarm, communicationRequest, hardwareFeedback, media, notification, physicalEmulation, ringtone, touch, unknown)
await Haptics.vibrate(
  HapticsType.success,
  usage: HapticsUsage.media,
);
```

The optional `usage` parameter is a hint for the system.
It can influence how the vibration is routed and which volume / haptics
settings control it (for example, notification vs touch feedback).

Use a concrete value whenever the vibration clearly matches one of the
defined categories (for example `HapticsUsage.notification` for reminders
or status updates), and use `HapticsUsage.touch` for taps and other UI feedback.
Omitting `usage` keeps Android's default classification, which is not guaranteed
to behave the same as explicitly specifying `HapticsUsage.unknown` on every OS.

When a native haptic constant is used, Android applies its touch-feedback
settings and ignores `usage`. Custom patterns and fallbacks apply `usage` on
API 33+, including devices without amplitude control.

## iOS: SwiftPM vs CocoaPods

Flutter has enabled Swift Package Manager (SwiftPM) by default since 3.44.
This plugin supports both SwiftPM and CocoaPods. Both configuration commands
below remain available in Flutter 3.47.

If you previously disabled SwiftPM, re-enable it globally with:

```shell
flutter config --enable-swift-package-manager
```

To use CocoaPods globally for the current user, run:

```shell
flutter config --no-enable-swift-package-manager
```

For a project-specific CocoaPods opt-out, set this in the app's `pubspec.yaml`:

```yaml
flutter:
  config:
    enable-swift-package-manager: false
```

Disabling SwiftPM does not remove its existing Xcode integration. See
[Flutter's SwiftPM guide](https://docs.flutter.dev/packages-and-plugins/swift-package-manager/for-app-developers)
for migration and removal steps.

## Platform Implementation

### iOS

Uses Apple's native haptic feedback APIs:
- `UINotificationFeedbackGenerator` for success, warning, and error
- `UIImpactFeedbackGenerator` for light, medium, heavy, rigid, and soft
- `UISelectionFeedbackGenerator` for selection

### Android

On Android, the plugin selects a strategy based on both the API level and
hardware support. These patterns approximate iOS feedback; their timing and
feel can vary across devices.

#### Strategy 1: Native HapticFeedbackConstants (when enabled)

When `useAndroidHapticConstants: true`, the plugin uses Android's system-level haptic constants via `View.performHapticFeedback()`:

| Type      | Android Constant                               | API Level |
|-----------|------------------------------------------------|-----------|
| success   | `CONFIRM`                                      | ≥ 30      |
| error     | `REJECT`                                       | ≥ 30      |
| light     | `VIRTUAL_KEY`                                  | ≥ 5       |
| medium    | `KEYBOARD_PRESS` (≥ 27) / `KEYBOARD_TAP` (≥ 8) | ≥ 8       |
| heavy     | `CONTEXT_CLICK`                                | ≥ 23      |
| selection | `CLOCK_TICK`                                   | ≥ 21      |

`warning`, `rigid`, and `soft` have no mapping in this plugin and use custom
patterns. Custom patterns also apply when the mapped constant is unavailable
or no activity is attached. Once a mapped constant is dispatched, the plugin
respects Android's result and does not bypass disabled feedback with a fallback.

#### Strategy 2: Haptic Primitives (API 30+)

When native constants aren't available or `useAndroidHapticConstants: false` (default), the plugin uses `VibrationEffect.Composition`, which is tuned to resemble the iOS patterns:

| Type      | Primitive(s)         | Description                              |
|-----------|----------------------|------------------------------------------|
| success   | CLICK × 2            | Two clicks with increasing intensity     |
| warning   | CLICK × 2            | Two clicks with decreasing intensity     |
| error     | CLICK × 4            | Four clicks with an accented third pulse |
| light     | TICK                 | Subtle, light feedback                   |
| medium    | CLICK                | Moderate feedback                        |
| heavy     | THUD                 | Strong, deep feedback                    |
| rigid     | CLICK                | Sharp, crisp feedback                    |
| soft      | SPIN*                | Gentle, longer feedback                  |
| selection | TICK                 | Subtle selection feedback                |

\* `SPIN` requires API 31+; falls back to `TICK` on API 30.

#### Strategy 3: Waveform Vibrations (API 26+)

For devices without the required primitives, the plugin uses
`VibrationEffect.createWaveform()`. Devices with amplitude control use the tuned
amplitudes; others use timing-only waveforms.

#### Strategy 4: Legacy Vibration (API < 26)

Basic timing-only patterns for older devices.

For detailed timing specifications and implementation rationale, see [HAPTIC_PATTERNS.md](https://github.com/nohli/haptic_feedback/blob/main/HAPTIC_PATTERNS.md).

## Testing

In native Flutter widget tests, `defaultTargetPlatform` is `TargetPlatform.android` by default, but the default platform implementation still calls the method channel and will throw `MissingPluginException` unless you register a mock. If you swap in a mock platform that returns `true`, `Haptics.canVibrate()` will return `true` even on a non-mobile host unless you override the target platform.

To test widgets that use haptic feedback, you can:

1. **Use `debugDefaultTargetPlatformOverride`** to test platform-specific behavior:

```dart
import 'package:flutter/foundation.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('test on iOS', (tester) async {
    final originalTarget = debugDefaultTargetPlatformOverride;
    debugDefaultTargetPlatformOverride = TargetPlatform.iOS;
    try {
      // Your test code here
    } finally {
      debugDefaultTargetPlatformOverride = originalTarget;
    }
  });
}
```

2. **Mock the platform interface** in your tests:

```dart
import 'package:flutter/foundation.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
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
  test('haptic calls use the fake platform', () async {
    final originalPlatform = HapticFeedbackPlatform.instance;
    final originalTarget = debugDefaultTargetPlatformOverride;
    addTearDown(() {
      HapticFeedbackPlatform.instance = originalPlatform;
      debugDefaultTargetPlatformOverride = originalTarget;
    });
    debugDefaultTargetPlatformOverride = TargetPlatform.android;
    HapticFeedbackPlatform.instance = FakeHapticFeedbackPlatform();

    expect(await Haptics.canVibrate(), isTrue);
    await Haptics.vibrate(HapticsType.success);
  });
}
```

3. **Mock the method channel** to avoid `MissingPluginException` while using the real platform class:

```dart
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:haptic_feedback/src/haptic_feedback_method_channel.dart';
import 'package:haptic_feedback/src/haptics_type.dart';

void main() {
  const channel = MethodChannelHapticFeedback.methodChannel;
  TestDefaultBinaryMessengerBinding.ensureInitialized();

  setUp(() {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall call) async {
        if (call.method == 'canVibrate') return true;
        return null; // success for vibrate calls
      },
    );
  });

  tearDown(() {
    final binding = TestDefaultBinaryMessengerBinding.instance;
    binding.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('canVibrate works with mocked channel', () async {
    expect(await MethodChannelHapticFeedback().canVibrate(), isTrue);
  });

  test('vibrate forwards to mocked channel', () async {
    await MethodChannelHapticFeedback().vibrate(HapticsType.success);
  });
}
```

You can find these examples as runnable tests in `example/test` on [GitHub](
https://github.com/nohli/haptic_feedback/tree/main/example/test).

## Automatic Permissions Inclusion

### Android VIBRATE Permission

When you integrate the `haptic_feedback` plugin into your Flutter project, it will automatically include the necessary `VIBRATE` permission in the final merged `AndroidManifest.xml` of your app. This is due to the permission being declared in the plugin's manifest.

#### What this means for you:

- **No Manual Action Required**: You don't have to add the `<uses-permission android:name="android.permission.VIBRATE"/>` permission to your app's `AndroidManifest.xml` manually. It will be automatically merged when building the app.

- **Transparency**: By using the `haptic_feedback` plugin, your app will request the `VIBRATE` permission. Ensure that you are aware of all permissions your app requires, especially if you publish it on app stores. Some users may be sensitive to app permissions, even if they don't require explicit consent.

- **Permission Overview**: To review all permissions that your app requests due to plugins and your own declarations, inspect the [final merged](https://stackoverflow.com/questions/74025731/where-is-the-merged-manifest-in-flutter-project) `AndroidManifest.xml` after a build. This will provide a comprehensive view of all permissions and other manifest entries.
