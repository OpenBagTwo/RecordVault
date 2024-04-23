package io.github.openbagtwo.ronco.mixin;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookshelfBlock.class)
public abstract class RecordVault {

  @Invoker("getHitPos")
  static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing){
    throw new AssertionError();
  }

  @Invoker("getSlotForHitPos")
  static int getSlotForHitPos(Vec2f hitPos){
    throw new AssertionError();
  }

  @Inject(
      method="onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
      at=@At("HEAD"),
      cancellable = true
  )
  public void onUsingRecord(
      BlockState state,
      World world,
      BlockPos pos,
      PlayerEntity player,
      Hand hand,
      BlockHitResult hit,
      CallbackInfoReturnable<ActionResult> callbackInfo
  ) {
    BlockEntity maybeBookshelf = world.getBlockEntity(pos);
    if (maybeBookshelf instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
      Optional shelf = this.getHitPos(hit, state.get(HorizontalFacingBlock.FACING));
      if (!shelf.isEmpty()) {
        int slot = this.getSlotForHitPos((Vec2f)shelf.get());
        if (!(Boolean)state.get((Property)ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot)))
        {
          ItemStack itemStack = player.getStackInHand(hand);
          if (itemStack.getItem() instanceof MusicDiscItem) {
            tryAddRecord(world, pos, player, chiseledBookshelfBlockEntity, itemStack, slot);
            callbackInfo.setReturnValue(ActionResult.success(world.isClient));
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
      world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }
  }

}
