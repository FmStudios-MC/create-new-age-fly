package org.antarcticgardens.cna;

import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CNAItems {
    // The creative tab lists block items before plain items, as Registrate did. Two lists, because
    // this class is initialised from inside CNABlocks' first registerBlockItem call.
    private static final List<Item> TAB_BLOCK_ITEMS = new ArrayList<>();
    private static final List<Item> TAB_ITEMS = new ArrayList<>();

    public static final Item OVERCHARGED_GOLD = register("overcharged_gold", Item::new);
    public static final Item OVERCHARGED_IRON = register("overcharged_iron", Item::new);
    public static final Item OVERCHARGED_DIAMOND = register("overcharged_diamond", Item::new);
    public static final Item NUCLEAR_FUEL = register("nuclear_fuel", Item::new);
    public static final Item THORIUM = register("thorium", Item::new);
    public static final Item RADIOACTIVE_THORIUM = register("radioactive_thorium", Item::new);

    public static final SequencedAssemblyItem INCOMPLETE_FUEL = registerHidden("incomplete_fuel", SequencedAssemblyItem::new);
    public static final SequencedAssemblyItem INCOMPLETE_REACTOR_CASING = registerHidden("incomplete_reactor_casing", SequencedAssemblyItem::new);
    public static final SequencedAssemblyItem INCOMPLETE_WIRE = registerHidden("incomplete_wire", SequencedAssemblyItem::new);
    public static final SequencedAssemblyItem INCOMPLETE_ENCHANTED_GOLDEN_APPLE = registerHidden("incomplete_enchanted_golden_apple", SequencedAssemblyItem::new);

    public static final Item OVERCHARGED_IRON_SHEET = register("overcharged_iron_sheet", Item::new);
    public static final Item OVERCHARGED_GOLDEN_SHEET = register("overcharged_golden_sheet", Item::new);
    public static final Item BLANK_CIRCUIT = register("blank_circuit", Item::new);
    public static final Item COPPER_CIRCUIT = register("copper_circuit", Item::new);

    public static final ElectricWireItem COPPER_WIRE = register("copper_wire", ElectricWireItem::newCopperWire);
    public static final ElectricWireItem OVERCHARGED_IRON_WIRE = register("overcharged_iron_wire", ElectricWireItem::newIronWire);
    public static final ElectricWireItem OVERCHARGED_GOLDEN_WIRE = register("overcharged_golden_wire", ElectricWireItem::newGoldenWire);
    public static final ElectricWireItem OVERCHARGED_DIAMOND_WIRE = register("overcharged_diamond_wire", ElectricWireItem::newDiamondWire);

    static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreateNewAge.CREATIVE_TAB_KEY,
                // Create Fly passes (null, -1) too: 26.2 has no way to place a tab relative to another mod's.
                CreativeModeTab.builder(null, -1)
                        .title(Component.translatable("tab." + CreateNewAge.MOD_ID + ".tab"))
                        .icon(() -> new ItemStack(CNABlocks.GENERATOR_COIL))
                        .displayItems((parameters, output) -> {
                            TAB_BLOCK_ITEMS.forEach(output::accept);
                            TAB_ITEMS.forEach(output::accept);
                        })
                        .build());
    }

    static void registerBlockItem(Block block, BiFunction<Block, Item.Properties, ? extends Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block));
        // useBlockDescriptionPrefix keeps the "block.create_new_age.*" translation keys of the lang files.
        Item item = factory.apply(block, new Item.Properties().setId(key).useBlockDescriptionPrefix());
        TAB_BLOCK_ITEMS.add(Registry.register(BuiltInRegistries.ITEM, key, item));
    }

    private static <I extends Item> I register(String name, Function<Item.Properties, I> factory) {
        I item = registerHidden(name, factory);
        TAB_ITEMS.add(item);
        return item;
    }

    private static <I extends Item> I registerHidden(String name, Function<Item.Properties, I> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
    }
}
