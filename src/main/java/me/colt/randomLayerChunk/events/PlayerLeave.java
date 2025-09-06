package me.colt.randomLayerChunk.events;

import me.colt.randomLayerChunk.CustomChunk;
import me.colt.randomLayerChunk.RandomLayerChunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLeave implements Listener {
    private final RandomLayerChunk randomLayerChunk;

    public PlayerLeave(RandomLayerChunk randomLayerChunk) {
        this.randomLayerChunk = randomLayerChunk;
        randomLayerChunk.getServer().getPluginManager().registerEvents(this, randomLayerChunk);
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if(randomLayerChunk.hasCustomChunk(uuid)) {
            CustomChunk customChunk = randomLayerChunk.getPlayerChunk(uuid);
            if(customChunk != null) customChunk.stopScheduler();
        }
    }
}
