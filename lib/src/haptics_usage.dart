/// Controls which Android vibration usage category is applied to a haptic pattern.
///
/// Android 13 (API level 33) introduced [VibrationAttributes], allowing apps to
/// tell the system why a vibration is being triggered. The platform can then
/// respect the user's preference for each category (media, alarms, touch, etc.).
/// On older Android versions this information is ignored.
///
/// If no usage is provided, the plugin falls back to Android's default
/// (`USAGE_UNKNOWN`), which may be muted on some OEM builds when touch haptics
/// are disabled.
enum HapticsUsage {
  /// Alarm vibrations.
  alarm,

  /// Vibrations which mean a request to enter/end a communication with the
  /// user, such as a voice prompt.
  communicationRequest,

  /// Vibrations which provide a feedback for hardware component interaction,
  /// such as a fingerprint sensor.
  hardwareFeedback,

  /// Media vibrations, such as music, movie, soundtrack, animations, games, or
  /// any interactive media that isn't for touch feedback specifically.
  media,

  /// Notification vibrations.
  notification,

  /// Vibrations which emulate physical hardware reactions, such as edge
  /// squeeze.
  physicalEmulation,

  /// Ringtone vibrations.
  ringtone,

  /// Touch vibrations.
  touch,

  /// Usage is unknown.
  unknown,
}
