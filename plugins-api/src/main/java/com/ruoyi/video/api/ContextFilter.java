package com.ruoyi.video.api;

import org.pf4j.ExtensionPoint;

public interface ContextFilter extends ExtensionPoint {

    /** 违规文字过滤器
     * @param content 需要过滤的内容
     * @return true:违规 false:正常
     * **/
    Boolean doCommentFilter(String content);
}
