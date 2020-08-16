package cn.maxmc.maxnicky.Utils;

import cn.maxmc.maxnicky.MaxNicky;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Map;

public class TabListener implements Listener {
    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        String[] s = e.getBuffer().split(" ");
        String str = s[s.length - 1];
        for (Map.Entry<String, String> stringStringEntry : MaxNicky.getNickTemp().entrySet()) {
            if(stringStringEntry.getValue().startsWith(str)){
                e.getCompletions().add(stringStringEntry.getValue());
            }
        }
    }
}
