package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.Identifier;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAPartialModels {
    public static final PartialModel COIL = block("carbon_brushes/coil");
    public static final PartialModel GENERATOR_COIL = block("generator_coil/block");

    private static PartialModel block(String path) {
        return PartialModel.of(Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "block/" + path));
    }

    /** Must run before models bake; called from the client entrypoint. */
    public static void init() {
    }
}
