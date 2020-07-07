package hu.Pdani.TSMenu.manager;

import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.utils.TSMHolder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static hu.Pdani.TSMenu.TSMenuPlugin.c;

import java.util.List;

public class GuiManager {
    private TSMenuPlugin plugin;
    public GuiManager(TSMenuPlugin plugin){
        this.plugin = plugin;
    }
    public void openMenu(Player target, String menu){
        FileConfiguration fc = plugin.getFM().getConfig(menu);
        if(fc == null){
            plugin.getLogger().warning("The requested menu ("+menu+") is not found!");
            return;
        }
        String title = fc.getString("TITLE");
        String perm = fc.getString("PERMISSION");
        int size = fc.getInt("ROWS",-1);
        if(title == null){
            plugin.getLogger().severe("The requested menu ("+menu+") has no title set!");
            return;
        }
        if(size == -1){
            plugin.getLogger().severe("The requested menu ("+menu+") has no size set!");
            return;
        }
        if(size < 1 || size > 6){
            plugin.getLogger().warning("The requested menu ("+menu+") has an invalid size set!");
            return;
        }
        if(perm != null && !perm.isEmpty()){
            if(!target.hasPermission(perm))
                return;
        }
        ConfigurationSection section = fc.getConfigurationSection("ITEMS");
        Inventory myInv = plugin.getServer().createInventory(new TSMHolder(), size*9, c(title));
        if(section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection isec = section.getConfigurationSection(key);
                if(isec == null)
                    continue;
                String name = isec.getString("name");
                String smat = isec.getString("material", "STONE");
                if(smat == null)
                    smat = "STONE";
                String function = isec.getString("function");
                int amount = isec.getInt("amount",1);
                List<String> lore = isec.getStringList("lore");
                List<String> enchants = isec.getStringList("enchants");
                int damage = isec.getInt("damage",0);
                int cmd = isec.getInt("custommodeldata",-1);
                int pos = isec.getInt("position",-1);
                Material material;
                try{
                    material = Material.valueOf(smat.toUpperCase());
                } catch (IllegalArgumentException ex){
                    plugin.getLogger().warning("Item '"+key+"' in menu '"+menu+"' has an invalid material specified!");
                    material = Material.STONE;
                }
                ItemStack item = new ItemStack(material,amount);
                ItemMeta meta = item.getItemMeta();
                if(name != null && !name.isEmpty())
                    meta.setDisplayName(c(name));
                else
                    meta.setDisplayName(material.name());
                meta.setUnbreakable(true);
                for(int i = 0; i < lore.size(); i++){
                    lore.set(i,c(lore.get(i)));
                }
                meta.setLore(lore);
                if(cmd > -1)
                    meta.setCustomModelData(cmd);
                for(String e : enchants){
                    String enam;
                    if(e.contains(":"))
                        enam = e.split(":")[0];
                    else
                        enam = e;
                    int val = 1;
                    if(e.contains(":")) {
                        try {
                            val = Integer.parseInt(e.split(":")[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                    Enchantment en = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enam));
                    if(en != null)
                        meta.addEnchant(en,val,true);
                }
                if(function != null && !function.isEmpty()){
                    switch (function.toLowerCase()){
                        case "menu":
                            String mn = isec.getString("menu");
                            if (mn == null)
                                mn = "";
                            NamespacedKey mke = new NamespacedKey(plugin, "menu");
                            meta.getPersistentDataContainer().set(mke, PersistentDataType.STRING, mn);
                            break;
                        case "command":
                        case "commands":
                            NamespacedKey cke = new NamespacedKey(plugin, "commands");
                            meta.getPersistentDataContainer().set(cke, PersistentDataType.STRING, key);
                            mke = new NamespacedKey(plugin, "menu");
                            meta.getPersistentDataContainer().set(mke, PersistentDataType.STRING, menu);
                            break;
                        case "close":
                            NamespacedKey close = new NamespacedKey(plugin, "close");
                            meta.getPersistentDataContainer().set(close, PersistentDataType.INTEGER, 1);
                            break;
                        default:
                            break;
                    }
                }
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                ItemStack inPos = myInv.getItem(pos);
                if(inPos == null || inPos.getType() == Material.AIR) {
                    if(myInv.getSize()-1 < pos){
                        plugin.getLogger().warning("Item '"+key+"' in menu '"+menu+"' has an invalid position!");
                        continue;
                    }
                    item.setItemMeta(meta);
                    if(meta instanceof Damageable){
                        Damageable d = (Damageable) item.getItemMeta();
                        d.setDamage(damage);
                        item.setItemMeta((ItemMeta) d);
                    }
                    myInv.setItem(pos,item);
                } else {
                    plugin.getLogger().warning("Position '"+pos+"' for item '"+key+"' in menu '"+menu+"' is already occupied!");
                }
            }
        }
        target.openInventory(myInv);
    }
}
