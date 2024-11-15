# Agora RTM2 Windows CPP Quick Start Demo

*其他语言版本： [简体中文](README.zh.md)*

The Agora RTM2 Quick Start  Demo is an open-source demo that will help you get message chat integrated directly into your applications using the Agora RTM SDK.

With this sample app, you can:

- Login RTM server
- Join channel
- Send and receive channel message
- Logout RTM server

## Developer Environment Requirements

- Windows 10/11
- x86-64
- C++ 11 and above
- Visual Studio

## Running the App

### 1. clone this repo
```
git clone git@github.com:AgoraIO/RTM2.git
```

### 2. replace appid 

create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID.


### 3. integration SDK

download the RTM SDK in Agora.io SDK. After decompressing, copy the sdk folder to the ./rtmdemo of this project.

### 4. build and run

open rtmdemo.sln(under the rtmdemo folder) with Visual Studio, and Update "main.cpp" with your App ID .

```
const std::string APP_ID = "Your APP ID";
```

Finally, you can build and run the program.


## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/signaling/overview/product-overview)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/RTM2/issues)

## License

The MIT License (MIT).