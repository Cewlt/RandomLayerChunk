package me.colt.randomLayerChunk.commands;

import me.colt.randomLayerChunk.CustomChunk;
import me.colt.randomLayerChunk.RandomLayerChunk;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChunkCommand implements CommandExecutor {
    private final RandomLayerChunk randomLayerChunk;

    public ClearChunkCommand(RandomLayerChunk randomLayerChunk) { this.randomLayerChunk = randomLayerChunk; }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("clearchunk")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player)commandSender;
            player.sendMessage("clearing your current chunk");
            Chunk playerChunk = player.getLocation().getChunk();
            if(randomLayerChunk.isCustomChunk(playerChunk)) {
                CustomChunk customChunk = randomLayerChunk.getCustomChunk(playerChunk);
                customChunk.deleteCustomChunk();
                player.sendMessage("this chunk was a custom chunk");
            } else {
                CustomChunk.deleteChunk(playerChunk);
                player.sendMessage("this was not a custom chunk");
            }
            return true;
        }
        return false;
    }
}
