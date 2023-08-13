import CoreHaptics
import Flutter

public class HapticFeedbackPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "haptic_feedback", binaryMessenger: registrar.messenger())
    let instance = HapticFeedbackPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "canVibrate":
      canVibrate(result: result)
    case "success":
      notification(type: .success, result: result)
    case "warning":
      notification(type: .warning, result: result)
    case "error":
      notification(type: .error, result: result)
    case "light":
      impact(style: .light, result: result)
    case "medium":
      impact(style: .medium, result: result)
    case "heavy":
      impact(style: .heavy, result: result)
    case "rigid":
      if #available(iOS 13.0, *) {
        impact(style: .rigid, result: result)
      } else {
        impact(style: .medium, result: result)
      }
    case "soft":
      if #available(iOS 13.0, *) {
        impact(style: .soft, result: result)
      } else {
        impact(style: .light, result: result)
      }
    case "selection":
      selection(result: result)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func canVibrate(result: @escaping FlutterResult) {
    if #available(iOS 13.0, *) {
      let supportsHaptics = CHHapticEngine.capabilitiesForHardware().supportsHaptics
      result(supportsHaptics)
      return
    }
    if let feedbackSupportLevel = UIDevice.current.value(forKey: "_feedbackSupportLevel") as? Int,
      feedbackSupportLevel == 2 {
      result(true)
      return
    }
    result(false)
  }

  private func notification(type: UINotificationFeedbackGenerator.FeedbackType, result: @escaping FlutterResult) {
    UINotificationFeedbackGenerator().notificationOccurred(type)
    result(true)
  }

  private func impact(style: UIImpactFeedbackGenerator.FeedbackStyle, result: @escaping FlutterResult) {
    UIImpactFeedbackGenerator(style: style).impactOccurred()
    result(true)
  }

  private func selection(result: @escaping FlutterResult) {
    UISelectionFeedbackGenerator().selectionChanged()
    result(true)
  }
}
