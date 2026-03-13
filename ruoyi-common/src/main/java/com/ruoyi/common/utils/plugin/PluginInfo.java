package com.ruoyi.common.utils.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PluginInfo {
       String pluginId;
       String version;
       String provider;
       String description;
       Boolean state;
       String path;
}
