package cn.maxmc.maxnicky;

import cn.maxmc.maxnicky.Utils.Inject;
import cn.maxmc.maxnicky.Utils.Injectable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SettingsManager {
    private static List<Class<? extends Injectable>> needConfigs = new ArrayList<>();
    private YamlConfiguration settings;

    public SettingsManager() {
        load();
        MaxNicky.log("§a成功加载配置文件!");
    }

    public void load() {
        File f = new File(MaxNicky.getInstance().getDataFolder(),"settings.yml");
        File parentFile = f.getParentFile();
        // Save config files
        if(!parentFile.exists()){
            boolean mkdirs = parentFile.mkdirs();
        }
        if(!f.exists()){
            MaxNicky.getInstance().saveResource("settings.yml",true);
        }

        // Load config.
        settings = YamlConfiguration.loadConfiguration(f);

        // Inject Data

        try {
            inject();
        } catch (IllegalAccessException e) {
            System.out.println("插件遇到了一个错误,请把下面的内容报告给开发者.");
            System.out.println("-----报错开始-----");
            e.printStackTrace();
            System.out.println("-----报错结束-----");
            System.out.println("插件遇到了一个错误,请把下面的内容报告给开发者.");
        }

    }

    public void reload() {
        settings = null;
        load();
    }

    public static void registerConfig(Class<? extends Injectable> clazz){
        needConfigs.add(clazz);
    }

    private void inject() throws IllegalAccessException {
        // getClasses
        for (Class<? extends Injectable> needConfig : needConfigs) {
            // getFields
            for (Field declaredField : needConfig.getDeclaredFields()) {

                Inject annotation = declaredField.getAnnotation(Inject.class);
                // Ignore if don't need.
                if(annotation == null){
                    continue;
                }


                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();
                // Inject value
                switch (type.getSimpleName()){
                    case "String":{
                        declaredField.set(null,settings.getString(annotation.path()));
//                        System.out.println("set "+declaredField.getName()+" to"+ settings.getString(annotation.path()));
                        break;
                    }
                    case "boolean":{
                        declaredField.set(null,settings.getBoolean(annotation.path()));
//                        System.out.println("set "+declaredField.getName()+" to"+ settings.getBoolean(annotation.path()));
                        break;
                    }
                    case "int":{
                        declaredField.set(null,settings.getInt(annotation.path()));
                        break;
                    }
                }
                declaredField.setAccessible(false);
            }
        }
    }
}
