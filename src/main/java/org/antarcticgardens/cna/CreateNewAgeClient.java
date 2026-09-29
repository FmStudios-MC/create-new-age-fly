package org.antarcticgardens.cna;

import net.fabricmc.api.ClientModInitializer;
import org.antarcticgardens.cna.client.CNABlockEntityBehaviours;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import org.antarcticgardens.cna.client.model.QuadListModel;
import org.antarcticgardens.cna.client.ponder.CNAPonders;
import org.antarcticgardens.cna.client.render.CNABlockEntityRenders;
import org.antarcticgardens.cna.client.render.CNAConnectedTextures;
import org.antarcticgardens.cna.client.render.CNAPartialModels;
import org.antarcticgardens.cna.client.tooltip.CNAItemTooltips;

/**
 * Client entrypoint: models, renderers, Flywheel visuals, connected textures, casing connectivity,
 * client halves of block entity behaviours, item tooltips and ponder scenes.
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
        PonderIndex.addPlugin(new CNAPonders());
    }
}
