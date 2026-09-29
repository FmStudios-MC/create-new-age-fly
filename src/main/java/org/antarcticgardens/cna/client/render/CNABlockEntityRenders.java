package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import org.antarcticgardens.cna.CNABlockEntityTypes;

/**
 * Renderers and Flywheel visuals, which upstream chained onto the Registrate block entity builders.
 * <p>
 * {@code visual} skips the renderer while Flywheel draws; {@code normal} always runs it. Registrate's
 * one-argument {@code visual(...)} kept the renderer running, which the energiser's beam, the brushes'
 * coil and the stirling engine's flywheel rely on: those renderers skip the shaft themselves when the
 * visual already draws it.
 */
public class CNABlockEntityRenders {
    public static void register() {
        AllBlockEntityRenders.normal(CNABlockEntityTypes.ENERGISER, EnergiserRenderer::new, SingleAxisRotatingVisual::shaft);
        AllBlockEntityRenders.normal(CNABlockEntityTypes.CARBON_BRUSHES, CarbonBrushesRenderer::new, SingleAxisRotatingVisual::shaft);
        AllBlockEntityRenders.normal(CNABlockEntityTypes.STIRLING_ENGINE, StirlingEngineRenderer::new, StirlingEngineVisual::new);
        // was visual(..., false): the renderer only runs without Flywheel
        AllBlockEntityRenders.visual(CNABlockEntityTypes.GENERATOR_COIL, KineticBlockEntityRenderer::new,
                SingleAxisRotatingVisual.of(CNAPartialModels.GENERATOR_COIL));

        AllBlockEntityRenders.visual(CNABlockEntityTypes.BASIC_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        AllBlockEntityRenders.visual(CNABlockEntityTypes.ADVANCED_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        AllBlockEntityRenders.visual(CNABlockEntityTypes.REINFORCED_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));

        AllBlockEntityRenders.render(CNABlockEntityTypes.ELECTRICAL_CONNECTOR, ElectricalConnectorRenderer::new);
        AllBlockEntityRenders.render(CNABlockEntityTypes.STREET_LIGHT, ElectricalConnectorRenderer::new);
    }
}
