import React, { useCallback, useEffect, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { ConfigProvider, message, Select } from 'antd';
import AgoraRTM, { RTMClient, RTMConfig } from 'agora-rtm';
import { ClientCard } from './components/client-card';
import { QueryBox } from './components/setting-card/query-box';
import { ClientSetting } from './components/setting-card/client-setting';
import { ChannelSetting } from './components/setting-card/channel-setting';
import { ChannelInfo } from './common/types';
import { generateStr, generateToken } from './common/utils';
import {
  DEFAULT_CHANNEL_INFO,
  DEFAULT_CIPHER_KEY,
  DEFAULT_RTM_CONFIG,
  DEFAULT_USERS,
  USERS,
} from './common/constants';
import logo from 'assets/agora.png';
import avatar from 'assets/avatar1.png';
import add from 'assets/add.png';
import 'styles/index.less';

const theme = {
  components: {
    Form: { itemMarginBottom: 12 },
    Button: { fontWeight: 100, contentFontSize: 13 },
  },
  token: {
    colorPrimary: '#099DFD',
    controlHeight: 36,
    borderRadius: 10,
    fontFamily: 'PingFang SC',
    fontWeight: 400,
  },
};

function App() {
  const [rtmClientList, setRtmClientList] = useState([]);
  const [channelInfo, setChannelInfo] =
    useState<ChannelInfo>(DEFAULT_CHANNEL_INFO);
  const clientSettingRef = useRef<any>();
  const setupRtm = (userId: string, config = DEFAULT_RTM_CONFIG) => {
    const { RTM } = AgoraRTM;
    try {
      if (!config.appId) {
        message.error('请填写APP ID');
        throw new Error('请填写appId');
      }
      const { appId, appCertificate, encrypt, ...options } = config;
      // 开启动态token
      if (appCertificate) {
        (options as RTMConfig).token = generateToken(appId, userId);
      }
      // 加密策略
      if (encrypt) {
        (options as RTMConfig).encryptionMode = 'AES_128_GCM';
        (options as RTMConfig).salt = new Uint8Array([
          0x02, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa,
          0xbb, 0xcc, 0xdd, 0xee, 0xff, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55,
          0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff,
        ]);
        (options as RTMConfig).cipherKey = config.cipherKey;
      }
      const rtm = new RTM(appId, userId, options as RTMConfig);
      clientSettingRef.current = config;
      return {
        rtm,
        token: (options as RTMConfig).token,
        encrypt,
        cipherKey: config.cipherKey,
      };
    } catch (status) {
      console.log('Error', status);
      return {};
    }
  };

  const update = (info: any, isReset?: boolean) => {
    // 先登出
    Promise.all(
      rtmClientList.map(async ({ rtm, userId }) => {
        try {
          await rtm.logout();
        } catch (error) {
          console.log(error);
        }
        return { rtm: null, userId };
      })
    ).then(clientList => {
      // 清空client
      setRtmClientList(isReset ? clientList.slice(0, 1) : clientList);
      // 重新create rtm
      requestAnimationFrame(() => {
        setRtmClientList(prev =>
          prev.map(({ userId }) => {
            const client = setupRtm(userId, {
              ...info,
              cipherKey: DEFAULT_CIPHER_KEY,
            });
            return { ...client, userId };
          })
        );
      });
    });
  };
  useEffect(() => {
    const list = DEFAULT_USERS.map(user => {
      const userId = `${user}_${generateStr(4)}`;
      const client = setupRtm(userId);
      return { ...client, userId };
    });
    setRtmClientList(list);
  }, []);

  const logout = useCallback(
    (userId: string, rtm: RTMClient) => async () => {
      try {
        await rtm.logout();
        setRtmClientList(prev => prev.filter(item => item.userId !== userId));
      } catch (error) {
        console.log(error);
      }
    },
    []
  );

  const addRtm = async () => {
    const usedUser = rtmClientList.map(item => item.userId?.split('_')?.[0]);
    const user = USERS.find(item => !usedUser.includes(item));
    const userId = `${user}_${generateStr(4)}`;
    const client = setupRtm(userId, clientSettingRef.current);
    setRtmClientList(prev => [...prev, { ...client, userId }]);
  };

  const updateChannelInfo = (values: ChannelInfo) => {
    if (JSON.stringify(values) === JSON.stringify(channelInfo)) return;
    setChannelInfo(values);
  };

  const updateClient =
    (userId: string, rtm: RTMClient) =>
    async (username: string, cipherKey: string) => {
      try {
        await rtm.logout();
        setRtmClientList(prev =>
          prev.map(item =>
            item.userId === userId ? { rtm: null, userId } : item
          )
        );
        requestAnimationFrame(() => {
          const client = setupRtm(username, {
            ...DEFAULT_RTM_CONFIG,
            cipherKey,
          });
          setRtmClientList(prev =>
            prev.map(item =>
              item.userId === userId ? { ...client, userId: username } : item
            )
          );
        });
      } catch (error) {
        console.log(error);
      }
    };
  return (
    <ConfigProvider theme={theme}>
      <div className="layout-container">
        <div className="header-container">
          <img src={logo} height={'40px'} width={'191px'} />
          <div className="avatar-container">
            <Select
              variant="filled"
              dropdownStyle={{ textAlign: 'right' }}
              disabled
              style={{ width: 246, marginRight: 28 }}
              defaultValue={'rtm'}
              options={[{ label: 'RTM Debug Console', value: 'rtm' }]}
            />
            <img src={avatar} height={48} width={48} />
          </div>
        </div>
        <div className="content-container">
          <div className="sider-container">
            <ClientSetting update={update} />
            <ChannelSetting
              channelInfo={channelInfo}
              updateChannelInfo={updateChannelInfo}
            />
            <QueryBox
              channelInfo={channelInfo}
              appId="cd1f455efba8496caef1db648f0849b1"
            />
          </div>
          <div className="clients-container">
            {rtmClientList.map(({ userId, rtm, ...left }) => {
              return rtm ? (
                <ClientCard
                  client={{ rtm, username: userId, ...left }}
                  key={userId}
                  channelInfo={channelInfo}
                  logout={logout(userId, rtm)}
                  updateClient={updateClient(userId, rtm)}
                />
              ) : (
                <></>
              );
            })}
            {rtmClientList.length < 3 && (
              <div className="client-card add-card">
                <div onClick={addRtm}>
                  <img src={add} />
                  <span>新建客户端</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </ConfigProvider>
  );
}

const root = createRoot(document.getElementById('root'));

root.render(<App />);
