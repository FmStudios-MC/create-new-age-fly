package org.antarcticgardens.cna.neoforge;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.antarcticgardens.cna.CNADataComponents;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

import java.util.Objects;

public class NeoForgeRegistrar implements PlatformRegistrar {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateNewAge.MOD_ID);

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_REGISTER =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateNewAge.MOD_ID);

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateNewAge.MOD_ID);

    @Override
    public void registerConfig(ModConfig.Type type, IConfigSpec spec) {
        ModLoadingContext.get().getActiveContainer().registerConfig(type, spec);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public RecipeType<?> registerRecipe(String name, RecipeSerializer<?> serializer) {
        RecipeType type = new RecipeType() {};

        RECIPE_TYPE_REGISTER.register(name, () -> type);
        RECIPE_SERIALIZER_REGISTER.register(name, () -> serializer);
        
        return type;
    }


    @Override
    public void beforeRegistration() {
        IEventBus eventBus = Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus());

        CreateNewAge.REGISTRATE.registerEventListeners(eventBus);
        DeferredHolder<CreativeModeTab, CreativeModeTab> tab = TAB_REGISTER.register("tab",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("tab." + CreateNewAge.MOD_ID + ".tab"))
                        .icon(CNABlocks.GENERATOR_COIL::asStack)
                        .build()
        );
        
        CreateNewAge.REGISTRATE.setCreativeTab(tab);

        TAB_REGISTER.register(eventBus);
        RECIPE_TYPE_REGISTER.register(eventBus);
        RECIPE_SERIALIZER_REGISTER.register(eventBus);

        CNADataComponents.DATA_COMPONENTS.register(eventBus);
    }

    @Override
    public void afterRegistration() {
        IEventBus eventBus = Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus());
    }
}
