package org.antarcticgardens.cna.client.behaviour;

import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;

/** Client half of the street light's light level setting. */
public class StreetLightScrollBehaviour extends ScrollValueBehaviour<StreetLightBlockEntity, ServerScrollValueBehaviour> {
    public StreetLightScrollBehaviour(StreetLightBlockEntity be) {
        super(CreateLang.translateDirect("create_new_age.street_light.light_level"), be, new ValueBox());
        needsWrench = true;
    }

    static class ValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 9, 12.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction == Direction.UP || direction == Direction.DOWN)
                return false;
            return super.isSideActive(state, direction);
        }
    }
}
