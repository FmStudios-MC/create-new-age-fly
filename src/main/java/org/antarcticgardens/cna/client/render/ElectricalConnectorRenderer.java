package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Wires between connectors, plus the wire hanging from the player's view while one is being placed.
 * Upstream's logic; each wire is built into a mesh (vertices and per-section light) and then written out.
 * Upstream's custom "wire" render type (block format, no culling, lightmap) is vanilla's
 * {@code entityCutout} now.
 */
public class ElectricalConnectorRenderer implements BlockEntityRenderer<AbstractElectricalConnector> {
    private static final ResourceLocation TOO_LONG = ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, "textures/wire/red.png");

    public ElectricalConnectorRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    public void render(AbstractElectricalConnector blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay, Vec3 cameraPos) {
        Level level = blockEntity.getLevel();
        if (level == null)
            return;
        List<TexturedMesh> wires = new ArrayList<>();
        blockEntity.getConnectorPositions().forEach((endPos, wireType) ->
                extractConnection(blockEntity.getBlockPos(), endPos, wireType, level, wires));
        extractHand(blockEntity, partialTick, wires);

        for (TexturedMesh wire : wires)
            wire.mesh().emit(poseStack.last(), bufferSource.getBuffer(RenderType.entityCutout(wire.texture())));
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

        Vec3 eyePos = minecraft.gameRenderer.getMainCamera().getPosition();
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

        ResourceLocation texture = wireItem.getWireType().getTextureLocation();
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

    // Wires reach far beyond the connector's own block.
    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public record TexturedMesh(ResourceLocation texture, Wire.Mesh mesh) {
    }
}
