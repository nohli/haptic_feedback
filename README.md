# haptic_feedback

A haptic feedback plugin for both iOS and Android.

While it utilizes [standard iOS haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS), it aims to emulate these same haptic patterns on Android for a consistent experience across platforms.

## Getting Started

### 1. Add the dependency

```shell
flutter pub add haptic_feedback
```

### 2. Use the plugin

```dart
final canVibrate = await Haptics.canVibrate;

await Haptics.vibrate(HapticType.success);
await Haptics.vibrate(HapticType.warning);
await Haptics.vibrate(HapticType.error);
await Haptics.vibrate(HapticType.light);
await Haptics.vibrate(HapticType.medium);
await Haptics.vibrate(HapticType.heavy);
await Haptics.vibrate(HapticType.rigid);
await Haptics.vibrate(HapticType.soft);
await Haptics.vibrate(HapticType.selection);
```
