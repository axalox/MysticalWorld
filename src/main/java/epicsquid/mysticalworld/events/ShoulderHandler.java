package epicsquid.mysticalworld.events;

import epicsquid.mysticallib.network.PacketHandler;
import epicsquid.mysticalworld.capability.PlayerShoulderCapability;
import epicsquid.mysticalworld.capability.PlayerShoulderCapabilityProvider;
import epicsquid.mysticalworld.network.ShoulderRide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class ShoulderHandler {
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    PlayerEntity player = event.getPlayer();
    World world = event.getWorld();

    if (!world.isRemote && event.getHand() == Hand.MAIN_HAND && player.isSneaking()) {
      LazyOptional<PlayerShoulderCapability> laycap = player.getCapability(PlayerShoulderCapabilityProvider.PLAYER_SHOULDER_CAPABILITY);
      if (laycap.isPresent()) {
        PlayerShoulderCapability cap = laycap.orElseThrow(IllegalStateException::new);
        if (cap.isShouldered()) {
          EntityType<?> type = ForgeRegistries.ENTITIES.getValue(cap.getRegistryName());
          if (type != null) {
            Entity animal = type.create(world);
            if (animal != null) {
              animal.read(cap.getAnimalSerialized());
              BlockPos pos = event.getPos();
              animal.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
              world.addEntity(animal);
              player.swingArm(Hand.MAIN_HAND);
              cap.drop();
              event.setCanceled(true);
              ShoulderRide message = new ShoulderRide(player, cap);
              PacketHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), message);
              PacketHandler.sendTo(message, (ServerPlayerEntity) player);
            }
          }
        }
      }
    }
  }

  public static void onDeath(LivingDeathEvent event) {
    LivingEntity living = event.getEntityLiving();
    if (living instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) living;
      World world = player.world;
      LazyOptional<PlayerShoulderCapability> laycap = player.getCapability(PlayerShoulderCapabilityProvider.PLAYER_SHOULDER_CAPABILITY, null);
      if (laycap.isPresent()) {
        PlayerShoulderCapability cap = laycap.orElseThrow(IllegalStateException::new);
        if (cap.isShouldered()) {
          EntityType<?> type = ForgeRegistries.ENTITIES.getValue(cap.getRegistryName());
          if (type != null) {
            Entity animal = type.create(world);
            if (animal != null) {
              animal.read(cap.getAnimalSerialized());
              BlockPos pos = player.getPosition();
              animal.setPosition(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
              world.addEntity(animal);
              player.swingArm(Hand.MAIN_HAND);
              cap.drop();
              event.setCanceled(true);
              ShoulderRide message = new ShoulderRide(player, cap);
              PacketHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), message);
              PacketHandler.sendTo(message, (ServerPlayerEntity) player);
            }
          }
        }
      }
    }
  }
}
