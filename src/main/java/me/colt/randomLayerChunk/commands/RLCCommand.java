package me.colt.randomLayerChunk.commands;

import me.colt.randomLayerChunk.RandomLayerChunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RLCCommand implements CommandExecutor {
    private final RandomLayerChunk randomLayerChunk;

    public RLCCommand(RandomLayerChunk randomLayerChunk) {
        this.randomLayerChunk = randomLayerChunk;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("rlc")) {
            if (!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            player.sendMessage("starting random layer chunk");
            randomLayerChunk.startRandomLayerChunk(player.getLocation().getChunk());
            return true;
        }
        return false;
    }
}
