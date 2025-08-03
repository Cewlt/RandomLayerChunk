package me.colt.randomLayerChunk;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CustomChunk  {
    private final RandomLayerChunk randomLayerChunk;
    private final Chunk bukkitChunk;
    private BukkitTask layerTask;

    // delayBetweenLayers in seconds
    private long delayBetweenLayers = 10L;

    private int addedLayersCount;
    private int currentHeight;

    private LivingEntity bigCow;

    public CustomChunk(RandomLayerChunk randomLayerChunk, Chunk bukkitChunk) {
        this.randomLayerChunk = randomLayerChunk;
        this.bukkitChunk = bukkitChunk;
        startScheduler();
    }

    public void startScheduler() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        layerTask = scheduler.runTaskTimer(randomLayerChunk, () -> {
            setNextLayer(randomLayerChunk.getRandomBlock());
            growBigCow();
        }, 20L * 5L, 20L * delayBetweenLayers);
    }

    public void stopScheduler() {
        if(layerTask != null) layerTask.cancel();
    }

    public Chunk getBukkitChunk() {
        return bukkitChunk;
    }

   public List<Location> getNextLayer() {
        List<Location> nextLayerLocations = new ArrayList<>();
        if(addedLayersCount == 0)  {
            currentHeight = randomLayerChunk.getStartHeightY();
        } else {
            currentHeight = randomLayerChunk.getStartHeightY() - addedLayersCount;
        }
        return getChunkLayer(bukkitChunk, currentHeight);
    }

    public List<Location> getChunkLayer(Chunk chunk, int yHeight) {
        List<Location> chunkLayer = new ArrayList<>();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                chunkLayer.add(new Location(chunk.getWorld(), x, yHeight, z));
            }
        }
        return chunkLayer;
    }

    public List<Location> getChunkWalls(Chunk chunk, int yHeight) {
        List<Location> wallBlocks = new ArrayList<>();
        World world = chunk.getWorld();

        int startX = chunk.getX() << 4; // chunk.getX() * 16
        int startZ = chunk.getZ() << 4; // chunk.getZ() * 16

        for (int i = 0; i < 16; i++) {
            wallBlocks.add(new Location(world, startX + i, yHeight, startZ));
            wallBlocks.add(new Location(world, startX + i, yHeight, startZ + 15));
            wallBlocks.add(new Location(world, startX, yHeight, startZ + i));
            wallBlocks.add(new Location(world, startX + 15, yHeight, startZ + i));
        }
        return wallBlocks;
    }


    public List<Location> getChunkWalls(Chunk chunk, int minY, int maxY) {
        List<Location> wallBlocks = new ArrayList<>();
        World world = chunk.getWorld();

        int startX = chunk.getX() << 4; // chunk.getX() * 16
        int startZ = chunk.getZ() << 4; // chunk.getZ() * 16

        for (int y = minY; y < maxY; y++) {
            for (int i = 0; i < 16; i++) {
                wallBlocks.add(new Location(world, startX + i, y, startZ));
                wallBlocks.add(new Location(world, startX + i, y, startZ + 15));
                wallBlocks.add(new Location(world, startX, y, startZ + i));
                wallBlocks.add(new Location(world, startX + 15, y, startZ + i));
            }
        }
        return wallBlocks;
    }

    public void setNextLayer(Material material) {
        if (bukkitChunk == null) return;
        List<Location> layer = getNextLayer();
        if (layer.isEmpty()) return;
        // enforced layers to ensure its possible to beat
        if (addedLayersCount == 0) {
            setFirstLayer(layer);
            addedLayersCount = 2;
        } else if(currentHeight == 225) {
            for (Location loc : layer) {
                loc.getBlock().setType(Material.IRON_ORE);
            }
            ++addedLayersCount;
        } else if(currentHeight == 175) {
            for (Location loc : layer) {
                loc.getBlock().setType(Material.DEEPSLATE_DIAMOND_ORE);
            }
            ++addedLayersCount;
        } else if(currentHeight == 125) {
            for (Location loc : layer) {
                loc.getBlock().setType(Material.OBSIDIAN);
            }
            ++addedLayersCount;
        } else if(currentHeight == 50) {
            // wip
            setEndPortalLayer(layer);
            addedLayersCount = addedLayersCount + 10;
        } else {
            if(material == Material.CHEST) {
                for (Location loc : layer) {
                    Block block = loc.getBlock();
                    block.setType(material);
                    if(new Random().nextInt(100) <= 20) {
                        Chest chest = (Chest) block.getState();
                        Inventory chestInventory = chest.getBlockInventory();
                        chestInventory.setContents(randomLayerChunk.getRandomLoot(loc));
                    }
                }
            } else {
                boolean spawnChest = false, chestSpawned = false;
                if(new Random().nextInt(100) <= 3) {
                    Location blockLocation = layer.get(1);
                }
                for (Location loc : layer) {
                    if(spawnChest && !chestSpawned) {

                        chestSpawned = true;
                        continue;
                    }
                    loc.getBlock().setType(material);
                }
            }
            ++addedLayersCount;
        }
    }

    private void setFirstLayer(List<Location> layer) {
        World world = layer.get(1).getWorld();
        // place water before shuffling list
        layer.get(25).getBlock().setType(Material.WATER);
        layer.get(26).getBlock().setType(Material.WATER);
        int grass = 0, flowers = 0;
        Collections.shuffle(layer);
        for (Location loc : layer) {
            new Location(world, loc.getX(), loc.getY() - 1, loc.getZ()).getBlock().setType(Material.STONE);
            if(loc.getBlock().getType() == Material.WATER) continue;
            loc.getBlock().setType(Material.GRASS_BLOCK);
            if(grass < 20) {
                new Location(world, loc.getX(), loc.getY() + 1, loc.getZ()).getBlock().setType(Material.SHORT_GRASS);
                ++grass;
                continue;
            }
            if(flowers < 5) {
                new Location(world, loc.getX(), loc.getY() + 1, loc.getZ()).getBlock().setType(Material.POPPY);
                ++flowers;
            }
        }
        Location torchFlower = layer.get(new Random().nextInt(layer.size()));
        torchFlower.setY(torchFlower.getY() + 1);
        torchFlower.getBlock().setType(Material.TORCHFLOWER);
        //
        Location tree = layer.get(new Random().nextInt(layer.size()));
        tree = new Location(world, tree.getX(), tree.getY() + 1, tree.getZ());
        world.generateTree(tree, TreeType.REDWOOD);
        //
        Location cow = layer.get(new Random().nextInt(layer.size()));
        cow.setY(cow.getY() + 2);
        bigCow = (LivingEntity)world.spawnEntity(cow, EntityType.MOOSHROOM);
    }

    private void setEndPortalLayer(List<Location> layer) {
        // layer 1
        List<Location> wallsLayerOne = getChunkWalls(bukkitChunk, currentHeight);
        for(Location loc : wallsLayerOne) {
            loc.getBlock().setType(Material.MOSSY_STONE_BRICKS);
        }
        // layer 2-5
        List<Location> layerFive = getChunkWalls(bukkitChunk, currentHeight - 1, currentHeight - 4);
        for(Location loc : layerFive) {
            loc.getBlock().setType(Material.STONE_BRICKS);
        }
    }

    private void growBigCow() {
        if(bigCow == null) return;
        double currentValue = bigCow.getAttribute(Attribute.SCALE).getBaseValue();
        bigCow.getAttribute(Attribute.SCALE).setBaseValue(currentValue + 0.1);
    }

    public void shrinkBigCow() {
        if(bigCow == null) return;
        double currentValue = bigCow.getAttribute(Attribute.SCALE).getBaseValue();
        bigCow.getAttribute(Attribute.SCALE).setBaseValue(currentValue - 0.1);
    }

    public void deleteCustomChunk() {
        List<Block> blocksInChunk = getAllBlocksInChunk();
        for(Block block : blocksInChunk) {
            block.setType(Material.AIR);
        }
        stopScheduler();
        randomLayerChunk.getActiveCustomChunks().remove(this);
    }

    public static void deleteChunk(Chunk chunk) {
        if(chunk == null) return;
        List<Block> blocksInChunk = getAllBlocksInChunk(chunk);
        for(Block block : blocksInChunk) {
            block.setType(Material.AIR);
        }
    }

    public List<Block> getAllBlocksInChunk() {
        List<Block> blocksInChunk = new ArrayList<>();
        World world = bukkitChunk.getWorld();
        int minX = bukkitChunk.getX() * 16;
        int minZ = bukkitChunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        for(int x = minX; x < maxX; x++) {
            for(int z = minZ; z < maxZ; z++) {
                for(int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    if(world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
                    blocksInChunk.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocksInChunk;
    }

    public static List<Block> getAllBlocksInChunk(Chunk chunk) {
       List<Block> blocksInChunk = new ArrayList<>();
        World world = chunk.getWorld();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        for(int x = minX; x < maxX; x++) {
            for(int z = minZ; z < maxZ; z++) {
                for(int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    if(world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
                    blocksInChunk.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocksInChunk;
    }
}
