package me.hope;

import com.google.common.collect.Lists;
import me.hope.core.PluginCommandMap;
import me.hope.core.PluginConfig;
import me.hope.core.PluginLogger;
import me.hope.core.inject.Injector;
import me.hope.core.inject.InjectorBuilder;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public class HopeGift extends JavaPlugin {
    private static PluginLogger loger ;
    private static HopeGift instance;
    private static PluginCommandMap<HopeGift> commandMap;
    private static PluginConfig configManager;
    private static Injector injector;

    @Override
    public void onEnable() {
        registerBeans();
        loger.sendConsoleMessage("CommandExecutor is Register!");
        this.getCommand("hopegift").setExecutor(commandMap::onCommand);
        loger.sendConsoleMessage("Hope's Injecter is running!");

        injector.injectClasses();

        configManager.saveAllDefaultConfig();
    }

    private void registerBeans() {
        injector = new InjectorBuilder().setPlugin(this).setDefaultPath("me.hope").build();

        instance = injector.register(HopeGift.class,this);
        loger = injector.register(PluginLogger.class,new PluginLogger(this.getLogger(),getCustomDataFile("logs/info.log")));
        commandMap = injector.register(PluginCommandMap.class,new PluginCommandMap<HopeGift>(this));
        configManager = injector.register(PluginConfig.class,new PluginConfig<HopeGift>(this, Lists.newArrayList("config","gift","cdk")));

        loger.sendConsoleMessage("Config Loader!");

        injector.register(Server.class,getServer());
        injector.register(PluginManager.class,getServer().getPluginManager());

    }

    @Override
    public void onDisable() {
        instance =  null;
        loger =     null;
        this.getCommand("hopegift").setExecutor(null);
        commandMap = null;
    }

    public File getCustomDataFile(String name){
        File file = new File(this.getDataFolder(),name);
        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.getLogger().warning("日志文件创建失败.");
            }
        }
        return file;
    }
}
