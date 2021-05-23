package me.hope.core.inject;

import me.hope.core.inject.annotation.NotSingleton;
import org.bukkit.plugin.java.JavaPlugin;

@NotSingleton
public class InjectorBuilder {
    private final Injector injector;
    public InjectorBuilder(){
        injector = new Injector();
    }
    public InjectorBuilder setPlugin(JavaPlugin plugin){
        injector.setPlugin(plugin);
        return this;
    }
    public InjectorBuilder setDefaultPath(String path){
        injector.setHandlerPath(path);
        return this;
    }
    public Injector build(){
        injector.reloadSingleton();
        return injector;
    }
}
