// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "haptic_feedback",
    platforms: [
        .iOS("11.0")
    ],
    products: [
        // Match Flutter's generated SwiftPM expectation (`haptic-feedback`).
        .library(name: "haptic-feedback", type: .dynamic, targets: ["haptic_feedback"])
    ],
    dependencies: [],
    targets: [
        .target(
            name: "haptic_feedback",
            dependencies: []
        )
    ]
)
