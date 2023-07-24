package io.github.openbagtwo.ronco.mixin;

import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class RecordVault {

	@Shadow
	DefaultedList<ItemStack> inventory;

	@Shadow
	abstract void updateState(int slot);

	@Overwrite
	public boolean isValid(int slot, ItemStack stack) {
		return (
				stack.isIn(ItemTags.BOOKSHELF_BOOKS)
				|| stack.isIn(ItemTags.MUSIC_DISCS)
		) && ((ChiseledBookshelfBlockEntity)(Object)this).getStack(slot).isEmpty();
	}
	@Overwrite
	public void setStack(int slot, ItemStack stack) {
		if (
				stack.isIn(ItemTags.BOOKSHELF_BOOKS)
				|| stack.isIn(ItemTags.MUSIC_DISCS)
		) {
			this.inventory.set(slot, stack);
			this.updateState(slot);
		}

	}
}