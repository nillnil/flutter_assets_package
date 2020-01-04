package com.nillnil.assets;

import com.nillnil.assets.config.AssetsConfig;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WuHongcheng
 * 2020/1/4.
 */
@EnableConfigurationProperties({AssetsConfig.class})
@Slf4j
public class Main implements CommandLineRunner {

    @Autowired
    AssetsConfig config;

    File targetFile;

    List<String> list = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        try {
            String source = config.getSource();
            String target = config.getTarget();
            if (source == null) {
                log.error("assets.source can be not null.");
                return;
            }
            if (target == null) {
                target = "./assets";
            }
            File sourceFile = new File(source);
            if (!(sourceFile.isDirectory() && sourceFile.exists())) {
                log.error("source is not directory or is not exists, source = {}", source);
            }
            targetFile = new File(target);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            } else {
                if (config.isOvercover()) {
                    FileUtils.deleteDirectory(targetFile);
                    targetFile.mkdir();
                }
            }
            if (targetFile.isDirectory()) {
                initDirectory();
                packageWithDirectory(sourceFile, null);
                storeYaml();
                deleteEmptyDirectory();
            } else {
                log.error("target is not directory, target = {}", target);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void initDirectory() {
        for (AssetsRatio assetsRatio: AssetsRatio.values()) {
            File file = new File(targetFile, assetsRatio.getValue());
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    private void deleteEmptyDirectory() throws IOException {
        for (AssetsRatio assetsRatio: AssetsRatio.values()) {
            File file = new File(targetFile, assetsRatio.getValue());
            String[] files = file.list();
            if (files != null && files.length == 0) {
                FileUtils.deleteDirectory(file);
            }
        }
    }

    private void packageWithFile(File file, String parent) throws IOException {
        String name = file.getName();
        String path = null;
        if (parent != null) {
            path = File.separator + parent + File.separator;
        }
        AssetsFile assetsFile = getAssetsFile(name, path);
        File newFile = assetsFile.getFile();
        AssetsRatio assetsRatio = assetsFile.getAssetsRatio();
        log.info("Copying file to {} ...", newFile.getAbsolutePath());
        FileUtils.copyFile(file, newFile);
        addToList(newFile.getPath(), assetsRatio);
    }

    private AssetsFile getAssetsFile(String fileName, String path) {
        AssetsFile assetsFile = null;
        for (AssetsRatio assetsRatio: AssetsRatio.values()) {
            if (fileName.contains(assetsRatio.getSign())) {
                File file = new File(targetFile,assetsRatio.getValue() + (path != null ? path: ""));
                file = new File(file, fileName.replace(assetsRatio.getSign(), ""));
                assetsRatio = AssetsRatio.ONE_HELF;
                assetsFile = new AssetsFile(file, assetsRatio);
                break;
            }
        }
        // 1x
        if (assetsFile == null) {
            File file = new File(targetFile, path != null ? path + fileName: fileName);
            AssetsRatio assetsRatio = AssetsRatio.ONE;
            assetsFile = new AssetsFile(file, assetsRatio);
        }
        return assetsFile;
    }

    private void packageWithDirectory(File directory, String parent) throws IOException {
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    packageWithFile(file, parent);
                } else if (file.isDirectory()) {
                    packageWithDirectory(file, file.getName());
                }
            }
        }
    }

    private void addToList(String fileName, AssetsRatio assetsRatio) {
        if (config.isStoreYaml()) {
            if (assetsRatio.equals(AssetsRatio.ONE)) {
                if (!config.isIgnoreOnex()) {
                    list.add(fileName);
                }
            } else {
                list.add(fileName);
            }
        }
    }

    private void storeYaml() throws FileNotFoundException {
        if (config.isStoreYaml()) {
            String yamlPath = config.getYamlPath();
            File file = new File(yamlPath);
            if (file.isDirectory()) {
                file = new File(yamlPath + File.separator + "pubspec.yaml");
            }
            @Cleanup
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
            if (list != null && !list.isEmpty()) {
                list.sort(String::compareTo);
                String root = targetFile.getPath();
                printWriter.println("  assets:");
                printWriter.println("     - " + root + File.separator);
                for (String string: list) {
                    if (!string.contains(".DS_Store")) {
                        printWriter.println("     - " + string);
                    }
                }
            }
            printWriter.flush();
        }
    }
}
