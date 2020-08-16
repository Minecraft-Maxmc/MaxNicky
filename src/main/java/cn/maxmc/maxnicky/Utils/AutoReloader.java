package cn.maxmc.maxnicky.Utils;

import cn.maxmc.maxnicky.MaxNicky;
import org.apache.log4j.helpers.FileWatchdog;
import org.bukkit.Bukkit;

import java.io.File;

public class AutoReloader extends FileWatchdog {

    protected AutoReloader(File file) {
        super(file.getAbsolutePath());
    }

    @Override
    protected void doOnChange() {
        Bukkit.getScheduler().runTask(MaxNicky.getInstance(),() -> {
            System.out.println(filename);
            MaxNicky.log("§b检测到配置文件变化,正在重载...");
            MaxNicky.getSettingsManager().reload();
            MaxNicky.log("§a配置文件重载完成!");
        });
    }

    public static AutoReloader getInstance(File file) {
        return new AutoReloader(file);
    }
}
