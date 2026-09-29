package org.antarcticgardens.cna;

import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Item;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNAItems {
    public static final ItemEntry<Item> OVERCHARGED_GOLD =
            REGISTRATE.item("overcharged_gold", Item::new)
                    .register();

    public static final ItemEntry<Item> OVERCHARGED_IRON =
            REGISTRATE.item("overcharged_iron", Item::new)
                    .register();

    public static final ItemEntry<Item> OVERCHARGED_DIAMOND =
            REGISTRATE.item("overcharged_diamond", Item::new)
                    .register();
    
    public static final ItemEntry<Item> NUCLEAR_FUEL =
            REGISTRATE.item("nuclear_fuel", Item::new)
                    .tag(CNATags.Item.NUCLEAR_FUEL.tag)
                    .tag(CNATags.createNuclearEnergyTag(28800))
                    .register();

    public static final ItemEntry<Item> THORIUM =
            REGISTRATE.item("thorium", Item::new)
                    .register();

    public static final ItemEntry<Item> RADIOACTIVE_THORIUM =
            REGISTRATE.item("radioactive_thorium", Item::new)
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_FUEL =
            REGISTRATE.item("incomplete_fuel", SequencedAssemblyItem::new)
                    .removeTab(CreateNewAge.CREATIVE_TAB_KEY)
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_REACTOR_CASING =
            REGISTRATE.item("incomplete_reactor_casing", SequencedAssemblyItem::new)
                    .removeTab(CreateNewAge.CREATIVE_TAB_KEY)
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_WIRE =
            REGISTRATE.item("incomplete_wire", SequencedAssemblyItem::new)
                    .removeTab(CreateNewAge.CREATIVE_TAB_KEY)
                    .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_ENCHANTED_GOLDEN_APPLE =
            REGISTRATE.item("incomplete_enchanted_golden_apple", SequencedAssemblyItem::new)
                    .removeTab(CreateNewAge.CREATIVE_TAB_KEY)
                    .register();


    public static final ItemEntry<Item> OVERCHARGED_IRON_SHEET =
            REGISTRATE.item("overcharged_iron_sheet", Item::new)
                    .register();

    public static final ItemEntry<Item> OVERCHARGED_GOLDEN_SHEET =
            REGISTRATE.item("overcharged_golden_sheet", Item::new)
                    .register();

    public static final ItemEntry<Item> BLANK_CIRCUIT =
            REGISTRATE.item("blank_circuit", Item::new)
                    .register();
    public static final ItemEntry<Item> COPPER_CIRCUIT =
            REGISTRATE.item("copper_circuit", Item::new)
                    .register();


    public static final ItemEntry<ElectricWireItem> COPPER_WIRE =
            REGISTRATE.item("copper_wire", ElectricWireItem::newCopperWire)
                    .register();

    public static final ItemEntry<ElectricWireItem> OVERCHARGED_IRON_WIRE =
            REGISTRATE.item("overcharged_iron_wire", ElectricWireItem::newIronWire)
                    .register();

    public static final ItemEntry<ElectricWireItem> OVERCHARGED_GOLDEN_WIRE =
            REGISTRATE.item("overcharged_golden_wire", ElectricWireItem::newGoldenWire)
                    .register();

    public static final ItemEntry<ElectricWireItem> OVERCHARGED_DIAMOND_WIRE =
            REGISTRATE.item("overcharged_diamond_wire", ElectricWireItem::newDiamondWire)
                    .register();

    public static void load() {  }
}
