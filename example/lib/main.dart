import 'package:flutter/material.dart';
import 'package:haptic_feedback/haptic_feedback.dart';

void main() => runApp(const MyApp());

///
class MyApp extends StatefulWidget {
  /// Example app that uses the plugin.
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData.light(),
      darkTheme: ThemeData.dark(),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Haptic feedback playground'),
        ),
        body: Builder(builder: (context) {
          return ListView(
            children: [
              for (final type in HapticsType.values)
                ListTile(
                  title: Text(type.name),
                  // ignore: invalid_use_of_visible_for_testing_member
                  subtitle: Text(type.description),
                  onTap: () async {
                    final can = await Haptics.canVibrate();

                    if (!mounted) return;
                    ScaffoldMessenger.of(context).hideCurrentSnackBar();
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: can
                            ? Text('$type', textAlign: TextAlign.center)
                            : const Text(
                                'Device is not capable of haptic feedback.',
                              ),
                        duration: const Duration(seconds: 1),
                      ),
                    );

                    if (!can) return;
                    await Haptics.vibrate(type);
                  },
                ),
            ],
          );
        }),
      ),
    );
  }
}
