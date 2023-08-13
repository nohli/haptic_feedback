import 'package:flutter/foundation.dart';

/// Pass the [HapticsType] to [Haptics.vibrate] to
/// trigger a haptic feedback.
enum HapticsType {
  /// https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS

  /// Indicates that a task or action has completed.
  success,

  /// Indicates that a task or action has produced a warning of some kind.
  warning,

  /// Indicates that an error has occurred.
  error,

  /// Indicates a collision between small or lightweight UI objects.
  light,

  /// Indicates a collision between medium-sized or medium-weight UI objects.
  medium,

  /// Indicates a collision between large or heavyweight UI objects.
  heavy,

  /// Indicates a collision between hard or inflexible UI objects.
  rigid,

  /// Indicates a collision between soft or flexible UI objects.
  soft,

  /// Indicates that a UI element’s values are changing.
  selection,
  ;

  const HapticsType();
}

/// For testing purposes only.
@visibleForTesting
extension HapticsTypeDescription on HapticsType {
  /// Get the description of the haptic feedback type.
  String get description {
    return switch (this) {
      HapticsType.success => 'Indicates that a task or action has completed.',
      HapticsType.warning =>
        'Indicates that a task or action has produced a warning of some kind.',
      HapticsType.error => 'Indicates that an error has occurred.',
      HapticsType.light =>
        'Indicates a collision between small or lightweight UI objects.',
      HapticsType.medium =>
        'Indicates a collision between medium-sized or medium-weight UI objects.',
      HapticsType.heavy =>
        'Indicates a collision between large or heavyweight UI objects.',
      HapticsType.rigid =>
        'Indicates a collision between hard or inflexible UI objects.',
      HapticsType.soft =>
        'Indicates a collision between soft or flexible UI objects.',
      HapticsType.selection =>
        'Indicates that a UI element’s values are changing.',
    };
  }
}
