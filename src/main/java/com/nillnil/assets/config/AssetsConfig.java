package com.nillnil.assets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WuHongcheng
 * 2020/1/4.
 */
@ConfigurationProperties("assets")
@Data
public class AssetsConfig {

    /**
     * 源路径
     */
    private String source;

    /**
     * 保存路径
     */
    private String target = "assets";

    /**
     * 是否覆盖掉target文件夹原有的文件
     */
    private boolean overcover = false;

    /**
     * 是否将配置保存成yaml
     */
    private boolean storeYaml = true;

    /**
     * 保存成yaml的路径，可以包括文件名
     */
    private String yamlPath = "pubspec.yaml";

    /**
     * 保存成yaml时是否忽略1.0x的
     */
    private boolean ignoreOnex = true;
}
