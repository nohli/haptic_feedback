import CoreHaptics
import Flutter
import UIKit

public class HapticFeedbackPlugin: NSObject, FlutterPlugin {
  private let notificationGenerator = UINotificationFeedbackGenerator()
  private let lightImpactGenerator = UIImpactFeedbackGenerator(style: .light)
  private let mediumImpactGenerator = UIImpactFeedbackGenerator(style: .medium)
  private let heavyImpactGenerator = UIImpactFeedbackGenerator(style: .heavy)
  private let rigidImpactGenerator: UIImpactFeedbackGenerator
  private let softImpactGenerator: UIImpactFeedbackGenerator
  private let selectionGenerator = UISelectionFeedbackGenerator()

  override init() {
    if #available(iOS 13.0, *) {
      rigidImpactGenerator = UIImpactFeedbackGenerator(style: .rigid)
      softImpactGenerator = UIImpactFeedbackGenerator(style: .soft)
    } else {
      rigidImpactGenerator = UIImpactFeedbackGenerator(style: .medium)
      softImpactGenerator = UIImpactFeedbackGenerator(style: .light)
    }
    super.init()
  }

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "haptic_feedback", binaryMessenger: registrar.messenger())
    let instance = HapticFeedbackPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "canVibrate":
      canVibrate(result: result)
    case "prepare":
      prepare(type: call.arguments as? String, result: result)
    case "success":
      notificationGenerator.notificationOccurred(.success)
      result(nil)
    case "warning":
      notificationGenerator.notificationOccurred(.warning)
      result(nil)
    case "error":
      notificationGenerator.notificationOccurred(.error)
      result(nil)
    case "light":
      lightImpactGenerator.impactOccurred()
      result(nil)
    case "medium":
      mediumImpactGenerator.impactOccurred()
      result(nil)
    case "heavy":
      heavyImpactGenerator.impactOccurred()
      result(nil)
    case "rigid":
      rigidImpactGenerator.impactOccurred()
      result(nil)
    case "soft":
      softImpactGenerator.impactOccurred()
      result(nil)
    case "selection":
      selectionGenerator.selectionChanged()
      result(nil)
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

  private func prepare(type: String?, result: @escaping FlutterResult) {
    switch type {
    case "success", "warning", "error":
      notificationGenerator.prepare()
    case "light":
      lightImpactGenerator.prepare()
    case "medium":
      mediumImpactGenerator.prepare()
    case "heavy":
      heavyImpactGenerator.prepare()
    case "rigid":
      rigidImpactGenerator.prepare()
    case "soft":
      softImpactGenerator.prepare()
    case "selection":
      selectionGenerator.prepare()
    default:
      break
    }
    result(nil)
  }
}
