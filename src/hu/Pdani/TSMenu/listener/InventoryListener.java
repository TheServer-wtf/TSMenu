package hu.Pdani.TSMenu.listener;

import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.utils.TSMHolder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static hu.Pdani.TSMenu.TSMenuPlugin.rFirst;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        int slot = event.getRawSlot();
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
        if (inventory.getHolder() instanceof TSMHolder) {
            if(slot > inventory.getSize()-1){
                event.setCancelled(true);
                return;
            }
            TSMenuPlugin plugin = TSMenuPlugin.getPlugin();
            event.setCancelled(true);
            if(clicked == null || clicked.getType() == Material.AIR)
                return;
            NamespacedKey mke = new NamespacedKey(plugin, "menu");
            NamespacedKey cke = new NamespacedKey(plugin, "commands");
            ItemMeta meta = clicked.getItemMeta();
            if(meta == null)
                return;
            PersistentDataContainer dc = meta.getPersistentDataContainer();
            if(dc.has(cke, PersistentDataType.STRING)) {
                String key = dc.get(cke, PersistentDataType.STRING);
                String menu = dc.get(mke, PersistentDataType.STRING);
                player.closeInventory();
                ConfigurationSection isec = plugin.getFM().getConfig(menu).getConfigurationSection("ITEMS."+key);
                if(isec == null)
                    return;
                List<String> cmds = isec.getStringList("commands");
                for(String c : cmds){
                    if(c.startsWith("CONSOLE$")) {
                        c = rFirst(c,"CONSOLE$", "");
                        c = c.replace("%player%", player.getName());
                        if (c.startsWith("/"))
                            c = rFirst(c,"/", "");
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), c);
                    } else {
                        c = c.replace("%player%", player.getName());
                        if (c.startsWith("/"))
                            c = rFirst(c,"/", "");
                        plugin.getServer().dispatchCommand(player, c);
                    }
                }
            } else if(dc.has(mke, PersistentDataType.STRING)){
                String menu = dc.get(mke, PersistentDataType.STRING);
                plugin.getGm().openMenu(player,menu);
            }
        }
    }
}
