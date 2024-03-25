import React, { useEffect, useRef, useState } from 'react';
import { Avatar, Modal } from 'antd';
import dayjs from 'dayjs';
import { RTMClient, RTMStreamChannel } from 'agora-rtm';
import { MsgSendContainer } from './msg-send-container';
import { EditTabs } from './edit-tabs';
import { ChannelInfo, ChannelType } from '../../common/types';
import { generateStr, jsonToStr } from '../../common/utils';
import { HeaderButton } from './header-button';
import deleteIcon from 'assets/delete.png';
import edit from 'assets/edit.png';
import map from 'assets/map.png';
import avatar from 'assets/avatar2.png';

interface ClientCardProps {
  client: {
    rtm: RTMClient;
    username: string;
    token?: string;
    encrypt?: boolean;
    cipherKey?: string;
  };
  channelInfo: ChannelInfo;
  logout: () => void;
  updateClient: (username: string, cipherKey: string) => Promise<void>;
}

interface CurrentChannel extends ChannelInfo {
  streamChannel?: RTMStreamChannel;
  msgChannelSubed?: boolean;
  streamChannelJoined?: boolean;
  topicJoined?: boolean;
}

export const ClientCard: React.FC<ClientCardProps> = ({
  client,
  channelInfo,
  logout,
  updateClient,
}) => {
  const { username, rtm, token } = client;
  const [userStates, setUserStates] = useState('');
  const [isEdit, setIsEdit] = useState(false);
  const [msgList, setMsgList] = useState([]);
  const [channelType, setChannelType] = useState<ChannelType>('MESSAGE');
  const logined = useRef<boolean>(false);
  const [currentChannel, setCurrentChannel] = useState<CurrentChannel>({
    msgChannelName: '',
    streamChannelName: '',
    topic: '',
  });
  const saveError = (type: string, error: unknown) => {
    const newMsg = {
      type,
      msg: JSON.stringify(error, null, 2),
      timestamp: Date.now(),
      id: `msg_${generateStr(8)}`,
    };
    setMsgList(prev => [...prev, newMsg]);
    setTimeout(() => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      document.getElementById(newMsg.id)?.scrollIntoViewIfNeeded?.();
    }, 1000);
  };

  const updateChannel = async (channelInfo: ChannelInfo) => {
    const newChannel: CurrentChannel = {
      ...channelInfo,
      msgChannelSubed: false,
      streamChannel: currentChannel.streamChannel,
      streamChannelJoined: false,
      topicJoined: false,
    };
    const { msgChannelName, streamChannelName, topic } = channelInfo;
    if (msgChannelName !== currentChannel.msgChannelName) {
      try {
        // 离开当前频道
        if (currentChannel.msgChannelName) {
          await rtm.unsubscribe(currentChannel.msgChannelName);
        }
        // 加入频道
        try {
          await rtm.subscribe(msgChannelName);
          newChannel.msgChannelSubed = true;
        } catch (error) {
          saveError('Subscribe Error', error);
          console.log(error);
        }
      } catch (error) {
        saveError('Unsubscribe Error', error);
      }
    }
    if (streamChannelName !== currentChannel.streamChannelName) {
      // 离开当前streamChannel
      if (currentChannel.streamChannelName) {
        currentChannel.streamChannel.leave();
      }
      try {
        const streamChannel = rtm.createStreamChannel(streamChannelName);
        newChannel.streamChannel = streamChannel;
        await streamChannel.join({ token });
        newChannel.streamChannelJoined = true;
        await streamChannel.joinTopic(topic);
        newChannel.topicJoined = true;
      } catch (error) {
        console.log(error);
      }
    } else {
      if (topic !== currentChannel.topic) {
        if (currentChannel.topic) {
          currentChannel.streamChannel.leaveTopic(currentChannel.topic);
        }
        try {
          await newChannel.streamChannel.joinTopic(topic);
          newChannel.topicJoined = true;
          await newChannel.streamChannel.subscribeTopic(topic);
        } catch (error) {
          console.log(error);
        }
      }
    }
    setCurrentChannel(newChannel);
  };

  const init = async () => {
    try {
      if (!logined.current) {
        await rtm.login();
        logined.current = true;
      }
      updateChannel(channelInfo);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    init();
  }, [channelInfo]);

  const getState = async () => {
    try {
      const { msgChannelName, streamChannelName } = currentChannel;
      const channelName =
        channelType === 'MESSAGE' ? msgChannelName : streamChannelName;
      const res = await rtm.presence.getState(
        username,
        channelName,
        channelType
      );
      setUserStates(jsonToStr(res.states));
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getState();
  }, [
    currentChannel.msgChannelName,
    currentChannel.streamChannelName,
    channelType,
  ]);

  useEffect(() => {
    const eventHandle =
      (
        type:
          | 'Message'
          | 'Status'
          | 'Presence'
          | 'Storage'
          | 'Topic'
          | 'Lock'
          | 'TokenPrivilegeWillExpire'
      ) =>
      (event: never) => {
        saveError(type, event);
      };
    rtm.addEventListener('status', eventHandle('Status'));
    rtm.addEventListener('presence', eventHandle('Presence'));
    rtm.addEventListener('message', eventHandle('Message'));
    rtm.addEventListener('storage', eventHandle('Storage'));
    rtm.addEventListener('lock', eventHandle('Lock'));
    rtm.addEventListener('topic', (event: any) => {
      eventHandle('Topic')(event as never);
      if (
        ['SNAPSHOT', 'REMOTE_JOIN', 'REMOTE_LEAVE'].includes(event.eventType)
      ) {
        const topicInfo = event.topicInfos[0];
        const users = topicInfo?.publishers?.map(
          (user: any) => user?.publisherUserId
        );
        if (users?.length) {
          // @ts-expect-error stChannelInstances
          const streamChannel = rtm.stChannelInstances?.find(
            (item: any) => item.channelName === event.channelName
          );
          if (event.eventType === 'REMOTE_LEAVE') {
            streamChannel.unsubscribeTopic(topicInfo.topicName, {
              users,
            });
          } else {
            streamChannel.subscribeTopic(topicInfo.topicName, { users });
          }
        }
      }
    });
    rtm.addEventListener(
      'tokenPrivilegeWillExpire',
      eventHandle('TokenPrivilegeWillExpire')
    );
    return () => {
      rtm.logout();
      rtm.removeEventListener('status', eventHandle('Status'));
      rtm.removeEventListener('presence', eventHandle('Presence'));
      rtm.removeEventListener('message', eventHandle('Message'));
      rtm.removeEventListener('storage', eventHandle('Storage'));
      rtm.removeEventListener('lock', eventHandle('Lock'));
      rtm.removeEventListener('topic', eventHandle('Topic'));
      rtm.removeEventListener(
        'tokenPrivilegeWillExpire',
        eventHandle('TokenPrivilegeWillExpire')
      );
    };
  }, []);
  // 退订频道
  const unsub = async () => {
    try {
      await rtm.unsubscribe(currentChannel.msgChannelName);
      setCurrentChannel(prev => ({ ...prev, msgChannelSubed: false }));
    } catch (error) {
      saveError('Unsubscribe Error', error);
      console.log(error);
    }
  };
  // 订阅频道
  const sub = async () => {
    try {
      await rtm.subscribe(currentChannel.msgChannelName);
      setCurrentChannel(prev => ({ ...prev, msgChannelSubed: true }));
    } catch (error) {
      saveError('Subscribe Error', error);
      console.log(error);
    }
  };
  // 离开streamChannel
  const leaveChannel = async () => {
    try {
      await currentChannel.streamChannel.leave();
      setCurrentChannel(prev => ({
        ...prev,
        streamChannelJoined: false,
        topicJoined: false,
      }));
    } catch (error) {
      saveError('Leave Channel Error', error);
      console.log(error);
    }
  };

  // 加入streamChannel
  const joinChannel = async () => {
    try {
      await currentChannel.streamChannel.join();
      setCurrentChannel(prev => ({
        ...prev,
        streamChannelJoined: true,
        topicJoined: false,
      }));
    } catch (error) {
      saveError('Join Channel Error', error);
      console.log(error);
    }
  };
  // 离开topic
  const leaveTopic = async () => {
    try {
      await currentChannel.streamChannel.leaveTopic(currentChannel.topic);
      setCurrentChannel(prev => ({
        ...prev,
        topicJoined: false,
      }));
    } catch (error) {
      saveError('Leave Topic Error', error);
      console.log(error);
    }
  };
  // 加入topic
  const joinTopic = async () => {
    try {
      await currentChannel.streamChannel.joinTopic(currentChannel.topic);
      setCurrentChannel(prev => ({
        ...prev,
        topicJoined: true,
      }));
      await currentChannel.streamChannel.subscribeTopic(currentChannel.topic);
    } catch (error) {
      saveError('Join Topic Error', error);
      console.log(error);
    }
  };
  const handleIsEdit = () => setIsEdit(prev => !prev);
  const whereNow = async () => {
    try {
      const res = await rtm.presence.whereNow(username);
      Modal.info({ title: 'Where Now', content: jsonToStr(res) });
    } catch (error) {
      saveError('Where Now Error', error);
      console.log(error);
    }
  };
  const updateState = async (state: string) => {
    try {
      const channelName =
        channelType === 'MESSAGE'
          ? channelInfo.msgChannelName
          : channelInfo.streamChannelName;
      const val = JSON.parse(state);
      await rtm.presence.setState(channelName, channelType, val);
      getState();
    } catch (error) {
      console.log(error);
    }
  };
  return (
    <div className="client-card">
      <div className="client-card-header-container">
        <div className="client-user-info">
          <Avatar src={avatar} size={32} />
          <div className="client-user">
            <p>{username}</p>
            <p className="desc">{userStates}</p>
          </div>
        </div>
        <div className="operation">
          <img
            width={24}
            height={24}
            style={{ marginRight: 24 }}
            src={edit}
            onClick={handleIsEdit}
          />
          <img width={24} height={24} src={deleteIcon} onClick={logout} />
        </div>
      </div>
      {isEdit ? (
        <EditTabs
          channelInfo={channelInfo}
          userStates={userStates}
          channelType={channelType}
          editExit={handleIsEdit}
          updateClient={updateClient}
          client={client}
          updateState={updateState}
        />
      ) : (
        <div className="channel-change-box">
          <div className="channel-type-box">
            <span
              className={channelType === 'MESSAGE' ? 'focused' : ''}
              onClick={() => setChannelType('MESSAGE')}
            >
              MESSGAE
            </span>
            <span
              className={channelType === 'STREAM' ? 'focused' : ''}
              onClick={() => setChannelType('STREAM')}
            >
              STREAM
            </span>
          </div>
          <img width={32} height={32} src={map} onClick={whereNow} />
          {channelType === 'MESSAGE' &&
            (currentChannel.msgChannelSubed ? (
              <HeaderButton onClick={unsub}>UNSUBSCRIBE</HeaderButton>
            ) : (
              <HeaderButton onClick={sub}>SUBSCRIBE</HeaderButton>
            ))}
          {channelType === 'STREAM' && (
            <>
              {currentChannel.streamChannelJoined ? (
                <HeaderButton onClick={leaveChannel}>
                  LEAVE CHANNEL
                </HeaderButton>
              ) : (
                <HeaderButton onClick={joinChannel}>JOIN CHANNEL</HeaderButton>
              )}
              {currentChannel.topicJoined ? (
                <HeaderButton onClick={leaveTopic}>LEAVE TOPIC</HeaderButton>
              ) : (
                <HeaderButton onClick={joinTopic}>JOIN TOPIC</HeaderButton>
              )}
            </>
          )}
        </div>
      )}
      <div className="msg-list">
        {msgList.map(({ msg, type, id, timestamp }) => (
          <div className="msg-info-container" key={id} id={id}>
            <div className={`bar ${type === 'Status' ? 'red' : ''}`} />
            <div className="msg-info">
              <div className={`msg-header ${type === 'Status' ? 'red' : ''}`}>
                <span className="msg-title">{type} Event</span>
                <span className="msg-time">
                  &nbsp;&nbsp;{dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')}
                </span>
              </div>
              <div className="msg-content">{msg}</div>
            </div>
          </div>
        ))}
      </div>
      <div className="msg-send">
        <MsgSendContainer
          rtm={rtm}
          streamChannel={currentChannel.streamChannel}
          channelInfo={channelInfo}
          channelType={channelType}
          saveError={saveError}
        />
      </div>
    </div>
  );
};
