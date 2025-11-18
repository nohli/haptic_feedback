import 'dart:io' show Platform;

import 'package:flutter/foundation.dart';

/// Returns `true` if the app is running on a supported platform.
/// Supported platforms are iOS and Android.
bool get isPlatformSupported {
  return !kIsWeb && (Platform.isAndroid || Platform.isIOS);
}
