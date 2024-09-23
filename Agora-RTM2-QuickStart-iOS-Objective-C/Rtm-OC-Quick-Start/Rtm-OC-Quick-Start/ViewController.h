#import <UIKit/UIKit.h>

#import <AgoraRtmKit/AgoraRtmKit.h>

@interface ViewController : UIViewController

// Buttons
@property (weak, nonatomic) IBOutlet UIButton *LoginButton;
@property (weak, nonatomic) IBOutlet UIButton *LogoutButton;
@property (weak, nonatomic) IBOutlet UIButton *SubsctibeButton;
@property (weak, nonatomic) IBOutlet UIButton *UnSubscribeButton;
@property (weak, nonatomic) IBOutlet UIButton *GroupMsgButton;

// Textfields
@property (weak, nonatomic) IBOutlet UITextField *UserIDTextField;
@property (weak, nonatomic) IBOutlet UITextField *ChannelIDTextField;
@property (weak, nonatomic) IBOutlet UITextField *GroupMsgTextField;

@property (weak, nonatomic) IBOutlet UITextView *MsgTextView;

@end
