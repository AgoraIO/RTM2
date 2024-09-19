//
//  ContentView.swift
//  swift-quick-start
//
//  Created by jiangyingyan on 2024/9/13.
//

import SwiftUI
import Combine
import AgoraRtmKit

struct Message: Identifiable, Equatable {
    let id = UUID()
    let content: String
}

class ChatViewModel: NSObject, ObservableObject, AgoraRtmClientDelegate{
    var appid: String = <#YOUR APPID#>
    var rtmKit: AgoraRtmClientKit? = nil
    @Published var username: String = ""
    @Published var message: String = ""
    @Published var channel: String = ""
    @Published var messages: [Message] = []
    
    func rtmKit(_ rtmKit: AgoraRtmClientKit, didReceiveLinkStateEvent event: AgoraRtmLinkStateEvent) {
        addToMessageList(str: "RTM link state change current state is: \(event.currentState.rawValue) previous state is :\(event.previousState.rawValue)")
    }
    
    func rtmKit(_ rtmKit: AgoraRtmClientKit, didReceiveMessageEvent event: AgoraRtmMessageEvent) {
        addToMessageList(str: "Message received.\n channel: \(event.channelName), publisher: \(event.publisher),  message content: \(event.message.stringData!)")
    }
    
    func login() {
        if rtmKit != nil {
            addToMessageList(str: "RTM alreay login! Logout first!")
            return
        }
        let config = AgoraRtmClientConfig(appId: appid, userId: username);
        rtmKit = try! AgoraRtmClientKit(config, delegate: self)
        rtmKit?.login(appid, completion: { response, error in
            if error != nil {
                self.addToMessageList(str: "login failed error code is \(error?.errorCode.rawValue), reason is \(error?.reason)")
            } else {
                self.addToMessageList(str: "\(self.username) login succsss")
            }
        })
    }
    
    func logout() {
        if rtmKit == nil {
            addToMessageList(str: "RTM alreay logout!")
            return
        }
        rtmKit?.logout()
        rtmKit?.destroy()
        rtmKit = nil
        addToMessageList(str: "RTM logout!")
    }
    
    func subscribe() {
        rtmKit?.subscribe(channelName: channel, option: nil, completion: { response, error in
            if error != nil {
                self.addToMessageList(str: "subscribe channel: \(self.channel) failed error code is \(error?.errorCode.rawValue), reason is \(error?.reason)")
            } else {
                self.addToMessageList(str: "susbcribe channel: \(self.channel) succsss")
            }
        })
    }
    
    func unsubscribe() {
        rtmKit?.unsubscribe(channel, completion: { response, error in
            if error != nil {
                self.addToMessageList(str: "unsubscribe channel: \(self.channel) failed error code is \(error?.errorCode.rawValue), reason is \(error?.reason)")
            } else {
                self.addToMessageList(str: "unsusbcribe channel: \(self.channel) succsss")
            }
        })
    }
    
    func sendMessage() {
        guard !message.isEmpty else { return }
        if (rtmKit != nil) {
            rtmKit?.publish(channelName: channel, message: message, option: nil, completion: { response, error in
                if error != nil {
                    self.addToMessageList(str: "publish failed error code is \(error?.errorCode.rawValue), reason is \(error?.reason)")
                } else {
                    self.addToMessageList(str: "publish message to channel: \(self.channel) succsss")
                }
            })
        }
        message = ""
    }
    
    func addToMessageList(str: String) {
        messages.append(Message(content: str))
    }
}

struct ContentView: View {
    @StateObject private var viewModel = ChatViewModel()
    
    var body: some View {
        VStack {
            TextField("input user name", text: $viewModel.username)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .font(.title)
            HStack {
                Button(action: viewModel.login) {
                    Text("login")
                        .font(.title)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                
                Button(action: viewModel.logout) {
                    Text("logout")
                        .font(.title)
                        .padding()
                        .background(Color.red)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
            }

            TextField("channel name", text: $viewModel.channel)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .font(.title)
            HStack {
                Button(action: viewModel.subscribe) {
                    Text("subscribe")
                        .font(.title)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                
                Button(action: viewModel.unsubscribe) {
                    Text("unsubscribe")
                        .font(.title)
                        .padding()
                        .background(Color.red)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
            }

            TextField("input message", text: $viewModel.message)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .font(.title)

            Button(action: viewModel.sendMessage) {
                Text("send")
                    .font(.title)
                    .padding()
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }

            // display log
            ScrollViewReader { scrollProxy in
                            List(viewModel.messages) { message in
                                Text(message.content)
                                    .id(message.id)
                            }
                            .onChange(of: viewModel.messages) { _ in
                                if let lastMessage = viewModel.messages.last {
                                    withAnimation {
                                        scrollProxy.scrollTo(lastMessage.id, anchor: .bottom)
                                    }
                                }
                            }
                        }
            .padding()
            
        }
        .padding()
    }
}
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
