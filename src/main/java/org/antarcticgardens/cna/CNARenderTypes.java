package org.antarcticgardens.cna;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class CNARenderTypes extends RenderType {
    private static final Function<Identifier, RenderType> WIRE = Util.memoize(arg ->
            RenderType.create(
                    "wire",
                    DefaultVertexFormat.BLOCK,
                    VertexFormat.Mode.QUADS,
                    256,
                    false,
                    false,
                    CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_SOLID_SHADER)
                            .setTextureState(new TextureStateShard(arg, false, false))
                            .setCullState(RenderStateShard.NO_CULL)
                            .setLightmapState(RenderStateShard.LIGHTMAP)
                            .createCompositeState(false)
            ));

    public static RenderType wire(Identifier texture) {
        return WIRE.apply(texture);
    }

    public CNARenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
}
