package me.hope;

import me.hope.core.inject.Injector;
import me.hope.core.inject.InjectorBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class HopeCore extends JavaPlugin {
    public static HopeCore instance;
    public static Logger logger;
    @Override
    public void onEnable() {
        Injector.clearSingletons();
        logger.info("正在清除全部缓存");
        Injector injector = new InjectorBuilder().setDefaultPath("me.hope.core").setPlugin(this).build();
        instance = injector.register(HopeCore.class,this);
        logger = injector.register(Logger.class,getLogger());

        injector.injectClasses();
        logger.info("Version:" + this.getDescription().getVersion());
    }
}
