package cn.maxmc.maxnicky;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class NickPlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "nicky";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TONY_All";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equals("nickname")){
            if(MaxNicky.getNickTemp().containsKey(player.getName())){
                return MaxNicky.getNickTemp().get(player.getName());
            }
            String nickOfPlayer = MaxNicky.getMysqlConnector().getNickOfPlayer(player);
            if(nickOfPlayer == null){
                return player.getName();
            }
            if(nickOfPlayer.equalsIgnoreCase("空")){
                return player.getName();
            }
            return nickOfPlayer;
        }
        return null;
    }
}
