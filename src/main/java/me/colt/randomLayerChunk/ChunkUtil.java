package me.colt.randomLayerChunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;

public class ChunkUtil {

    public static void createEndPortal(World world, int centerX, int centerY, int centerZ) {
        for (int x = -1; x <= 1; x++) {
            placeFrame(world, centerX + x, centerY, centerZ - 2, BlockFace.SOUTH);
        }
        for (int x = -1; x <= 1; x++) {
            placeFrame(world, centerX + x, centerY, centerZ + 2, BlockFace.NORTH);
        }
        for (int z = -1; z <= 1; z++) {
            placeFrame(world, centerX - 2, centerY, centerZ + z, BlockFace.EAST);
        }
        for (int z = -1; z <= 1; z++) {
            placeFrame(world, centerX + 2, centerY, centerZ + z, BlockFace.WEST);
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block b = world.getBlockAt(centerX + dx, centerY, centerZ + dz);
                b.setType(Material.END_PORTAL);
            }
        }
    }

    private static void placeFrame(World world, int x, int y, int z, BlockFace rotation) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(Material.END_PORTAL_FRAME);
        EndPortalFrame data = (EndPortalFrame)block.getBlockData();
        data.setFacing(rotation);
        data.setEye(true);
        block.setBlockData(data);
    }

    public static Location getChunkCenter(Chunk chunk, int yHeight) {
        World world = chunk.getWorld();
        int centerX = (chunk.getX() * 16) + 8;
        int centerZ = (chunk.getZ() * 16) + 8;
        return new Location(world, centerX, yHeight, centerZ);
    }
}
