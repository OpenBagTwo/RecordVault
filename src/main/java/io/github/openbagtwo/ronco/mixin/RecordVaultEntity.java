package io.github.openbagtwo.ronco.mixin;

import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class RecordVaultEntity {

	@Shadow
	abstract DefaultedList<ItemStack> getHeldStacks();

	@Shadow
	abstract void updateState(int slot);

  @Shadow
  abstract public int getMaxCountPerStack();

  /**
   * @author OpenBagTwo
   * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
   */
	@Overwrite
	public boolean canAccept(ItemStack stack) {
		return stack.isIn(ItemTags.BOOKSHELF_BOOKS) || (stack.get(DataComponentTypes.JUKEBOX_PLAYABLE) != null);
	}

	/**
	 * @author OpenBagTwo
	 * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
	 */
	@Overwrite
	public void setStack(int slot, ItemStack stack) {
		if (canAccept(stack)) {
      this.getHeldStacks().set(slot, stack);
      this.updateState(slot);
		} else if (stack.isEmpty()) {
			((ChiseledBookshelfBlockEntity)(Object)this).removeStack(slot,  this.getMaxCountPerStack());
		}

	}
}