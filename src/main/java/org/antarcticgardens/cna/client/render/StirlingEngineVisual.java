package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;

import java.util.function.Consumer;

/** The engine's shaft. Its miniature flywheel is drawn by {@link StirlingEngineRenderer}. */
public class StirlingEngineVisual extends KineticBlockEntityVisual<StirlingEngineBlockEntity> {
    protected final RotatingInstance shaft;

    public StirlingEngineVisual(VisualizationContext context, StirlingEngineBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT))
                .createInstance();
        shaft.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(rotationAxis())
                .setChanged();
    }

    @Override
    public void update(float pt) {
        shaft.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft);
    }

    @Override
    protected void _delete() {
        shaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
    }
}
