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
  /// Vibrations that should follow the alarm volume/vibration toggle.
  alarm,

  /// Vibrations for accessibility events, such as with a screen reader.
  communicationRequest,

  /// Vibrations for hardware feedback, such as virtual keyboard key presses.
  hardwareFeedback,

  /// Vibrations meant to accompany media playback (for example, breathing cues).
  media,

  /// Vibrations that inform the user about notifications.
  notification,

  /// Vibrations for emulating physical objects or interactions.
  physicalEmulation,

  /// Vibrations meant to mimic a ringtone.
  ringtone,

  /// Vibrations tied to touch interactions (taps, drags, etc.).
  touch,

  /// Use Android's default vibration routing.
  unknown,
}
