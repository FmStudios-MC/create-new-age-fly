package org.antarcticgardens.cna.content.electricity.wire;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;

public enum WireType {
    COPPER(1024, () -> new ItemStack(CNAItems.COPPER_WIRE)),
    OVERCHARGED_IRON(2048, () -> new ItemStack(CNAItems.OVERCHARGED_IRON_WIRE)),
    OVERCHARGED_GOLD(4096, () -> new ItemStack(CNAItems.OVERCHARGED_GOLDEN_WIRE)),
    OVERCHARGED_DIAMOND(8192, () -> new ItemStack(CNAItems.OVERCHARGED_DIAMOND_WIRE));

    private final long conductivity;
    private final IRegistrateIsAFuckingShitNeverUseIt dropProvider;

    WireType(int conductivity, IRegistrateIsAFuckingShitNeverUseIt dropProvider) {
        this.conductivity = conductivity;
        this.dropProvider = dropProvider;
    }

    public Identifier getTextureLocation() {
        return Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "textures/wire/" + name().toLowerCase() + ".png");
    }

    public long getConductivity() {
        return (long) (conductivity * CNAConfig.getServer().conductivityMultiplier.get());
    }

    public ItemStack getDroppedItem() {
        return dropProvider.getDroppedItem();
    }
}
