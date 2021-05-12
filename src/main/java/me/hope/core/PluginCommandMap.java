package me.hope.core;

import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PluginCommandMap <O extends JavaPlugin> implements CommandExecutor {
    private O instance;
    public PluginCommandMap(O plugin){
        this.instance = plugin;
    }
    protected static Map<String, CommandExecutor> commands = Maps.newHashMap();

    public  <T extends CommandExecutor> T getCommandExecutor(String name) {
        return (T) commands.get(name);
    }

    public  <T extends CommandExecutor> T registerCommand(String name, T command) {
        T put = (T) commands.put(name, command);
        //instance.getLogger().info("[] : 命令" + name + "注册完成.");
        return put;
    }

    public Map<String, CommandExecutor> getCommandMap() {
        return commands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean success = false;
        if (!instance.isEnabled()) {
            throw new CommandException("Cannot execute command '" + label + "' in plugin " + instance.getDescription().getFullName() + " - plugin is disabled.");
        } else if (!command.testPermission(sender)) {
            return true;
        } else {
            try {
                if (args ==null | args.length==0){ return true;}
                for(Map.Entry<String,CommandExecutor> entry : getCommandMap().entrySet()){
                    if (args[0].equalsIgnoreCase(entry.getKey())){
                        label = entry.getKey();
                        String[] newargs = {};
                        if (args.length >1){
                            newargs = new String[args.length - 1];
                            System.arraycopy(args,1,newargs,0, (args.length - 1));
                        }
                        success = entry.getValue().onCommand(sender,command,entry.getKey(),newargs);
                    }
                }
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + instance.getDescription().getFullName(), var9);
            }
            return success;
        }

    }
}
