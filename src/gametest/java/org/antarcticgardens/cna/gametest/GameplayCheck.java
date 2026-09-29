package org.antarcticgardens.cna.gametest;

import com.zurrtum.create.content.logistics.depot.DepotBlockEntity;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.world.TestWorldSave;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.wire.WireType;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Builds small working setups and checks their state after they have run: generation into a motor
 * over a wire, an energising recipe, heat transfer, a reactor taking fuel, and that the state
 * survives saving and reloading the world. Every check logs one "CNA-TEST PASS/FAIL" line; the
 * test fails at the end if any check did.
 */
public class GameplayCheck implements FabricClientGameTest {
    static final int Y = 150;

    // generation: creative motor -> coil (in a ring of magnets) -> brushes -> connector ~ wire ~ connector -> motor
    static final BlockPos COIL = new BlockPos(1, Y, 0);
    static final BlockPos BRUSHES = new BlockPos(2, Y, 0);
    static final BlockPos BRUSHES_CONNECTOR = BRUSHES.above();
    static final BlockPos MOTOR = new BlockPos(8, Y, 0);
    static final BlockPos MOTOR_CONNECTOR = MOTOR.above();
    // energising: energiser two above a depot, driven from the side, iron ingot dropped on the depot
    static final BlockPos DEPOT = new BlockPos(12, Y, 0);
    static final BlockPos ENERGISER = DEPOT.above(2);
    // heat: lava under a pipe, pipe into a heater
    static final BlockPos LAVA = new BlockPos(0, Y, 6);
    static final BlockPos PIPE = LAVA.above();
    static final BlockPos HEATER = PIPE.east();
    // reactor: fuel acceptor facing a line of rods
    static final BlockPos ACCEPTOR = new BlockPos(6, Y, 6);
    static final BlockPos ROD = ACCEPTOR.east();

    private final List<String> failures = new ArrayList<>();

    @Override
    public void runTest(ClientGameTestContext context) {
        TestWorldSave save;
        try (TestSingleplayerContext singleplayer = context.worldBuilder().setUseConsistentSettings(true).create()) {
            singleplayer.getConnection().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();
            save = singleplayer.getWorldSave();

            server.runCommand("gamemode creative @p");
            server.runCommand("tp @p 6 " + (Y + 4) + " -6");
            server.runCommand("fill -4 " + (Y - 4) + " -4 20 " + (Y - 4) + " 10 minecraft:smooth_stone");
            server.runCommand("fill -4 " + (Y - 3) + " -4 20 " + (Y + 6) + " 10 minecraft:air");

            buildGeneration(server);
            buildEnergising(server);
            buildHeat(server);
            buildReactor(server);
            context.waitTicks(5);

            server.runOnServer(mc -> {
                ServerLevel level = mc.overworld();
                if (level.getBlockEntity(BRUSHES_CONNECTOR) instanceof AbstractElectricalConnector a
                        && level.getBlockEntity(MOTOR_CONNECTOR) instanceof AbstractElectricalConnector b)
                    a.connect(b, WireType.COPPER);
                if (level.getBlockEntity(ENERGISER) instanceof EnergiserBlockEntity energiser)
                    energiser.getEnergyStorage().internalInsert(1_000_000, false);
                if (level.getBlockEntity(ACCEPTOR) instanceof ReactorFuelAcceptorBlockEntity acceptor)
                    acceptor.container.setItem(0, new ItemStack(CNAItems.NUCLEAR_FUEL, 4));
            });
            server.runCommand("summon item " + DEPOT.getX() + ".5 " + (Y + 1) + " " + DEPOT.getZ() + ".5 {Item:{id:\"minecraft:iron_ingot\",count:1}}");

            context.waitTicks(400);

            check(server, "carbon brushes collect energy from the coil", level ->
                    level.getBlockEntity(BRUSHES) instanceof CarbonBrushesBlockEntity b ? "output " + b.getLastOutput() + "/t" + (b.getLastOutput() > 0 ? "" : " FAIL") : "missing FAIL");
            check(server, "wire connects the two connectors", level ->
                    level.getBlockEntity(BRUSHES_CONNECTOR) instanceof AbstractElectricalConnector a && a.isConnected(MOTOR_CONNECTOR) ? "connected" : "not connected FAIL");
            check(server, "motor receives energy over the wire and spins", level -> {
                if (!(level.getBlockEntity(MOTOR) instanceof MotorBlockEntity m))
                    return "missing FAIL";
                String state = "stored " + m.getEnergyStorage().getAmount() + ", used " + m.getLastConsumed() + "/t, speed " + m.getSpeed();
                return m.getSpeed() != 0 && m.getLastConsumed() > 0 ? state : state + " FAIL";
            });
            check(server, "energiser turns an iron ingot into overcharged iron", level -> {
                if (!(level.getBlockEntity(DEPOT) instanceof DepotBlockEntity depot))
                    return "missing FAIL";
                ItemStack held = depot.getHeldItem() == null ? ItemStack.EMPTY : depot.getHeldItem().stack;
                String name = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
                String diag = level.getBlockEntity(ENERGISER) instanceof EnergiserBlockEntity e
                        ? " (energiser speed " + e.getSpeed() + ", stored " + e.getEnergyStorage().getAmount()
                        + ", recipe " + e.getEnergisingBehaviour().getRecipe(new ItemStack(net.minecraft.world.item.Items.IRON_INGOT))
                        + ", charged " + e.getEnergisingBehaviour().charged + "/" + e.getEnergisingBehaviour().needed + ")" : "";
                return held.is(CNAItems.OVERCHARGED_IRON) ? name : "depot holds " + name + diag + " FAIL";
            });
            check(server, "heat pipe draws heat from lava", level ->
                    level.getBlockEntity(PIPE) instanceof HeatPipeBlockEntity p ? "heat " + p.getHeat() + (p.getHeat() > 0 ? "" : " FAIL") : "missing FAIL");
            check(server, "heater receives heat from the pipe", level ->
                    level.getBlockEntity(HEATER) instanceof HeaterBlockEntity h
                            ? "heat " + h.getHeat() + ", strength " + level.getBlockState(HEATER).getValue(HeaterBlock.STRENGTH)
                            + (h.getHeat() > 0 || level.getBlockState(HEATER).getValue(HeaterBlock.STRENGTH).ordinal() > 0 ? "" : " FAIL")
                            : "missing FAIL");
            check(server, "reactor rod takes fuel from the acceptor", level ->
                    level.getBlockEntity(ROD) instanceof ReactorRodBlockEntity r ? "fuel " + r.fuel + ", heat " + r.getHeat() + (r.fuel > 0 ? "" : " FAIL") : "missing FAIL");

            context.takeScreenshot("gameplay_scene");
        }

        // reopen: saved state must come back
        try (TestSingleplayerContext singleplayer = save.open()) {
            singleplayer.getConnection().waitForChunksRender();
            TestServerContext server = singleplayer.getServer();
            context.waitTicks(20);
            check(server, "wire survives save and reload", level ->
                    level.getBlockEntity(BRUSHES_CONNECTOR) instanceof AbstractElectricalConnector a && a.isConnected(MOTOR_CONNECTOR) ? "connected" : "not connected FAIL");
            check(server, "energiser keeps its energy across save and reload", level ->
                    level.getBlockEntity(ENERGISER) instanceof EnergiserBlockEntity e ? "stored " + e.getEnergyStorage().getAmount()
                            + (e.getEnergyStorage().getAmount() > 0 ? "" : " FAIL") : "missing FAIL");
        }

        if (!failures.isEmpty())
            throw new AssertionError("CNA gameplay checks failed: " + failures);
    }

