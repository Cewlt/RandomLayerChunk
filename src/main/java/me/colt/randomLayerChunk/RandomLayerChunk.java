package me.colt.randomLayerChunk;

import me.colt.randomLayerChunk.commands.ClearChunkCommand;
import me.colt.randomLayerChunk.commands.RLCCommand;
import me.colt.randomLayerChunk.events.MilkCowEvent;
import me.colt.randomLayerChunk.events.PlayerLeave;
import me.colt.randomLayerChunk.worldgeneration.VoidWorldGenerator;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class RandomLayerChunk extends JavaPlugin {
    private final RandomLayerChunk randomLayerChunk = this;
    private ConfigManager configManager;
    private List<CustomChunk> activeCustomChunks;

    public ArrayList<Material> disallowedBlocks;
    public ArrayList<Material> allowedBlocks;
    public boolean disallowShulkers;
    public boolean disallowInfestedBlocks;

    @Override
    public void onEnable() {
        RandomLoot.loadList();
        configManager = new ConfigManager(this);
        configManager.loadAllConfig();
        getCommand("clearchunk").setExecutor(new ClearChunkCommand(randomLayerChunk));
        getCommand("rlc").setExecutor(new RLCCommand(randomLayerChunk));
        new MilkCowEvent(randomLayerChunk);
        new PlayerLeave(randomLayerChunk);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new VoidWorldGenerator();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public List<CustomChunk> getActiveCustomChunks() {
        return activeCustomChunks;
    }

    public CustomChunk getCustomChunk(Chunk chunk) {
        if(!isCustomChunk(chunk)) return null;
        for(CustomChunk customChunk : activeCustomChunks) {
            if(customChunk.getBukkitChunk().getX() == chunk.getX()) return customChunk;
        }
        return null;
    }

    public boolean isCustomChunk(Chunk chunk) {
        if(activeCustomChunks == null || activeCustomChunks.isEmpty()) return false;
        for(CustomChunk customChunk : activeCustomChunks) {
            if(customChunk.getBukkitChunk().getX() == chunk.getX()) return true;
        }
        return false;
    }

    public boolean hasCustomChunk(UUID uuid) {
        return getActiveCustomChunks()
                .stream()
                .anyMatch(customChunk -> customChunk.getOwnerUUID().equals(uuid));
    }

    public CustomChunk getPlayerChunk(UUID uuid) {
        if(!hasCustomChunk(uuid)) return null;
        return getActiveCustomChunks()
                .stream()
                .filter(customChunk -> customChunk.getOwnerUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Material> getDisallowedBlocks() {
        return disallowedBlocks;
    }

    public CustomChunk startRandomLayerChunk(Chunk chunk, UUID uuid) {
        if(activeCustomChunks == null) activeCustomChunks = new ArrayList<>();
        if(allowedBlocks == null) allowedBlocks = new ArrayList<>();
        CustomChunk customChunk = new CustomChunk(randomLayerChunk, chunk, uuid);
        activeCustomChunks.add(customChunk);
        return customChunk;
    }

    public Material getRandomBlock() {
        if(allowedBlocks.isEmpty()) {
            allowedBlocks.add(Material.WATER_CAULDRON);
            allowedBlocks.add(Material.LAVA_CAULDRON);
            for(Material m : Material.values()) {
                if(m.name().contains("CHEST")) continue;
                if(!m.isBlock() || !m.isOccluding()
                    || m.hasGravity() || !m.isSolid()) continue;
                if(disallowedBlocks.contains(m)) continue;
                if(disallowShulkers && m.name().contains("SHULKER")) continue;
                if(disallowInfestedBlocks && m.name().contains("INFESTED")) continue;
                allowedBlocks.add(m);
            }
        }
        Material randomBlock = allowedBlocks.get(new Random().nextInt(allowedBlocks.size()));
        return randomBlock;
    }
}
