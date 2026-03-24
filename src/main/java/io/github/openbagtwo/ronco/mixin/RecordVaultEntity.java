package io.github.openbagtwo.ronco.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class RecordVaultEntity {

	@Shadow
	abstract NonNullList<ItemStack> getItems();

	@Shadow
	abstract void updateState(int slot);

  @Shadow
  abstract public int getMaxStackSize();

  /**
   * @author OpenBagTwo
   * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
   */
	@Overwrite
	public boolean acceptsItemType(ItemStack stack) {
		return stack.is(ItemTags.BOOKSHELF_BOOKS) || (stack.get(DataComponents.JUKEBOX_PLAYABLE) != null);
	}

	/**
	 * @author OpenBagTwo
	 * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
	 */
	@Overwrite
	public void setItem(int slot, ItemStack stack) {
		if (acceptsItemType(stack)) {
      this.getItems().set(slot, stack);
      this.updateState(slot);
		} else if (stack.isEmpty()) {
			((ChiseledBookShelfBlockEntity)(Object)this).removeItem(slot,  this.getMaxStackSize());
		}

	}
}