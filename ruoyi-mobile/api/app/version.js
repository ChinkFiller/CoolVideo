import request from '@/utils/request';

export function checkVersion(version, plt, appid) {
    return request.get('/common/client/config', { version, plt, appid});
}
