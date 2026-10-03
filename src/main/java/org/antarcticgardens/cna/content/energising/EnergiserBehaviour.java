package org.antarcticgardens.cna.content.energising;

import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.content.kinetics.belt.BeltHelper;
import com.zurrtum.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.zurrtum.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.zurrtum.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.energy.EnergyHelper;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.antarcticgardens.cna.energy.ItemStackHolder;
import org.joml.Math;

import java.util.List;
import java.util.stream.Collectors;

public class EnergiserBehaviour extends BeltProcessingBehaviour {
    protected int tier;
    protected EnergiserBlockEntity be;
    public boolean capacitorMode = false;

    public EnergiserBehaviour(EnergiserBlockEntity be) {
        super(be);
        whenItemEnters(this::itemEnter);
        whileItemHeld(this::itemHeld);
        this.be = be;
    }

    public EnergisingRecipe getRecipe(ItemStack stack) {
        // Create Fly expands sequenced assembly steps into ordinary recipes, so one lookup covers
        // both of upstream's paths. Recipes only exist on the server in 26.2.
        if (!(be.getLevel() instanceof ServerLevel level)) {
            return null;
        }

        return level.recipeAccess()
                .getRecipeFor(CNARecipeTypes.ENERGISING, new SingleRecipeInput(stack), level)
                .map(RecipeHolder::value)
                .orElse(null);
    }

    public EnergisingRecipe currentRecipe;
    public long charged;
    public long needed;

    private boolean shouldCreateParticles = false;

    @Override
    public void read(ValueInput nbt, boolean clientPacket) {
        charged = nbt.getLongOr("charged", 0L);
        needed = nbt.getLongOr("needed", 0L);
        shouldCreateParticles = nbt.getBooleanOr("shouldCreateParticles", false);
        // Upstream read "capacitorModer", so the flag never survived a reload.
        capacitorMode = nbt.getBooleanOr("capacitorMode", false);
        super.read(nbt, clientPacket);
    }

    @Override
    public void write(ValueOutput nbt, boolean clientPacket) {
        nbt.putLong("charged", charged);
        nbt.putLong("needed", needed);
        nbt.putBoolean("shouldCreateParticles",shouldCreateParticles);
        nbt.putBoolean("capacitorMode", capacitorMode);
        if (clientPacket)
            shouldCreateParticles = false;
        super.write(nbt, clientPacket);
    }

    public long sinceUpdate = 0;

    @Override
    public void tick() {
        super.tick();
        if (be.getLevel() == null) {
            return;
        }
        if (!be.getLevel().isClientSide() && needed > 0) {
            sinceUpdate--;
            if (sinceUpdate <= 0) {
                needed = 0;
                // Refund what a recipe drew. In capacitor mode "charged" is the item's own energy,
                // which never came out of this storage (upstream refunded it anyway).
                if (!capacitorMode)
                    be.getEnergyStorage().internalInsert(charged, false);
                charged = 0;
                currentRecipe = null;
                capacitorMode = false;
                blockEntity.sendData();
            }
        }

        if (!capacitorMode) {
            be.lastCharged = -1;

            if (needed > 0) {
                be.lastCharged = be.getEnergyStorage().internalExtract(
                        (long) Math.min(eSpeed(),
                                needed - charged), false);
                charged += be.lastCharged;
            }
        }

        if (be.getLevel().isClientSide()) {
            be.size -= 0.15f;
            be.size = Math.clamp(0, 1, be.size);
            if (needed > 0 && charged > 0) {
                be.size = (float) Math.lerp(be.size, (float) charged / needed, 0.6);
            } else if(shouldCreateParticles) {
                shouldCreateParticles = false;
                var rand = be.getLevel().getRandom();
                for (int i = 0 ; i < 6 ; i++) {
                    be.getLevel().addParticle(ParticleTypes.GLOW,
                            false,
                            false,
                            be.getBlockPos().getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.4,
                            be.getBlockPos().getY() - 1.4 + (rand.nextFloat() - 0.5) * 0.4,
                            be.getBlockPos().getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.4,
                            0.0, 0.5, 0.0);

                }
                be.getLevel().playLocalSound(be.getBlockPos().getX() + 0.5,
                        be.getBlockPos().getY() - 1.4,
                        be.getBlockPos().getZ() + 0.5,
                        SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 0.3f, 2.0f, true);
            }
        }
    }

