import React from 'react';

interface SettingContainerProps {
  title: string;
  children: React.ReactNode;
}

export const SettingContainer: React.FC<SettingContainerProps> = ({
  title,
  children,
}) => {
  return (
    <div className="setting-container">
      <span className="title">{title}</span>
      {children}
    </div>
  );
};
