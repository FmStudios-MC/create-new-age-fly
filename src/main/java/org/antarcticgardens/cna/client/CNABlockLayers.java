package org.antarcticgardens.cna.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

import static org.antarcticgardens.cna.CNABlocks.*;

/**
 * Chunk layers for the blocks with see-through pixels. 1.21.10 does not derive them from the textures
 * (26.2 does): upstream set them through Registrate's {@code addLayer} and the NeoForge-only
 * {@code render_type} of the motor, connector, street light and solar plate models.
 */
public class CNABlockLayers {
    public static void register() {
        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT,
                BASIC_MOTOR_EXTENSION, ADVANCED_MOTOR_EXTENSION, REACTOR_GLASS,
                BASIC_SOLAR_HEATING_PLATE, ADVANCED_SOLAR_HEATING_PLATE,
                BASIC_MOTOR, ADVANCED_MOTOR, REINFORCED_MOTOR, ELECTRICAL_CONNECTOR, STREET_LIGHT);
    }
}
