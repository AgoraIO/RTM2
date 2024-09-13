# Agora RTM2 Linux CPP Quick Start Demo

*其他语言版本： [简体中文](README.zh.md)*

The Agora RTM2 Qucik Start  Demo is an open-source demo that will help you get message chat integrated directly into your Android applications using the Agora RTM SDK.

With this sample app, you can:

- Login RTM server
- Join channel
- Send and receive channel message
- Logout RTM server

## Running the App

First, create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID.
Update "rtm_quick_start.cpp" with your App ID .

```
const std::string APP_ID = "Your APP ID";

```

## Integration mode

- The first step is to download the RTM SDK in Agora.io SDK. After decompressing, copy the *.so under the LIB folder to the ./libs of this project and copy the *.h under the INCLUDE folder to the ./include of this project.

- The second step: execute the compilation script build.sh to compile the program, which will ultimately generate the executable file rtm_demo in the build directory.

Finally, set the dynamic library loading path with export LD_LIBRARY_PATH=./lib, and then you can run the program using ./build/rtm_demo.

## Developer Environment Requirements

- ubuntu 18.04 or Debian 9.9
- x86-64
- C++ 11

## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/RTM2/issues)

## License

The MIT License (MIT).