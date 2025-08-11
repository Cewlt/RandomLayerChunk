package me.colt.randomLayerChunk;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;

import org.apache.commons.rng.simple.RandomSource;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTables;

import java.util.*;

public class RandomLoot {
    private static Map<LootTables, Double> preList;

    // Sampling from a collection of items with user-defined probabilities.
    private static DiscreteProbabilityCollectionSampler weighedList;

    public RandomLoot() {}

    public static void loadList() {
        if(preList == null) {
            preList = new HashMap<LootTables, Double>();
            preList.put(LootTables.END_CITY_TREASURE, 5.0);
            preList.put(LootTables.BASTION_TREASURE, 10.0);
            preList.put(LootTables.WOODLAND_MANSION, 20.0);
            preList.put(LootTables.SHIPWRECK_TREASURE, 25.0);
            preList.put(LootTables.IGLOO_CHEST, 55.0);
            preList.put(LootTables.SIMPLE_DUNGEON, 75.0);
        }
        if(weighedList == null) {
            UniformRandomProvider uniformRandomProvider = RandomSource.XO_RO_SHI_RO_128_PP.create();
            weighedList = new DiscreteProbabilityCollectionSampler<>(uniformRandomProvider, preList);
        }
    }

    public static ItemStack[] getRandomLoot(Location location) {
        Collection<ItemStack> itemStackCollection;
        LootTables lootTable = (LootTables) weighedList.sample();
        itemStackCollection = lootTable.getLootTable().populateLoot(new Random(),
                new LootContext.Builder(location).build());
        return itemStackCollection.stream()
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);
    }
}
