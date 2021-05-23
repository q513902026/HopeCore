package me.hope.core.inject;

import com.google.common.collect.Maps;
import me.hope.HopeCore;
import me.hope.core.inject.annotation.Inject;
import me.hope.core.inject.annotation.NotSingleton;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;

@NotSingleton
public class Injector {
    private  final Map<Class<?>, Object> instances = Maps.newHashMap();
    private  String handlerPath = "";
    private  JavaPlugin plugin;
    private  Singleton<? extends JavaPlugin> singleton;
    private  boolean init = true;

    public void setHandlerPath(String path){
        handlerPath = path;
    }

    public void setPlugin(JavaPlugin plugin){
        this.plugin = plugin;
    }
    public void reloadSingleton(){
        if (init){
            init = false;
            Singleton.clear(plugin);
        }
        singleton = Singleton.getInstance(plugin);
    }
    public  <T> T register(Class<T> clazz, T instance) {
        provide(instance);
        instances.put(clazz, instance);
        return instance;
    }

    public  void provide(Object instance){
        singleton.set(instance.getClass(),instance);
    }

    public  <O> O getSingleton(Class<O> clazz){
        return singleton.get(clazz);
    }

    public  void injectClasses(){
        for(Class<?> clazz : InjectFinder.getClasses(HopeCore.instance,"me.hope.core",true)){
            inject(clazz);
        }
        for(Class<?> clazz :InjectFinder.getClasses(getSingleton(plugin.getClass()),handlerPath,true)){
            inject(clazz);
        }
    }
    public  void inject(Class<?> clazz){
        Object instance = getSingleton(clazz);
        injectFields(instance,clazz);
    }

    public  void inject(Object obj) {
        Class<?> clazz =  obj.getClass();
        injectFields(obj, clazz);
    }

    private  void injectFields(Object obj, Class<?> clazz) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (instances.containsKey(field.getType())) {
                    field.set(obj, instances.get(field.getType()));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static void clearSingletons(){
        Singleton.clearAll();
    }
    @NotSingleton
    private static class Singleton<T extends JavaPlugin>{
        private static final Map<JavaPlugin, Singleton<? extends JavaPlugin>>  enumSingleton = Maps.newHashMap();
        public static <T extends JavaPlugin> Singleton<? extends JavaPlugin> getInstance(T plugin){
            if(!enumSingleton.containsKey(plugin)){
                enumSingleton.put(plugin,new Singleton<JavaPlugin>(plugin));
                //System.out.println("[Hope's Singleton]: "+plugin.getDescription().getName()+" is register!");
            }
            return enumSingleton.get(plugin);
        }
        public static <T extends JavaPlugin> void clear(T plugin){
            enumSingleton.remove(plugin);
        }
        static void clearAll(){
            enumSingleton.clear();
        }
        private final T plugin;
        private Singleton(T plugin){
            this.plugin = plugin;
        }
        private final Map<Class<?>,Object> singleton = Maps.newHashMap();
        private void set(Class<?> clazz, Object instance){
            if(!singleton.containsKey(clazz)){
                singleton.put(clazz,instance);
                //System.out.println("[Hope's Singleton<"+this.plugin.getDescription().getName()+">]: <"+clazz.getName()+"> set singleton!");
            }
        }
        public  <O> O get(Class<O> clazz){
            if (!singleton.containsKey(clazz)){
                try {
                    set(clazz,clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException  e) {
                    return null;
                }
            }
            return (O) singleton.get(clazz);
        }

        public T getPlugin() {
            return plugin;
        }
    }
}