    private void check(TestServerContext server, String name, Function<ServerLevel, String> probe) {
        String result = server.computeOnServer(mc -> probe.apply(mc.overworld()));
        boolean failed = result.endsWith("FAIL");
        if (failed)
            failures.add(name);
        System.out.println("CNA-TEST " + (failed ? "FAIL" : "PASS") + " | " + name + " | " + result);
    }

    static void set(TestServerContext server, BlockPos pos, String block) {
        server.runCommand("setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + block);
    }

    static void buildGeneration(TestServerContext server) {
        set(server, COIL.west(), "create:creative_motor[facing=east]");
        set(server, COIL, "create_new_age:generator_coil[axis=x]");
        // the coil's magnet ring: three blocks on each side, two away, in the plane across its axis
        for (int i = -1; i <= 1; i++) {
            set(server, COIL.offset(0, 2, i), "create_new_age:netherite_magnet");
            set(server, COIL.offset(0, -2, i), "create_new_age:netherite_magnet");
            set(server, COIL.offset(0, i, 2), "create_new_age:netherite_magnet");
            set(server, COIL.offset(0, i, -2), "create_new_age:netherite_magnet");
        }
        set(server, BRUSHES, "create_new_age:carbon_brushes[facing=east]");
        set(server, BRUSHES_CONNECTOR, "create_new_age:electrical_connector[facing=up]");
        set(server, MOTOR, "create_new_age:basic_motor[facing=east]");
        set(server, MOTOR_CONNECTOR, "create_new_age:electrical_connector[facing=up]");
    }

    static void buildEnergising(TestServerContext server) {
        set(server, DEPOT, "create:depot");
        // horizontal shaft, like Create's press: driven from the side
        set(server, ENERGISER, "create_new_age:basic_energiser[facing=east]");
        set(server, ENERGISER.west(), "create:creative_motor[facing=east]");
    }

    private static void buildHeat(TestServerContext server) {
        set(server, LAVA.below(), "minecraft:stone");
        set(server, LAVA.north(), "minecraft:stone");
        set(server, LAVA.south(), "minecraft:stone");
        set(server, LAVA.west(), "minecraft:stone");
        set(server, LAVA.east(), "minecraft:stone");
        set(server, LAVA, "minecraft:lava");
        set(server, PIPE, "create_new_age:heat_pipe");
        set(server, HEATER, "create_new_age:heater");
    }

    private static void buildReactor(TestServerContext server) {
        set(server, ACCEPTOR, "create_new_age:reactor_fuel_acceptor[facing=east]");
        set(server, ROD, "create_new_age:reactor_rod");
        set(server, ROD.east(), "create_new_age:reactor_rod");
    }

    @SuppressWarnings("unused")
    private static BlockEntity be(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }
}
