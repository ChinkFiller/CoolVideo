package com.ruoyi.common.enums;

import lombok.Getter;

@Getter
public enum ResultCodes {
    //通用
    UNKNOWN_ERROR(-1, "未知错误"),
    OPTION_ERROR(-1, "操作失败"),
    ERROR(500, "服务器扛下了太多T_T"),
    UPLOAD_EMPTY_ERROR(400, "发送请求参数错误"),
    BAD_REQUEST(400, "发送请求数据异常"),
    SIGN_ERROR(403, "签名错误"),
    PERMISSION_ERROR(403, "权限不足"),
    NOT_FOUND(404,"没有找到"),
    METHOD_NOT_ALLOW(405, "不支持的访问方法"),
    MERGE_CHUNK_ERROR(500, "合并文件区块失败"),

    //视频
    GET_VIDEO_ERROR(-100001, "视频地址解析失败"),
    AGREE_FAIL(-100002, "点赞失败"),
    DISAGREE_FAIL(-100003, "取消点赞失败"),
    AGREE_REPEAT(-100004, "该视频已点赞"),

    //图片
    IMG_TOKEN_ERROR(-100002, "参数错误"),
    IMG_SAVA_ERROR(-100001, "图片保存失效"),

    //用户
    USER_NOT_FOUND(-200001,"用户不存在" ),
    USER_TOKEN_ERROR(-200002,"密钥失效" ),
    USER_PERMISSION_ERROR(-200003,"用户权限不足" ),
    USER_PASSWORD_ERROR(-200004,"账号或密码错误" ),
    SUBSCRIBE_FAIL(-20005,"订阅视频失败" ),
    SUBSCRIBE_REPEAT(-20006,"该视频已订阅" ),
    SUBSCRIBE_CANCEL_ERROR(-20007,"取消订阅失败" ),
    USER_REPEAT(-200005,"该账号已存在" ),
    VERIFY_CODE_ERROR(-200006, "验证码错误"),
    REGISTER_FAIL(-200007,"注册失败" ),
    USER_DISABLED(-200008,"该账号已被禁用"),

    //评论
    COMMENT_NO_FOUND(-300001,"评论不存在" ),
    COMMENT_AGREE_REPEAT(-300002,"该评论已点赞" ),
    COMMENT_AGREE_FAIL(-300003,"点赞失败" ),
    COMMENT_DISAGREE_FAIL(-300004,"取消点赞失败" ),
    COMMENT_REPEAT(-300005,"该评论已存在" ),
    USER_COMMENT_PROHIBIT(-300006,"你已被禁止评论" ),
    VIDEO_COMMENT_PROHIBIT(-300007, "该视频已被禁止评论"),
    COMMENT_CONTAIN_PROHIBIT_WORD(-300008, "评论包含敏感词汇"),

    //请求
    AJAX_ERROR(-400001,"请求失败" ),
    AJAX_TOKEN_ERROR(-400002,"请求TOKEN无效" ),
    AJAX_REPEAT(-400003,"请求重复" ),

    //邮件
    MAIL_SEND_ERROR(-500001,"邮件发送失败" ),
    MAIL_PARAMA_ERROR(-500002,"发送人,接收人,主题,内容均不可为空"),

    // 系统App
    APP_STOP_ERROR(-600001,"该App已停用，请联系系统管理员"),
    APP_NOT_EXIST_ERROR(-600001,"该App不存在，请检查AppID是否正确"),

    // 插件系统
    PLUGIN_DOWNLOAD_ERROR(-700001,"插件下载失败"),
    PLUGIN_NOT_FOUND(-700002, "该功能插件不存在");
    private int code;
    private String msg;

    ResultCodes(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
