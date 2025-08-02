package me.colt.randomLayerChunk.events;

import me.colt.randomLayerChunk.CustomChunk;
import me.colt.randomLayerChunk.RandomLayerChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class MilkCowEvent implements Listener {
    private RandomLayerChunk randomLayerChunk;

    public MilkCowEvent(RandomLayerChunk randomLayerChunk) {
        this.randomLayerChunk = randomLayerChunk;
        randomLayerChunk.getServer().getPluginManager().registerEvents(this, randomLayerChunk);
    }

    @EventHandler
    public void milkCowEvent(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().getType() == EntityType.MOOSHROOM) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOWL) {
                Player player = event.getPlayer();
                Chunk playerChunk = player.getLocation().getChunk();
                if(randomLayerChunk.isCustomChunk(playerChunk)) {
                    CustomChunk customChunk = randomLayerChunk.getCustomChunk(playerChunk);
                    customChunk.shrinkBigCow();
                    player.sendMessage("u shrunk big cow - keep going");
                }
            }
        }
    }

}
