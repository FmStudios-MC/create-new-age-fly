package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.wire.WireType;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wires between connectors, plus the wire hanging from the player's view while one is being placed.
 * Upstream's logic; the wires are built into meshes while extracting and only written out on submit.
 * Upstream's custom "wire" render type (block format, no culling, lightmap) is vanilla's
 * {@code entityCutout} now.
 */
public class ElectricalConnectorRenderer implements BlockEntityRenderer<AbstractElectricalConnector, ElectricalConnectorRenderer.State> {
    private static final Identifier TOO_LONG = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "textures/wire/red.png");

    public ElectricalConnectorRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(AbstractElectricalConnector blockEntity, State state, float partialTick, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(blockEntity, state, breakProgress);
        state.wires.clear();
        if (level == null)
            return;
        blockEntity.getConnectorPositions().forEach((endPos, wireType) ->
                extractConnection(blockEntity.getBlockPos(), endPos, wireType, level, state.wires));
        extractHand(blockEntity, partialTick, state.wires);
    }

    // Makes sure that only one of the two connectors renders the wire
    public boolean shouldRenderConnection(BlockPos pos, BlockPos endPos) {
        return pos.compareTo(endPos) < 0;
    }

    private void extractConnection(BlockPos pos, BlockPos endPos, WireType wireType, Level level, List<TexturedMesh> out) {
        if (!shouldRenderConnection(pos, endPos))
            return;

        if (!(level.getBlockEntity(pos) instanceof AbstractElectricalConnector originConnector) ||
                !(level.getBlockEntity(endPos) instanceof AbstractElectricalConnector endConnector))
            return;

        Vec3 originPoint = originConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(pos));
        Vec3 endPoint = endConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(endPos));
        out.add(new TexturedMesh(wireType.getTextureLocation(), wire(originPoint, endPoint).build(level)));
    }

    private void extractHand(AbstractElectricalConnector blockEntity, float partialTick, List<TexturedMesh> out) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !minecraft.options.getCameraType().isFirstPerson())
            return;

        Level level = player.level();
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof ElectricWireItem))
            itemStack = player.getOffhandItem();
        if (!(itemStack.getItem() instanceof ElectricWireItem wireItem))
            return;

        BlockPos bound = wireItem.getBoundConnector(itemStack);
        if (bound == null || !bound.equals(blockEntity.getBlockPos()))
            return;

        Vec3 eyePos = minecraft.gameRenderer.mainCamera().position();
        Vec3 viewVector = player.getViewVector(partialTick);
        Vec3 wireEnd = eyePos.add(viewVector.scale(player.blockInteractionRange()));
        Vec3 wireStart = blockEntity.getConnectionPoint().add(Vec3.atLowerCornerOf(bound));

        HitResult lookingAt = minecraft.hitResult;
        if (lookingAt instanceof BlockHitResult blockHit) {
            BlockPos lookedAtPos = blockHit.getBlockPos();
            BlockEntity lookedBlockEntity = level.getBlockEntity(lookedAtPos);
            boolean snapped = false;

            if (lookedBlockEntity != null) {
                if (lookedBlockEntity == blockEntity)
                    return;
                if (lookedBlockEntity instanceof AbstractElectricalConnector otherConnector) {
                    if (otherConnector.isConnected(blockEntity.getBlockPos()))
                        return;
                    wireEnd = otherConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(lookedAtPos));
                    snapped = true;
                }
            }

            if (!snapped) {
                Vec3 vec = blockHit.getLocation().subtract(viewVector.scale(0.1));
                if (eyePos.distanceToSqr(wireEnd) > eyePos.distanceToSqr(vec))
                    wireEnd = vec;
            }
        }

        Identifier texture = wireItem.getWireType().getTextureLocation();
        double distanceSqr = wireEnd.distanceToSqr(wireStart);
        int maxDistance = CNAConfig.getServer().maxWireLength.get();

        if (distanceSqr >= (maxDistance * maxDistance)) {
            if (distanceSqr > (maxDistance * maxDistance) * 4)
                return;
            texture = TOO_LONG;
        }

        out.add(new TexturedMesh(texture, wire(wireStart, wireEnd).build(level)));
    }

    private static Wire wire(Vec3 start, Vec3 end) {
        return new Wire(
                start.toVector3f(),
                end.toVector3f(),
                CNAConfig.getClient().wireSectionsPerMeter.get(),
                CNAConfig.getClient().wireThickness.get().floatValue(),
                CNAConfig.getServer().maxWireLength.get()
        );
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        for (TexturedMesh wire : state.wires)
            queue.submitCustomGeometry(matrices, RenderTypes.entityCutout(wire.texture()), wire.mesh()::emit);
    }

    // Wires reach far beyond the connector's own block.
    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public record TexturedMesh(Identifier texture, Wire.Mesh mesh) {
    }

    public static class State extends BlockEntityRenderState {
        public final List<TexturedMesh> wires = new ArrayList<>();
    }
}
