package cn.maxmc.maxnicky;

import cn.maxmc.maxnicky.Utils.IllegalNickException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {
    private static final String MAXMC = "§6§lMAXMC §a§l>> ";
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage("§7===========================");
            sender.sendMessage("§6MaxNicky §7, author:§bTONY_All");
            sender.sendMessage("  ");
            sender.sendMessage("§7Type §8/nick help §7for help");
            sender.sendMessage("§7===========================");
            return true;
        }

        String scmd = args[0];
        Player p = null;
        if(sender instanceof Player){
            p = (Player) sender;
        }
        // Unset
        if(scmd.equals("unset")){
            if(args.length == 1){
                if(!sender.hasPermission("maxnicky.unset")){
                    sender.sendMessage(MAXMC+"§c您没有权限执行该命令");
                    return true;
                }
                if(p == null){
                    sender.sendMessage(MAXMC+"§c该指令只能由玩家执行!");
                   return true;
                }
                sender.sendMessage(MAXMC+"§a成功关闭称号!");
                p.setDisplayName(p.getName());
                MaxNicky.getNickTemp().remove(p.getName());
                try {
                    MaxNicky.getMysqlConnector().setNick(p,"空");
                } catch (IllegalNickException ignored) { }
                return true;
            }
            if(args.length == 2){
                if(!sender.hasPermission("maxnicky.unset.others")){
                    sender.sendMessage(MAXMC+"§c您没有权限执行该命令");
                    return true;
                }
                String target = args[1];
                OfflinePlayer targetP = getOfflinePlayer(target);
                if(targetP == null){
                    sender.sendMessage(MAXMC+"§c该玩家不存在");
                    return true;
                }
                try {
                    MaxNicky.getMysqlConnector().setNick(targetP,"空");
                } catch (IllegalNickException ignored) { }
                sender.sendMessage(MAXMC+"§a成功关闭该玩家称号");
                return true;
            }
            sender.sendMessage(MAXMC+"§c您的参数有误,请重试.");
            return true;
        }

        if(scmd.equals("help")) {
            sender.sendMessage("§7===========================");
            sender.sendMessage("§7/nick <昵称> §e设置自己的昵称");
            if(sender.hasPermission("maxnicky.set.others")) {
                sender.sendMessage("§7/nick <昵称> <玩家> §e设置该玩家的昵称");
            }
            sender.sendMessage("§7/nick unset §e取消自己的昵称");
            if(sender.hasPermission("maxnicky.unset.others")) {
                sender.sendMessage("§7/nick unset <玩家> §e取消该玩家的昵称");
            }
            sender.sendMessage("§7===========================");
            return true;
        }
        if(args.length == 1){
            if(!sender.hasPermission("maxnicky.use")){
                sender.sendMessage(MAXMC+"§c您没有权限执行该指令");
                return true;
            }
            if(p == null) {
                sender.sendMessage(MAXMC+"§c该指令只能由玩家执行");
                return true;
            }
            String nick = args[0].replace('&','§');
            if(nick.equalsIgnoreCase("空")){
                sender.sendMessage(MAXMC+"§c昵称不能为空");
                return true;
            }
            try {
                MaxNicky.getMysqlConnector().setNick(p,nick);
                MaxNicky.getNickTemp().put(p.getName(),nick);
                p.setDisplayName(nick);
                sender.sendMessage(MAXMC+"§a成功设置昵称为§b"+nick);
            } catch (IllegalNickException e) {
                p.sendMessage(MAXMC+"§c您的昵称中必须包含至少一个中文!");
            }
            return true;
        }
        if(args.length == 2){
            if(!sender.hasPermission("maxnicky.use.others")){
                sender.sendMessage(MAXMC+"§c您没有权限执行该指令");
                return true;
            }
            String name = args[1];
            OfflinePlayer pl = getOfflinePlayer(name);
            if(pl == null) {
                sender.sendMessage(MAXMC+"§c该玩家不存在");
                return true;
            }
            String nick = args[0].replace('&','§');
            if(nick.equalsIgnoreCase("空")){
                sender.sendMessage(MAXMC+"§c昵称不能为空");
                return true;
            }
            try {
                MaxNicky.getMysqlConnector().setNick(pl,nick);
                if(pl.isOnline()){
                    ((Player)pl).setDisplayName(nick);
                }
                sender.sendMessage(MAXMC+"§a成功设置该玩家昵称为§b"+nick);
            } catch (IllegalNickException e) {
                sender.sendMessage(MAXMC+"§c您的昵称中必须包含至少一个中文!");
            }
        }
        return true;
    }


    private OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if(offlinePlayer.getName().equals(name)){
                return offlinePlayer;
            }
        }
        return null;
    }
}
