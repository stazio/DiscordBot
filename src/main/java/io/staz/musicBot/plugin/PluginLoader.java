package io.staz.musicBot.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginLoader {

    @SneakyThrows
    public static ArrayList<PluginInfo> findPlugins(boolean loadPlugin) {
        File folder = new File("plugins");
        File[] files = folder.listFiles();
        ArrayList<PluginInfo> pluginInfo = new ArrayList<PluginInfo>();
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                ZipFile zip = new ZipFile(file);
                ZipEntry entry = zip.getEntry("config.yml");
                if (entry != null) {
                    InputStream instream = zip.getInputStream(entry);
                    PluginInfo info = mapper.readValue(instream, PluginInfo.class);
                    pluginInfo.add(info);
                    if (loadPlugin)
                        loadLibrary(file);
                }
            }
        }
        return pluginInfo;
    }

    /*
     * Adds the supplied Java Archive library to java.class.path. This is benign
     * if the library is already loaded.
     * Source: https://stackoverflow.com/questions/27187566/load-jar-dynamically-at-runtime
     */
    @SneakyThrows
    private static synchronized void loadLibrary(java.io.File jar) {
        /*We are using reflection here to circumvent encapsulation; addURL is not public*/
        java.net.URLClassLoader loader = (java.net.URLClassLoader) ClassLoader.getSystemClassLoader();
        java.net.URL url = jar.toURI().toURL();
        /*Disallow if already loaded*/
        for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())) {
            if (it.equals(url)) {
                return;
            }
        }
        java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{java.net.URL.class});
        method.setAccessible(true); /*promote the method to public access*/
        method.invoke(loader, new Object[]{url});
    }

    @SneakyThrows
    public static Class<? extends Plugin> findClass(String className) {
        Class<?> plugin = Class.forName(className);
        return plugin.asSubclass(Plugin.class);
    }
}
