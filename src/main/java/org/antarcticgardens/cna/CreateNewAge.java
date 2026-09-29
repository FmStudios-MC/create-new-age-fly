package org.antarcticgardens.cna;

import com.zurrtum.create.api.boiler.BoilerHeater;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.MagnetPlacementHelper;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CreateNewAge {
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MOD_ID = "create_new_age";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, 
            Identifier.fromNamespaceAndPath(MOD_ID, "tab"));

    private static CreateNewAge instance;
    
    private Platform platform;
    private int magnetPlacementHelperId;
    
    protected CreateNewAge() {
        instance = this;
    }

    protected void initialize(Platform platform) {
        this.platform = platform;
        
        LOGGER.info("Hello 1.21.1 Create!");

        platform.getRegistrar().beforeRegistration();
        
        CNABlocks.load();
        CNABlockEntityTypes.load();
        CNAItems.load();
        CNAEffects.load();
        CNASounds.load();
        CNATags.load();
        CNAPartialModels.load();
        CNARecipeTypes.load();
        
        magnetPlacementHelperId = PlacementHelpers.register(new MagnetPlacementHelper());
        
        platform.getRegistrar().afterRegistration();
        CNAConfig.load();

        CNAComputerCraftProxy.register();

        platform.commonSetup(() -> {
            BoilerHeater.REGISTRY.register(CNABlocks.HEATER.get(), ((level, pos, state) -> state.getValue(HeaterBlock.STRENGTH).ordinal() - 1));
            for (var run : CNABlocks.doLater) {
                run.run();
            }
        });
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public int getMagnetPlacementHelperId() {
        return magnetPlacementHelperId;
    }
    
    public static CreateNewAge getInstance() {
        return instance;
    }
}
