# Agora RTM Swift Quick Start iOS

*其他语言版本： [简体中文](README.zh.md)*

The swift-quick-start App is an open-source demo that will help you get message chat integrated directly into your iOS applications using the Agora RTM SDK.

With this sample app, you can:

- Log in / log out
- subscribe / unsubscribe channel
- Send and receive channel message online

## Running the App
First, create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID. Update "ContentView.swift" with your App ID.

```
var appid: String = <#YOUR APPID#>
```

Next, download Agora RTM Objective-C SDK from https://doc.shengwang.cn/doc/rtm2/homepage
unzip package and move all xcframework in libs to swift-quick-start

Finally, Open swift-quick-start.xcworkspace, connect your iPhone／iPad device, setup your development signing and run.

## Developer Environment Requirements
* XCode 8.0 +
* Real devices (iPhone or iPad)
* iOS simulator is supported

## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/RTM/issues)

## License

The MIT License (MIT).
