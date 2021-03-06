package hu.Pdani.TSMenu.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Updater {
    private FileConfiguration config;
    private String repo;
    private String latest;
    private JavaPlugin plugin;
    /**
     * Creates a new Updater object
     * @param repo Github repository (e.g. Owner/Plugin)
     */
    public Updater(String repo, JavaPlugin plugin){
        if(repo == null || repo.isEmpty())
            return;
        if(plugin == null)
            return;
        this.plugin = plugin;
        this.repo = repo;
        try {
            URL url = new URL("https://raw.githubusercontent.com/"+repo+"/master/src/plugin.yml");
            Scanner scan = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while(scan.hasNext()){
                sb.append(scan.nextLine());
                sb.append(System.getProperty("line.separator"));
            }
            config = new YamlConfiguration();
            config.loadFromString(sb.toString());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            config = null;
        }
    }

    /**
     * Get the current repo
     * @return the current repo
     */
    public String getRepo(){
        return repo;
    }

    /**
     * Get the latest version if one is available
     * @return the latest version if available, null otherwise
     */
    public String getLatest(){
        return latest;
    }

    /**
     * Check if there is a newer version available
     * @return true if there is a newer version
     */
    public boolean check(){
        if(config != null){
            String current = plugin.getDescription().getVersion();
            String latest = config.getString("version");
            if(compareTo(current,latest) == -1){
                this.latest = latest;
                return true;
            }
        }
        return false;
    }

    private int compareTo(String current, String check) {
        if(current == null || check == null)
            return 0;
        String[] thisParts = current.split("\\.");
        String[] thatParts = check.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }
}
