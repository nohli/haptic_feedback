import 'package:flutter/foundation.dart';

const _supportedPlatforms = {
  TargetPlatform.iOS,
  TargetPlatform.android,
};

/// Returns `true` if the app is running on a supported platform.
/// Supported platforms are iOS and Android.
bool get isPlatformSupported {
  return !kIsWeb && _supportedPlatforms.contains(defaultTargetPlatform);
}
