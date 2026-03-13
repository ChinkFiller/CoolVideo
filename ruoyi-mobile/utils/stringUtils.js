
export function formatTime(time) {
    if (!time) return '';
    const date = new Date(time);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
}

export function formatWatchTime(seconds) {
    if (!seconds) return '00:00'
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds
        .toString()
        .padStart(2, '0')}`
}


export function getVideoState(stateCode,part){
    if (stateCode==='0'){
        return `${part}集全`
    }else if (stateCode==='1'){
        return `已更新至${part}集`
    }else if (stateCode==='2'){
        return `停更至${part}`
    }else{
        return `未知`
    }
}


export function createIterator(array) {
    let index = 0;
    return {
        next: function () {
            if (index < array.length) {
                return { value: array[index++], done: false };
            } else {
                return { value: undefined, done: true };
            }
        }
    };
}
