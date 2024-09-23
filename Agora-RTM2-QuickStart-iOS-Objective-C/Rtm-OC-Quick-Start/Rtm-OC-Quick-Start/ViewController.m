//
// ViewController.m
// RtmQuickstart
//
// Created by macoscatalina on 2021/6/8.
// Copyright © 2021 macoscatalina. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()<AgoraRtmClientDelegate>

@property(nonatomic, strong)AgoraRtmClientKit* kit;

@property NSString* appID;
@property NSString* token;

@property NSString* uid;
@property NSString* peerID;
@property NSString* channelID;
@property NSString* peerMsg;
@property NSString* channelMsg;

@property NSString* text;
@property NSMutableArray* textArray;

- (void)AddMsgToRecord:(NSString*)text;

@end

@implementation ViewController

- (void)viewDidLoad {
[super viewDidLoad];
// Enter your App ID
self.appID = <#APP ID#>;
self.MsgTextView.textColor = UIColor.blueColor;
self.textArray = [[NSMutableArray alloc]init];
}

// Add the event listener

// Add message to the UI TextView
- (void)AddMsgToRecord:(NSString*)text {


[self.textArray addObject:(self.text)];
self.MsgTextView.text = [self.textArray componentsJoinedByString:(@"\n")];

}

- (IBAction)Login:(id)sender {
    self.uid = self.UserIDTextField.text;
    // Enter your token
    self.token = @"your_token";

    AgoraRtmClientConfig*  rtm_config = [[AgoraRtmClientConfig alloc] initWithAppId:_appID userId:_uid];

    NSError* initError = nil;
    _kit = [[AgoraRtmClientKit alloc] initWithConfig:rtm_config delegate:self error:&initError];
    if (initError != nil) {
        self.text = [NSString stringWithFormat:@"init error %@",initError];
        NSLog(@"%@", self.text);
        [self AddMsgToRecord:(self.text)];
    }
    // Log in the RTM server
}

- (IBAction)Logout:(id)sender {
if (_kit != nil) {
    // Log out from the RTM server
    }
}

- (IBAction)SubscribeChannel:(id)sender {
self.channelID = self.ChannelIDTextField.text;
if (_kit != nil) {
    // Subscribe to a channel
    }
}

- (IBAction)UnsubscribeChannel:(id)sender {
    if (_kit == nil)
        return;
    // Unsubscribe from a channel
}

- (IBAction)SendMessageToMessageChannel:(id)sender {
self.channelID = self.ChannelIDTextField.text;
self.channelMsg = self.GroupMsgTextField.text;
    // Publish a message
}
@end
