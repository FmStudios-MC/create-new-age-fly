package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

/**
 * A detached item slot, standing in for ESL's {@code ItemStackHolder}: lets an item's energy
 * storage change the stack (Team Reborn items keep their charge in a data component), and hands the
 * result back through {@link #toItemStack()} once the transaction is done.
 */
public class ItemStackHolder extends SingleStackStorage {
    private ItemStack stack;

    public ItemStackHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    protected ItemStack getStack() {
        return stack;
    }

    @Override
    protected void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack toItemStack() {
        return stack;
    }

    /** The item's energy storage, or null when the item has none. */
    public @Nullable EnergyStorage findEnergyStorage() {
        return ContainerItemContext.ofSingleSlot(this).find(EnergyStorage.ITEM);
    }
}
