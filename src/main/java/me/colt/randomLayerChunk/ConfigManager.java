package me.colt.randomLayerChunk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final RandomLayerChunk randomLayerChunk;
    private FileConfiguration config;

    // what height the random layer chunk will start at
    public int startHeightY = 250;

    private boolean notifyLoot;
    private String notifyLootMessage;

    private long delayBetweenLayers;

    private int distanceBetweenChunks = 1;

    private boolean stopWhenEmpty;

    private Map<Integer, Material> forcedLayers;

    public ConfigManager(RandomLayerChunk randomLayerChunk) {
        this.randomLayerChunk = randomLayerChunk;
        config = randomLayerChunk.getConfig();
    }

    public void loadAllConfig() {
        randomLayerChunk.saveDefaultConfig();
        loadStartHeight();
        loadDisallowedBlocks();
        loadNotifyLoot();
        loadDelayBetweenLayers();
        loadForcedLayers();
        loadDistanceBetweenChunks();
        loadStopWhenEmpty();
    }

    private void loadStartHeight() {
        if(config.contains("startHeightY") && config.isInt("startHeightY")) {
            this.startHeightY = config.getInt("startHeightY");
        }
    }

    public int getStartHeightY() {
        return this.startHeightY;
    }

    private void loadDisallowedBlocks() {
        if(randomLayerChunk.disallowedBlocks == null) randomLayerChunk.disallowedBlocks = new ArrayList<>();
        if(!config.contains("disallowed-blocks")) return;
        List<String> disallowList = config.getStringList("disallowed-blocks");
        if(disallowList.isEmpty()) return;
        if(disallowList.contains("SHULKER")) {
            disallowList.remove("SHULKER");
            randomLayerChunk.disallowShulkers = true;
        }
        if(disallowList.contains("INFESTED")) {
            disallowList.remove("INFESTED");
            randomLayerChunk.disallowInfestedBlocks = true;
        }
        for(String blockName : disallowList) {
            Material mat = Material.matchMaterial(blockName);
            if(mat == null) {
                Bukkit.broadcastMessage(blockName + " could not be matched");
                continue;
            }
            randomLayerChunk.disallowedBlocks.add(mat);
        }
    }

    private void loadNotifyLoot() {
        if(config.contains("notifyLootBarrel") && config.isBoolean("notifyLootBarrel")) {
            this.notifyLoot = config.getBoolean("notifyLootBarrel");
        } else {
            this.notifyLoot = true;
        }
        if(config.contains("notifyLootBarrel-message") && config.isString("notifyLootBarrel-message")) {
            this.notifyLootMessage = config.getString("notifyLootBarrel-message");
        }
    }

    private void loadDelayBetweenLayers() {
        if(config.contains("delayBetweenLayers") && config.isInt("delayBetweenLayers")) {
            this.delayBetweenLayers = config.getInt("delayBetweenLayers");
        } else {
            this.delayBetweenLayers = 10;
        }
    }

    private void loadForcedLayers() {
        forcedLayers = new HashMap<>();
        if(config.contains("forcedLayers") && !config.getStringList("forcedLayers").isEmpty()) {
            for(String string : config.getStringList("forcedLayers")) {
                String[] split = string.split(":");
                if(split.length != 2) continue;
                String yHeight = split[0];
                String block = split[1];
                if(Material.matchMaterial(block) == null) {
                    if(block.equals("END_LAYER")) {
                        block = "END_PORTAL";
                    } else {
                        Bukkit.broadcastMessage("Configuration issue block not found: " + block);
                        continue;
                    }
                }
                forcedLayers.put(Integer.valueOf(yHeight), Material.matchMaterial(block));
            }
        }
    }

    private void loadDistanceBetweenChunks() {
        if(config.contains("distanceBetweenChunks") && config.isInt("distanceBetweenChunks")) {
            this.distanceBetweenChunks = config.getInt("distanceBetweenChunks");
        }
    }

    private void loadStopWhenEmpty() {
        if(config.contains("stopLayersWhenUnoccupied") && config.isBoolean("stopLayersWhenUnoccupied")) {
            this.stopWhenEmpty = config.getBoolean("stopLayersWhenUnoccupied");
        } else {
            this.stopWhenEmpty = true;
        }
    }

    public Map<Integer, Material> getForcedLayers() {
        return this.forcedLayers;
    }

    public boolean getNotifyLoot() {
        return this.notifyLoot;
    }

    public boolean getStopWhenEmpty() {
        return this.stopWhenEmpty;
    }

    public String getNotifyLootMessage() {
        return this.notifyLootMessage;
    }

    public long getDelayBetweenLayers() {
        return this.delayBetweenLayers;
    }

    public int getDistanceBetweenChunks() {
        return this.distanceBetweenChunks;
    }

    public String colorFormat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String formatLootMessage(String blockDisplayName, String height) {
        String message = getNotifyLootMessage();
        if(notifyLootMessage.contains("{BLOCK_NAME}")) {
            message = message.replace("{BLOCK_NAME}", blockDisplayName);
        }
        if(notifyLootMessage.contains("{HEIGHT}")) {
            message = message.replace("{HEIGHT}", height);
        }
        return colorFormat(message);
    }
}
