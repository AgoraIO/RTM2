import React from 'react';
import { Button, ButtonProps } from 'antd';

export const HeaderButton = ({ style, ...props }: ButtonProps) => {
  return (
    <Button
      type="primary"
      ghost
      style={{
        borderRadius: 5,
        fontWeight: 300,
        padding: '6px 7px',
        marginLeft: 'auto',
        ...style,
      }}
      {...props}
    />
  );
};
