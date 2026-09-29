package org.antarcticgardens.cna.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Transparency;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.antarcticgardens.cna.CreateNewAge;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

/**
 * A block model made of free quads, for geometry that cuboid elements cannot express.
 * <p>
 * Upstream loaded the generator coil through NeoForge's OBJ loader; Fabric has none, and the
 * coil's windings are parallelograms, which a rotated element can only approximate.
 * {@code tools/obj_to_json.py --quads} turns the OBJ into this model's JSON:
 * <pre>
 * { "fabric:type": "create_new_age:quads", "parent": ..., "textures": ..., "display": ...,
 *   "quads": [ { "texture": "#0", "vertices": [[x, y, z, u, v] x4] } ] }
 * </pre>
 * Positions and UVs are in model units (0 to 16), as in element JSON.
 */
public record QuadListModel(@Nullable Identifier parent, TextureSlots.Data textureSlots, @Nullable ItemTransforms transforms,
                            @Nullable Boolean ambientOcclusion, List<Quad> quads) implements UnbakedModel {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "quads");

    public record Quad(String texture, Vector3fc[] positions, float[] u, float[] v) {
    }

    public static void register() {
        UnbakedModelDeserializer.register(ID, QuadListModel::deserialize);
    }

    private static QuadListModel deserialize(JsonObject json, JsonDeserializationContext context) {
        Identifier parent = json.has("parent") ? Identifier.parse(GsonHelper.getAsString(json, "parent")) : null;
        TextureSlots.Data textures = TextureSlots.parseTextureMap(GsonHelper.getAsJsonObject(json, "textures", new JsonObject()));
        ItemTransforms transforms = json.has("display") ? context.deserialize(json.get("display"), ItemTransforms.class) : null;
        Boolean ao = json.has("ambientocclusion") ? GsonHelper.getAsBoolean(json, "ambientocclusion") : null;

        List<Quad> quads = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "quads")) {
            JsonObject quad = element.getAsJsonObject();
            JsonArray vertices = GsonHelper.getAsJsonArray(quad, "vertices");
            if (vertices.size() != 4)
                throw new IllegalArgumentException("A quad needs exactly 4 vertices, got " + vertices.size());
            Vector3fc[] positions = new Vector3fc[4];
            float[] u = new float[4];
            float[] v = new float[4];
            for (int i = 0; i < 4; i++) {
                JsonArray vertex = vertices.get(i).getAsJsonArray();
                positions[i] = new Vector3f(vertex.get(0).getAsFloat(), vertex.get(1).getAsFloat(), vertex.get(2).getAsFloat()).div(16);
                u[i] = vertex.get(3).getAsFloat() / 16;
                v[i] = vertex.get(4).getAsFloat() / 16;
            }
            quads.add(new Quad(GsonHelper.getAsString(quad, "texture"), positions, u, v));
        }
        return new QuadListModel(parent, textures, transforms, ao, List.copyOf(quads));
    }

    @Override
    public UnbakedGeometry geometry() {
        return this::bake;
    }

    private QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName name) {
        ModelBaker.Interner interner = baker.interner();
        Matrix4fc transform = state.transformation().getMatrix();
        QuadCollection.Builder builder = new QuadCollection.Builder();

        for (Quad quad : quads) {
            String slot = quad.texture().startsWith("#") ? quad.texture().substring(1) : quad.texture();
            Material.Baked material = baker.materials().resolveSlot(textureSlots, slot, name);
            TextureAtlasSprite sprite = material.sprite();

            Vector3fc[] positions = new Vector3fc[4];
            long[] uvs = new long[4];
            for (int i = 0; i < 4; i++) {
                // Rotated about the block centre, as vanilla's FaceBakery does with blockstate rotations.
                Vector3f position = new Vector3f(quad.positions()[i]).sub(0.5f, 0.5f, 0.5f);
                transform.transformPosition(position);
                positions[i] = interner.vector(position.add(0.5f, 0.5f, 0.5f));
                uvs[i] = UVPair.pack(sprite.getU(quad.u()[i]), sprite.getV(quad.v()[i]));
            }

            Vector3f normal = new Vector3f(positions[1]).sub(positions[0])
                    .cross(new Vector3f(positions[3]).sub(positions[0]));
            Direction direction = Direction.getApproximateNearest(normal.x, normal.y, normal.z);

            BakedQuad.MaterialInfo info = interner.materialInfo(BakedQuad.MaterialInfo.of(material, Transparency.NONE, -1, true, 0));
            builder.addUnculledFace(new BakedQuad(positions[0], positions[1], positions[2], positions[3],
                    uvs[0], uvs[1], uvs[2], uvs[3], direction, info));
        }
        return builder.build();
    }
}
