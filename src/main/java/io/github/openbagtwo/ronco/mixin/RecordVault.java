package io.github.openbagtwo.ronco.mixin;

import java.util.OptionalInt;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookshelfBlock.class)
public abstract class RecordVault {

  @Invoker("getSlotForHitPos")
  abstract OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state);

  @Inject(
      method="onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;",
      at=@At("HEAD"),
      cancellable = true
  )
  public void onUsingRecord(
      ItemStack stack,
      BlockState state,
      World world,
      BlockPos pos,
      PlayerEntity player,
      Hand hand,
      BlockHitResult hit,
      CallbackInfoReturnable<ItemActionResult> callbackInfo
  ) {
    BlockEntity maybeBookshelf = world.getBlockEntity(pos);
    if (maybeBookshelf instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
      if (stack.get(DataComponentTypes.JUKEBOX_PLAYABLE) != null){
        OptionalInt slot = this.getSlotForHitPos(hit, state);
        if (!slot.isEmpty()) {
          if (!((Boolean) state.get((Property)ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot.getAsInt())))) {
            tryAddRecord(world, pos, player, chiseledBookshelfBlockEntity, stack, slot.getAsInt());
            callbackInfo.setReturnValue(ItemActionResult.success(world.isClient));
            callbackInfo.cancel();
          }
        }
      }
    }
  }

  private static void tryAddRecord(World world, BlockPos pos, PlayerEntity player, ChiseledBookshelfBlockEntity blockEntity, ItemStack stack, int slot) {
    if (!world.isClient) {
      // treat it like an enchanted book
      player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
      SoundEvent soundEvent = SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED;
      blockEntity.setStack(slot, stack.split(1));
      world.playSound((PlayerEntity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (player.isCreative()) {
        stack.increment(1);
      }
    }
  }

}
