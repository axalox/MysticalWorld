package epicsquid.mysticalworld.events;

import com.google.common.collect.Sets;
import epicsquid.mysticalworld.MysticalWorld;
import epicsquid.mysticalworld.init.ModModifiers;
import epicsquid.mysticalworld.loot.Serendipity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;

import java.util.Set;

@SuppressWarnings("unused")
public class LootHandler {
  private static Set<ResourceLocation> tables = Sets
      .newHashSet(LootTables.CHESTS_SIMPLE_DUNGEON, LootTables.CHESTS_ABANDONED_MINESHAFT, LootTables.CHESTS_DESERT_PYRAMID, LootTables.CHESTS_JUNGLE_TEMPLE,
          LootTables.CHESTS_WOODLAND_MANSION);
  private static ResourceLocation grass_table = new ResourceLocation("minecraft", "blocks/grass");
  private static ResourceLocation squid_table = new ResourceLocation("minecraft", "entities/squid");

  public static void onLootLoad(LootTableLoadEvent event) {
    if (event.isCanceled()) {
      MysticalWorld.LOG.error("LootTableLoadEvent " + event.getName() + " has been cancelled by another mod!");
    }
    if (tables.contains(event.getName())) {
      event.getTable().addPool(
          LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(MysticalWorld.MODID, "chests/inject")).weight(1).quality(0)).name("mystical_world_chest_injection").build());
    }

    if (event.getName().equals(squid_table)) {
      event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(MysticalWorld.MODID, "entities/squid_inject"))).name("mystical_world_squid_injection").build());
    }

    // Inject grass drops - should prevent having to override the entire grass drop table
    if (event.getName().equals(grass_table)) {
      event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(MysticalWorld.MODID, "blocks/grass_inject"))).name("mystical_world_grass_injection").build());
    }
  }

  public static void onLooting(LootingLevelEvent event) {
    if (event.getEntityLiving() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) event.getEntityLiving();

      int looting = event.getLootingLevel();

      event.setLootingLevel(looting + Serendipity.calculateAdditional(player.getAttribute(ModModifiers.SERENDIPITY)));
    }
  }
}
