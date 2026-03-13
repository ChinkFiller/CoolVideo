export function goToVideoDetail(id) {
    uni.navigateTo({
        url: '/pages/detail/detail?id=' + id
    })
}

export function goToVideoPlayer(id) {
    uni.navigateTo({
        url: '/pages/player/player?id=' + id
    })
}


export function back(){
    uni.navigateBack({})
}


