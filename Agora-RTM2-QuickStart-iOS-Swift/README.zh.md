# Agora RTM Tutorial iOS

*Read this in other languages: [English](README.md)*

这个开源示例项目演示了如何快速集成 Agora RTM SDK，实现消息通讯。

在这个示例项目中包含了以下功能：

- 登录和登出；
- 订阅频道和取消订阅频道；
- 发送和接收频道消息；

## 问题描述
iOS 系统版本升级至 14.0 版本后，用户使用集成了声网 RTM SDK 的 app 时会看到查找本地网络设备的弹窗提示。默认弹窗界面如下图所示：

![](../pictures/ios_14_privacy_zh.png)

[解决方案](https://docs.agora.io/cn/faq/local_network_privacy_rtm)

## 运行示例程序
首先在 [Agora.io 注册](https://dashboard.agora.io/cn/signup/) 注册账号，并创建自己的测试项目，获取到 AppID。将 AppID 填写进 ContentView.swift

```
var appid: String = <#YOUR APPID#>
```

从 https://doc.shengwang.cn/doc/rtm2/homepage 下载Objective-C的RTM SDK 并将libs内的所有xcframework导入到swift-quick-start文件夹下

最后使用 XCode 打开 swift-quick-start.xcworkspace，连接 iPhone／iPad 测试设备，设置有效的开发者签名后即可运行。

## 运行环境
* XCode 8.0 +
* 两台 iOS 真机设备
* 支持模拟器

## 联系我们

- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug, 欢迎提交 [issue](https://github.com/AgoraIO/RTM/issues)

## 代码许可

The MIT License (MIT).
