package io.github.openbagtwo.ronco.mixin;

import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class RecordVaultEntity {

	@Shadow
	DefaultedList<ItemStack> inventory;

	@Shadow
	abstract void updateState(int slot);

	@Unique
	private boolean isValidForBookshelf(ItemStack stack) {
		return stack.isIn(ItemTags.BOOKSHELF_BOOKS) || (stack.get(DataComponentTypes.JUKEBOX_PLAYABLE) != null);
	}

	/**
	 * @author OpenBagTwo
	 * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
	 */
	@Overwrite
	public boolean isValid(int slot, ItemStack stack) {
		return (
				isValidForBookshelf(stack)
						&& ((ChiseledBookshelfBlockEntity)(Object)this).getStack(slot).isEmpty()
		);
	}
	/**
	 * @author OpenBagTwo
	 * @reason Allow any music disc (regardless of item tags) to go into the bookshelf
	 */
	@Overwrite
	public void setStack(int slot, ItemStack stack) {
		if (isValidForBookshelf(stack)) {
			this.inventory.set(slot, stack);
			this.updateState(slot);
		}

	}
}