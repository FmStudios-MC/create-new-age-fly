package org.antarcticgardens.cna;

import com.zurrtum.create.api.boiler.BoilerHeater;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.magnet.MagnetPlacementHelper;
import org.antarcticgardens.cna.content.electricity.network.NetworkTicker;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.data.worldgen.CNAPlacedFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateNewAge implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Create: New Age");

    public static final String MOD_ID = "create_new_age";
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MOD_ID, "tab"));

    private static int magnetPlacementHelperId;

    @Override
    public void onInitialize() {
        CNAConfig.load();

        CNASounds.init();
        CNAEffects.init();
        CNADataComponents.init();
        CNABlocks.init();
        CNABlockEntityTypes.init();
        CNAItems.init();
        CNARecipeTypes.init();

        MotorBlockEntity.registerEnergyStorage();
        ElectricalConnectorBlockEntity.registerEnergyStorage();
        CarbonBrushesBlockEntity.registerEnergyStorage();
        EnergiserBlockEntity.registerEnergyStorage();
        StreetLightBlockEntity.registerEnergyStorage();

        magnetPlacementHelperId = PlacementHelpers.register(new MagnetPlacementHelper());

        // NeoForge ran this in FMLCommonSetupEvent; on Fabric the blocks already exist here.
        BoilerHeater.REGISTRY.register(CNABlocks.HEATER, (level, pos, state) -> state.getValue(HeaterBlock.STRENGTH).ordinal() - 1);

        // Was LevelTickEvent.Pre on both sides. Networks only ever tick with a server level, since
        // the client never builds them.
        ServerTickEvents.START_LEVEL_TICK.register(NetworkTicker::tickWorld);

        // Replaces the NeoForge biome modifiers under data/create_new_age/neoforge/biome_modifier.
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, CNAPlacedFeatures.THORIUM_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, CNAPlacedFeatures.MAGNETITE_BLOCK);

        // The optional "Monkey Edition" datapack shipped inside the jar, off by default.
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(mod -> ResourceLoader.registerBuiltinPack(
                Identifier.fromNamespaceAndPath(MOD_ID, "create_new_age_monkey_edition"), mod,
                Component.translatable("create_new_age.monkey_edition"), PackActivationType.NORMAL));
    }

    public static int getMagnetPlacementHelperId() {
        return magnetPlacementHelperId;
    }
}
