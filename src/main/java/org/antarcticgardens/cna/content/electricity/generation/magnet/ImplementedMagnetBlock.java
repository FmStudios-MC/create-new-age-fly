package org.antarcticgardens.cna.content.electricity.generation.magnet;

import java.util.function.Function;
import net.minecraft.world.level.block.Block;


public class ImplementedMagnetBlock extends Block implements IMagneticBlock {
    private final int strength;

    public ImplementedMagnetBlock(Properties properties, int strength) {
        super(properties.strength(5.0F, 7.0F));
        this.strength = strength;
    }


    @Override
    public float getStrength() {
        return strength;
    }

    public static Function<Properties, ImplementedMagnetBlock> simple(int level) {
        return (p) -> new ImplementedMagnetBlock(p, level);
    }
}
