// TODO：可以修改为自己的用户列表
export const USERS = ['Tony', 'Lily', 'Jerry'];

export const DEFAULT_USERS = USERS.slice(0, 1);

export const DEFAULT_CIPHER_KEY = '';

export const DEFAULT_RTM_CONFIG = {
  // TODO：请替换为您自己的 Agora AppId
  appId: '',
  // TODO：使用token鉴权时请修改为true
  appCertificate: false,
  encrypt: false,
  cipherKey: DEFAULT_CIPHER_KEY,
  logUpload: true,
  logLevel: 'debug',
  cloudProxy: false,
  presenceTimeout: 10,
  process: '测试阶段',
};

export const DEFAULT_CHANNEL_INFO = {
  msgChannelName: 'ms_channel',
  streamChannelName: 'st_channel',
  topic: 'first_topic',
};
