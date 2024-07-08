export const jsonToStr = (json: unknown) => JSON.stringify(json, null, 2);

// TODO：需修改为用户生成的token，token需匹配appId和userId，否则无法登录
export const generateToken = (appId: string, userId: string) => {
  switch (userId) {
    case 'Tony':
      return '';
    case 'Lily':
      return '';
    case 'Jerry':
      return '';
    default:
      return '';
  }
};

export function generateStr(n: number) {
  const chars = [
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
  ];
  let res = '';
  for (let i = 0; i < n; i++) {
    const id = Math.ceil(Math.random() * chars.length);
    if (chars[id] !== undefined) {
      res += chars[id];
    } else {
      i--;
    }
  }
  return res;
}
