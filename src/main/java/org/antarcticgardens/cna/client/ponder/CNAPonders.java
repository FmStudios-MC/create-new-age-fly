package org.antarcticgardens.cna.client.ponder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;
import com.zurrtum.create.client.ponder.api.level.PonderLevel;
import com.zurrtum.create.client.ponder.api.registration.*;
import net.minecraft.resources.Identifier;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAPonders implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateNewAge.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
        register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        PonderPlugin.super.registerSharedText(helper);
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
        PonderPlugin.super.onPonderLevelRestore(ponderLevel);
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
        PonderPlugin.super.indexExclusions(helper);
    }

    public static void register(PonderSceneRegistrationHelper<Identifier> helper) {
        // Keyed by item, as Create Fly does; blocks and items both qualify.
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));

        HELPER.addStoryBoard(CNABlocks.BASIC_ENERGISER, "energiser", EnergiserPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REINFORCED_ENERGISER, "energiser", EnergiserPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.ADVANCED_ENERGISER, "energiser", EnergiserPonder::ponder);

        HELPER.addStoryBoard(CNABlocks.HEAT_PIPE, "heating", HeatingPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.HEAT_PUMP, "heating", HeatingPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.BASIC_SOLAR_HEATING_PLATE, "heating", HeatingPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.ADVANCED_SOLAR_HEATING_PLATE, "heating", HeatingPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.STIRLING_ENGINE, "heating", HeatingPonder::ponder);

        HELPER.addStoryBoard(CNABlocks.HEATER, "heater", HeaterPonder::ponder);

        HELPER.addStoryBoard(CNABlocks.REACTOR_CASING, "reactor", ReactorPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REACTOR_GLASS, "reactor", ReactorPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REACTOR_FUEL_ACCEPTOR, "reactor", ReactorPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REACTOR_ROD, "reactor", ReactorPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REACTOR_HEAT_VENT, "reactor", ReactorPonder::ponder);
        HELPER.addStoryBoard(CNAItems.NUCLEAR_FUEL, "reactor", ReactorPonder::ponder);

        HELPER.addStoryBoard(CNABlocks.ELECTRICAL_CONNECTOR, "wires", ElectricityPonder::ponder);
        HELPER.addStoryBoard(CNAItems.COPPER_WIRE, "wires", ElectricityPonder::ponder);
        HELPER.addStoryBoard(CNAItems.OVERCHARGED_DIAMOND_WIRE, "wires", ElectricityPonder::ponder);
        HELPER.addStoryBoard(CNAItems.OVERCHARGED_GOLDEN_WIRE, "wires", ElectricityPonder::ponder);
        HELPER.addStoryBoard(CNAItems.OVERCHARGED_IRON_WIRE, "wires", ElectricityPonder::ponder);

        HELPER.addStoryBoard(CNABlocks.CARBON_BRUSHES, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.GENERATOR_COIL, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.MAGNETITE_BLOCK, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.REDSTONE_MAGNET, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.LAYERED_MAGNET, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.FLUXUATED_MAGNETITE, "generation", GenerationPonder::ponder);
        HELPER.addStoryBoard(CNABlocks.NETHERITE_MAGNET, "generation", GenerationPonder::ponder);

        HELPER.forComponents(CNABlocks.BASIC_MOTOR, CNABlocks.ADVANCED_MOTOR, CNABlocks.REINFORCED_MOTOR)
                .addStoryBoard("motor", MotorPonder::motor);

        HELPER.forComponents(CNABlocks.BASIC_MOTOR_EXTENSION, CNABlocks.ADVANCED_MOTOR_EXTENSION)
                .addStoryBoard("motor_extension", MotorExtensionPonder::motorExtension);

    }

    static Identifier ELECTRICAL = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "electrical");
    static Identifier WIRING = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "wiring");
    static Identifier MAGNETS = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "magnets");
    static Identifier ELECTRICITY_GENERATION = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "electricity_generation");
    static Identifier HEATING = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "heating");
    static Identifier REACTOR = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "reactor");
    static Identifier MOTOR_EXTENSION = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "motor_extension");

    public static void register(PonderTagRegistrationHelper<Identifier> helper) {
        PonderTagRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));

        HELPER.registerTag(ELECTRICAL)
                .item(CNABlocks.ADVANCED_ENERGISER)
                .addToIndex()
                .register();

        HELPER.registerTag(WIRING)
                .item(CNAItems.COPPER_WIRE)
                .addToIndex()
                .register();

        HELPER.registerTag(MAGNETS)
                .item(CNABlocks.REDSTONE_MAGNET)
                .addToIndex()
                .register();

        HELPER.registerTag(ELECTRICITY_GENERATION)
                .item(CNABlocks.GENERATOR_COIL)
                .addToIndex()
                .register();

        HELPER.registerTag(HEATING)
                .item(CNABlocks.HEAT_PIPE)
                .addToIndex()
                .register();

        HELPER.registerTag(REACTOR)
                .item(CNABlocks.REACTOR_ROD)
                .addToIndex()
                .register();

        HELPER.registerTag(MOTOR_EXTENSION)
                .item(CNABlocks.BASIC_MOTOR_EXTENSION)
                .addToIndex()
                .register();

        HELPER.addToTag(ELECTRICAL)
                .add(CNABlocks.BASIC_ENERGISER)
                .add(CNABlocks.REINFORCED_ENERGISER)
                .add(CNABlocks.ADVANCED_ENERGISER)

                .add(CNABlocks.BASIC_MOTOR)
                .add(CNABlocks.ADVANCED_MOTOR)
                .add(CNABlocks.REINFORCED_MOTOR)

                .add(CNABlocks.GENERATOR_COIL)

                .add(CNABlocks.ELECTRICAL_CONNECTOR);

        HELPER.addToTag(WIRING)
                .add(CNABlocks.ELECTRICAL_CONNECTOR)

                .add(CNAItems.COPPER_WIRE)
                .add(CNAItems.OVERCHARGED_IRON_WIRE)
                .add(CNAItems.OVERCHARGED_GOLDEN_WIRE)
                .add(CNAItems.OVERCHARGED_DIAMOND_WIRE);

        HELPER.addToTag(MAGNETS)
                .add(CNABlocks.MAGNETITE_BLOCK)
                .add(CNABlocks.REDSTONE_MAGNET)
                .add(CNABlocks.LAYERED_MAGNET)
                .add(CNABlocks.FLUXUATED_MAGNETITE)
                .add(CNABlocks.NETHERITE_MAGNET);

        HELPER.addToTag(ELECTRICITY_GENERATION)
                .add(CNABlocks.CARBON_BRUSHES)
                .add(CNABlocks.GENERATOR_COIL)
                .add(CNABlocks.MAGNETITE_BLOCK)
                .add(CNABlocks.REDSTONE_MAGNET)
                .add(CNABlocks.LAYERED_MAGNET)
                .add(CNABlocks.FLUXUATED_MAGNETITE)
                .add(CNABlocks.NETHERITE_MAGNET);

        HELPER.addToTag(HEATING)
                .add(CNABlocks.HEAT_PIPE)
                .add(CNABlocks.HEAT_PUMP)
                .add(CNABlocks.HEATER)
                .add(CNABlocks.STIRLING_ENGINE)
                .add(CNABlocks.REACTOR_ROD)
                .add(CNABlocks.BASIC_SOLAR_HEATING_PLATE)
                .add(CNABlocks.ADVANCED_SOLAR_HEATING_PLATE);

        HELPER.addToTag(REACTOR)
                .add(CNABlocks.REACTOR_CASING)
                .add(CNABlocks.REACTOR_GLASS)
                .add(CNABlocks.REACTOR_ROD)
                .add(CNABlocks.REACTOR_HEAT_VENT)
                .add(CNABlocks.REACTOR_FUEL_ACCEPTOR)
                .add(CNAItems.NUCLEAR_FUEL);

        HELPER.addToTag(MOTOR_EXTENSION)
                .add(CNABlocks.BASIC_MOTOR_EXTENSION)
                .add(CNABlocks.ADVANCED_MOTOR_EXTENSION);
    }
}
