export function formatDate(dateString) {
    if (!dateString) return '未知';
    const date = new Date(dateString);
    if (isNaN(date)) return ''; // 防止无效日期

    const pad = (n) => String(n).padStart(2, '0');

    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
