package com.kinglloy.album.engine.component;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinyalin
 * @since 2017/11/22.
 */

public class ClassLoaderPool {
    private static final Map<String, ETWClassLoader> CLASS_LOADER_MAP = new HashMap<>();

    public static ETWClassLoader getClassLoader(Context context, String dexPath,
                                                String optimizedDirectory,
                                                String librarySearchPath, ClassLoader parent) {
        ETWClassLoader classLoader;
        if (CLASS_LOADER_MAP.containsKey(dexPath)) {
            classLoader = CLASS_LOADER_MAP.get(dexPath);
        } else {
            classLoader = new ETWClassLoader(context, dexPath,
                    optimizedDirectory, librarySearchPath, parent);
            CLASS_LOADER_MAP.put(dexPath, classLoader);
        }
        return classLoader;
    }

}
