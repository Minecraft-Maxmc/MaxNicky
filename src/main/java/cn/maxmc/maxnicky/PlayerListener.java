package cn.maxmc.maxnicky;

import cn.maxmc.maxnicky.Utils.IllegalNickException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String nickOfPlayer = MaxNicky.getMysqlConnector().getNickOfPlayer(e.getPlayer());
        if(nickOfPlayer == null){
            try {
                MaxNicky.getMysqlConnector().setNick(e.getPlayer(),"空");
            } catch (IllegalNickException ignored) { }
            return;
        }
        if(!nickOfPlayer.equals("空")){
            MaxNicky.getNickTemp().put(e.getPlayer().getName(),nickOfPlayer);
            e.getPlayer().setDisplayName(nickOfPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        MaxNicky.getNickTemp().remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        for (Map.Entry<String, String> stringStringEntry : MaxNicky.getNickTemp().entrySet()) {
            if(e.getMessage().contains(stringStringEntry.getValue())){
                e.setMessage(e.getMessage().replace(stringStringEntry.getValue(),stringStringEntry.getKey()));
            }
        }
    }
}
