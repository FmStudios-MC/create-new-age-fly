package org.antarcticgardens.cna;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.neoforge.NeoForgeRegistrar;
import org.antarcticgardens.cna.neoforge.compat.jei.ForgeJeiEnergisingSubcategory;
import org.antarcticgardens.cna.neoforge.content.nuclear.reactor.fuelacceptor.NeoForgeReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.platform.PlatformRegistrar;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NeoForgePlatform extends Platform {
    private final NeoForgeRegistrar registrationHelper = new NeoForgeRegistrar();
    private final IEventBus modEventBus;

    public NeoForgePlatform(IEventBus eventBus) {
        modEventBus = eventBus;
    }

    @Override
    public PlatformRegistrar getRegistrar() {
        return registrationHelper;
    }

    @Override
    public void commonSetup(Runnable commonSetup) {
        modEventBus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(commonSetup));
    }

    @Override
    public Object getEnergisingRecipeSubCategory() {
        return (Supplier<Supplier<SequencedAssemblySubCategory>>) () -> ForgeJeiEnergisingSubcategory::new;
    }

    @Override
    public ReactorFuelAcceptorBlockEntity platformReactorFuelAcceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new NeoForgeReactorFuelAcceptorBlockEntity(type, pos, blockState);
    }

    public static void registerDatapack(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            Path path = ModList.get().getModFileById(CreateNewAge.MOD_ID).getFile().findResource("resourcepacks/create_new_age_monkey_edition");
            final String version = ModList.get().getModFileById(CreateNewAge.MOD_ID).getFile().getModInfos().stream().map(IModInfo::getVersion).map(ArtifactVersion::toString).collect(Collectors.joining(","));

            PackLocationInfo packLocationInfo = new PackLocationInfo(
                    "create_new_age:create_new_age_monkey_edition",
                    Component.translatable("create_new_age.monkey_edition"),
                    PackSource.create((arg) -> Component.translatable("pack.nameAndSource", arg, Component.translatable("pack.source.builtin")).withStyle(ChatFormatting.GRAY), false),
                    Optional.of(new KnownPack(CreateNewAge.MOD_ID, "create_new_age_monkey_edition", version))
            );

            Pack builtinDataPack = Pack.readMetaAndCreate(
                    packLocationInfo,
                    new PathPackResources.PathResourcesSupplier(path),
                    PackType.SERVER_DATA,
                    new PackSelectionConfig(false, Pack.Position.TOP, false)
            );

            event.addRepositorySource((packConsumer) -> packConsumer.accept(builtinDataPack));
        }
    }
}
