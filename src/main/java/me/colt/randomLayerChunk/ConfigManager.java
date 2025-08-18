package me.colt.randomLayerChunk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final RandomLayerChunk randomLayerChunk;
    private FileConfiguration config;

    private boolean notifyLoot;
    private String notifyLootMessage;

    public ConfigManager(RandomLayerChunk randomLayerChunk) {
        this.randomLayerChunk = randomLayerChunk;
        config = randomLayerChunk.getConfig();
    }

    public void loadAllConfig() {
        randomLayerChunk.saveDefaultConfig();
        loadStartHeightConfig();
        loadDisallowedBlocksConfig();
        loadNotifyLoot();
    }

    public void loadStartHeightConfig() {
        if(!config.contains("startHeightY")
                || !config.isInt("startHeightY")) {
            randomLayerChunk.startHeightY = 250;
            return;
        }
        randomLayerChunk.startHeightY = config.getInt("startheightY");
    }

    public void loadDisallowedBlocksConfig() {
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

    public void loadNotifyLoot() {
        if(config.contains("notifyLootBarrel") && config.isBoolean("notifyLootBarrel")) {
            this.notifyLoot = config.getBoolean("notifyLootBarrel");
        } else {
            this.notifyLoot = true;
        }
        if(config.contains("notifyLootBarrel-message") && config.isString("notifyLootBarrel-message")) {
            this.notifyLootMessage = config.getString("notifyLootBarrel-message");
        }
    }

    public boolean getNotifyLoot() {
        return this.notifyLoot;
    }

    public String getNotifyLootMessage() {
        return this.notifyLootMessage;
    }

    public String colorFormat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String formatLootMessage(String blockDisplayName, String height) {
        String message = notifyLootMessage;
        if(notifyLootMessage.contains("{BLOCK_NAME}")) {
            message = message.replace("{BLOCK_NAME}", blockDisplayName);
        }
        if(notifyLootMessage.contains("{HEIGHT}")) {
            message = message.replace("{HEIGHT}", height);
        }
        return colorFormat(message);
    }
}
