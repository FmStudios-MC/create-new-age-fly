package org.antarcticgardens.cna.content.heat.pipe;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ReactorEncasedHeatPipeBlock extends EncasedHeatPipeBlock{

    public ReactorEncasedHeatPipeBlock(Properties properties, Supplier<Block> casing) {
        super(properties.strength(6.0f), casing);
    }

}
