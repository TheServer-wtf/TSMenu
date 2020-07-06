package hu.Pdani.TSMenu;

import hu.Pdani.TSMenu.utils.AMyCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MyCommand extends AMyCommand {

    public MyCommand(CommandMap commandMap, JavaPlugin plugin, String name) {
        super(plugin, name);
        addDescription("Open a menu specified in it's config");
        addUsage("/"+name);

        registerCommand(commandMap);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!TSMenuPlugin.getFileManager().isCommand(command.getName().toLowerCase())){
            return true;
        }
        if (commandSender instanceof Player){
            Player sender = (Player) commandSender;
            String file = TSMenuPlugin.getFileManager().getFileOnCommand(command.getName());
            if(file != null){
                TSMenuPlugin.getGuiManager().openMenu(sender,file);
            }
        } else {
            commandSender.sendMessage("In-game only.");
        }

        return true;
    }
}