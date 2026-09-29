package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllCasings;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.client.foundation.block.connected.SimpleCTBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlock;

/**
 * Connected textures and casing connectivity. Upstream attached both in the blocks' Registrate
 * {@code onRegister} callbacks; Create Fly keeps them in client-side registries keyed by block.
 */
public class CNAConnectedTextures {
    public static void register() {
        EncasedCTBehaviour heatCasing = new EncasedCTBehaviour(CNASpriteShifts.HEAT_CASING);
        AllModels.register(CNABlocks.HEAT_CASING, CTModel.of(heatCasing));
        AllModels.register(CNABlocks.ENCASED_HEAT_PIPE, CTModel.of(heatCasing));
        AllCasings.make(CNABlocks.HEAT_CASING, CNASpriteShifts.HEAT_CASING);
        AllCasings.make(CNABlocks.ENCASED_HEAT_PIPE, CNASpriteShifts.HEAT_CASING,
                (state, face) -> !state.getValue(EncasedHeatPipeBlock.getDirectionProperty(face)));

        EncasedCTBehaviour reactorCasing = new EncasedCTBehaviour(CNASpriteShifts.REACTOR_CASING);
        AllModels.register(CNABlocks.REACTOR_CASING, CTModel.of(reactorCasing));
        AllModels.register(CNABlocks.REACTOR_ENCASED_HEAT_PIPE, CTModel.of(reactorCasing));
        AllCasings.make(CNABlocks.REACTOR_CASING, CNASpriteShifts.REACTOR_CASING);
        AllCasings.make(CNABlocks.REACTOR_ENCASED_HEAT_PIPE, CNASpriteShifts.REACTOR_CASING,
                (state, face) -> !state.getValue(EncasedHeatPipeBlock.getDirectionProperty(face)));

        AllModels.register(CNABlocks.REDSTONE_MAGNET, CTModel.of(new SimpleCTBehaviour(CNASpriteShifts.REDSTONE_MAGNET)));
        AllModels.register(CNABlocks.REACTOR_GLASS, CTModel.of(new SimpleCTBehaviour(CNASpriteShifts.REACTOR_GLASS)));
    }
}
