package hu.Pdani.TSMenu;

import com.sun.istack.internal.NotNull;
import hu.Pdani.TSItem.TSItemPlugin;
import hu.Pdani.TSMenu.listener.CommandListener;
import hu.Pdani.TSMenu.listener.InventoryListener;
import hu.Pdani.TSMenu.manager.FileManager;
import hu.Pdani.TSMenu.manager.GuiManager;
import hu.Pdani.TSMenu.utils.Updater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSMenuPlugin extends JavaPlugin {

    private static FileManager fm;
    private static GuiManager gm;
    private static TSMenuPlugin plugin;
    private static TSItemPlugin tsitem;

    @Override
    public void onEnable() {
        plugin = this;
        Updater updater = new Updater("TheServer-wtf/TSMenu",plugin);
        if(updater.check()){
            getLogger().warning("There is a new version ("+updater.getLatest()+") available! Download it at https://github.com/"+updater.getRepo());
        } else {
            getLogger().info("You are running the latest version.");
        }
        Plugin tsitpl = getServer().getPluginManager().getPlugin("TSItem");
        if((tsitpl instanceof TSItemPlugin)){
            tsitem = (TSItemPlugin) tsitpl;
        }
        fm = new FileManager(this);
        fm.loadFiles();
        gm = new GuiManager(this);
        PluginCommand cmd = getCommand("tsmenu");
        if(cmd != null)
            cmd.setExecutor(new CommandListener());
        getServer().getPluginManager().registerEvents(new InventoryListener(),this);
        CommandMap commandMap = null;
        try {
            Field f = null;
            f = getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe(e.getMessage());
        }
        if(commandMap != null){
            for(String k : fm.getCommandFiles()){
                String mc = fm.getCommand(k);
                if(mc != null){
                    new MyCommand(commandMap, this, mc.toLowerCase());
                }
            }
        }
        getLogger().info("The plugin is now enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin is now disabled!");
    }

    public void registerCommand(@NotNull String command){
        if(command == null || command.isEmpty())
            return;
        CommandMap commandMap = null;
        try {
            Field f = null;
            f = getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe(e.getMessage());
        }
        if(commandMap != null){
            if(getServer().getPluginCommand(command.toLowerCase()) == null)
                new MyCommand(commandMap, this, command.toLowerCase());
        }
    }

    public static TSMenuPlugin getPlugin() {
        return plugin;
    }

    public TSItemPlugin getTSItem() { return getTSItemPlugin(); }

    public static TSItemPlugin getTSItemPlugin() { return tsitem; }

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
