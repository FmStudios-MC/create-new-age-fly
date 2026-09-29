package org.antarcticgardens.cna;


import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.antarcticgardens.cna.content.electricity.network.NetworkTicker;
import org.antarcticgardens.cna.neoforge.compat.computercraft.NeoComputerCraftCompat;
import org.antarcticgardens.cna.neoforge.content.nuclear.reactor.fuelacceptor.NeoForgeReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.neoforge.data.CreateNewAgeDatagenNeoForge;
import org.antarcticgardens.esl.ESLNeoForge;
import org.antarcticgardens.esl.energy.EnergyStorage;
import org.antarcticgardens.esl.neoforge.energy.E2FEnergyStorageAdapter;

@Mod(CreateNewAge.MOD_ID)
public class CreateNewAgeNeoForge extends CreateNewAge {
    public CreateNewAgeNeoForge(IEventBus eventBus, ModContainer modContainer) {
        this.initialize(new NeoForgePlatform(eventBus));
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Pre e) -> NetworkTicker.tickWorld(e.getLevel()));

        eventBus.addListener(EventPriority.HIGHEST, CreateNewAgeDatagenNeoForge::gatherData);
        eventBus.addListener(NeoForgePlatform::registerDatapack);
        eventBus.addListener(NeoForgeReactorFuelAcceptorBlockEntity::registerCapabilities);
        eventBus.addListener(NeoComputerCraftCompat::registerComputerCraftCapabilities);
        eventBus.addListener(this::registerCapabilities);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        // TODO: This sucks ass I tried running this in EnergyStorage.registerForBlockEntity
        //  but the RegisterCapabilitiesEvent only fires on game start so IDK what else to do
        registerBlockEnergyCapability(event, CNABlockEntityTypes.BASIC_MOTOR.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.ADVANCED_MOTOR.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.REINFORCED_MOTOR.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.ELECTRICAL_CONNECTOR.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.CARBON_BRUSHES.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.ENERGISER.get());
        registerBlockEnergyCapability(event, CNABlockEntityTypes.STREET_LIGHT.get());
    }

    private <BE extends BlockEntity> void registerBlockEnergyCapability(RegisterCapabilitiesEvent event, BlockEntityType<BE> blockEntityType) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                blockEntityType,
                (be, context) -> {
                    EnergyStorage storage = ESLNeoForge.getInstance().getBlockEnergyStorageManager().find(be.getLevel(), be.getBlockPos(), context);
                    return E2FEnergyStorageAdapter.getOrCreate(storage);
                }
        );
    }
}
