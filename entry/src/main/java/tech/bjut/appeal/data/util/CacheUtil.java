package tech.bjut.appeal.data.util;

import java.io.File;

public class CacheUtil {

    private static File cacheDir = null;

    public static File getCacheDir() {
        if (cacheDir == null) {
            throw new RuntimeException("CacheUtil not initialized");
        }
        return cacheDir;
    }

    public static void setCacheDir(File cacheDir) {
        CacheUtil.cacheDir = cacheDir;
    }
}
