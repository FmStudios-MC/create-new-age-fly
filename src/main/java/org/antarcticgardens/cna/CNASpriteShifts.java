package org.antarcticgardens.cna;

import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.CTType;
import net.minecraft.resources.Identifier;

public class CNASpriteShifts {
    public static CTSpriteShiftEntry HEAT_CASING = omni("heat_casing");
    public static CTSpriteShiftEntry REACTOR_CASING = omni("reactor_casing");
    public static CTSpriteShiftEntry REACTOR_GLASS = omni("reactor_glass");
    public static CTSpriteShiftEntry REDSTONE_MAGNET = omni("redstone_magnet");


    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry rect(String name) {
        return getCT(AllCTTypes.RECTANGLE, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String name) {
        return CTSpriteShifter.getCT(type, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/" + name),
                Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/" + name + "_connected"));
    }
}
