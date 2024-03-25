import React from 'react';
import { SettingContainer } from '../common/setting-container';
import { SettingForm } from '../common/setting-form';
import { FormButton } from '../common/button';
import { Form, Tag } from 'antd';
import { DEFAULT_RTM_CONFIG } from '../../common/constants';

interface ClientSettingProps {
  update: (info: any, isReset?: boolean) => void;
}

export const ClientSetting: React.FC<ClientSettingProps> = ({ update }) => {
  const [form] = Form.useForm();

  const resetClient = () => {
    form.setFieldsValue(DEFAULT_RTM_CONFIG);
    update(DEFAULT_RTM_CONFIG, true);
  };

  const updateClient = () => {
    update(form.getFieldsValue());
  };
  return (
    <SettingContainer title={'客户端设置'}>
      <SettingForm
        form={form}
        initialValues={DEFAULT_RTM_CONFIG}
        formItemLayout={{ labelCol: { span: 14 }, wrapperCol: { span: 8 } }}
        formItems={[
          {
            label: 'APP ID',
            name: 'appId',
            type: 'input',
            props: { width: 152, type: 'password' },
          },
          {
            label: 'APP 证书',
            type: 'switch',
            name: 'appCertificate',
            props: { disabled: true },
          },
          {
            label: '项目阶段',
            type: 'custom',
            name: 'process',
            component: <Tag>测试阶段</Tag>,
          },
          { label: '端侧加密', type: 'switch', name: 'encrypt' },
          { label: 'CloudProxy', type: 'switch', name: 'cloudProxy' },
          {
            label: 'Presence Timeout',
            type: 'input',
            name: 'presenceTimeout',
            props: { width: 152, type: 'number' },
          },
          {
            label: '日志等级',
            name: 'logLevel',
            type: 'select',
            props: {
              width: 152,
              options: [
                { label: 'DEBUG', value: 'debug' },
                { label: 'INFO', value: 'info' },
                { label: 'WARN', value: 'warn' },
                { label: 'ERROR', value: 'error' },
                { label: 'NONE', value: 'none' },
              ],
            },
          },
        ]}
      />
      <div
        style={{
          width: '100%',
          display: 'flex',
          justifyContent: 'space-between',
        }}
      >
        <FormButton onClick={updateClient}>更新客户端</FormButton>
        <FormButton onClick={resetClient}>复位所有客户端</FormButton>
      </div>
    </SettingContainer>
  );
};
