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
  /// Usage value to use for alarm vibrations.
  alarm,

  /// Usage value to use for vibrations which mean a request to enter/end a
  /// communication with the user, such as a voice prompt.
  communicationRequest,

  /// Usage value to use for vibrations which provide a feedback for hardware
  /// component interaction, such as a fingerprint sensor.
  hardwareFeedback,

  /// Usage value to use for media vibrations, such as music, movie,
  /// soundtrack, animations, games, or any interactive media that isn't for
  /// touch feedback specifically.
  media,

  /// Usage value to use for notification vibrations.
  notification,

  /// Usage value to use for vibrations which emulate physical hardware
  /// reactions, such as edge squeeze.
  physicalEmulation,

  /// Usage value to use for ringtone vibrations.
  ringtone,

  /// Usage value to use for touch vibrations.
  touch,

  /// Usage value to use when usage is unknown.
  unknown,
}
