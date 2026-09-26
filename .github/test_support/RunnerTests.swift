import integration_test
import XCTest

final class RunnerTests: XCTestCase {
  func testIntegrationTest() {
    var testCount = 0
    var failures: [String] = []

    FLTIntegrationTestRunner().testIntegrationTest { testSelector, success, failureMessage in
      testCount += 1
      if !success {
        failures.append("\(NSStringFromSelector(testSelector)): \(failureMessage ?? "Unknown failure")")
      }
    }

    XCTAssertGreaterThan(testCount, 0)
    XCTAssertTrue(failures.isEmpty, failures.joined(separator: "\n"))
  }
}
