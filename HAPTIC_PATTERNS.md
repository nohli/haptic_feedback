## Patterns

### iOS patterns

Approximation of Apple’s built-in haptics (from [Playing haptics](https://developer.apple.com/design/human-interface-guidelines/playing-haptics#iOS)).

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

### Android mapping strategy

System enums via [`View.performHapticFeedback`](https://developer.android.com/reference/android/view/View#performHapticFeedback(int)) and [`HapticFeedbackConstants`](https://developer.android.com/reference/android/view/HapticFeedbackConstants).

| iOS Enum  | Android mapping                                                                                                                       | API level |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------|-----------|
| success   | [`HapticFeedbackConstants.CONFIRM`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#CONFIRM)             | ≥ 30      |
| warning   |                                                                                                                                       |           |
| error     | [`HapticFeedbackConstants.REJECT`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#REJECT)               | ≥ 30      |
| light     | [`HapticFeedbackConstants.VIRTUAL_KEY`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#VIRTUAL_KEY)     | ≥ 5       |
| medium    | [`HapticFeedbackConstants.KEYBOARD_TAP`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#KEYBOARD_TAP)   | ≥ 8       |
| heavy     | [`HapticFeedbackConstants.CONTEXT_CLICK`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#CONTEXT_CLICK) | ≥ 23      |
| rigid     |                                                                                                                                       |           |
| soft      |                                                                                                                                       |           |
| selection | [`HapticFeedbackConstants.CLOCK_TICK`](https://developer.android.com/reference/android/view/HapticFeedbackConstants#CLOCK_TICK)       | ≥ 21      |


### Derived Android primitives patterns

[`Primitives`](https://developer.android.com/reference/android/os/VibrationEffect.Composition#summary) (API ≥ 30, `SPIN` requires API ≥ 31).

| iOS Enum  | Pulse | Primitive | Strength | Delay after previous (ms) |
|-----------|:-----:|-----------|---------:|--------------------------:|
| success   |   1   | CLICK     |      0.7 |                         0 |
|           |   2   | CLICK     |      1.0 |                        55 |
| warning   |   1   | CLICK     |      0.9 |                         0 |
|           |   2   | CLICK     |      0.7 |                        91 |
| error     |   1   | CLICK     |      0.8 |                         0 |
|           |   2   | CLICK     |      0.8 |                        45 |
|           |   3   | THUD      |      1.0 |                        43 |
|           |   4   | CLICK     |      0.6 |                        41 |
| light     |   1   | TICK      |      0.6 |                         0 |
| medium    |   1   | CLICK     |      0.8 |                         0 |
| heavy     |   1   | THUD      |      1.0 |                         0 |
| rigid     |   1   | CLICK     |      0.9 |                         0 |
| soft      |   1   | SPIN*     |      0.7 |                         0 |
| selection |   1   | TICK      |      0.6 |                         0 |

\* On API < 31, `SPIN` is replaced by `TICK` at the same strength.

### Derived Android waveform patterns

Waveforms via [`VibrationEffect.createWaveform(long[], int[], int)`](https://developer.android.com/reference/android/os/VibrationEffect#createWaveform(long[],%20int[],%20int)) (API ≥ 26).  
Amplitudes (0–255) from normalized intensities:

- 0.6 → 153
- 0.7 → 178
- 0.8 → 204
- 0.9 → 229
- 1.0 → 255

| iOS Enum  | Segment | Role    | Duration (ms) | Amplitude (0–255) | Normalized Intensity |
|-----------|:-------:|---------|--------------:|------------------:|---------------------:|
| success   |    1    | vibrate |            55 |               178 |                 0.70 |
|           |    2    | pause   |            55 |                 0 |                 0.00 |
|           |    3    | vibrate |            53 |               255 |                 1.00 |
| warning   |    1    | vibrate |            55 |               229 |                 0.90 |
|           |    2    | pause   |            91 |                 0 |                 0.00 |
|           |    3    | vibrate |            55 |               178 |                 0.70 |
| error     |    1    | vibrate |            51 |               204 |                 0.80 |
|           |    2    | pause   |            45 |                 0 |                 0.00 |
|           |    3    | vibrate |            55 |               204 |                 0.80 |
|           |    4    | pause   |            43 |                 0 |                 0.00 |
|           |    5    | vibrate |            55 |               255 |                 1.00 |
|           |    6    | pause   |            41 |                 0 |                 0.00 |
|           |    7    | vibrate |            68 |               153 |                 0.60 |
| light     |    1    | vibrate |            55 |               153 |                 0.60 |
| medium    |    1    | vibrate |            51 |               204 |                 0.80 |
| heavy     |    1    | vibrate |            55 |               255 |                 1.00 |
| rigid     |    1    | vibrate |            34 |               229 |                 0.90 |
| soft      |    1    | vibrate |            82 |               178 |                 0.70 |
| selection |    1    | vibrate |            41 |               153 |                 0.60 |

### Legacy Android behavior (API < 26)

Below API 26, the plugin uses the old  
[`Vibrator.vibrate(long[], int)`](https://developer.android.com/reference/android/os/Vibrator#vibrate(long[],%20int)) API:

- It takes the **`lengths`** array for the pattern
- **Timings / rhythm** are preserved (same number of pulses and pauses),
- **Strength / intensity** is **not** – everything is just “full motor on” whenever it’s vibrating.
