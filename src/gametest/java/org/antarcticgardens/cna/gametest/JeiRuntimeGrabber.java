package org.antarcticgardens.cna.gametest;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

/** Hands the render check JEI's runtime, so it can open a recipe category. */
public class JeiRuntimeGrabber implements IModPlugin {
    static volatile IJeiRuntime runtime;

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("create_new_age_gametest", "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }
}
