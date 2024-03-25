import React from 'react';
import { Form } from 'antd';
import { SettingContainer } from '../common/setting-container';
import { ChannelInfo } from '../../common/types';
import { SettingForm } from '../common/setting-form';
import { FormButton } from '../common/button';

export const ChannelSetting: React.FC<{
  channelInfo: ChannelInfo;
  updateChannelInfo: (values: ChannelInfo) => void;
}> = ({ channelInfo, updateChannelInfo }) => {
  const [form] = Form.useForm();

  const update = () => {
    const values = form.getFieldsValue();
    updateChannelInfo(values);
  };
  return (
    <SettingContainer title="频道设置">
      <SettingForm
        form={form}
        initialValues={channelInfo as never}
        formItems={[
          {
            label: 'Message Channel Name',
            name: 'msgChannelName',
            type: 'input',
            props: { width: 152 },
          },
          {
            label: 'Stream Channel Name',
            name: 'streamChannelName',
            type: 'input',
            props: { width: 152 },
          },
          {
            label: 'Topic Name',
            name: 'topic',
            type: 'input',
            props: { width: 152 },
          },
        ]}
      />
      <div style={{ textAlign: 'right' }}>
        <FormButton onClick={update}>更新频道配置</FormButton>
      </div>
    </SettingContainer>
  );
};
