## Patterns

### iOS patterns

The table below approximates Apple’s built-in haptics (from the documentation [Playing haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS)). They are mapped to explicit durations, pauses, and normalized intensities.
We measured the relative bar lengths from Apple’s diagrams (full grey line = 0.8 s, tallest bar = intensity 1.0).

These numbers aren’t official Apple timings; they’re practical approximations so the enums feel roughly the same on Android and iOS.

| iOS Enum  | Pulse | Start (ms) | Duration (ms) | Intensity |
|-----------|:-----:|-----------:|--------------:|----------:|
| success   |   1   |          0 |            55 |       0.7 |
|           |   2   |        110 |            53 |       1.0 |
| warning   |   1   |          0 |            55 |       0.9 |
|           |   2   |        146 |            55 |       0.7 |
| error     |   1   |          0 |            51 |       0.8 |
|           |   2   |         96 |            55 |       0.8 |
|           |   3   |        194 |            55 |       1.0 |
|           |   4   |        290 |            68 |       0.6 |
| light     |   1   |          0 |            55 |       0.6 |
| medium    |   1   |          0 |            51 |       0.8 |
| heavy     |   1   |          0 |            55 |       1.0 |
| rigid     |   1   |          0 |            34 |       0.9 |
| soft      |   1   |          0 |            82 |       0.7 |
| selection |   1   |          0 |            41 |       0.6 |

### Derived Android primitives patterns

These are conceptual mappings to Android’s haptic primitives (for use with `VibrationEffect.Composition` etc.). They won’t be 1:1 with iOS but keep the same “character”.

| iOS Enum  | Semantics        | Suggested Android primitive composition                     |
|-----------|------------------|-------------------------------------------------------------|
| success   | 2-pulse, strong  | `CLICK` → `HEAVY_CLICK`                                     |
| warning   | 2-pulse, caution | `HEAVY_CLICK` → `CLICK`                                     |
| error     | 3+1 multi-pulse  | `HEAVY_CLICK` → `HEAVY_CLICK` → `HEAVY_CLICK` → `CLICK`     |
| light     | light impact     | `TICK` or a soft `CLICK`                                    |
| medium    | medium impact    | `CLICK`                                                     |
| heavy     | heavy impact     | `HEAVY_CLICK`                                               |
| rigid     | short sharp hit  | short `HEAVY_CLICK`                                         |
| soft      | long soft hit    | `THUD` or `CLICK` → `TICK` composition                      |
| selection | tiny tick        | `TICK`                                                      |

### Derived Android waveform patterns

These are explicit waveform patterns (durations + amplitudes) for devices that support custom vibration waveforms.  
Amplitudes are in the Android range 0–255, derived from the normalized intensities:

- 0.6 → 153
- 0.7 → 178
- 0.8 → 204
- 0.9 → 230
- 1.0 → 255

Pauses are inferred from the iOS start times (`pause = nextStart - (currentStart + currentDuration)`).

| iOS Enum  | Segment | Role   | Duration (ms) | Amplitude (0–255) | Normalized Intensity |
|-----------|:-------:|--------|--------------:|------------------:|---------------------:|
| success   |    1    | vib    |            55 |               178 |                 0.70 |
|           |    2    | pause  |            55 |                 0 |                 0.00 |
|           |    3    | vib    |            53 |               255 |                 1.00 |
| warning   |    1    | vib    |            55 |               230 |                 0.90 |
|           |    2    | pause  |            91 |                 0 |                 0.00 |
|           |    3    | vib    |            55 |               178 |                 0.70 |
| error     |    1    | vib    |            51 |               204 |                 0.80 |
|           |    2    | pause  |            45 |                 0 |                 0.00 |
|           |    3    | vib    |            55 |               204 |                 0.80 |
|           |    4    | pause  |            43 |                 0 |                 0.00 |
|           |    5    | vib    |            55 |               255 |                 1.00 |
|           |    6    | pause  |            41 |                 0 |                 0.00 |
|           |    7    | vib    |            68 |               153 |                 0.60 |
| light     |    1    | vib    |            55 |               153 |                 0.60 |
| medium    |    1    | vib    |            51 |               204 |                 0.80 |
| heavy     |    1    | vib    |            55 |               255 |                 1.00 |
| rigid     |    1    | vib    |            34 |               230 |                 0.90 |
| soft      |    1    | vib    |            82 |               178 |                 0.70 |
| selection |    1    | vib    |            41 |               153 |                 0.60 |
