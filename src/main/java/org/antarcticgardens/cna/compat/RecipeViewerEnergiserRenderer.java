package org.antarcticgardens.cna.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNABlocks;

import java.util.function.Function;

public class RecipeViewerEnergiserRenderer {
    private final Function<BlockState, GuiGameElement.GuiRenderBuilder> blockElement;
    
    public RecipeViewerEnergiserRenderer(Function<BlockState, GuiGameElement.GuiRenderBuilder> blockElement) {
        this.blockElement = blockElement;
    }
    
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        blockElement.apply(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(20)
                .render(graphics);

        blockElement.apply(CNABlocks.BASIC_ENERGISER.getDefaultState())
                .scale(20)
                .render(graphics);

        blockElement.apply(AllBlocks.DEPOT.getDefaultState())
                .scale(20)
                .atLocal(0, 2, 0)
                .render(graphics);

        matrixStack.popPose();
    }

    public static float getCurrentAngle() {
        return (AnimationTickHolder.getRenderTime() * 4f) % 360;
    }

    public static BlockState shaft(Direction.Axis axis) {
        return AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, axis);
    }
}
