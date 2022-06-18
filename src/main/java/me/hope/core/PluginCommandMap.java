package me.hope.core;

import com.google.common.collect.Maps;
import me.hope.core.inject.annotation.CommandAlias;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;

public class PluginCommandMap <O extends JavaPlugin> implements CommandExecutor {
    private final O instance;
    protected Map<String, CommandExecutor> commands;
    public PluginCommandMap(O plugin){
        this.instance = plugin;
        commands = Maps.newHashMap();
    }


    public  <T extends CommandExecutor> T getCommandExecutor(String name) {
        return (T) commands.get(name);
    }

    public  <T extends CommandExecutor> T registerCommand(String name, T command) {
        return (T) commands.put(name, command);
    }

    public Map<String, CommandExecutor> getCommandMap() {
        return commands;
    }

    private <T extends CommandExecutor> boolean checkCommandLabel(Class<T> commandExecutorClazz,String commandName,String labelName){
        if (commandName.equalsIgnoreCase(labelName)){
            return true;
        }else {
            if (commandExecutorClazz.isAnnotationPresent(CommandAlias.class)) {
                CommandAlias commandAlias = commandExecutorClazz.getAnnotation(CommandAlias.class);
                for (String alias : commandAlias.value()) {
                    if (alias.equalsIgnoreCase(labelName)) {
                        return true;
                    }
                }
            }
            return false;
        }
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
                if (args ==null | Objects.requireNonNull(args).length==0){ return true;}
                for(Map.Entry<String,CommandExecutor> entry : getCommandMap().entrySet()){
                    if (checkCommandLabel(entry.getValue().getClass(),(label = entry.getKey()),args[0])){
                        String[] new_args = {};
                        if (args.length >1){
                            new_args = new String[args.length - 1];
                            System.arraycopy(args,1,new_args,0, (args.length - 1));
                        }
                        success = entry.getValue().onCommand(sender,command,label,new_args);
                    }
                }
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + instance.getDescription().getFullName(), var9);
            }
            return success;
        }

    }
}
