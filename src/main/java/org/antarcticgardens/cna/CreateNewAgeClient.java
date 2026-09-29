package org.antarcticgardens.cna;

import net.fabricmc.api.ClientModInitializer;
import org.antarcticgardens.cna.client.CNABlockEntityBehaviours;
import org.antarcticgardens.cna.client.tooltip.CNAItemTooltips;

/**
 * Client entrypoint. Renderers, Flywheel visuals, connected textures, casing connectivity and the
 * client halves of block entity behaviours are registered here (phase 3 of the port, see
 * PORTING.md); ponder scenes follow in phase 5.
 */
public class CreateNewAgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CNABlockEntityBehaviours.register();
        CNAItemTooltips.register();
    }
}
