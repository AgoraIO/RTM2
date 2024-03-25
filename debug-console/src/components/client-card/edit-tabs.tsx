import { Button, Form, Input, Space } from 'antd';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { SettingForm } from '../common/setting-form';
import { RTMClient } from 'agora-rtm';
import { ChannelInfo, ChannelType } from '../../common/types';
import refresh from 'assets/token-refresh.png';

interface SetupFormProps {
  client: {
    rtm: RTMClient;
    username: string;
    token?: string;
    encrypt?: boolean;
    cipherKey?: string;
  };
  userStates: string;
  editExit: () => void;
  updateClient: (username: string, cipherKey: string) => Promise<void>;
  updateState: (state: string) => void;
}
export const SetupForm: React.FC<SetupFormProps> = ({
  userStates,
  editExit,
  updateClient,
  client: { username, rtm, token, cipherKey, encrypt },
  updateState,
}) => {
  const [newToken, setNewToken] = useState<string>('');
  const [states, setStates] = useState<string>(userStates);
  const [user, setUser] = useState<string>(username);
  const [key, setKey] = useState<string>(cipherKey);
  const renewToken = () => {
    try {
      rtm.renewToken(newToken);
    } catch (error) {
      console.log(error);
    }
  };
  const update = () => {
    updateClient(user, key);
    editExit();
  };
  const setState = async () => {
    await updateState(states);
    editExit();
  };
  return (
    <div>
      <div className="flex-container">
        <label className="form-label">User ID</label>
        <Input
          style={{ width: 320 }}
          value={user}
          onChange={e => setUser(e.target.value)}
        />
      </div>
      {encrypt && (
        <div className="flex-container">
          <label className="form-label">Cipher Key</label>
          <Input
            style={{ width: 320 }}
            value={key}
            onChange={e => setKey(e.target.value)}
          />
        </div>
      )}
      <div className="flex-container">
        <label className="form-label">User States</label>
        <Input
          style={{ width: 320 }}
          value={states}
          onChange={e => setStates(e.target.value)}
          addonAfter={<img onClick={setState} src={refresh} />}
        />
      </div>
      <div className="flex-container">
        <label className="form-label">Token</label>
        <Input
          disabled={!token}
          style={{ width: 320 }}
          value={token}
          onChange={e => setNewToken(e.target.value)}
          addonAfter={<img onClick={renewToken} src={refresh} />}
        />
      </div>
      <Space size={13} style={{ width: '100%', justifyContent: 'flex-end' }}>
        <Button type="primary" ghost onClick={editExit}>
          取消
        </Button>
        <Button type="primary" ghost onClick={update}>
          更新客户端
        </Button>
      </Space>
    </div>
  );
};

const StorageForm: React.FC<{
  rtm: RTMClient;
  channelInfo: ChannelInfo;
  channelType: ChannelType;
}> = ({ rtm, channelInfo, channelType }) => {
  const [form] = Form.useForm();
  useEffect(() => {
    const { msgChannelName, streamChannelName } = channelInfo;
    const type = form.getFieldValue('channelType') || channelType;
    switch (type) {
      case 'MESSAGE':
        form.setFieldsValue({ channelType: type, channelName: msgChannelName });
        break;
      case 'STREAM':
        form.setFieldsValue({
          channelType: type,
          channelName: streamChannelName,
        });
        break;
    }
  }, [channelInfo, channelType]);
  const setMetadata = () => {
    form.validateFields().then(values => {
      const { channelName, channelType, metadata, options } = values;
      rtm.storage.setChannelMetadata(
        channelName,
        channelType,
        metadata,
        options
      );
    });
  };
  const updateMetadata = () => {
    form.validateFields().then(values => {
      const { channelName, channelType, metadata, options } = values;
      rtm.storage.updateChannelMetadata(
        channelName,
        channelType,
        metadata,
        options
      );
    });
  };
  const removeMetadata = () => {
    form.validateFields().then(values => {
      const { channelName, channelType, options } = values;
      rtm.storage.removeChannelMetadata(channelName, channelType, options);
    });
  };
  const getMetadata = () => {
    form.validateFields().then(values => {
      const { channelName, channelType } = values;
      rtm.storage.getChannelMetadata(channelName, channelType);
    });
  };
  return (
    <div>
      <SettingForm
        form={form}
        formItems={[
          {
            label: 'Channel Name',
            type: 'input',
            name: 'channelName',
            props: { width: 320 },
          },
          {
            label: 'Channel Type',
            type: 'select',
            name: 'channelType',
            props: {
              width: 320,
              options: [
                { label: 'MESSAGE', value: 'MESSAGE' },
                { label: 'STREAM', value: 'STREAM' },
              ],
            },
          },
          {
            label: 'Metadata',
            type: 'input',
            name: 'metadata',
            props: { width: 320 },
          },
          {
            label: 'Options',
            type: 'input',
            name: 'options',
            props: { width: 320 },
          },
        ]}
      />
      <div className="flex-container">
        <Button type="primary" ghost onClick={setMetadata}>
          设置属性
        </Button>
        <Button type="primary" ghost onClick={updateMetadata}>
          更新属性
        </Button>
        <Button type="primary" ghost onClick={removeMetadata}>
          删除属性
        </Button>
        <Button type="primary" ghost onClick={getMetadata}>
          获取属性
        </Button>
      </div>
    </div>
  );
};