    private ProcessingResult itemHeld(TransportedItemStack transportedItemStack, TransportedItemStackHandlerBehaviour handler) {
        if (currentRecipe == null) {
            currentRecipe = getRecipe(transportedItemStack.stack);
        }
        if (be.getSpeed() == 0 || (currentRecipe == null && !capacitorMode) || be.getLevel() == null) {
            return ProcessingResult.PASS;
        }
        if (capacitorMode) {
            ItemStackHolder holder = new ItemStackHolder(transportedItemStack.stack);
            EnergyStorage itemStorage = holder.findEnergyStorage();
            
            if (itemStorage != null) {
                // Committed before anything else: upstream returned from inside the transaction on
                // the last step, which rolled the energiser's storage back while the item kept the
                // charged stack.
                try (Transaction t = Transaction.openOuter()) {
                    be.lastCharged = EnergyHelper.moveEnergy(be.getEnergyStorage(), itemStorage, eSpeed(), t);
                    t.commit();
                }

                charged = itemStorage.getAmount();
                needed = itemStorage.getCapacity();
                sinceUpdate = 10;
                transportedItemStack.stack = holder.toItemStack();

                blockEntity.sendData();

                if (charged >= needed) {
                    capacitorMode = false;
                    charged = 0;
                    needed = 0;
                    shouldCreateParticles = true;
                    return ProcessingResult.PASS;
                }

                return ProcessingResult.HOLD;
            }

            capacitorMode = false;
            return ProcessingResult.PASS;
        }

        int count = transportedItemStack.stack.getCount();
        needed = (long) count * currentRecipe.getEnergyNeeded();
        sinceUpdate = 10;

        if (charged >= needed) {
            List<TransportedItemStack> out = currentRecipe.assemble(new SingleRecipeInput(transportedItemStack.stack), handler.getLevel().getRandom()).stream()
                    .map(stack -> {
                        TransportedItemStack copy = transportedItemStack.copy();
                        boolean centered = BeltHelper.isItemUpright(stack);
                        stack.setCount(stack.getCount() * count);
                        copy.stack = stack;
                        copy.angle = centered ? 180 : be.getLevel().getRandom().nextInt(360);
                        return copy;
                    })
                    .collect(Collectors.toList());

            if (charged > needed) {
                be.getEnergyStorage().internalInsert(charged - needed, false);
            }

            charged = 0;
            needed = 0;

            if (out.isEmpty()) {
                handler.handleProcessingOnItem(transportedItemStack,
                        TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            } else {
                handler.handleProcessingOnItem(transportedItemStack,
                        TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(out));
            }
            shouldCreateParticles = true;
            blockEntity.sendData();
            currentRecipe = null;
        }

        return ProcessingResult.HOLD;
    }

    private long eSpeed() {
        return EnergiserBlock.getStrength(be.tier) * (long) Math.abs(be.getSpeed() * 0.1);
    }

    private ProcessingResult itemEnter(TransportedItemStack transportedItemStack, TransportedItemStackHandlerBehaviour transportedItemStackHandlerBehaviour) {
        if (be.getSpeed() == 0) {
            return ProcessingResult.PASS;
        }

        EnergyStorage itemStorage = new ItemStackHolder(transportedItemStack.stack).findEnergyStorage();

        if (itemStorage != null && itemStorage.getAmount() < itemStorage.getCapacity()) {
            capacitorMode = true;
            charged = itemStorage.getAmount();
            needed = itemStorage.getCapacity();
            sinceUpdate = 10;
            return ProcessingResult.HOLD;
        }
        
        capacitorMode = false;

        currentRecipe = getRecipe(transportedItemStack.stack);
        sinceUpdate = 10;
        if (currentRecipe == null)
            return ProcessingResult.PASS;

        return ProcessingResult.HOLD;
    }

}
