import React from 'react';
import {
  AutoComplete,
  Col,
  Form,
  FormInstance,
  FormRule,
  Input,
  Row,
  Select,
  Switch,
} from 'antd';
import FormItem from 'antd/es/form/FormItem';

const { TextArea } = Input;
interface FormItem {
  type: 'input' | 'select' | 'textarea' | 'switch' | 'autoComplete' | 'custom';
  name: string;
  label: string;
  props?: any;
  rules?: FormRule[];
  span?: number;
  labelStyle?: any;
  component?: React.ReactNode;
}

interface SettingFormProps {
  form: FormInstance;
  formItems: FormItem[];
  initialValues?: Record<string, unknown>;
  formItemLayout?: { labelCol: { span: number }; wrapperCol: { span: number } };
}

export const SettingForm: React.FC<SettingFormProps> = ({
  form,
  formItems,
  initialValues,
  formItemLayout,
}) => {
  const itemRender = (item: FormItem) => {
    const { type, name, rules } = item;
    const { width, ...props } = item.props || {};
    switch (type) {
      case 'input':
        return (
          <FormItem name={name} rules={rules}>
            <Input variant="filled" style={{ width }} {...props} />
          </FormItem>
        );
      case 'select':
        return (
          <FormItem name={name}>
            <Select
              variant="filled"
              style={{ width }}
              dropdownStyle={{ textAlign: 'right' }}
              getPopupContainer={triggerNode => triggerNode.parentElement}
              {...props}
            />
          </FormItem>
        );
      case 'autoComplete':
        console.log('autoComplete', props);
        return (
          <FormItem name={name}>
            <AutoComplete
              variant="filled"
              style={{ width }}
              getPopupContainer={triggerNode => triggerNode.parentElement}
              {...props}
            />
          </FormItem>
        );
      case 'textarea':
        return (
          <FormItem name={name}>
            <TextArea />
          </FormItem>
        );
      case 'switch':
        return (
          <FormItem valuePropName="checked" name={name}>
            <Switch {...props} />
          </FormItem>
        );
      case 'custom':
        return item.component;
      default:
        return null;
    }
  };
  return (
    <Form
      form={form}
      initialValues={initialValues}
      {...formItemLayout}
      colon={false}
    >
      <Row>
        {formItems.map((item: FormItem) => (
          <Col
            span={item.span || 24}
            key={item.name}
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <label
              style={{
                fontWeight: 100,
                color: '#4C4949',
                marginBottom: 20,
                ...item.labelStyle,
              }}
            >
              {item.label}
            </label>
            {itemRender(item)}
          </Col>
        ))}
      </Row>
    </Form>
  );
};