const LockForm: React.FC<{
  rtm: RTMClient;
  channelInfo: ChannelInfo;
  channelType: ChannelType;
}> = ({ rtm, channelInfo, channelType }) => {
  const [lockList, setLockList] = useState([]);
  const [form] = Form.useForm();
  useEffect(() => {
    const { msgChannelName, streamChannelName } = channelInfo;
    const type = form.getFieldValue('channelType') || channelType;
    switch (type) {
      case 'MESSAGE':
        form.setFieldsValue({ channelType: type, channelName: msgChannelName });
        break;
      case 'STREAM':
        form.setFieldsValue({
          channelType: type,
          channelName: streamChannelName,
        });
        break;
    }
  }, [channelInfo, channelType]);
  const getLock = () => {
    form.validateFields().then(async (values: any) => {
      const { channelName, channelType } = values;
      try {
        const res = await rtm.lock.getLock(channelName, channelType);
        const { lockDetails } = res;
        setLockList(
          lockDetails.map(({ lockName: label }) => ({ label, value: label }))
        );
      } catch (error) {
        console.log(error);
      }
    });
  };
  const setLock = () => {
    form.validateFields().then((values: any) => {
      const { channelName, channelType, lock, options } = values;
      rtm.lock.setLock(channelName, channelType, lock, options);
    });
  };
  const removeLock = () => {
    form.validateFields().then((values: any) => {
      const { channelName, channelType, lock } = values;
      rtm.lock.removeLock(channelName, channelType, lock);
    });
  };
  const acquireLock = () => {
    form.validateFields().then((values: any) => {
      const { channelName, channelType, lock, options } = values;
      rtm.lock.acquireLock(channelName, channelType, lock, options);
    });
  };
  const releaseLock = () => {
    form.validateFields().then((values: any) => {
      const { channelName, channelType, lock } = values;
      rtm.lock.releaseLock(channelName, channelType, lock);
    });
  };
  const revokeLock = () => {
    form.validateFields().then((values: any) => {
      const { channelName, channelType, lock, owner } = values;
      rtm.lock.revokeLock(channelName, channelType, lock, owner);
    });
  };
  return (
    <div>
      <SettingForm
        form={form}
        formItems={[
          {
            label: 'Channel Name',
            type: 'input',
            name: 'channelName',
            props: { width: 320 },
          },
          {
            label: 'Channel Type',
            type: 'select',
            name: 'channelType',
            props: {
              width: 320,
              options: [
                { label: 'MESSAGE', value: 'MESSAGE' },
                { label: 'STREAM', value: 'STREAM' },
              ],
            },
          },
          {
            label: 'Lock Name',
            type: 'autoComplete',
            name: 'lock',
            props: { onFocus: getLock, options: lockList, width: 100 },
            span: 12,
          },
          {
            label: 'Owner',
            type: 'input',
            name: 'owner',
            labelStyle: { textAlign: 'center', width: 100 },
            span: 12,
          },
          {
            label: 'Options',
            type: 'input',
            name: 'options',
            props: { width: 320 },
          },
        ]}
      />
      <div className="flex-container">
        <Button type="primary" ghost onClick={setLock}>
          设置锁
        </Button>
        <Button type="primary" ghost onClick={acquireLock}>
          获取锁
        </Button>
        <Button type="primary" ghost onClick={releaseLock}>
          释放锁
        </Button>
        <Button type="primary" ghost onClick={revokeLock}>
          撤销锁
        </Button>
        <Button type="primary" ghost onClick={removeLock}>
          删除锁
        </Button>
      </div>
    </div>
  );
};

interface EditTabProps {
  client: {
    rtm: RTMClient;
    username: string;
    token?: string;
    encrypt?: boolean;
    cipherKey?: string;
  };
  userStates: string;
  channelInfo: ChannelInfo;
  channelType: ChannelType;
  editExit: () => void;
  updateClient: (username: string, cipherKey: string) => Promise<void>;
  updateState: (state: string) => void;
}

export const EditTabs: React.FC<EditTabProps> = ({
  userStates,
  channelInfo,
  channelType,
  editExit,
  updateClient,
  client,
  updateState,
}) => {
  const { rtm } = client;
  const [tab, setTab] = useState('SETUP');
  const ref = useRef<HTMLDivElement | null>(null);
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        editExit();
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);
  const tabComp = useMemo(() => {
    switch (tab) {
      case 'SETUP':
        return (
          <SetupForm
            client={client}
            userStates={userStates}
            editExit={editExit}
            updateClient={updateClient}
            updateState={updateState}
          />
        );
      case 'STORAGE':
        return (
          <StorageForm
            rtm={rtm}
            channelInfo={channelInfo}
            channelType={channelType}
          />
        );
      case 'LOCK':
        return (
          <LockForm
            rtm={rtm}
            channelInfo={channelInfo}
            channelType={channelType}
          />
        );
    }
  }, [tab]);
  return (
    <div className="edit-tabs-container" ref={ref}>
      <div className="edit-tabs">
        {['SETUP', 'STORAGE', 'LOCK'].map(item => (
          <span
            key={item}
            onClick={() => setTab(item)}
            className={tab === item ? 'focused' : ''}
          >
            {item}
          </span>
        ))}
      </div>
      <div className="edit-form-container">{tabComp}</div>
    </div>
  );
};
