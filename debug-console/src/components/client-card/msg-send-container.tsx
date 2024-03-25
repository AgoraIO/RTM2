import React, { useEffect, useRef, useState } from 'react';
import { Input } from 'antd';
import { RTMClient, RTMStreamChannel } from 'agora-rtm';
import { ChannelInfo, ChannelType } from '../../common/types';
import send from 'assets/send.png';
import more from 'assets/more.png';

interface MsgSendContainerProps {
  rtm: RTMClient;
  streamChannel: RTMStreamChannel;
  channelInfo: ChannelInfo;
  channelType: ChannelType;
  saveError: (type: string, error: any) => void;
}

const DEFAULT_MSG = 'Send message here';

export const MsgSendContainer: React.FC<MsgSendContainerProps> = ({
  rtm,
  streamChannel,
  channelInfo,
  channelType,
  saveError,
}) => {
  const ref = useRef<HTMLDivElement | null>(null);
  const [msg, setMsg] = useState<string>(DEFAULT_MSG);
  const [customType, setCustomType] = useState<string>();
  const [showMore, setShowMore] = useState<boolean>(false);
  const handleShowMore = () => setShowMore(prev => !prev);
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setShowMore(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);
  const publishChannelMsg = async () => {
    try {
      await rtm.publish(channelInfo.msgChannelName, msg, { customType });
      setMsg(DEFAULT_MSG);
      setCustomType('');
    } catch (error) {
      saveError('Publish Error', error);
      console.log(error);
    }
  };
  const publishTopicMsg = async () => {
    try {
      await streamChannel?.publishTopicMessage(channelInfo.topic, msg, {
        customType,
      });
      setMsg(DEFAULT_MSG);
      setCustomType('');
    } catch (error) {
      saveError('Publish Error', error);
      console.log(error);
    }
  };

  const publish = async () => {
    switch (channelType) {
      case 'MESSAGE':
        publishChannelMsg();
        break;
      case 'STREAM':
        publishTopicMsg();
    }
  };

  return (
    <div ref={ref}>
      <div className="msg-input-container">
        <Input
          value={msg}
          placeholder={DEFAULT_MSG}
          onChange={e => setMsg(e.target.value)}
          style={{ width: 320, height: 45, textAlign: 'left' }}
          onPressEnter={publish}
        />
        <div className="button" onClick={publish}>
          <img width={24} height={24} src={send} />
        </div>
        <div className="button" onClick={handleShowMore}>
          <img width={24} height={24} src={more} />
        </div>
      </div>
      {showMore && (
        <div className="msg-custom-container">
          <span>CustomType</span>
          <Input
            value={customType}
            style={{ width: 320, height: 45, textAlign: 'left' }}
            onChange={e => setCustomType(e.target.value)}
            onPressEnter={publish}
          />
        </div>
      )}
    </div>
  );
};
