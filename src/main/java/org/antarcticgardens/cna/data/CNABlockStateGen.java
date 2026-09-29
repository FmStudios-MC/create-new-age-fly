package org.antarcticgardens.cna.data;

import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlock;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorMode;
import org.antarcticgardens.cna.content.electricity.light.LampPostBlock;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlock;
import org.antarcticgardens.cna.content.energising.EnergiserBlock;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlock;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlock;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlock;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlock;
import org.joml.Vector3f;

import net.neoforged.neoforge.client.model.generators.*;

public class CNABlockStateGen {
    public static <P extends EnergiserBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> energiser() {
        return (c, p) -> p.horizontalBlock(c.get(), p.models().withExistingParent(c.getName(), p.modLoc("block/energiser"))
                        .texture("all", "block/" + c.getName()));
    }

    public static <P extends ElectricalConnectorBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> electricalConnector() {
        return (c, p) -> {
            VariantBlockStateBuilder builder = p.getVariantBuilder(c.get());

            ModelFile.ExistingModelFile inert = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/inert"));
            ModelFile.ExistingModelFile pull = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/pull"));

            builder.forAllStates(state -> {
                Direction dir = state.getValue(BlockStateProperties.FACING);
                ElectricalConnectorMode mode = state.getValue(ElectricalConnectorBlock.MODE);

                return ConfiguredModel.builder()
                        .modelFile(switch(mode) {
                            case INERT -> inert;
                            case PULL -> pull;
                        })
                        .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                        .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                        .build();
            });
        };
    }
    
