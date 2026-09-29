package org.antarcticgardens.cna.content.electricity.network;

import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

public record EnergyBlockEntity(BlockEntity entity, EnergyStorage storage) {
}
