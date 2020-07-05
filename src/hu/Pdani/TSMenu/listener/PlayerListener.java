package hu.Pdani.TSMenu.listener;

import hu.Pdani.TSMenu.TSMenuPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import static hu.Pdani.TSMenu.TSMenuPlugin.rFirst;

public class PlayerListener implements Listener {
    @EventHandler
    public void preCmd(PlayerCommandPreprocessEvent event){
        String cmd = event.getMessage();
        Player player = event.getPlayer();
        if(cmd.startsWith("/")){
            cmd = rFirst(cmd,"/","");
        }
        for(String k : TSMenuPlugin.getFileManager().getFileList()){
            FileConfiguration sec = TSMenuPlugin.getFileManager().getConfig(k);
            if(sec.isSet("COMMAND")){
                String exec = sec.getString("COMMAND");
                if(exec.equalsIgnoreCase(cmd)){
                    event.setCancelled(true);
                    TSMenuPlugin.getGuiManager().openMenu(player,k);
                    break;
                }
            }
        }
    }
}
