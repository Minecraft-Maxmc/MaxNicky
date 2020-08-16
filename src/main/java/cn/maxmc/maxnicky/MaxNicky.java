package cn.maxmc.maxnicky;

import cn.maxmc.maxnicky.Utils.AutoReloader;
import cn.maxmc.maxnicky.Utils.Inject;
import cn.maxmc.maxnicky.Utils.Injectable;
import cn.maxmc.maxnicky.Utils.TabListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MaxNicky extends JavaPlugin implements Injectable {
    @Getter
    private static MaxNicky instance;
    @Getter
    private static SettingsManager settingsManager;
    @Getter
    private static MysqlConnector mysqlConnector;
    @Getter
    private static Map<String,String> nickTemp;
    @Inject(path = "database.enable")
    private static boolean enable;

    public MaxNicky() {
        instance = this;
        nickTemp = new HashMap<>();
    }

    @Override
    public void onEnable() {
        SettingsManager.registerConfig(MysqlConnector.class);
        SettingsManager.registerConfig(MaxNicky.class);
        settingsManager = new SettingsManager();
        AutoReloader reloader = AutoReloader.getInstance(new File(getDataFolder(), "settings.yml"));
        reloader.setDelay(1000);
        reloader.start();
        if(!enable){
            MaxNicky.log("§cMySql未配置,插件已关闭.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        mysqlConnector = new MysqlConnector();
        new NickPlaceholder().register();
        Bukkit.getPluginCommand("nick").setExecutor(new NickCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        if(isHigher()){
            Bukkit.getPluginManager().registerEvents(new TabListener(),this);
        }
    }

    public static void log(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[MaxNicky§7]"+msg);
    }

    private boolean isHigher() {
        String versionM1 = getVersionM1();
        String verStr = versionM1.split("\\.")[1];
        int ver = Integer.parseInt(verStr);
        return ver >= 12;
    }

    private static String getVersionM1() {
        String ver = Bukkit.getVersion();
        String substring = ver.substring(ver.indexOf("("), ver.indexOf(")"));
        String[] split = substring.split(":");
        return split[1].replace(" ", "");
    }
}
