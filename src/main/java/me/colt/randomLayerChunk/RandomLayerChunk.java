package me.colt.randomLayerChunk;

import me.colt.randomLayerChunk.commands.ClearChunkCommand;
import me.colt.randomLayerChunk.commands.RLCCommand;
import me.colt.randomLayerChunk.events.MilkCowEvent;
import me.colt.randomLayerChunk.worldgeneration.VoidWorldGenerator;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public final class RandomLayerChunk extends JavaPlugin {
    private final RandomLayerChunk randomLayerChunk = this;
    private List<CustomChunk> activeCustomChunks;

    private ArrayList<Material> disallowedBlocks;
    private ArrayList<Material> allowedBlocks;
    private boolean disallowShulkers = false;
    private boolean disallowInfestedBlocks = false;

    // what height the random layer chunk will start at
    private int startHeightY = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        RandomLoot.loadList();
        getCommand("clearchunk").setExecutor(new ClearChunkCommand(randomLayerChunk));
        getCommand("rlc").setExecutor(new RLCCommand(randomLayerChunk));
        new MilkCowEvent(randomLayerChunk);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new VoidWorldGenerator();
    }

    public List<CustomChunk> getActiveCustomChunks() {
        return activeCustomChunks;
    }

    public int getStartHeightY() {
        if(startHeightY == 0) {
            loadStartHeightConfig();
        }
        return startHeightY;
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

    private void loadStartHeightConfig() {
        if(!this.getConfig().contains("startHeightY")
                || !this.getConfig().isInt("startHeightY")) {
            startHeightY = 250;
            return;
        }
        this.startHeightY = this.getConfig().getInt("startheightY");
    }

    private void loadDisallowedBlocksConfig() {
        if(disallowedBlocks == null) disallowedBlocks = new ArrayList<>();
        if(!this.getConfig().contains("disallowed-blocks")) return;
        List<String> disallowList = this.getConfig().getStringList("disallowed-blocks");
        if(disallowList.isEmpty()) return;
        if(disallowList.contains("SHULKER")) {
            disallowList.remove("SHULKER");
            disallowShulkers = true;
        }
        if(disallowList.contains("INFESTED")) {
            disallowList.remove("INFESTED");
            disallowInfestedBlocks = true;
        }
        for(String blockName : disallowList) {
            Material mat = Material.matchMaterial(blockName);
            if(mat == null) {
                Bukkit.broadcastMessage(blockName + " could not be matched");
                continue;
            }
            disallowedBlocks.add(mat);
        }
    }

    public ArrayList<Material> getDisallowedBlocks() {
        return disallowedBlocks;
    }

    public CustomChunk startRandomLayerChunk(Chunk chunk) {
        if(activeCustomChunks == null) activeCustomChunks = new ArrayList<>();
        if(allowedBlocks == null) allowedBlocks = new ArrayList<>();
        if(disallowedBlocks == null) loadDisallowedBlocksConfig();
        CustomChunk customChunk = new CustomChunk(randomLayerChunk, chunk);
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
