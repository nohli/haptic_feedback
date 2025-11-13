#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint haptic_feedback.podspec --configuration=Debug --skip-tests --use-modular-headers` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'haptic_feedback'
  s.version          = '1.0.1'
  s.summary          = 'A Flutter plugin for haptic feedback.'
  s.description      = <<-DESC
A Flutter plugin for haptic feedback. While it utilizes standard iOS haptics, it aims to emulate these same haptic patterns on Android for a consistent experience across platforms.
                       DESC
  s.homepage         = 'https://github.com/eneskaraosman/haptic_feedback'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'EnesKaraosman' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'haptic_feedback/Sources/haptic_feedback/**/*.swift'
  s.platform = :ios, '13.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
