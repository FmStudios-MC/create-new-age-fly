package org.antarcticgardens.cna.compat.computercraft;

import com.zurrtum.create.compat.Mods;
import com.zurrtum.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.FallbackComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.ComputerBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;

import java.util.function.Function;

public class CNAComputerCraftProxy {

    public static void register() {
        fallbackFactory = FallbackComputerBehaviour::new;
        Mods.COMPUTERCRAFT.executeIfInstalled(() -> CNAComputerCraftProxy::registerWithDependency);
    }

    private static void registerWithDependency() {
        /* Comment if computercraft.implementation is not in the source set */
        computerFactory = CNAComputerBehaviour::new;
        ComputerBehaviour.registerItemDetailProviders();
    }

    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

    public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
        if (computerFactory == null)
            return fallbackFactory.apply(sbe);
        return computerFactory.apply(sbe);
    }
}
