package com.nillnil.assets;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

/**
 * @author WuHongcheng
 * 2020/1/4.
 */
@Data
@AllArgsConstructor
public class AssetsFile {

    private File file;

    private AssetsRatio assetsRatio;
}
