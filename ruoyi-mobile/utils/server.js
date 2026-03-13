export function getServer(){
    const serverBaseUrl=uni.getStorageSync("SERVER_URL")
    return serverBaseUrl.charAt(serverBaseUrl.length-1)==='/'?serverBaseUrl.slice(0,-1):serverBaseUrl
}

