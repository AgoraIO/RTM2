package io.agora;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.agora.rtm.GetOnlineUsersOptions;
import io.agora.rtm.GetOnlineUsersResult;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.RtmConstants.RtmMessageQos;
import io.agora.rtm.RtmConstants.RtmChannelType;
import io.agora.rtm.RtmEventListener;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.SubscribeOptions;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.LockEvent;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PresenceEvent;
import io.agora.rtm.StorageEvent;
import io.agora.rtm.TopicEvent;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.StateItem;
import io.agora.rtm.UserState;
import io.agora.rtm.TopicInfo;
import io.agora.rtm.PublisherInfo;
import io.agora.rtm.LockDetail;
import io.agora.rtm.Metadata;
import io.agora.rtm.MetadataItem;
import io.agora.rtm.MetadataOptions;
import io.agora.rtm.RtmConstants.RtmConnectionChangeReason;
import io.agora.rtm.RtmConstants.RtmConnectionState;

class APPID {
    public static final String APP_ID = "Your App ID";
}

public class RtmJavaDemo {
    private RtmClient mRtmClient;
    private boolean loginStatus = false;
    private Scanner scn;

    private final RtmEventListener mRtmEventListener = new RtmEventListener() {
        @Override
        public void onMessageEvent(MessageEvent event) {
            System.out.println("onMessageEvent: " + event.toString());
        }

        @Override
        public void onPresenceEvent(PresenceEvent event) {
            System.out.println("onPresenceEvent: " + event.toString());
        }

        @Override
        public void onTopicEvent(TopicEvent event) {
            System.out.println("onTopicEvent: " + event.toString());
        }

        @Override
        public void onLockEvent(LockEvent event) {
            System.out.println("onLockEvent: " + event.toString());
        }

        @Override
        public void onStorageEvent(StorageEvent event) {
            System.out.println("onStorageEvent: " + event.toString());
        }

        @Override
        public void onConnectionStateChanged(String channelName, RtmConstants.RtmConnectionState state, RtmConstants.RtmConnectionChangeReason reason) {
            System.out.println("onConnectionStateChange, state: " + state + ", reason: " + reason);
        }

        @Override
        public void onTokenPrivilegeWillExpire(String channelName) {
            System.out.println("onTokenPrivilegeWillExpire");
        }
    };

    public void init() {
        try {
            scn = new Scanner(System.in);
            System.out.println("Please enter userID (literal \"null\" or starting " +
                    "with space is not allowed, no more than 64 charaters!):");
            String userId = scn.nextLine();

            RtmConfig rtmConfig = new RtmConfig.Builder(APPID.APP_ID, userId)
                    .eventListener(mRtmEventListener)
                    .build();
            mRtmClient = RtmClient.create(rtmConfig);
            // set parameter
            //mRtmClient.setParameters("{\"rtc.vos_list\":[\"114.236.138.120:4051\"]}");
            //mRtmClient.setParameters("{\"rtm.sync_ap_address\":[\"114.236.137.40\", 8443]}");
            //mRtmClient.setParameters("{\"rtm.link_address0\":[\"183.131.160.141\", 9130]}");
            //mRtmClient.setParameters("{\"rtm.link_address1\":[\"183.131.160.142\", 9131]}");
            //mRtmClient.setParameters("{\"rtm.link_encryption\": false}");
        } catch (Exception e) {
            System.out.println("Rtm sdk init fatal error! " + e.toString());
            throw new RuntimeException("Need to check rtm sdk init process");
        }
    }

    public void release() {
        RtmClient.release();
    }

    public boolean login() {
        mRtmClient.login(APPID.APP_ID, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                loginStatus = true;
                System.out.println("login success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                loginStatus = false;
                System.out.println("login failure! " + errorInfo.toString());
            }
        });
        return true;
    }

    public void logout() {
        loginStatus = false;
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                System.out.println("logout success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println("logout failure! " + errorInfo.toString());
            }
        });
    }

    public void privateChat(String peer) {
        String msg;
        String peerId = peer;
        while (true) {
            System.out.println("please input message you want to send," +
                    " or input \'quit\' " + " to leave privateChat");
            msg = scn.nextLine();
            if (msg.equals("quit")) {
                return;
            }
            sendPeerMessage(peerId, msg);
        }
    }

    public void sendPeerMessage(String peerId, String msg) {
        PublishOptions options = new PublishOptions();
        options.setChannelType(RtmChannelType.USER);
        mRtmClient.publish(peerId, msg, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                System.out.println("send message to " + peerId + " success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println(errorInfo.toString());
            }
        });
    }

    public void groupChat(String channel) {
        String msg;
        String channelName = channel;
        SubscribeOptions subscirbeOptions = new SubscribeOptions(true, true, true, true);

        mRtmClient.subscribe(channelName, subscirbeOptions, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                System.out.println("join channel success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println(errorInfo.toString());
            }
        });

        while (true) {
            System.out.println("please input message you want to send," +
                    " or input \'quit\' " + " to leave groupChat, " +
                    "or input \'members\' to list members");
            msg = scn.nextLine();
            if (msg.equals("quit")) {
                return;
            } else if (msg.equals("members")) {
                getChannelMembers(channel);
            } else {
                sendChannelMessage(channel, msg);
            }
        }
    }

    public void sendChannelMessage(String channel, String msg) {
        PublishOptions options = new PublishOptions();
        mRtmClient.publish(channel, msg, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                System.out.println("send message to channel success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println(errorInfo.toString());
            }
        });
    }

    public void getChannelMembers(String channel) {
        GetOnlineUsersOptions options = new GetOnlineUsersOptions(true, false);
        mRtmClient.getPresence().getOnlineUsers(channel, RtmConstants.RtmChannelType.MESSAGE, options, new ResultCallback<GetOnlineUsersResult>() {
            @Override
            public void onSuccess(GetOnlineUsersResult result) {
                System.out.println("get online user success, result: " + result.toString());
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println(errorInfo.toString());
            }
        });
    }

    public static void main(String[] args) {
        RtmJavaDemo client_ = new RtmJavaDemo();
        client_.init();
        while (true) {
            if (!client_.loginStatus) {
                if (!client_.login())
                    continue;
            }
            System.out.println("1: peer to peer chat\n"
                    + "2: group chat\n"
                    + "3: logout");
            System.out.println("please input your choice:");
            Scanner scn = new Scanner(System.in);
            int choice;
            if (scn.hasNextInt()) {
                choice = scn.nextInt();
            } else {
                System.out.println("your input is not an int type");
                continue;
            }
            if (choice == 1) {
                System.out.println("please input your destination user ID:");
                scn.nextLine();
                String peer = scn.nextLine();
                client_.privateChat(peer);
            } else if (choice == 2) {
                System.out.println("please input your channel ID:");
                scn.nextLine();
                String channel = scn.nextLine();
                client_.groupChat(channel);
            } else if (choice == 3) {
                client_.logout();
                System.out.println("quit the demo? yes/no");
                scn.nextLine();
                if (scn.hasNextLine()) {
                    String quit = scn.nextLine();
                    if (quit.equals("yes")) {
                        client_.release();
                        client_ = null;
                        break;
                    }
                }
            } else {
                continue;
            }
        }
        System.out.println("leaving demo...");
        System.exit(0);
    }
}