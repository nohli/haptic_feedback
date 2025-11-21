## 0.6.2

* Fix legacy Android vibration on SDK 24/25 (Android 7.0/7.1): without a leading 0ms delay the first pulse became a pause and the pattern lost a buzz; prepending 0 keeps the intended multi-pulse sequence.

## 0.6.1

* Add optional `usage` parameter to control vibration category on Android 13+ devices. This allows you to specify how the haptic feedback should be categorized (e.g., for notifications, alarms, touch feedback, etc.), which helps the system handle vibrations more appropriately.

## 0.6.0

* Breaking: Flutter SDK constraint is now >=3.10.0 (Dart 3-only). Apps on older Flutter versions can’t upgrade.

## 0.5.1+2

* Update article link.

## 0.5.1+1

* Add an article link about using the plugin to the readme. Thanks @kamranbekirovyz!

## 0.5.1

* Update podspec version.

## 0.5.0

* Early return if platform is not supported (instead of an exception). Supported are iOS an Android. Thanks @rizerco!
* Upgrade Android dependencies (Gradle, AGP, Java, Kotlin).
* Widen `flutter_lints` constraints.

## 0.4.2

* Update package description.

## 0.4.1

* Update example.

## 0.4.0

* `canVibrate()` is now a method.
* Catching of native errors on Android: You can call `vibrate()` without checking `canVibrate()` first.
* Improve docs.

## 0.3.3+1

* Fix typo in readme. Thanks @walid-ashik!

## 0.3.3

* Update podspec version.

## 0.3.2

* Maintenance release.
* Update `gradle`, `kotlin`, and `mockito`.

## 0.3.1

* Update podspec version.

## 0.3.0

* Update package identifier.

## 0.2.5

* Refactor example.

## 0.2.4

* Small refactoring.

## 0.2.3

* Check API level before calling `hasAmplitudeControl()`.

## 0.2.2

* Update version in `podspec`.

## 0.2.1

* Refactor Kotlin code.

## 0.2.0

* Improve docs.
* Add private constructor to `Haptics`.

## 0.1.1

* Improve docs.

## 0.1.0

* Format Swift code.

## 0.0.9

* Call fallback methods on iOS < 13 for `.rigid` and `.soft` haptics.

## 0.0.8

* Refactor `canVibrate` implementation for supporting lower iOS versions.

## 0.0.7

* Format native code.

## 0.0.6

* Refactor `canVibrate` method into a getter for improved readability and usage.

## 0.0.5

* Make methods static.

## 0.0.4

* Add `namespace` to build.gradle.
* Update `Kotlin` version of example.
* Upgrade `Gradle` version of example.
* Upgrade `Android Gradle Plugin` version of example.

## 0.0.3

* Fine-tune Android haptics.

## 0.0.2

* Update description.

## 0.0.1

* Initial release.
