package me.hope.core.inject;

import com.google.common.collect.Sets;
import me.hope.core.inject.annotation.NotSinglethon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@NotSinglethon
public class InjectFinder {
    private static JarFile jarfile;
    public static <T extends JavaPlugin> Collection<Class<?>> getClasses(T plugin, String rootPackage, boolean recursive)  {
        Set<Class<?>> classes = Sets.newHashSet();
        String packDirName = rootPackage.replace('.', '/');
        Enumeration<URL> dirs;
        try{
            dirs = plugin.getClass().getClassLoader().getResources(packDirName);
            while(dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                switch(protocol){
                    case "file":
                        String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
                        classes.addAll(findClassesByFile(rootPackage,filePath,recursive));
                        break;
                    case "jar":
                        JarURLConnection conn = (JarURLConnection) url.openConnection();
                        JarFile jar = conn.getJarFile();
                        if (jar == null){
                            jar = jarfile;
                        }else{
                            jarfile = jar;
                        }
                        classes.addAll(findClassesByJar(rootPackage,jar,recursive));
                        break;
                }
            }
        }catch(IOException e){

        }
        return classes;
    }
    private static Collection<Class<?>> findClassesByJar(String rootPackage,JarFile jarFile,boolean recursive){
        Set<Class<?>> classes = Sets.newHashSet();
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        String packDirName = rootPackage.replace('.', '/');
        while(jarEntryEnumeration.hasMoreElements()){
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String name = jarEntry.getName();
            if('/' == name.charAt(0)){
                name = name.substring(1);
            }
            if(name.startsWith(packDirName)){
                int index = name.lastIndexOf('/');
                if(index != -1 ){
                    rootPackage = name.substring(0,index).replace('/','.');
                }
                if((index != -1) || recursive){
                    if(name.endsWith(".class") && !(jarEntry.isDirectory())){
                        String className = name.substring(rootPackage.length()+1,name.length()-6);
                        try {
                            Class clazz = getClass(rootPackage,className);
                            if (clazz != null){
                                classes.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return classes;
    }
    private static Collection<Class<?>> findClassesByFile(String rootPackage,String filePath,boolean recursive){
        Set<Class<?>> classes = Sets.newHashSet();
        findAndAddClasses(rootPackage,filePath,recursive,classes);
        return classes;
    }
    private static void findAndAddClasses(String rootPackage,String filePath,boolean recursive,Collection<Class<?>> classes){
        File dir = new File(rootPackage);
        if(!dir.exists() || !dir.isDirectory()){
            return;
        }
        File[] dirFiles = dir.listFiles(pathname -> (recursive && pathname.isDirectory()) || (pathname.getName().endsWith(".class")));
        for(File file:dirFiles){
            if(file.isDirectory()){
                findAndAddClasses(rootPackage+"."+file.getName(),file.getAbsolutePath(),recursive,classes);
            }else{
                String className =file.getName().substring(0,file.getName().length()-6);
                try{
                    Class clazz = getClass(rootPackage,className);
                    if (clazz != null){
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static Class<?> getClass(String rootPackage,String className) throws ClassNotFoundException {
        Class clazz = Class.forName(rootPackage + '.' + className);
        return clazz.isAnnotationPresent(NotSinglethon.class) ? null:clazz;
    }
}
