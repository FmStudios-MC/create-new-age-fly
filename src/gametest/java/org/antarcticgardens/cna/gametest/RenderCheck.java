package org.antarcticgardens.cna.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import com.zurrtum.create.client.ponder.foundation.ui.PonderUI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.wire.WireType;

/**
 * Builds a scene with every block on a platform in the sky and takes screenshots of it, with and
 * without motion, plus the inventory with the items. Only a visual check: it asserts nothing, and
 * a crash while rendering fails the run.
 */
public class RenderCheck implements FabricClientGameTest {
    private static final int Y = 150;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().setUseConsistentSettings(true).create()) {
            singleplayer.getClientLevel().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();

            server.runCommand("gamemode creative @p");
            context.runOnClient(mc -> {
                mc.player.getAbilities().flying = true;
            });
            server.runCommand("time set noon");
            server.runCommand("weather clear");
            server.runCommand("fill -4 " + (Y - 1) + " -8 18 " + (Y - 1) + " 10 minecraft:smooth_stone");
            server.runCommand("fill -4 " + Y + " -8 18 " + (Y + 8) + " 10 minecraft:air");

            // row 1: generators and consumers, some driven by creative motors
            set(server, 0, 0, "create_new_age:basic_motor[facing=north]");
            set(server, 2, 0, "create_new_age:advanced_motor[facing=north]");
            set(server, 4, 0, "create_new_age:reinforced_motor[facing=north]");
            set(server, 6, 0, "create:creative_motor[facing=east]");
            set(server, 7, 0, "create_new_age:generator_coil[axis=x]");
            set(server, 10, 0, "create:creative_motor[facing=east]");
            set(server, 11, 0, "create_new_age:carbon_brushes[facing=east]");
            set(server, 13, 1, "create_new_age:basic_energiser");
            set(server, 13, 2, 0, "create:creative_motor[facing=down]");
            set(server, 15, 0, "create_new_age:stirling_engine[axis=x]");
            set(server, 16, 0, "create_new_age:heater");

            // row 2: connected textures (2x2 each) and magnets
            square(server, 0, 3, "create_new_age:heat_casing");
            square(server, 3, 3, "create_new_age:reactor_casing");
            square(server, 6, 3, "create_new_age:reactor_glass");
            square(server, 9, 3, "create_new_age:redstone_magnet");
            set(server, 12, 3, "create_new_age:magnetite_block");
            set(server, 13, 3, "create_new_age:layered_magnet");
            set(server, 14, 3, "create_new_age:fluxuated_magnetite");
            set(server, 15, 3, "create_new_age:netherite_magnet");

            // row 3: heat, reactor, light, wiring
            for (int x = 0; x <= 3; x++)
                set(server, x, 6, "create_new_age:heat_pipe");
            set(server, 4, 6, "create_new_age:heat_pump[facing=east]");
            set(server, 5, 6, "create_new_age:basic_solar_heating_plate");
            set(server, 6, 6, "create_new_age:advanced_solar_heating_plate");
            set(server, 7, 6, "create_new_age:reactor_rod");
            set(server, 8, 6, "create_new_age:reactor_heat_vent[facing=up]");
            set(server, 9, 6, "create_new_age:reactor_fuel_acceptor[facing=up]");
            set(server, 10, 6, "create_new_age:street_light");
            set(server, 11, 6, "create_new_age:lamp_post");
            set(server, 12, 6, "create_new_age:copper_wire_block");
            set(server, 13, 6, "create_new_age:thorium_ore");
            set(server, 14, 6, "create_new_age:electrical_connector[facing=up]");
            set(server, 17, 6, "create_new_age:electrical_connector[facing=up]");
            set(server, 15, 8, "create_new_age:encased_heat_pipe");
            set(server, 16, 8, "create_new_age:basic_motor_extension[facing=north]");

            context.waitTicks(5);
            server.runOnServer(minecraftServer -> {
                ServerLevel level = minecraftServer.overworld();
                if (level.getBlockEntity(new BlockPos(14, Y, 6)) instanceof AbstractElectricalConnector a
                        && level.getBlockEntity(new BlockPos(17, Y, 6)) instanceof AbstractElectricalConnector b)
                    a.connect(b, WireType.COPPER);
            });

            look(context, server, 8, Y + 5, -9, 8, Y, 4);
            context.waitTicks(40);
            singleplayer.getClientLevel().waitForChunksRender();
            look(context, server, 8, Y + 5, -9, 8, Y, 4);
            context.waitTicks(5);
            context.takeScreenshot("cna_overview");


            // close-ups of each connected-texture group, from the south-west
            String[] groups = {"heat_casing", "reactor_casing", "reactor_glass", "redstone_magnet"};
            for (int i = 0; i < groups.length; i++) {
                int gx = i * 3;
                look(context, server, gx - 1.5, Y + 1, 1.2, gx + 1, Y + 1, 4);
                context.waitTicks(10);
                context.takeScreenshot("ct_" + groups[i]);
            }

            look(context, server, 11, Y + 2, -4, 11, Y, 0);
            context.waitTicks(20);
            context.takeScreenshot("cna_generators_a");
            context.waitTicks(7);
            context.takeScreenshot("cna_generators_b");

            look(context, server, 6, Y + 3, 10, 6, Y, 6);
            context.waitTicks(20);
            context.takeScreenshot("cna_heat_and_reactor");

            look(context, server, 15.5, Y + 2, 10, 15, Y, 6);
            context.waitTicks(20);
            context.takeScreenshot("cna_wires");

            server.runCommand("clear @p");
            for (String item : new String[]{"basic_motor", "advanced_motor", "reinforced_motor", "basic_energiser",
                    "stirling_engine", "carbon_brushes", "generator_coil", "electrical_connector", "copper_wire",
                    "heat_pipe", "reactor_casing", "overcharged_diamond", "nuclear_fuel", "basic_motor_extension",
                    "street_light", "thorium_ore"})
                server.runCommand("give @p create_new_age:" + item);
            context.waitTicks(5);
            context.setScreen(() -> new InventoryScreen(Minecraft.getInstance().player));
            context.waitTicks(5);
            context.takeScreenshot("cna_items");
            context.setScreen(() -> null);

            // one ponder per scene file; each opens its first storyboard
            for (String item : new String[]{"basic_energiser", "generator_coil", "heater", "heat_pipe", "basic_motor",
                    "basic_motor_extension", "reactor_rod", "electrical_connector"}) {
                Item ponderItem = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("create_new_age", item));
                context.setScreen(() -> PonderUI.of(new ItemStack(ponderItem)));
                context.waitTicks(120);
                context.takeScreenshot("ponder_" + item);
                context.setScreen(() -> null);
                context.waitTicks(2);
            }
        }
    }

    /** Teleports the player to (px, py, pz) looking at the centre of block (tx, ty, tz). */
    private static void look(ClientGameTestContext context, TestServerContext server, double px, double py, double pz,
                             int tx, int ty, int tz) {
        double dx = tx + 0.5 - px, dy = ty + 0.5 - (py + 1.62), dz = tz + 0.5 - pz;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        server.runCommand("tp @p " + px + " " + py + " " + pz + " " + yaw + " " + pitch);
        context.waitTicks(2);
        context.runOnClient(mc -> {
            mc.player.setYRot(yaw);
            mc.player.setXRot(pitch);
        });
    }

    private static void set(TestServerContext server, int x, int z, String block) {
        set(server, x, 0, z, block);
    }

    private static void set(TestServerContext server, int x, int dy, int z, String block) {
        server.runCommand("setblock " + x + " " + (Y + dy) + " " + z + " " + block);
    }

    private static void square(TestServerContext server, int x, int z, String block) {
        server.runCommand("fill " + x + " " + Y + " " + z + " " + (x + 1) + " " + (Y + 1) + " " + (z + 1) + " " + block);
    }
}
