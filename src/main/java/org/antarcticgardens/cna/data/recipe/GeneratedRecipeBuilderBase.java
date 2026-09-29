package org.antarcticgardens.cna.data.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.zurrtum.create.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.antarcticgardens.cna.CreateNewAge;

@SuppressWarnings("unchecked")
public abstract class GeneratedRecipeBuilderBase<SELF> {
    protected final ItemLike result;

    protected String name = null;
    protected String suffix = "";
    protected RecipeCategory category = RecipeCategory.MISC;
    protected int amount = 1;
    protected Criterion<InventoryChangeTrigger.TriggerInstance> trigger;

    public GeneratedRecipeBuilderBase(ItemLike result) {
        this.result = result;
    }

    protected SELF suffix(String suffix) {
        this.suffix = suffix;
        return (SELF) this;
    }
    
    protected SELF name(String name) {
        this.name = name;
        return (SELF) this;
    }

    protected SELF category(RecipeCategory category) {
        this.category = category;
        return (SELF) this;
    }

    protected SELF unlockedBy(TagKey<Item> required) {
        trigger = RegistrateRecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(required).build());
        return (SELF) this;
    }

    protected SELF unlockedBy(ItemLike... required) {
        trigger = RegistrateRecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(required).build());
        return (SELF) this;
    }

    protected SELF amount(int amount) {
        this.amount = amount;
        return (SELF) this;
    }

    protected Identifier createLocation() {
        return createLocation("");
    }

    protected Identifier createLocation(String type) {
        String name = this.name == null ? RegisteredObjectsHelper.getKeyOrThrow(result.asItem()).getPath() : this.name;
        return Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, type + (type.isEmpty() ? "" : "/") + name + suffix);
    }
}
