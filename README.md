


## CoolVideo(库视频)是什么?
* 一款前端使用uniapp，后端使用springboot的番剧共享平台
* 一款开源的追番软件
* 自动爬取全网资源，可自己部署服务器使用。
* 支持外链播放和本地资源播放

## App端截图
<img src="Images/index.png" style="width: 24%"><img src="Images/player.png" style="width: 24%"><img src="Images/weeks.png" style="width: 24%"><img src="Images/drew.png" style="width: 24%">

## CoolVideo(库视频)有哪些功能？
* 支持多种`媒体方式`
    *  可以快速把一个视频上传到服务器，让其他的用户观看
    *  可以解析外链视频,并把解析的链接缓存到服务器
    *  可以开启爬虫给程序，从别的网站获取信息并缓存
* 更完善的`功能`
    *  影视周期表，结合爬虫可以快速获取每周的更新
    *  历史记录，点赞，追番等实时保存到服务器
    *  内置邮箱注册系统，方便创建账号
    *  无内置广告，可以在购买服务器后轻松打造属于自己的追番软件
    *  系统内置IP拦截器，防止被恶意爬虫爆破
    *  支持弹幕功能，以及完善的举报处理机制

## 服务端部署
* 确保服务器的JDK版本>=17
* 将编译好或下载的jar包上传到服务器
  * 运行这个jar包
  ```shell
    java -jar CoolVideo.jar
  ```
  * 第一次运行会生成配置文件application.properties
    <br><br>
    开始写入配置文件(此时也可以直接运行，只不过邮箱注册将会无法使用)
    <br><br>
    邮件注册配置(自己使用的可以不用管这个)
  ```properties
    #你的邮件服务器地址
    mail-host=stmp.qq.com
    #邮件地址
    mail-username=10000000@qq.com
    #邮件的授权密码，不是你的邮箱密码，这个是需要申请的
    mail-password=AABBCCDDEEFF
  ```
  * 数据库配置(用于配置视频信息存储的)
  * `mysql`模式需要服务器搭建mysql数据库服务,用于视频数据量较大，且公开服务使用的情况
  * `sqlite`模式使用的轻量级数据库，性能有限，适合小规模使用或者个人使用
* 后台登录
  * 系统会默认开启一个后台地址，登录成功后可以以图形化的界面管理和上传视频资源
  * 后台系统比较简陋，目前周期表自定义修改没有写，后面会补上的(毕竟有爬虫自动更新了)
  * `注意`第一次运行数据库没有数据，需要手动的去控制台执行initdb指令初始化数据(没有这个初始化的话无法登录后台)
  * `注意`初始化成功后默认带一个账号为admin，密码为1234的管理员用户，可用于登录后台
  * 忘记管理员密码时可以去控制台用alluser指令来查看所有的账户密码
* 爬虫系统
  * 后台管理界面会带一个爬虫管理面板，实时显示爬虫程序的日志以及运行状态，还可以网页端启动与关闭
  * `注意` 目前并没有采用py脚本直接运行，而是采用的编译打包，linux平台可能会有兼容性问题
* IP拦截器
  * 系统内置一个访问频率过滤器，防止被恶意爆破
  * 具体设置在配置文件中有详情
  * 这个设置频率最大是1，即60秒内一个ip可以访问60次，超过会被BanIP,计算是`60*频率=每60秒可以访问的次数`
  * 这个防护是不可通过配置文件关闭的，可以自己重新编译一个服务端把拦截器删除
* 动态Banner和快速功能
  * 后台管理可以实时修改你的首页的banner的内容以及快速功能的设置
  * 目前banner支持影视推广，即把某个番剧单独拿出来，主页点击即可跳转
  * 目前显示公告和通知的方式只有跳转显示一个网页，这个网页不内置，需要单独设置。
* 异常弹幕处理
  * 用户举报后，举报信息便会存到本地，管理员登录后台便可轻松处理举报信息

## 前端手机APP设置

* 直接下载release的apk文件
  * 进入App，点击左上角的更多按钮
  * 点击设置
  * 把开发模式打开，即可自己设置服务器的地址
  * 注意格式问题,必须以`http://`或者`https://`开头
* 编译安装
  * 详见uniapp的打包安装 [Uniapp多平台打包](https://uniapp.dcloud.net.cn/tutorial/build/SafePack.html)

## 关于违规言论处理问题
* 由于开放评论会涉及有违规言论等问题，目前仅设置了弹幕功能和弹幕违规处理举报系统
* 设置了关键词拦截，对于包含关键词的都会被拦截
* 评论功能预留了接口，但是没有写，后期考虑加上

## 关于没有服务器的如何使用
* 目前本地安装软件时会默认提供一个可用的链接，但是受限于种种问题，使用这个源的将无法使用评论以及弹幕(以后也不会有)
* 默认源链接保持实时更新，但是默认源服务器规模比较小，建议还是后期购置一台服务器，使用专属源。


## 关于修改发行以及商用等问题
* 本软件开源，可以用于商用以及第三方重新修改使用。
* 对于使用本模板自定义软件进行发表或传播违规违法内容的，与作者本人无关。
* 程序代码仅供学习交流使用，不存在传播违法信息的情况

## 有问题反馈
在使用中有任何问题，欢迎反馈。

* 邮件(hongbai502@gamil.com)