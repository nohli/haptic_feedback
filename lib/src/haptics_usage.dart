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

  /// Usage value to use for vibrations related to incoming calls.
  communicationRequest,

  /// Usage value to use for vibrations which emulate hardware feedback.
  hardwareFeedback,

  /// Usage value to use for media vibrations, such as music or movie vibrations.
  media,

  /// Usage value to use for notification vibrations.
  notification,

  /// Usage value to use for vibrations which emulate physical effects.
  physicalEmulation,

  /// Usage value to use for ringtone vibrations.
  ringtone,

  /// Usage value to use for touch vibrations.
  touch,

  /// Usage value to use when the vibration usage is unknown.
  unknown,
}
