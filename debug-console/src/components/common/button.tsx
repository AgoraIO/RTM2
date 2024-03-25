import React from 'react';
import { Button, ButtonProps } from 'antd';

export const FormButton: React.FC<ButtonProps> = ({ style, ...props }) => {
  return (
    <Button type="primary" ghost style={{ width: 152, ...style }} {...props} />
  );
};
