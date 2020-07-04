package hu.Pdani.TSMenu.listener;

import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.utils.TSMException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static hu.Pdani.TSMenu.TSMenuPlugin.c;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("tsmenu.admin"))
            return true;
        String version = TSMenuPlugin.getPlugin().getDescription().getVersion();
        List<String> authors = TSMenuPlugin.getPlugin().getDescription().getAuthors();
        String longauthors = TSMenuPlugin.getPlugin().getDescription().getAuthors().stream().collect(Collectors.joining(", ", "", ""));
        String description = TSMenuPlugin.getPlugin().getDescription().getDescription();
        if(args.length == 0 || (args[0].equalsIgnoreCase("help"))){
            sendHelp(sender);
            return true;
        }
        switch (args[0].toUpperCase()){
            case "ABOUT":
                sender.sendMessage(c("&6&lTSMenu plugin"));
                sender.sendMessage(c("&a "+description));
                sender.sendMessage(c("&eVersion: "+version));
                sender.sendMessage(c("&eAuthors: "+longauthors));
                break;
            case "RELOADALL":
                TSMenuPlugin.getFileManager().reloadFiles();
                sender.sendMessage(c("&aReloaded all files!"));
                break;
            case "RELOAD":
                if(args.length < 2) {
                    sender.sendMessage(c("&cUsage: /tsmenu reload <file>"));
                    break;
                }
                try {
                    TSMenuPlugin.getFileManager().reloadFile(args[1]);
                    sender.sendMessage(c("&aFile '"+args[1]+"' reloaded!"));
                } catch (TSMException | IllegalArgumentException e) {
                    sender.sendMessage(c("&c"+e.getMessage()));
                }
                break;
            case "UNLOAD":
                if(args.length < 2) {
                    sender.sendMessage(c("&cUsage: /tsmenu unload <file>"));
                    break;
                }
                try {
                    TSMenuPlugin.getFileManager().unloadFile(args[1]);
                    sender.sendMessage(c("&aFile '"+args[1]+"' unloaded!"));
                } catch (TSMException | IllegalArgumentException e) {
                    sender.sendMessage(c("&c"+e.getMessage()));
                }
                break;
            case "LOAD":
                if(args.length < 2) {
                    sender.sendMessage(c("&cUsage: /tsmenu load <file>"));
                    break;
                }
                try {
                    TSMenuPlugin.getFileManager().loadFile(args[1]);
                    sender.sendMessage(c("&aFile '"+args[1]+"' loaded!"));
                } catch (TSMException | IllegalArgumentException e) {
                    sender.sendMessage(c("&c"+e.getMessage()));
                }
                break;
            case "LIST":
                int num = 1;
                if(args.length >= 2) {
                    try {
                        num = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                String[] list = TSMenuPlugin.getFileManager().getFileList();
                if(list.length == 0){
                    sender.sendMessage(c("&cThere are no loaded files."));
                }
                int page = (args.length < 2) ? 1 : num;
                int limit = 10;
                int start = (page-1) * limit;
                if(start >= list.length)
                    start = 0;
                int end = start + limit;
                if(list.length < end)
                    end = list.length;
                for(int i = start; i < end; i++){
                    sender.sendMessage((i+1)+". "+list[i]);
                }
                if(list.length > end)
                    sender.sendMessage(c("&eNext page: /tsmenu list "+(page+1)));
                break;
            case "OPEN":
                if(args.length < 2){
                    sender.sendMessage(c("&cUsage: /tsmenu open <menu> [player]"));
                    break;
                } else if(args.length < 3) {
                    if(!(sender instanceof Player)){
                        sender.sendMessage(c("&cUsage: /tsmenu open <menu> [player]"));
                        break;
                    }
                    TSMenuPlugin.getGuiManager().openMenu((Player) sender,args[1]);
                    break;
                }
                Player target = TSMenuPlugin.getPlugin().getServer().getPlayer(args[2]);
                if(target == null){
                    sender.sendMessage(c("&cThe requested player is not online!"));
                    break;
                }
                TSMenuPlugin.getGuiManager().openMenu(target,args[1]);
                sender.sendMessage(c("&cAttempting to open menu, if the opening failed, please check the console for errors!"));
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender){
        String version = TSMenuPlugin.getPlugin().getDescription().getVersion();
        List<String> authors = TSMenuPlugin.getPlugin().getDescription().getAuthors();
        sender.sendMessage(c("&eTSMenu plugin v"+version+", created by "+authors.get(0)));
        sender.sendMessage(c("&7- &9/tsmenu reloadall &7- &6Reload all files"));
        sender.sendMessage(c("&7- &9/tsmenu reload <file> &7- &6Reload the specified file"));
        sender.sendMessage(c("&7- &9/tsmenu load <file> &7- &6Load the specified file"));
        sender.sendMessage(c("&7- &9/tsmenu unload <file> &7- &6Unload the specified file"));
        sender.sendMessage(c("&7- &9/tsmenu open <file> [player] &7- &6Open the specified menu"));
        sender.sendMessage(c("&7- &9/tsmenu list [page] &7- &6List all the loaded files"));
        sender.sendMessage(c("&7- &9/tsmenu help &7- &6Display this list"));
        sender.sendMessage(c("&7- &9/tsmenu about &7- &6Information about the plugin"));
    }
}
