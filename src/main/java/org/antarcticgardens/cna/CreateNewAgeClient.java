package org.antarcticgardens.cna;

import net.fabricmc.api.ClientModInitializer;
import org.antarcticgardens.cna.client.CNABlockEntityBehaviours;
import org.antarcticgardens.cna.client.model.QuadListModel;
import org.antarcticgardens.cna.client.render.CNABlockEntityRenders;
import org.antarcticgardens.cna.client.render.CNAConnectedTextures;
import org.antarcticgardens.cna.client.render.CNAPartialModels;
import org.antarcticgardens.cna.client.tooltip.CNAItemTooltips;

/**
 * Client entrypoint: models, renderers, Flywheel visuals, connected textures, casing connectivity,
 * client halves of block entity behaviours and item tooltips. Ponder scenes follow in phase 5.
 */
public class CreateNewAgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        QuadListModel.register();
        CNAPartialModels.init();
        CNAConnectedTextures.register();
        CNABlockEntityRenders.register();
        CNABlockEntityBehaviours.register();
        CNAItemTooltips.register();
    }
}
