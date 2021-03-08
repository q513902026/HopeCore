package me.hope.core.inject;

import me.hope.core.inject.annotation.NotSinglethon;
import org.bukkit.plugin.java.JavaPlugin;

@NotSinglethon
public class InjectorBuilder {
    private Injector injector;
    public InjectorBuilder(){
        injector = new Injector();
    }
    public InjectorBuilder setPlugin(JavaPlugin plugin){
        injector.setPlugin(plugin);
        return this;
    }
    public InjectorBuilder setDefaultPath(String path){
        injector.setHanderPath(path);
        return this;
    }
    public Injector build(){
        injector.reloadSingleton();
        return injector;
    }
}
