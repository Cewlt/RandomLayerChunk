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
    public final RandomLayerChunk randomLayerChunk = this;
    private List<CustomChunk> activeCustomChunks;

    private ArrayList<Material> disallowedBlocks;
    private ArrayList<Material> allowedBlocks;
    private boolean disallowShulkers = false;
    private boolean disallowInfestedBlocks = false;
    public int startHeightY = 250;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("clearchunk").setExecutor(new ClearChunkCommand(randomLayerChunk));
        getCommand("rlc").setExecutor(new RLCCommand(randomLayerChunk));
        new MilkCowEvent(randomLayerChunk);
    }

    @Override
    public void onDisable() {
        // example wipe chunks
        for(CustomChunk customChunk : activeCustomChunks) {
            customChunk.deleteCustomChunk();
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new VoidWorldGenerator();
    }

    public RandomLayerChunk getRandomLayerChunk() {
        return randomLayerChunk;
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
        if(activeCustomChunks == null) return false;
        for(CustomChunk customChunk : activeCustomChunks) {
            if(customChunk.getBukkitChunk().getX() == chunk.getX()) return true;
        }
        return false;
    }

    private void loadDisallowedBlocksConfig() {
        if(disallowedBlocks == null) disallowedBlocks = new ArrayList<>();
        List<String> disallowList = this.getConfig().getStringList("disallowed-blocks");
        if(disallowList.isEmpty()) {
            Bukkit.broadcastMessage("disallow-blocks list is empty");
            return;
        }
        if(disallowList.contains("shulker")) disallowShulkers = true;
        if(disallowList.contains("infested")) disallowInfestedBlocks = true;
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

    public ItemStack[] getRandomLoot(Location location) {
        Collection<ItemStack> itemStackCollection;
        int random = new Random().nextInt(100);
        if(random < 5) {
            itemStackCollection = LootTables.END_CITY_TREASURE.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("end city");
        } else if(random < 10) {
            itemStackCollection = LootTables.BASTION_TREASURE.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("bastion");
        } else if(random <= 25) {
            itemStackCollection = LootTables.SHIPWRECK_TREASURE.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("shipwrekc");
        } else if(random > 40 && random < 60) {
            itemStackCollection = LootTables.WOODLAND_MANSION.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("wooland mansion");
        } else if(random >= 75) {
            itemStackCollection = LootTables.IGLOO_CHEST.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("igloo");
        } else {
            itemStackCollection = LootTables.SIMPLE_DUNGEON.getLootTable()
                    .populateLoot(new Random(), new LootContext.Builder(location).build());
            Bukkit.broadcastMessage("dungeon");
        }
        return itemStackCollection.stream()
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);
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
                if(m.name().contains("CHEST")) {
                    allowedBlocks.add(Material.CHEST);
                    continue;
                }
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
