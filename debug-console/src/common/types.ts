export interface ChannelInfo {
  msgChannelName: string;
  streamChannelName: string;
  topic: string;
}

export type ChannelType = 'MESSAGE' | 'STREAM';
