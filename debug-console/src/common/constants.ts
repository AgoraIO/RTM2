export const USERS = ['Tony', 'Lily', 'Jerry'];

export const DEFAULT_USERS = USERS.slice(0, 1);

export const DEFAULT_CIPHER_KEY = '';

export const DEFAULT_RTM_CONFIG = {
  appId: '',
  appCertificate: false,
  encrypt: false,
  cipherKey: DEFAULT_CIPHER_KEY,
  logUpload: true,
  logLevel: 'debug',
  cloudProxy: true,
  presenceTimeout: 10,
  process: '测试阶段',
};

export const DEFAULT_CHANNEL_INFO = {
  msgChannelName: 'ms_channel',
  streamChannelName: 'st_channel',
  topic: 'first_topic',
};
