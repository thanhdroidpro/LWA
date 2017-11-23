package com.kinglloy.album.engine.component;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.kinglloy.common.utils.NativeFileHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;

/**
 * @author jinyalin
 * @since 2017/8/9.
 */

public class ETWClassLoader extends DexClassLoader {
    private static final String TAG = "StyleClassLoader";
    private static final String SO_DIR = "lib/";
    private static final String LIB_PREFIX = "lib";

    private Context context;
    private String dexPath;
    private String componentName;
    private String nativePath;

    public ETWClassLoader(Context context, String dexPath, String optimizedDirectory,
                          String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        this.context = context;
        this.dexPath = dexPath;

        try {
            String[] tmp = dexPath.split("/");
            componentName = tmp[tmp.length - 1].split("\\.")[0];
        } catch (Exception e) {
            componentName = String.valueOf(dexPath.hashCode());
        }
    }

    @Override
    public String findLibrary(String name) {
        if (!name.contains(LIB_PREFIX)) {
            name = LIB_PREFIX + name;
        }
        maybeCopyNativeLib(name);
        String soName = NativeFileHelper.INSTANCE.getNativeFileName(dexPath, name);
        File targetFile = new File(NativeFileHelper.INSTANCE.getNativeDir(context), soName);
        return targetFile.exists() ? targetFile.getAbsolutePath() : null;
    }

    private String findBestCPUArch() throws IOException {
        if (Build.VERSION.SDK_INT >= 21) {
            Set<String> componentSupportedArchs = new HashSet<>();
            ZipFile zipfile = new ZipFile(dexPath);
            ZipEntry entry;
            Enumeration e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory())
                    continue;
                if (entry.getName().contains(SO_DIR)) {
                    int archNameStartIndex = entry.getName().indexOf(SO_DIR) + SO_DIR.length();
                    String archName = entry.getName().substring(archNameStartIndex, entry.getName().length());
                    String archType = archName.substring(0, archName.indexOf("/"));
                    componentSupportedArchs.add(archType);
                }
            }

            for (String deviceAbi : Build.SUPPORTED_ABIS) {
                for (String componentAbi : componentSupportedArchs) {
                    if (Objects.equals(deviceAbi, componentAbi)) {
                        return deviceAbi;
                    }
                }
            }
            return null;
        } else {
            return Build.CPU_ABI;
        }
    }

    private boolean maybeCopyNativeLib(String libName) {
        try {
            File soFile = new File(NativeFileHelper.INSTANCE.getNativeDir(context).getAbsolutePath()
                    + File.separator + NativeFileHelper.INSTANCE.getNativeFileName(dexPath, libName));
            if (soFile.exists()) {
                if (soFile.length() > 0) {
                    return true;
                } else {
                    soFile.delete();
                }
            }

            String cpuArch = findBestCPUArch();
            if (cpuArch == null) {
                return false;
            }

            ZipFile zipfile = new ZipFile(dexPath);
            ZipEntry entry;
            Enumeration e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".so"))
                    continue;
                if (entry.getName().contains(SO_DIR + cpuArch) && entry.getName().contains(libName)) {
                    if (soFile.exists()) {
                        // check version
                    }
                    OutputStream fos = new FileOutputStream(soFile);
                    Log.d(TAG, "copy so " + entry.getName() + " of " + cpuArch);
                    copySo(zipfile.getInputStream(entry), fos);
                    break;
                }

            }

            zipfile.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copySo(InputStream input, OutputStream output) throws IOException {
        BufferedInputStream bufferedInput = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            bufferedInput = new BufferedInputStream(input);
            bufferedOutput = new BufferedOutputStream(output);
            int count;
            byte data[] = new byte[8192];
            while ((count = bufferedInput.read(data, 0, 8192)) != -1) {
                bufferedOutput.write(data, 0, count);
            }
        } finally {
            if (bufferedInput != null) {
                bufferedInput.close();
            }
            if (bufferedOutput != null) {
                bufferedOutput.close();
            }
        }
    }
}
