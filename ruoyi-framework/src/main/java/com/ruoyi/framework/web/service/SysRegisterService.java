package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.domain.model.RegisterVerifyCodeBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ResultCodes;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.mail.MailUtils;
import com.ruoyi.common.utils.sign.Md5Utils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    private ISysConfigService configService;

    @Lazy
    @Autowired
    MailUtils mailUtils;

    @Value("${spring.mail.username}")
    String hostEmail;

    @Value("${ruoyi.name}")
    String appName;

    @Value("${ruoyi.logo}")
    String appIcon;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setEmail(username);


        // 邮箱注册检查
        if (StringUtils.isNotEmpty(username) && !userService.checkEmailUnique(sysUser)){
            return "注册邮箱'" + username + "'失败，邮箱账号已存在";
        }

        // 验证邮箱验证码
        String code=redisCache.getCacheObject(username);
        // 这个账号的验证码过期或者不符
        if (code==null||!code.equals(registerBody.getCode())){
            return ResultCodes.VERIFY_CODE_ERROR.getMsg();
        }

        // 删除已使用的验证码
        redisCache.deleteObject(username);

        //设置为普通的用户
        sysUser.setRoleId(100L);
        sysUser.setNickName("用户-"+ UUID.getUUIDWithOutLine().substring(0,8));
        sysUser.setUserName(username);
        sysUser.setPwdUpdateDate(DateUtils.getNowDate());
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        boolean regFlag = userService.registerUser(sysUser);
        if (!regFlag) {
            return  "注册失败";
        }else {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            return "";
        }
    }

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }

    public void getEmailVerifyCode(RegisterVerifyCodeBody body) {
        String sign=body.getSign(), email=body.getUsername(), code=body.getCode(),uuid=body.getUuid();
        if (!sign.equals(Md5Utils.hash(email+code))){
            throw new ServiceException(ResultCodes.SIGN_ERROR);
        }
        if (configService.selectCaptchaEnabled()) {
            // 验证动态人机验证码
            validateCaptcha(code,uuid);
        }

        //若验证码没有过期，直接返回
        if (redisCache.getCacheObject(email)!=null){return;}
        //若验证码过期，重新生成
        String verifyCode= EncryptUtils.generateRandomCode();

        // 将验证码存入redis，并设置超时时间为10分钟
        redisCache.setCacheObject(email,verifyCode,10, TimeUnit.MINUTES);

        Context c=new Context();
        c.setVariable("logoUrl",appIcon);
        c.setVariable("appName",appName);
        c.setVariable("code",verifyCode);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        c.setVariable("date",f.format(LocalDateTime.now()));
        String content=templateEngine.process("MailVerifyCodeTemplate.html",c);
        // 发送邮件验证码
        System.out.println("发送邮件验证码: "+verifyCode);
        try {
            // 开启线程发送，异步响应
            new Thread(()->{
                mailUtils.sendHtml(hostEmail,email,"验证码",content);
            }).start();
        }catch (Exception e){
            throw new RuntimeException(ResultCodes.MAIL_SEND_ERROR.getMsg());
        }
    }

    public void findPassword(String username){
        SysUser user=userService.selectUserByUserName(username);
        if (user==null){
            throw new ServiceException(ResultCodes.USER_NOT_FOUND);
        }
        if (user.getStatus().equals("1")){
            throw new ServiceException(ResultCodes.USER_DISABLED);
        }

        user.setPassword(SecurityUtils.encryptPassword("123456"));
        if (userService.resetPwd(user)==0){
            throw new ServiceException(ResultCodes.UNKNOWN_ERROR);
        }
    }
}
