package io.github.openbagtwo.ronco.mixin;

import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookShelfBlock.class)
public abstract class RecordVault {

  @Shadow
  @Final
  public static EnumProperty<Direction> FACING;

  @Inject(
      method="useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
      at=@At("HEAD"),
      cancellable = true
  )
  public void onUsingRecord(
      ItemStack stack,
      BlockState state,
      Level world,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit,
      CallbackInfoReturnable<InteractionResult> callbackInfo
  ) {
    BlockEntity maybeBookshelf = world.getBlockEntity(pos);
    if (maybeBookshelf instanceof ChiseledBookShelfBlockEntity chiseledBookshelfBlockEntity) {
      if (stack.get(DataComponents.JUKEBOX_PLAYABLE) != null){
        OptionalInt slot = ((ChiseledBookShelfBlock) (Object) this).getHitSlot(hit, state.getValue(FACING));
        if (!slot.isEmpty()) {
          if (!((Boolean) state.getValue((Property)ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot.getAsInt())))) {
            tryAddRecord(world, pos, player, chiseledBookshelfBlockEntity, stack, slot.getAsInt());
            callbackInfo.setReturnValue(InteractionResult.SUCCESS);
            callbackInfo.cancel();
          }
        }
      }
    }
  }

  @Unique
  private static void tryAddRecord(Level world, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, ItemStack stack, int slot) {
    if (!world.isClientSide()) {
      // treat it like an enchanted book
      player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
      SoundEvent soundEvent = SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED;
      blockEntity.setItem(slot, stack.consumeAndReturn(1, player));
      world.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
  }

}
