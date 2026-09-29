package org.antarcticgardens.cna.neoforge.content.nuclear.reactor.fuelacceptor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;

public class NeoForgeReactorFuelAcceptorBlockEntity extends ReactorFuelAcceptorBlockEntity {
    public IItemHandler capability;

    public NeoForgeReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        capability = new FuelAcceptorInventoryHandler();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CNABlockEntityTypes.REACTOR_FUEL_ACCEPTOR.get(),
                (be, context) -> ((NeoForgeReactorFuelAcceptorBlockEntity) be).capability
        );
    }

    @Override
    public void invalidate() {
        if (capability != null)
            invalidateCapabilities();
        super.invalidate();
    }

    private class FuelAcceptorInventoryHandler extends InvWrapper {
        public FuelAcceptorInventoryHandler() {
            super(container);
        }
    }
}