    public static <P extends HeatPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> heatPipe() {
        return (c, p) -> {
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
            ModelFile.ExistingModelFile center = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/center"));
            ModelFile.ExistingModelFile side = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/side"));
            
            builder.part()
                    .modelFile(center)
                    .addModel()
                    .end();
            
            for (Direction dir : Direction.values()) {
                Vector3f euler = new Vector3f();
                dir.getRotation().getEulerAnglesXYZ(euler);
                
                if (dir.getAxis().isHorizontal()) {
                    dir = dir.getOpposite();
                }
                
                builder.part()
                        .modelFile(side)
                        .rotationX((int) Math.round(Math.toDegrees(euler.x)))
                        .rotationY((int) Math.round(Math.toDegrees(euler.z)))
                        .addModel()
                        .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), true)
                        .end();
            }
        };
    }

    public static <P extends EncasedHeatPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> encasedHeatPipe() {
        return (c, p) -> {
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
            ModelFile.ExistingModelFile sideFlat = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/side_flat"));
            ModelFile.ExistingModelFile sideOpen = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/side_open"));
            ModelFile.ExistingModelFile topFlat = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/top_flat"));
            ModelFile.ExistingModelFile topOpen = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/top_open"));

            for (Direction dir : Direction.values()) {
                ModelFile flat = dir.getAxis().isHorizontal() ? sideFlat : topFlat;
                ModelFile open = dir.getAxis().isHorizontal() ? sideOpen : topOpen;
                int rotY = dir.getAxis().isHorizontal() ? (int) dir.toYRot() : 0;
                int rotX = dir.getAxis().isVertical() ? (dir == Direction.UP ? 0 : 180) : 0;

                builder.part()
                        .modelFile(flat)
                        .rotationY(rotY)
                        .rotationX(rotX)
                        .addModel()
                        .condition(EncasedHeatPipeBlock.getDirectionProperty(dir), false)
                        .end();

                builder.part()
                        .modelFile(open)
                        .rotationY(rotY)
                        .rotationX(rotX)
                        .addModel()
                        .condition(EncasedHeatPipeBlock.getDirectionProperty(dir), true)
                        .end();
            }
        };
    }

    public static <P extends HeatPumpBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> heatPump() {
        return (c, p) -> {
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());
            ModelFile.ExistingModelFile center = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/center"));
            ModelFile.ExistingModelFile centerUp = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/center_up"));
            ModelFile.ExistingModelFile centerDown = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/center_down"));
            ModelFile.ExistingModelFile front = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/front"));
            ModelFile.ExistingModelFile side = p.models().getExistingFile(p.modLoc("block/heat_pipe/side"));

            for (Direction dir : Direction.values()) {
                Vector3f euler = new Vector3f();
                dir.getRotation().getEulerAnglesXYZ(euler);

                if (dir.getAxis().isHorizontal()) {
                    dir = dir.getOpposite();
                }
                
                int rotX = (int) Math.round(Math.toDegrees(euler.x));
                int rotY = (int) Math.round(Math.toDegrees(euler.z));

                if(dir == Direction.UP){
                    builder.part()
                            .modelFile(centerUp)
                            .addModel()
                            .condition(BlockStateProperties.FACING, dir)
                            .end();
                }else if(dir == Direction.DOWN){
                    builder.part()
                            .modelFile(centerDown)
                            .addModel()
                            .condition(BlockStateProperties.FACING, dir)
                            .end();
                }else{
                    builder.part()
                            .modelFile(center)
                            .rotationY(rotY)
                            .addModel()
                            .condition(BlockStateProperties.FACING, dir)
                            .end();
                }


                builder.part()
                        .modelFile(front)
                        .rotationX(rotX)
                        .rotationY(rotY)
                        .addModel()
                        .condition(BlockStateProperties.FACING, dir)
                        .end();

                builder.part()
                        .modelFile(side)
                        .rotationX(rotX)
                        .rotationY(rotY)
                        .addModel()
                        .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), true)
                        .end();
            }
        };
    }
    
    public static <P extends HeaterBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> heater() {
        return (c, p) -> {
            VariantBlockStateBuilder builder = p.getVariantBuilder(c.get());
            
            for (BlazeBurnerBlock.HeatLevel level : BlazeBurnerBlock.HeatLevel.values()) {
                String id = "block/" + c.getName() + "_top_" + level.ordinal();
                
                ModelBuilder<?> model = p.models()
                        .withExistingParent(id, p.modLoc("block/" + c.getName()))
                        .texture("top", id);
                
                builder.addModels(builder.partialState().with(BlazeBurnerBlock.HEAT_LEVEL, level), new ConfiguredModel(model));
            }
        };
    }

    public static <P extends ReactorRodBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> reactorRod() {
        return (c, p) -> {
            VariantBlockStateBuilder builder = p.getVariantBuilder(c.get());
            
            ModelBuilder<?> off = p.models().withExistingParent(c.getName() + "_off", p.modLoc("block/" + c.getName()))
                    .texture("all", p.modLoc("block/" + c.getName() + "_off"));
            ModelBuilder<?> on = p.models().withExistingParent(c.getName() + "_on", p.modLoc("block/" + c.getName()))
                    .texture("all", p.modLoc("block/" + c.getName() + "_on"));
            
            builder.forAllStates(state -> {
                Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                boolean lit = state.getValue(BlockStateProperties.LIT);
                
                return ConfiguredModel.builder()
                        .modelFile(lit ? on : off)
                        .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                        .rotationY(axis == Direction.Axis.X ? 90 : axis == Direction.Axis.Z ? 180 : 0)
                        .build();
            });
        };
    }

    public static <P extends StirlingEngineBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> stirlingEngine() {
        return (c, p) -> {
            VariantBlockStateBuilder builder = p.getVariantBuilder(c.get());
            ModelFile.ExistingModelFile model = p.models().getExistingFile(p.modLoc("block/" + c.getName()));

            builder.forAllStates(state -> {
                Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationX(axis == Direction.Axis.Y ? 90 : 0)
                        .rotationY(axis == Direction.Axis.Z ? 0 : (axis == Direction.Axis.X ? 90 : 0))
                        .build();
            });
        };
    }

    public static <P extends StreetLightBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> streetLight() {
        return (c, p) -> {
            VariantBlockStateBuilder builder = p.getVariantBuilder(c.get());

            ModelBuilder<?> unlit = p.models().withExistingParent(c.getName() + "/unlit", p.modLoc("block/" + c.getName() + "/block"))
                    .texture("lit", p.modLoc("block/" + c.getName() + "_unlit"));
            ModelBuilder<?> lit5 = p.models().withExistingParent(c.getName() + "/lit_5", p.modLoc("block/" + c.getName() + "/block"))
                    .texture("lit", p.modLoc("block/" + c.getName() + "_lit_5"));
            ModelBuilder<?> lit10 = p.models().withExistingParent(c.getName() + "/lit_10", p.modLoc("block/" + c.getName() + "/block"))
                    .texture("lit", p.modLoc("block/" + c.getName() + "_lit_10"));
            ModelBuilder<?> lit15 = p.models().withExistingParent(c.getName() + "/lit_15", p.modLoc("block/" + c.getName() + "/block"))
                    .texture("lit", p.modLoc("block/" + c.getName() + "_lit_15"));

            for (int level = 0; level <= 15; level++) {
                ModelFile model;
                if (level == 0) {
                    model = unlit;
                }else if (level <= 5) {
                    model = lit5;
                }else if (level <= 10) {
                    model = lit10;
                }else {
                    model = lit15;
                }

                builder.addModels(builder.partialState().with(StreetLightBlock.LIGHT_LEVEL, level), new ConfiguredModel(model));
            }
        };
    }

    public static <P extends LampPostBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> lamppost() {
        return (c, p) -> {
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

            ModelFile.ExistingModelFile pole = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/pole"));
            ModelFile.ExistingModelFile top = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/top"));
            ModelFile.ExistingModelFile bottom = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/bottom"));
            ModelFile.ExistingModelFile center = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/center"));
            ModelFile.ExistingModelFile side = p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/side"));

            builder.part()
                    .modelFile(pole)
                    .addModel()
                    .end();

            builder.part()
                    .modelFile(top)
                    .addModel()
                    .condition(LampPostBlock.TOP, true)
                    .end();

            builder.part()
                    .modelFile(bottom)
                    .addModel()
                    .condition(LampPostBlock.BOTTOM, true)
                    .condition(LampPostBlock.NORTH, false)
                    .condition(LampPostBlock.EAST, false)
                    .condition(LampPostBlock.SOUTH, false)
                    .condition(LampPostBlock.WEST, false)
                    .end();

            builder.part()
                    .modelFile(center)
                    .addModel()
                    .useOr()
                    .condition(LampPostBlock.NORTH, true)
                    .condition(LampPostBlock.EAST, true)
                    .condition(LampPostBlock.SOUTH, true)
                    .condition(LampPostBlock.WEST, true)
                    .end();

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                builder.part()
                        .modelFile(side)
                        .rotationY((int) dir.getOpposite().toYRot())
                        .addModel()
                        .condition(LampPostBlock.getDirectionProperty(dir), true)
                        .end();
            }
        };
    }
}
