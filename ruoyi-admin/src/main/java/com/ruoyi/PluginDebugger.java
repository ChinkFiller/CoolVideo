package com.ruoyi;

import org.pf4j.ExtensionPoint;
import org.pf4j.PluginManager;

import java.util.List;

public class PluginDebugger {

    public static void debugExtensions(PluginManager pluginManager) {
        System.out.println("=== 扩展点调试开始 ===");

        // 打印所有 ExtensionPoint 实现类
        List<ExtensionPoint> exts = pluginManager.getExtensions(ExtensionPoint.class);
        for (ExtensionPoint ext : exts) {
            Class<?> clazz = ext.getClass();
            ClassLoader cl = clazz.getClassLoader();

            System.out.println("扩展实现类: " + clazz.getName());
            System.out.println("  来自类加载器: " + cl);

            // 打印该实现类实现的接口
            for (Class<?> intf : clazz.getInterfaces()) {
                System.out.println("    实现接口: " + intf.getName()
                        + " | 类加载器: " + intf.getClassLoader());
            }
        }

        // 打印主程序里用的 PlayerInfoService 接口的类加载器
        try {
            Class<?> playerService = Class.forName("com.ruoyi.video.api.PlayerService"); // 换成你真实的包名
            System.out.println("主程序里的 PlayerInfoService 类加载器: " + playerService.getClassLoader());
        } catch (ClassNotFoundException e) {
            System.err.println("主程序找不到 PlayerInfoService");
        }

        System.out.println("=== 扩展点调试结束 ===");
    }
}
