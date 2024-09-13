import 'package:flutter/material.dart';
import 'package:agora_rtm/agora_rtm.dart';
import 'dart:convert';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  const userId = 'your_userId';
  const appId = 'your_appId';
  const channelName = 'getting-started';
  late RtmClient rtmClient;

  try {
    // create rtm client
    final (status, client) = await RTM( appId, userId);
    if (status.error == true) {
        print('${status.operation} failed due to ${status.reason}, error code: ${status.errorCode}');
    } else {
        rtmClient = client;
        print('Initialize success!');
    }

    // add events listner
    rtmClient.addListener(
    // add message event handler
    message: (event) {
      print('recieved a message from channel: ${event.channelName}, channel type : ${event.channelType}');
      print('message content: ${utf8.decode(event.message!)}, custome type: ${event.customType}');
    },
    // add link state event handler
    linkState: (event) {
      print('link state changed from ${event.previousState} to ${event.currentState}');
      print('reason: ${event.reason}, due to operation ${event.operation}');
    });

  } catch (e) {
    print('Initialize falid!:${e}');
  }

  try {
    // login rtm service
    var (status,response) = await rtmClient.login(appId);
    if (status.error == true) {
        print('${status.operation} failed due to ${status.reason}, error code: ${status.errorCode}');
    } else {
        print('login RTM success!');
    }
  } catch (e) {
    print('Failed to login: $e');
  }

  try {
    // subscribe to 'getting-started' channel
    var (status,response) = await rtmClient.subscribe(channelName);
    if (status.error == true) {
        print('${status.operation} failed due to ${status.reason}, error code: ${status.errorCode}');
    } else {
        print('subscribe channel: ${channelName} success!');
    }
  } catch (e) {
    print('Failed to subscribe channel: $e');
  }

  // Send a message every second for 100 seconds
  for (var i = 0; i < 100; i++) {
    try {
      var (status, response) = await rtmClient.publish(
          channelName,
          'message number : ${i}',
          channelType: RtmChannelType.message,
          customType: 'PlainText'
          );
      if (status.error == true ){
        print('${status.operation} failed, errorCode: ${status.errorCode}, due to ${status.reason}'); 
      } else {
        print('${status.operation} success! message number:$i');
      }
    } catch (e) {
      print('Failed to publish message: $e');
    }
    await Future.delayed(Duration(seconds: 1));
  }

   try {
    // unsubscribe channel
    var (status,response) = await rtmClient.unsubscribe(channelName);
    if (status.error == true) {
        print('${status.operation} failed due to ${status.reason}, error code: ${status.errorCode}');
    } else {
        print('unsubscribe success!');
    }
  } catch (e) {
    print('something went wrong with logout: $e');
  }
  
  try {
    // logout rtm service
    var (status,response) = await rtmClient.logout();
    if (status.error == true) {
        print('${status.operation} failed due to ${status.reason}, error code: ${status.errorCode}');
    } else {
        print('logout RTM success!');
    }
  } catch (e) {
    print('something went wrong with logout: $e');
  }
}

