package me.hope.core;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum CommandType {
    /**
     * 仅玩家能执行
     */
    PLAYER,
    /**
     * 仅控制台能执行
     */
    CONSOLE,
    /**
     * 都可以执行
     */
    ALL;

    CommandType(){}

    public boolean canPass(CommandSender sender){
        if (this == PLAYER){
            return (sender instanceof Player);
        }else if (this == CONSOLE){
            return (sender instanceof ConsoleCommandSender);
        }else{
            return true;
        }
    }
}
