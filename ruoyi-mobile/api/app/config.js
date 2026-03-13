import request from "../../utils/request";


export function getConfig(key){
    return request.get(`/system/config/configKey/${key}`)
}
