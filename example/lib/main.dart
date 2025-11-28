import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:haptic_feedback/haptic_feedback.dart';
import 'package:haptic_feedback_example/haptics_type_description.dart';
import 'package:open_settings_plus/core/open_settings_plus.dart';

void main() => runApp(const MyApp());

///
class MyApp extends StatelessWidget {
  /// Example app that uses the plugin.
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      darkTheme: ThemeData.dark(),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Haptic feedback playground'),
        ),
        body: const _HapticsList(),
      ),
    );
  }
}

class _HapticsList extends StatefulWidget {
  const _HapticsList();

  @override
  State<_HapticsList> createState() => _HapticsListState();
}

class _HapticsListState extends State<_HapticsList> {
  HapticsUsage? _selectedUsage;
  bool _useNativeHaptics = true;

  @override
  Widget build(BuildContext context) {
    final isAndroid =
        !kIsWeb && defaultTargetPlatform == TargetPlatform.android;

    return ListView(
      children: [
        if (isAndroid) ...[
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('Usage type'),
                const SizedBox(width: 12),
                DropdownMenu(
                  onSelected: (hapticsUsage) => _selectedUsage = hapticsUsage,
                  dropdownMenuEntries: HapticsUsage.values.map((type) {
                    return DropdownMenuEntry(value: type, label: type.name);
                  }).toList(),
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                TextButton(
                  onPressed: OpenSettingsPlusAndroid().sound,
                  child: Text('Open system settings'),
                ),
                const SizedBox(width: 24),
                const Text('Use native enums'),
                const SizedBox(width: 8),
                Switch.adaptive(
                  value: _useNativeHaptics,
                  onChanged: (value) =>
                      setState(() => _useNativeHaptics = value),
                )
              ],
            ),
          ),
        ],
        for (final type in HapticsType.values)
          ListTile(
            title: Text(type.name),
            subtitle: Text(type.description),
            onTap: () async {
              // Check if device is capable of haptic feedback
              final can = await Haptics.canVibrate();

              // Show snackbar
              if (!context.mounted) return;
              final snackbarMessage = can
                  ? '$type'
                  : 'This device is not capable of haptic feedback.';
              ScaffoldMessenger.of(context).removeCurrentSnackBar();
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(snackbarMessage, textAlign: TextAlign.center),
                  duration: const Duration(seconds: 1),
                ),
              );

              // Vibrate only if device is capable of haptic feedback
              if (!can) return;
              await Haptics.vibrate(type,
                  usage: _selectedUsage, useNativeHaptics: _useNativeHaptics);
            },
          ),
      ],
    );
  }
}
