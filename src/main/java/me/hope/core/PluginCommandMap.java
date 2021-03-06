package me.hope.core;

import com.google.common.collect.Maps;
import me.hope.core.inject.annotation.command.CommandAlias;
import me.hope.core.inject.annotation.command.CommandPermission;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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
    public <T extends CommandExecutor> void registerCommands(Map<String,Class<? extends T>> commandMap, Function<Class<? extends T>, T> loadFunction){
        for (Map.Entry<String, Class<? extends T>> classEntry : commandMap.entrySet()) {
            registerCommand(classEntry.getKey(),loadFunction.apply(classEntry.getValue()));
        }
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
    private <T extends CommandExecutor> boolean checkCommandPermission(Class<T> commandExecutorClazz,CommandSender sender){
        if (commandExecutorClazz.isAnnotationPresent(CommandPermission.class)){
            CommandPermission commandPermission = commandExecutorClazz.getAnnotation(CommandPermission.class);
            if (commandPermission.type() == CommandType.ALL){
                return sender.hasPermission(commandPermission.value());
            }else{
                return commandPermission.type().canPass(sender);
            }
        }else{
            return true;
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
                    Class<? extends CommandExecutor> commandExecutorClazz = entry.getValue().getClass();
                    String commandLabel = entry.getKey();
                    if (checkCommandLabel(commandExecutorClazz,commandLabel,args[0]) & checkCommandPermission(commandExecutorClazz,sender)){
                        String[] new_args = {};
                        if (args.length >1){
                            new_args = new String[args.length - 1];
                            System.arraycopy(args,1,new_args,0, (args.length - 1));
                        }
                        success = entry.getValue().onCommand(sender,command,commandLabel,new_args);
                    }
                }
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + label + "' in plugin " + instance.getDescription().getFullName(), var9);
            }
            return success;
        }

    }
}
