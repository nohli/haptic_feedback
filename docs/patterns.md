## Patterns

The table below approximates Apple’s built-in haptics (from the documentation [Playing haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS)). They are mapped to explicit durations, pauses, and normalized intensities.
We measured the relative bar lengths from Apple’s diagrams (full grey line = 0.8 s, tallest bar = intensity 1.0).

These numbers aren’t official Apple timings; they’re practical approximations so the enums feel roughly the same on Android and iOS.


| iOS Enum  | Pulse | Start (ms) | Duration (ms) | Intensity |
|-----------|:-----:|-----------:|--------------:|----------:|
| success   |   1   |          0 |            55 |       0.7 |
|           |   2   |        112 |            53 |       1.0 |
| warning   |   1   |          0 |            55 |       0.9 |
|           |   2   |        146 |            55 |       0.7 |
| error     |   1   |          0 |            45 |       0.8 |
|           |   2   |         96 |            55 |       0.8 |
|           |   3   |        194 |            55 |       1.0 |
|           |   4   |        292 |            69 |       0.6 |
| light     |   1   |          0 |            55 |       0.6 |
| medium    |   1   |          0 |            45 |       0.8 |
| heavy     |   1   |          0 |            55 |       1.0 |
| rigid     |   1   |          0 |            34 |       0.9 |
| soft      |   1   |          0 |            82 |       0.7 |
| selection |   1   |          0 |            41 |       0.6 |