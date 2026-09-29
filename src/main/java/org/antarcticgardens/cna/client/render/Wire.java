package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.config.CNAConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A sagging wire between two points, as a strip of crossed quads.
 * <p>
 * 26.2 renders block entities in two passes: state is extracted while the level is readable, and
 * geometry is submitted later without it. So {@link #build} computes every vertex, including its
 * light, up front, and {@link Mesh#emit} only writes them out. The maths is upstream's unchanged.
 */
public class Wire {
    public static final float SAG_FACTOR = 0.9f;
    private static final Vector3f GLOBAL_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private final Vector3f start;
    private final Vector3f direction;
    private final Vector3f up;
    private final float sectionsPerMeter;
    private final float totalLength;
    private final float thickness;
    private final int maxLength;

    public Wire(Vector3f start, Vector3f end, float sectionsPerMeter, float thickness, int maxLength) {
        this.start = start;
        Vector3f difference = end.sub(start);
        this.totalLength = difference.length();
        direction = difference.div(totalLength);
        up = calculateUp(direction);
        this.sectionsPerMeter = sectionsPerMeter;
        this.thickness = thickness;
        this.maxLength = maxLength;
    }

    private Vector3f calculateUp(Vector3f direction) {
        Vector3f right = new Vector3f(direction).cross(GLOBAL_UP);
        if (right.equals(new Vector3f(0.0f), 0.01f))
            return new Vector3f(1.0f, 0.0f, 0.0f);
        return right.cross(direction).normalize();
    }

    private float catenary(double x, double length, int sections) {
        double a = length / CNAConfig.getServer().maxWireLength.get() * SAG_FACTOR;
        x = (x / sections * 2 - 1);
        return (float) ((Math.cosh(x) - Math.cosh(1.0f)) * a);
    }

    /** Vertices relative to the block containing the wire's start. */
    public Mesh build(Level level) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(start.x - Math.floor(start.x), start.y - Math.floor(start.y), start.z - Math.floor(start.z));
        poseStack.mulPose(new Matrix4f().rotateTowards(direction, up));

        int maxSections = Mth.ceil((maxLength * Mth.sqrt(3.0F)) * sectionsPerMeter * 2.0);
        int sectionsAmount = Math.min((int) Math.ceil(totalLength * sectionsPerMeter), maxSections);
        if (sectionsAmount <= 0 || !Float.isFinite(totalLength))
            return new Mesh(new float[0], new int[0]);

        float catenaryScalar = new Vector3f(up).mul(GLOBAL_UP).length();
        float lastCatenary = 0.0f;
        float sectionLength = 1.0F / sectionsPerMeter;

        // 2 quads of 4 vertices per section; x, y, z, u, v per vertex
        float[] vertices = new float[sectionsAmount * 8 * 5];
        int[] light = new int[sectionsAmount * 8];
        int vertex = 0;
        Vector3f position = new Vector3f();

        for (int sectionId = 1; sectionId <= sectionsAmount; sectionId++) {
            float sectionOffset = sectionLength * sectionId;
            Vector3f lightPos = new Vector3f(start)
                    .add(new Vector3f(direction).mul(sectionOffset))
                    .add(new Vector3f(up).mul(lastCatenary));
            BlockPos lightBlockPos = BlockPos.containing(new Vec3(lightPos));
            int packedLight = LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, lightBlockPos),
                    level.getBrightness(LightLayer.SKY, lightBlockPos));

            float catenary = catenary(sectionId, totalLength, sectionsAmount) * catenaryScalar;
            float ht = thickness / 2;
            float vOffset = (sectionId % 2 == 0) ? 0.5f : 0.0f;
            Matrix4f pose = poseStack.last().pose();
            for (int i = -1; i <= 1; i += 2) {
                float[][] corners = {
                        {ht, ht * i + lastCatenary, 0.0f, 0.0f, 0.5f + vOffset},
                        {-ht, -ht * i + lastCatenary, 0.0f, 0.0f, 0.0f + vOffset},
                        {-ht, -ht * i + catenary, sectionLength, 1.0f, 0.0f + vOffset},
                        {ht, ht * i + catenary, sectionLength, 1.0f, 0.5f + vOffset}};
                for (float[] c : corners) {
                    pose.transformPosition(c[0], c[1], c[2], position);
                    int o = vertex * 5;
                    vertices[o] = position.x;
                    vertices[o + 1] = position.y;
                    vertices[o + 2] = position.z;
                    vertices[o + 3] = c[3];
                    vertices[o + 4] = c[4];
                    light[vertex++] = packedLight;
                }
            }

            lastCatenary = catenary;
            poseStack.translate(0.0f, 0.0f, sectionLength);
        }
        return new Mesh(vertices, light);
    }

    public record Mesh(float[] vertices, int[] light) {
        public void emit(PoseStack.Pose pose, VertexConsumer consumer) {
            for (int i = 0; i < light.length; i++) {
                int o = i * 5;
                consumer.addVertex(pose, vertices[o], vertices[o + 1], vertices[o + 2])
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                        .setUv(vertices[o + 3], vertices[o + 4])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light[i])
                        .setNormal(pose, 0.0f, 1.0f, 0.0f);
            }
        }
    }
}
