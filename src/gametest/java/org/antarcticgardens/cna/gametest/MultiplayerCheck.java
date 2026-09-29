package org.antarcticgardens.cna.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.minecraft.server.level.ServerLevel;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.wire.WireType;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

import static org.antarcticgardens.cna.gametest.GameplayCheck.*;

/**
 * A dedicated server with a client connected over the network: the generation chain runs on the
 * server, and the client must see its state through synced block entity data. Screenshots show
 * the goggle overlay (client tooltip behaviours reading synced values) and the motor's value box.
 */
public class MultiplayerCheck implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestDedicatedServerContext server = context.worldBuilder().setUseConsistentSettings(true).createServer();
             TestDedicatedServerConnection connection = server.connect()) {
            connection.waitForChunksRender();

            server.runCommand("gamemode creative @a");
            // the floor is right under the lowest magnet, so the player can stand at eye level with the scene
            server.runCommand("fill -4 " + (Y - 3) + " -6 20 " + (Y - 3) + " 10 minecraft:smooth_stone");
            server.runCommand("fill -4 " + (Y - 2) + " -6 20 " + (Y + 6) + " 10 minecraft:air");
            buildGeneration(server);
            buildEnergising(server);
            context.waitTicks(5);
            server.runOnServer(mc -> {
                ServerLevel level = mc.overworld();
                if (level.getBlockEntity(BRUSHES_CONNECTOR) instanceof AbstractElectricalConnector a
                        && level.getBlockEntity(MOTOR_CONNECTOR) instanceof AbstractElectricalConnector b)
                    a.connect(b, WireType.COPPER);
                if (level.getBlockEntity(ENERGISER) instanceof EnergiserBlockEntity energiser)
                    energiser.getEnergyStorage().internalInsert(5_000, false);
            });
            context.waitTicks(200);

            String motor = server.computeOnServer(mc -> mc.overworld().getBlockEntity(MOTOR) instanceof MotorBlockEntity m
                    ? "speed " + m.getSpeed() + ", stored " + m.getEnergyStorage().getAmount() : "missing");
            String clientMotor = context.computeOnClient(mc -> mc.level.getBlockEntity(MOTOR) instanceof MotorBlockEntity m
                    ? "speed " + m.getSpeed() + ", stored " + m.getEnergyStorage().getAmount() : "missing");
            System.out.println("CNA-TEST INFO | dedicated server motor | " + motor);
            System.out.println("CNA-TEST INFO | client view of the motor | " + clientMotor);
            System.out.println("CNA-TEST INFO | dedicated server energiser | " + server.computeOnServer(mc ->
                    mc.overworld().getBlockEntity(ENERGISER) instanceof EnergiserBlockEntity e ? "stored " + e.getEnergyStorage().getAmount() : "missing"));

            server.runCommand("item replace entity @a armor.head with create:goggles");
            RenderCheck.look(context, server, MOTOR.getX() + 0.5, Y - 2, MOTOR.getZ() - 2.5, MOTOR.getX(), Y, MOTOR.getZ());
            context.waitTicks(20);
            connection.waitForChunksRender();
            System.out.println("CNA-TEST INFO | client camera | " + context.computeOnClient(mc -> mc.player.position() + " yaw "
                    + mc.player.getYRot() + " pitch " + mc.player.getXRot() + ", block at motor " + mc.level.getBlockState(MOTOR)
                    + ", hit " + mc.hitResult.getType()));
            context.takeScreenshot("mp_goggles_motor");

            RenderCheck.look(context, server, ENERGISER.getX() + 0.5, Y - 2, ENERGISER.getZ() - 3.5,
                    ENERGISER.getX(), ENERGISER.getY(), ENERGISER.getZ());
            context.waitTicks(20);
            context.takeScreenshot("mp_goggles_energiser");

            server.runCommand("item replace entity @a weapon.mainhand with create:wrench");
            RenderCheck.look(context, server, MOTOR.getX() + 0.5, Y - 2, MOTOR.getZ() - 2.5, MOTOR.getX(), Y, MOTOR.getZ());
            context.waitTicks(20);
            context.takeScreenshot("mp_motor_value_box");
        }
    }
}
