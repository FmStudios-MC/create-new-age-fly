package org.antarcticgardens.cna;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(value = CreateNewAge.MOD_ID, dist = Dist.CLIENT)
public class CreateNewAgeClientNeoForge extends CreateNewAgeClient {
    public CreateNewAgeClientNeoForge() {
        this.initialize();

        ModContainer modContainer = ModList.get()
                .getModContainerById(CreateNewAge.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("What the..."));

        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateNewAge.MOD_ID);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }
}
