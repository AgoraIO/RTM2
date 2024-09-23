//
//  Rtm_OC_Quick_StartUITestsLaunchTests.m
//  Rtm-OC-Quick-StartUITests
//
//  Created by jiangyingyan on 2024/9/23.
//

#import <XCTest/XCTest.h>

@interface Rtm_OC_Quick_StartUITestsLaunchTests : XCTestCase

@end

@implementation Rtm_OC_Quick_StartUITestsLaunchTests

+ (BOOL)runsForEachTargetApplicationUIConfiguration {
    return YES;
}

- (void)setUp {
    self.continueAfterFailure = NO;
}

- (void)testLaunch {
    XCUIApplication *app = [[XCUIApplication alloc] init];
    [app launch];

    // Insert steps here to perform after app launch but before taking a screenshot,
    // such as logging into a test account or navigating somewhere in the app

    XCTAttachment *attachment = [XCTAttachment attachmentWithScreenshot:XCUIScreen.mainScreen.screenshot];
    attachment.name = @"Launch Screen";
    attachment.lifetime = XCTAttachmentLifetimeKeepAlways;
    [self addAttachment:attachment];
}

@end
