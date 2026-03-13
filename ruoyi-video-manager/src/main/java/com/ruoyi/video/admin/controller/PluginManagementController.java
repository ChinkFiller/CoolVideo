package com.ruoyi.video.admin.controller;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.plugin.PluginInfo;
import com.ruoyi.common.utils.plugin.PluginLoader;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.video.admin.service.PluginService;
import com.ruoyi.video.api.PlayerService;
import com.ruoyi.video.api.PluginSettingGUI;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

//插件管理服务
@RestController
@RequestMapping("/system/plugin")
public class PluginManagementController extends BaseController {


    @Autowired
    private PluginManager pluginManager;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private PluginLoader pluginLoader;

    @Autowired
    ISysConfigService configService;


    // 如果解析器插件正在使用, 则删除解析器解析设置
    private void deleteParserIfParserUsing(String mode) {
        SysConfig config = configService.selectFullConfigByKey("video.parser.list");

        // 解析配置里面的对应的数据
        List<String> parserList = JSONArray.parseArray(config.getConfigValue(), String.class);

        // 删除里面的对应项
        parserList.remove(mode);

        // 更新配置
        config.setConfigValue(JSONArray.toJSONString(parserList));
        configService.updateConfig(config);
    }

    private void addParserIfParserCanUse(String mode) {
        SysConfig config = configService.selectFullConfigByKey("video.parser.list");
        // 解析配置里面的对应的数据
        List<String> parserList = JSONArray.parseArray(config.getConfigValue(), String.class);

        // 已加载的解析器类插件有这个插件,加入到末尾
        for (PluginInfo plugin:pluginLoader.getPluginInfosByClass(PlayerService.class) ){
            // 加入条件，插件id与新安装的插件id相同，其次是插件id不再解析列表中
            if (plugin.getPluginId().equals(mode) && !parserList.contains(mode)){
                parserList.add(mode);
            }
        }
        // 更新配置
        config.setConfigValue(JSONArray.toJSONString(parserList));
        configService.updateConfig(config);
    }


    //显示所有的插件
    @PreAuthorize("@ss.hasPermi('system:plugin:list')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size
    ) {
        return AjaxResult.success(pluginLoader.listPlugins(size,page));
    }


    //通过文件安装插件
    @PreAuthorize("@ss.hasPermi('system:plugin:install')")
    @PostMapping("/install")
    public AjaxResult save(
            @RequestParam("file") MultipartFile file
    ) {
        //文件保存路径
        String filePath="plugins/"+file.getOriginalFilename();
        File targetFile=new File(filePath);
        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();

            // 写入完成后再加载插件
            pluginManager.loadPlugin(targetFile.toPath());

            return AjaxResult.success("插件上传并加载成功");

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("插件安装失败：" + e.getMessage());
        }
    }



    //通过远程链接安装插件
    @PreAuthorize("@ss.hasPermi('system:plugin:install')")
    @GetMapping("/install")
    public AjaxResult install(
            @RequestParam("url") String url
    ) {
        String path = pluginService.downloadAndSavePlugin(url);
        pluginManager.loadPlugin(Paths.get(path));
        return AjaxResult.success();
    }


    //通过插件id卸载插件
    @PreAuthorize("@ss.hasPermi('system:plugin:uninstall')")
    @GetMapping("/uninstall/{pluginId}")
    public AjaxResult uninstall(
            @PathVariable("pluginId") String pluginId
    ) {
        // 卸载并删除插件
        pluginManager.deletePlugin(pluginId);
        // 重载插件实现
        pluginLoader.reload();
        // 如果该插件为解析器且设置了正在使用，更改配置清除使用状态
        this.deleteParserIfParserUsing(pluginId);
        // 检查是否有插件正在使用，设置为none
        return AjaxResult.success();
    }


    //通过插件id停用插件
    @PreAuthorize("@ss.hasPermi('system:plugin:stop')")
    @GetMapping("/stop/{pluginId}")
    public AjaxResult delete(
            @PathVariable("pluginId") String pluginId
    ) {
        //停用插件
        pluginManager.stopPlugin(pluginId);
        // 重载插件实现
        pluginLoader.reload();
        // 如果该插件为解析器且设置了正在使用，更改配置清除使用状态
        this.deleteParserIfParserUsing(pluginId);
        return AjaxResult.success();
    }


    //通过插件id启用插件
    @PreAuthorize("@ss.hasPermi('system:plugin:start')")
    @GetMapping("/start/{pluginId}")
    public AjaxResult start(
            @PathVariable("pluginId") String pluginId
    ) {
        //启用插件
        pluginManager.startPlugin(pluginId);
        // 重载插件实现
        pluginLoader.reload();

        // 如果该插件为解析器且设置了正在使用，将该解析id加入到解析器列表中去
        addParserIfParserCanUse(pluginId);
        return AjaxResult.success();
    }


    //通过插件id查询插件的设置
    @PreAuthorize("@ss.hasPermi('system:plugin:setting')")
    @GetMapping("/detail/setting/{pluginId}")
    public AjaxResult detailSetting(
            @PathVariable("pluginId") String pluginId
    ) {
        PluginSettingGUI pluginSettingGUI=pluginLoader.loadPluginById(pluginId, PluginSettingGUI.class);
        if (pluginSettingGUI!=null){
            return AjaxResult.success(pluginSettingGUI.getSettingPage());
        }else{
            return AjaxResult.error("插件不存在");
        }
    }

    //通过插件id更新插件的设置
    @PreAuthorize("@ss.hasPermi('system:plugin:setting')")
    @PutMapping("/update/setting/{pluginId}")
    public AjaxResult updateSetting(
            @PathVariable("pluginId") String pluginId,
            @RequestBody Map<String,Object> setting
    ) {
        PluginSettingGUI pluginSettingGUI=pluginLoader.loadPluginById(pluginId, PluginSettingGUI.class);
        if (pluginSettingGUI!=null){
            pluginSettingGUI.submitSetting(setting);
            // 重载解析器插件实现列表
            pluginLoader.reload();
            return AjaxResult.success();
        }else{
            return AjaxResult.error("插件不存在");
        }
    }
}
