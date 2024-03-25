import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import AgoraRTM, { RTMClient } from 'agora-rtm';
import { generateStr, jsonToStr } from '../../common/utils';
import { ChannelInfo } from '../../common/types';
import refreshIcon from 'assets/refresh.png';

interface QueryBoxProps {
  appId: string;
  channelInfo: ChannelInfo;
}
type TAB_LIST = 'Presence' | 'Storage' | 'Lock';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const QueryInfo: React.FC<{ query: any }> = ({ query }) => {
  const [info, setInfo] = useState<string>();
  const refresh = useCallback(async () => {
    try {
      const res = await query?.();
      setInfo(jsonToStr(res));
    } catch (error) {
      console.log(error);
    }
  }, [query]);
  useEffect(() => {
    refresh();
  }, [refresh]);
  return (
    <div className="info">
      <img width={16} height={16} src={refreshIcon} onClick={refresh} />
      <pre>{info}</pre>
    </div>
  );
};

export const QueryBox: React.FC<QueryBoxProps> = ({ appId, channelInfo }) => {
  const [current, setCurrent] = useState<TAB_LIST>();
  const ref = useRef<RTMClient>();

  const setupRtm = async () => {
    const { RTM } = AgoraRTM;
    try {
      const rtm = new RTM(appId, `client_${generateStr(4)}`);
      await rtm.login();
      ref.current = rtm;
      setCurrent('Presence');
    } catch (status) {
      console.log('Error');
      console.log(status);
    }
  };
  useEffect(() => {
    setupRtm();
    () => {
      ref.current?.logout();
    };
  }, []);
  const onTabChange = useCallback(
    (tab: TAB_LIST) => () => {
      setCurrent(tab);
    },
    []
  );
  const tabs = useMemo(() => {
    const { msgChannelName, streamChannelName } = channelInfo;
    return {
      Presence: () =>
        Promise.all([
          ref.current?.presence.whoNow(msgChannelName, 'MESSAGE', {
            includedState: true,
            includedUserId: true,
          }),
          ref.current?.presence.whoNow(streamChannelName, 'STREAM', {
            includedState: true,
            includedUserId: true,
          }),
        ]).then(res => {
          return res;
        }),
      Storage: () =>
        Promise.all([
          ref.current?.storage.getChannelMetadata(msgChannelName, 'MESSAGE'),
          ref.current?.storage.getChannelMetadata(streamChannelName, 'STREAM'),
        ]).then(res => {
          return res;
        }),
      Lock: () =>
        Promise.all([
          ref.current?.lock.getLock(msgChannelName, 'MESSAGE'),
          ref.current?.lock.getLock(streamChannelName, 'STREAM'),
        ]).then(res => {
          return res;
        }),
    };
  }, [channelInfo, ref.current]);
  return (
    <div className="query-box">
      <div className="title">
        {Object.keys(tabs).map(name => (
          <label
            className={current === name ? 'selected' : ''}
            key={name}
            onClick={onTabChange(name as TAB_LIST)}
          >
            {name}&nbsp;&nbsp;
          </label>
        ))}
      </div>
      <QueryInfo query={tabs[current]} />
    </div>
  );
};
