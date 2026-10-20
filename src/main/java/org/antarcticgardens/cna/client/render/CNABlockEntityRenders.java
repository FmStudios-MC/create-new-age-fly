package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;

/**
 * Renderers and Flywheel visuals, which upstream chained onto the Registrate block entity builders.
 * <p>
 * {@code visual} skips the renderer while Flywheel draws; {@code normal} always runs it. Registrate's
 * one-argument {@code visual(...)} kept the renderer running, which the energiser's beam, the brushes'
 * coil and the stirling engine's flywheel rely on: those renderers skip the shaft themselves when the
 * visual already draws it. Create Fly's own helpers of the same names are private on 1.21.8.
 */
public class CNABlockEntityRenders {
    public static void register() {
        normal(CNABlockEntityTypes.ENERGISER, EnergiserRenderer::new, SingleAxisRotatingVisual::shaft);
        normal(CNABlockEntityTypes.CARBON_BRUSHES, CarbonBrushesRenderer::new, SingleAxisRotatingVisual::shaft);
        normal(CNABlockEntityTypes.STIRLING_ENGINE, StirlingEngineRenderer::new, StirlingEngineVisual::new);
        // was visual(..., false): the renderer only runs without Flywheel
        visual(CNABlockEntityTypes.GENERATOR_COIL, KineticBlockEntityRenderer::new,
                SingleAxisRotatingVisual.of(CNAPartialModels.GENERATOR_COIL));

        visual(CNABlockEntityTypes.BASIC_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        visual(CNABlockEntityTypes.ADVANCED_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));
        visual(CNABlockEntityTypes.REINFORCED_MOTOR, MotorRenderer::new, OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));

        BlockEntityRenderers.register(CNABlockEntityTypes.ELECTRICAL_CONNECTOR, ElectricalConnectorRenderer::new);
        BlockEntityRenderers.register(CNABlockEntityTypes.STREET_LIGHT, ElectricalConnectorRenderer::new);
    }

    private static <T extends BlockEntity, P extends T> void visual(BlockEntityType<P> type, BlockEntityRendererProvider<T> renderer,
                                                                    SimpleBlockEntityVisualizer.Factory<P> visual) {
        BlockEntityRenderers.register(type, renderer);
        SimpleBlockEntityVisualizer.builder(type).factory(visual).skipVanillaRender(be -> true).apply();
    }

    private static <T extends BlockEntity, P extends T> void normal(BlockEntityType<P> type, BlockEntityRendererProvider<T> renderer,
                                                                    SimpleBlockEntityVisualizer.Factory<P> visual) {
        BlockEntityRenderers.register(type, renderer);
        SimpleBlockEntityVisualizer.builder(type).factory(visual).skipVanillaRender(be -> false).apply();
    }
}
