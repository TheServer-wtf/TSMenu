package hu.Pdani.TSMenu.manager;

import hu.Pdani.TSMenu.TSMenuPlugin;
import hu.Pdani.TSMenu.utils.TSMException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileManager {
    private HashMap<String, File> files = new HashMap<>();
    private HashMap<String, FileConfiguration> configs = new HashMap<>();
    private HashMap<String,String> cmds = new HashMap<>();
    private TSMenuPlugin plugin;
    public FileManager(TSMenuPlugin plugin){
        this.plugin = plugin;
    }

    public void reloadFiles(){
        files.clear();
        configs.clear();
        plugin.getLogger().info("Reloading menu files...");
        loadFiles();
    }

    public void loadFiles(){
        if(!files.isEmpty() && !configs.isEmpty()){
            return;
        }
        File dir = new File(plugin.getDataFolder(),"/menus");
        if(!dir.exists()) {
            plugin.getDataFolder().mkdir();
            dir.mkdir();
            plugin.saveResource("menus"+File.separator+"first.yml",false);
            plugin.saveResource("menus"+File.separator+"second.yml",false);
        }
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for(File child : directoryListing){
                if(!child.isFile())
                    continue;
                String name = child.getName().replace(".yml","");
                if(name.contains(" ")){
                    plugin.getLogger().warning("Ignoring file '"+name+".yml': invalid name");
                    continue;
                }
                if(files.containsKey(name)){
                    plugin.getLogger().warning("Ignoring file '"+name+".yml': duplicate entry");
                    continue;
                }
                files.put(name,child);
                configs.put(name, YamlConfiguration.loadConfiguration(child));
                FileConfiguration sec = configs.get(name);
                String mc = sec.getString("COMMAND",null);
                if(mc != null && !mc.isEmpty()){
                    if(cmds.containsKey(name) && !cmds.get(name).equalsIgnoreCase(mc)){
                        cmds.remove(name);
                    }
                    cmds.put(name,mc);
                    plugin.registerCommand(mc);
                } else {
                    cmds.remove(name);
                }
            }
        }
        plugin.getLogger().info("Loaded "+files.size()+" menu files!");
    }

    public void reloadFile(String name) throws TSMException,IllegalArgumentException {
        unloadFile(name);
        loadFile(name);
    }

    public void loadFile(String name) throws TSMException,IllegalArgumentException {
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("The specified name is invalid!");
        }
        File file = new File(plugin.getDataFolder(),"/menus/"+name+".yml");
        if(!file.exists()){
            throw new TSMException("The requested file does not exist.");
        }
        String obj = name;
        if(name.contains(".yml"))
            obj = obj.replace(".yml","");
        if(files.containsKey(obj)){
            plugin.getLogger().warning("Ignoring file '"+obj+".yml': duplicate entry");
            return;
        }
        files.put(obj,file);
        configs.put(obj, YamlConfiguration.loadConfiguration(file));
        FileConfiguration sec = configs.get(name);
        String mc = sec.getString("COMMAND",null);
        if(mc != null && !mc.isEmpty()){
            if(cmds.containsKey(name) && !cmds.get(name).equalsIgnoreCase(mc)){
                cmds.remove(name);
            }
            cmds.put(name,mc);
            plugin.registerCommand(mc);
        } else {
            cmds.remove(name);
        }
    }

    public void unloadFile(String name) throws TSMException,IllegalArgumentException{
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("The specified name is invalid!");
        }
        if(!files.containsKey(name)){
            throw new TSMException("The specified file is not loaded!");
        }
        files.remove(name);
        configs.remove(name);
    }

    public FileConfiguration getConfig(String name){
        return configs.get(name);
    }

    public List<String> getListPage(int page){
        String[] list = files.keySet().toArray(new String[0]);
        List<String> send = new ArrayList<>();
        int limit = 10;
        int start = (page-1) * limit;
        if(start >= list.length)
            start = 0;
        int end = start + limit;
        if(list.length < end)
            end = list.length;
        for(int i = start; i < end; i++){
            send.add(list[i]);
        }
        return send;
    }

    public String[] getFileList(){
        return files.keySet().toArray(new String[0]);
    }

    public String getCommand(String file){
        return cmds.get(file);
    }

    public String[] getCommandFiles(){
        return cmds.keySet().toArray(new String[0]);
    }

    public boolean hasCommand(String file){
        return cmds.containsKey(file);
    }

    public boolean isCommand(String cmd){
        return cmds.containsValue(cmd);
    }

    public String getFileOnCommand(String cmd){
        for(String k : cmds.keySet()){
            if(cmds.get(k).equalsIgnoreCase(cmd)){
                return k;
            }
        }
        return null;
    }
}
