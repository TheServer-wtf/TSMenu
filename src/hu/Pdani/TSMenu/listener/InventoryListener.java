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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static hu.Pdani.TSMenu.TSMenuPlugin.c;
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
            NamespacedKey omke = new NamespacedKey(plugin, "openmenu");
            NamespacedKey cke = new NamespacedKey(plugin, "commands");
            NamespacedKey close = new NamespacedKey(plugin, "close");
            NamespacedKey cmdata = new NamespacedKey(plugin, "cmdata");
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
            } else if(dc.has(omke, PersistentDataType.STRING)){
                String menu = dc.get(omke, PersistentDataType.STRING);
                plugin.getGm().openMenu(player,menu);
            } else if(dc.has(close, PersistentDataType.INTEGER)){
                player.closeInventory();
            } else if(dc.has(cmdata,PersistentDataType.STRING)){
                if(plugin.getTSItem() == null)
                    return;
                NamespacedKey tsitem = new NamespacedKey(plugin.getTSItem(), "tsitem");
                NamespacedKey newdata = new NamespacedKey(plugin, "newdata");
                if(!dc.has(newdata,PersistentDataType.INTEGER)) return;
                Integer cmd = dc.get(newdata,PersistentDataType.INTEGER);
                String check = dc.get(cmdata,PersistentDataType.STRING);
                if(check == null || cmd == null) return;
                Inventory inv = player.getInventory();
                for(ItemStack i : inv.getContents()){
                    if(i == null) continue;
                    if(!i.hasItemMeta()) continue;
                    ItemMeta im = i.getItemMeta();
                    PersistentDataContainer ipdc = im.getPersistentDataContainer();
                    if(!ipdc.has(tsitem,PersistentDataType.STRING)) continue;
                    String tsiname = ipdc.get(tsitem,PersistentDataType.STRING);
                    if(tsiname != null && tsiname.equalsIgnoreCase(check)){
                        im.setCustomModelData(cmd);
                        i.setItemMeta(im);
                        player.closeInventory();
                    }
                }
            }

            NamespacedKey upd = new NamespacedKey(plugin, "update");
            // UPDATE ITEM
            if(dc.has(upd,PersistentDataType.STRING)){
                String key = dc.get(upd,PersistentDataType.STRING);
                String menu = dc.get(mke, PersistentDataType.STRING);
                ConfigurationSection isec = plugin.getFM().getConfig(menu).getConfigurationSection("ITEMS."+key+".update");
                if(isec == null)
                    return;
                PlayerInventory inv = player.getInventory();
                String type = isec.getString("select.type",null);
                if(type == null)
                    return;
                int limit = isec.getInt("select.limit",0);
                switch (type) {
                    case "tsitem":
                        NamespacedKey tsitem = new NamespacedKey(plugin.getTSItem(), "tsitem");
                        int index = 0;
                        for (ItemStack i : inv.getContents()) {
                            if(limit > 0 && index >= limit) break;
                            if (i == null) continue;
                            if (!i.hasItemMeta()) continue;
                            ItemMeta im = i.getItemMeta();
                            PersistentDataContainer ipdc = im.getPersistentDataContainer();
                            if (!ipdc.has(tsitem, PersistentDataType.STRING)) continue;
                            String tsiname = ipdc.get(tsitem, PersistentDataType.STRING);
                            if(tsiname == null) continue;
                            String check = isec.getString("select.name");
                            if(tsiname.equalsIgnoreCase(check)){
                                i.setItemMeta(updateMeta(im, isec));
                            }
                            Material material = null;
                            String sm = isec.getString("material");
                            if(sm != null) {
                                try {
                                    material = Material.valueOf(sm.toUpperCase());
                                } catch (Exception ignored) {}
                                if(material != null)
                                    i.setType(material);
                            }
                            index++;
                        }
                        break;
                    case "inhand":
                        tsitem = new NamespacedKey(plugin.getTSItem(), "tsitem");
                        ItemStack i = inv.getItemInMainHand();
                        if(i.getType() != Material.AIR){
                            if (!i.hasItemMeta()) break;
                            ItemMeta im = i.getItemMeta();
                            PersistentDataContainer ipdc = im.getPersistentDataContainer();
                            if (!ipdc.has(tsitem, PersistentDataType.STRING)) break;
                            String tsiname = ipdc.get(tsitem, PersistentDataType.STRING);
                            if(tsiname == null) break;
                            String check = isec.getString("select.name");
                            if(tsiname.equalsIgnoreCase(check)){
                                i.setItemMeta(updateMeta(im, isec));
                            }
                            Material material = null;
                            String sm = isec.getString("material");
                            if(sm != null) {
                                try {
                                    material = Material.valueOf(sm.toUpperCase());
                                } catch (Exception ignored) {}
                                if(material != null)
                                    i.setType(material);
                            }
                        }
                        break;
                    case "offhand":
                        tsitem = new NamespacedKey(plugin.getTSItem(), "tsitem");
                        i = inv.getItemInOffHand();
                        if(i.getType() != Material.AIR){
                            if (!i.hasItemMeta()) break;
                            ItemMeta im = i.getItemMeta();
                            PersistentDataContainer ipdc = im.getPersistentDataContainer();
                            if (!ipdc.has(tsitem, PersistentDataType.STRING)) break;
                            String tsiname = ipdc.get(tsitem, PersistentDataType.STRING);
                            if(tsiname == null) break;
                            String check = isec.getString("select.name");
                            if(tsiname.equalsIgnoreCase(check)){
                                i.setItemMeta(updateMeta(im, isec));
                            }
                            Material material = null;
                            String sm = isec.getString("material");
                            if(sm != null) {
                                try {
                                    material = Material.valueOf(sm.toUpperCase());
                                } catch (Exception ignored) {}
                                if(material != null)
                                    i.setType(material);
                            }
                        }
                        break;
                    case "slot":
                        tsitem = new NamespacedKey(plugin.getTSItem(), "tsitem");
                        i = inv.getItem(isec.getInt("select.slot",0));
                        if(i != null && i.getType() != Material.AIR){
                            if (!i.hasItemMeta()) break;
                            ItemMeta im = i.getItemMeta();
                            PersistentDataContainer ipdc = im.getPersistentDataContainer();
                            if (!ipdc.has(tsitem, PersistentDataType.STRING)) break;
                            String tsiname = ipdc.get(tsitem, PersistentDataType.STRING);
                            if(tsiname == null) break;
                            String check = isec.getString("select.name");
                            if(tsiname.equalsIgnoreCase(check)){
                                i.setItemMeta(updateMeta(im, isec));
                            }
                            Material material = null;
                            String sm = isec.getString("material");
                            if(sm != null) {
                                try {
                                    material = Material.valueOf(sm.toUpperCase());
                                } catch (Exception ignored) {}
                                if(material != null)
                                    i.setType(material);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private ItemMeta updateMeta(ItemMeta meta, ConfigurationSection options){
        List<String> lore = options.getStringList("lore");
        for(int i = 0; i < lore.size(); i++){
            lore.set(i,c(lore.get(i)));
        }
        if(meta.hasLore() && meta.getLore().size() > 0) {
            meta.setLore(lore);
        } else {
            if(lore.size() > 0){
                meta.setLore(lore);
            }
        }
        String name = options.getString("name",null);
        if(name != null) {
            if(name.isEmpty())
                meta.setDisplayName(null);
            else
                meta.setDisplayName(c(name));
        }
        int cmd = options.getInt("custommodeldata",0);
        meta.setCustomModelData(cmd);
        return meta;
    }
}
