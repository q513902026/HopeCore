package me.hope.core;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PluginConfig<O extends JavaPlugin> {

    protected Map<String,FileConfiguration> configs = Maps.newHashMap();
    protected Map<String,File> configFiles = Maps.newHashMap();
    protected File dataFolder;
    private PluginLogger logger;
    private O instance;

    private PluginConfig(File dataFolder){
        this.dataFolder = dataFolder;
    }
    private PluginConfig(File dataFolder,String configName){
        this.dataFolder = dataFolder;
        this.configFiles.put(configName,new File(this.dataFolder,configName+".yml"));
    }
    public PluginConfig(O instance,String configName){
        this(instance.getDataFolder(),configName);
        this.instance = instance;
    }
    public PluginConfig(O instance, List<String> configNames){
        this(instance.getDataFolder());
        this.instance = instance;
        for(String configName:configNames){
            addConfig(configName);
        }
    }
    public PluginConfig(O instance,String subFolder,String configName){
        this(new File(instance.getDataFolder()+File.separator+subFolder,configName),configName);
        this.instance = instance;
    }
    public void setPluginLogger(PluginLogger pluginLogger){
        this.logger = pluginLogger;
    }
    public void addConfig(String configName){
        this.configFiles.put(configName,new File(this.dataFolder,configName+".yml"));
    }
    public Map<String,Object> getConfigMaps(String configName){
        return configs.get(configName).getValues(true);
    }

    public void saveDefaultConfig(String configName){
        if (!configFiles.get(configName).exists()) {
            saveCustomConfig(configName + ".yml", false);
        }
    }
    public void reloadConfig(String configName) {
        configs.put(configName,YamlConfiguration.loadConfiguration(configFiles.get(configName)));

        final InputStream defConfigStream = getCustomConfig(configName + ".yml");
        if (defConfigStream == null) {
            return;
        }
        configs.get(configName).setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }
    public FileConfiguration getConfig(String configName){
        if (configs.get(configName) == null) {
            reloadConfig(configName);
        }
        return configs.get(configName);
    }
    public void saveConfig(String configName){
        try {
            getConfig(configName).save(configFiles.get(configName));
        } catch (IOException ex) {
            instance.getLogger().warning("Could not save config to " + configFiles.get(configName));
        }
    }
    private void saveCustomConfig(String customConfigPath,boolean replace){
        instance.saveResource(customConfigPath, replace);
    }
    private InputStream getCustomConfig(String name){
        return instance.getResource(name);
    }

    public void saveAllDefaultConfig() {

        for(String configName:configFiles.keySet()){
            //logger.sendConsoleMessage("load Config<"+configName+"> ...");
            saveDefaultConfig(configName);
        }
        logger.sendConsoleMessage("Config is Loaded!");
    }
    public void reloadAllConfig(){
        logger.sendConsoleMessage("Reload All Config!");
        for(String configName:configFiles.keySet()){
            reloadConfig(configName);
        }
    }
    public Set<FileConfiguration> getConfigs(){
        Set<FileConfiguration> config_sets = Sets.newHashSet();
        for(String configName:configFiles.keySet()){
            config_sets.add(getConfig(configName));
        }
        return config_sets;
    }
}
