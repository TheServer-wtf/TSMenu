package hu.Pdani.TSMenu;

import hu.Pdani.TSMenu.listener.CommandListener;
import hu.Pdani.TSMenu.listener.InventoryListener;
import hu.Pdani.TSMenu.manager.FileManager;
import hu.Pdani.TSMenu.manager.GuiManager;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSMenuPlugin extends JavaPlugin {

    private static FileManager fm;
    private static GuiManager gm;
    private static TSMenuPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        fm = new FileManager(this);
        fm.loadFiles();
        gm = new GuiManager(this);
        PluginCommand cmd = getCommand("tsmenu");
        if(cmd != null)
            cmd.setExecutor(new CommandListener());
        getServer().getPluginManager().registerEvents(new InventoryListener(),this);
        getLogger().info("The plugin is now enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin is now disabled!");
    }

    public static TSMenuPlugin getPlugin() {
        return plugin;
    }

    public GuiManager getGm() {
        return getGuiManager();
    }

    public static GuiManager getGuiManager() {
        return gm;
    }

    public FileManager getFM(){
        return getFileManager();
    }

    public static FileManager getFileManager(){
        return fm;
    }

    public static String c(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }

    public static String rFirst(String target, String search, String replace){
        return target.replaceFirst(Pattern.quote(search), Matcher.quoteReplacement(replace));
    }

}
