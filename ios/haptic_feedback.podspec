#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint haptic_feedback.podspec` to validate before publishing.
#

Pod::Spec.new do |s|
  s.name             = 'haptic_feedback'
  s.version          = '0.0.7'
  s.summary          = 'A Flutter plugin for haptic feedback.'
  s.description      = <<-DESC
Haptic Feedback.
                       DESC
  s.homepage         = 'https://github.com/nohli/haptic_feedback'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Joachim Nohl' => 'pub@achim.io' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '13.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
