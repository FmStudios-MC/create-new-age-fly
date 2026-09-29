package org.antarcticgardens.cna;

import com.zurrtum.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CNATags {
    public enum Block {
        MAGNET(true),
        STOPS_RADIATION(false);

        public final boolean hasItemTag;
        public final TagKey<net.minecraft.world.level.block.Block> blockTag;
        public final TagKey<net.minecraft.world.item.Item> itemTag;
        
        Block(boolean hasItemTag) {
            Identifier location = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, CreateLang.asId(name()));
            this.hasItemTag = hasItemTag;
            this.blockTag = TagKey.create(Registries.BLOCK, location);
            this.itemTag = hasItemTag ? TagKey.create(Registries.ITEM, location) : null;
        }
        
        public static void load() {  }
        
        public static void generate(RegistrateTagsProvider<net.minecraft.world.level.block.Block> provider) {
            TagGen.CreateTagsProvider<net.minecraft.world.level.block.Block> prov = 
                    new TagGen.CreateTagsProvider<>(provider, net.minecraft.world.level.block.Block::builtInRegistryHolder);
            
            prov.tag(MAGNET.blockTag)
                    .add(Blocks.RESPAWN_ANCHOR);
            
            prov.tag(createMagneticForgeTag(4))
                    .add(Blocks.RESPAWN_ANCHOR);
        }
    }
    
    public enum Item {
        NUCLEAR_FUEL("nuclear/fuel"),
        HAZMAT_SUIT("hazmat_suit");
        
        public final TagKey<net.minecraft.world.item.Item> tag;
        
        Item(String id) {
            tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, id));
        }
        
        public static void load() {  }

        public static void generate(RegistrateTagsProvider<net.minecraft.world.item.Item> provider) {
            TagGen.CreateTagsProvider<net.minecraft.world.item.Item> prov =
                    new TagGen.CreateTagsProvider<>(provider, net.minecraft.world.item.Item::builtInRegistryHolder);

            prov.tag(HAZMAT_SUIT.tag)
                    .add(Items.LEATHER_HELMET)
                    .add(Items.LEATHER_CHESTPLATE)
                    .add(Items.LEATHER_LEGGINGS)
                    .add(Items.LEATHER_BOOTS)
                    .add(AllItems.GOGGLES.get());
        }
    }
    
    public static class Common {
        public static TagKey<net.minecraft.world.item.Item>
                NUGGETS_COPPER = CommonMetal.COPPER.nuggets,
                NUGGETS_ZINC = CommonMetal.ZINC.nuggets,
                PLATES_COPPER = CommonMetal.COPPER.plates,
                PLATES_IRON = CommonMetal.IRON.plates,
                PLATES_GOLD = CommonMetal.GOLD.plates;
    }
    
    public static TagKey<net.minecraft.world.item.Item> createNuclearEnergyTag(int energy) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "nuclear/energy_" + energy));
    }

    public static TagKey<net.minecraft.world.level.block.Block> createMagneticForgeTag(int force) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "magnet/force_" + force));
    }
    
    static void load() {
        Block.load();
        Item.load();
    }
}
