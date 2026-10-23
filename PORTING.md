# Porting notes: Create: New Age → Create Fly (Fabric, MC 26.2, ported back to 1.21.11, 1.21.10 and 1.21.8)

Working document. Read *State* first.

## State

**Phases 1 to 5 done, 2026-09-29.** Everything upstream had is ported and compiles with nothing
excluded but datagen. Verified with the automated render check (`./gradlew runClientGameTest`, see
*Testing*): every block renders, kinetic parts spin, connected textures join, wires hang between
connectors, items show in the inventory, all 8 ponder scenes open, the JEI energising category
lists its 5 recipes, CC: Tweaked finds the peripherals. A dedicated server (`runServer`) loads all
data and reaches `Done` without errors.

**Phase 6 (automated gameplay checks) done, 2026-09-29.** `GameplayCheck` and `MultiplayerCheck`
pass: generation (coil + magnets + brushes, 135 energy/t) over a wire into a motor that spins,
an energising recipe on a depot, lava heating a pipe and a heater, a reactor rod taking fuel,
wires and energy surviving save/reload, and on a dedicated server with a connected client the same
chain runs and the goggle overlay and motor value box show synced values.

**Release review, 2026-10-03 → first full release `1.2.1-fly.26.2`.** Upstream `1.21.1` has no
newer commits. Fixed (mostly upstream bugs): energiser capacitor mode returned from inside its
transaction on the last step (item kept the charge, energiser was rolled back) and refunded an
item's own charge to the energiser when the item left; `capacitorMode` was read under a misspelt
key; `remove()`/`connect()` dereferenced a network a never-ticked connector does not have yet;
`NetworkTicker` kept every left world's `ServerLevel` (now cleared on server stop); coil
efficiency divided by zero. `GameplayCheck` covers the energiser and connector cases (test-only
energy items: amethyst and echo shards).

**1.21.11 port, 2026-10-19 (branch `1.21.11`, version `1.2.1-fly.1.21.11-beta.1`).** The 26.2 code moved
back to Minecraft 1.21.11 with Create Fly `1.21.11-6.0.9-5`; see *1.21.11 port* for what changed.
`runClientGameTest` passes (all `GameplayCheck` lines, `MultiplayerCheck` with a dedicated server,
CC: Tweaked finds the peripherals) and the `RenderCheck` screenshots look right (blocks, connected
textures, wires, items, JEI, ponders, goggle overlay). The renderers' own shafts, drawn only when
Flywheel is off, are not covered by the screenshots.

**1.21.10 port, 2026-10-20 (branch `1.21.10`, version `1.2.1-fly.1.21.10-beta.1`).** The 1.21.11 branch
moved back to Minecraft 1.21.10 with Create Fly `1.21.10-6.0.9-2`; see *1.21.10 port*. CC: Tweaked has
no 1.21.10 build, so the peripherals are compiled but not tested.

**1.21.8 port, 2026-10-20 (branch `1.21.8`, version `1.2.1-fly.1.21.8-beta.1`).** The 1.21.10 branch
moved back to Minecraft 1.21.8 with Create Fly `1.21.8-6.0.9-2`; see *1.21.8 port*. The renderers are
back on the pre-1.21.9 immediate API (close to upstream's), and the recipe viewer is REI instead of JEI. `runClientGameTest`
passes (all `GameplayCheck` lines, `MultiplayerCheck`, CC: Tweaked finds the peripherals) and the
`RenderCheck` screenshots look right, including the REI energising category.

**Release review, 2026-10-23 → first full release `1.2.1-fly.1.21.8`.** Of the two fixes of the
1.21.10/1.21.11 releases only the Monkey Edition datapack's format applied (`pack_format` 81,
1.21.8's data format; `min_format`/`max_format` only exist from 1.21.9). The energiser's beam keeps
its alpha here: `EnergiserRenderer` passes four channels to `SuperByteBuffer.color`. Also fixed:
`ElectricalConnectorBlock.onWrenched` dereferenced the network of a never-ticked connector, a motor
extension read without `stressMultiplier` got 0 (stalling its motor), the wire item went on to the
distance check after dropping a removed connector, `StringFormatUtil` indexed past its postfixes from
10^18 on, the motor wrote `eUse` as a float and read it as a long, and CC: Tweaked's motor
`getTier` was not final.

Still only checkable by playing: balance and feel, the energiser's beam and the stirling engine's
flywheel in motion, placing wires by hand, reactor overheating and corium, other energy mods
through Team Reborn Energy, and long sessions.

| | |
|---|---|
| Upstream | https://gitlab.com/antarcticgardens/create-new-age, branch `1.21.1` (NeoForge) |
| Port repo | https://github.com/FmStudios-MC/create-new-age-fly, branch `main` (was `fly/26.2`). Upstream is not a remote; fetch it by URL when merging upstream changes. Branch `1.21.11` is the port to Minecraft 1.21.11 (see *1.21.11 port*), branch `1.21.10` the one to 1.21.10 (see *1.21.10 port*), branch `1.21.8` the one to 1.21.8 (see *1.21.8 port*) |
| Target | This branch: Minecraft 1.21.8 (Mojang mappings, remapped to intermediary), Fabric Loader 0.18.4, Fabric API 0.136.1+1.21.8. `1.21.10`: Minecraft 1.21.10, Fabric API 0.138.4+1.21.10. `1.21.11`: Minecraft 1.21.11, Fabric API 0.141.6+1.21.11. `main`: Minecraft 26.2, Fabric Loader 0.19.3, Fabric API 0.160.0+26.2 (CC: Tweaked 1.120.2 needs >= 0.156) |
| Optional | This branch: REI 20.0.811 (with Architectury 17.0.8 and Cloth Config 19.0.147), CC: Tweaked 1.116.1. `1.21.10`: JEI 26.3.0.31 (Modrinth id `8yGN172x`); CC: Tweaked has no 1.21.10 build. `1.21.11`: JEI 27.7.0.30 (`Mia233qP`), CC: Tweaked 1.117.1. `main`: JEI 30.26.0.182 (`x6nG9OT2`), CC: Tweaked 1.120.2 |
| Create Fly | This branch: `maven.modrinth:create-fly:1.21.8-6.0.9-2` (latest 1.21.8 release). `1.21.10`: `1.21.10-6.0.9-2`. `1.21.11`: `1.21.11-6.0.9-5` (built from Create Fly's `1.21.11_obfuscation` branch). `main`: `26.2-rc-2-6.0.9-1` |
| JDK | Gradle runs on 25, requested via `gradle/gradle-daemon-jvm.properties` (Gradle finds an installed JDK 25 even when `JAVA_HOME` is 21). This branch compiles for Java 21 |
| Licence | Upstream is BSD-3-Clause style: keep `LICENSE` + copyright notice, do not use the authors' name to promote the port |

Sibling folders in `D:\Documents\Claude\Create Ported`:

- `create-fly/` – full Create Fly source (master; newer than the rc-2 jar we compile against). Grep it first when asking "what did X become".
- `create-new-age-upstream/` – untouched upstream checkout.
- `reference/create-connected-fly/` – another addon already ported to Create Fly 26.2. Its `PORTING.md` is an excellent list of traps. **AGPL-3.0: read it, never copy code from it.**
- `reference/createaddition/` – Crafts & Additions on Create Fly 26.2; also uses Team Reborn Energy.

## Plan

1. ~~Setup~~
2. ~~Core: registration, energy, networks, motors, generators, heat, reactor logic~~ (compiles; untested)
3. ~~Client: renderers, visuals, connected textures, item models, wires~~
4. ~~Resources: recipe JSONs, item model definitions~~ (done early: a single malformed sequenced assembly recipe stops the server loading any world)
5. ~~Ponders, JEI, ComputerCraft~~
6. ~~Automated gameplay checks~~; a human play-test remains
7. Release: name/description, licence notice, changelog, Modrinth/CurseForge pages

## Compat (phase 5)

- **Ponders** (`client/ponder`): keyed by item as Create Fly does (`ItemLike`, since CNA mixes blocks and items). `tools/fix_ponder_nbt.py` removed two structure fields 26.2 cannot decode (a belt's `Casing: NONE`, a funnel's empty `Filter`). Ponder structures are written back out through current code on every hover, so check `Serialization errors` in the log after changing one.
- **JEI** (`client/compat/jei`): `jei_mod_plugin` entrypoint. **Recipes only reach clients for serializers registered with Fabric's `RecipeSynchronization`** (done in `CreateNewAge`); without it the category is silently empty.
- **ComputerCraft** (`compat/computercraft`): Create Fly's pattern, a `ComputerBehaviour` added per block entity type plus `PeripheralLookup`, only when CC: Tweaked is loaded. CC's `fabric-api` artifact must be non-transitive, or its POM drags the whole Fabric API in and Loom's interface injection breaks compilation.

## Decisions

- **Single source set** (as upstream). Client-only code lives under `org.antarcticgardens.cna.client`; nothing outside it (and outside the phase 3 renderers) may import `net.minecraft.client` or `com.zurrtum.create.client`. Check with the snippet under *Tools*.
- **No Registrate shim.** Registration is rewritten in Create Fly's vanilla style in `CNABlocks`, `CNAItems`, `CNABlockEntityTypes`, `CNARecipeTypes`, `CNASounds`, `CNAEffects`, `CNADataComponents`. Blockstates, models, loot tables and tags that Registrate generated are committed under `src/generated/resources`.
- **Block entities keep their `(type, pos, state)` constructors.** `CNABlockEntityTypes.register` hands the type in after construction (26.2's supplier only passes pos and state).
- **Blocks reach block entity types lazily** (`() -> CNABlockEntityTypes.X`): `CNABlockEntityTypes` lists the blocks, so reading it from a block's constructor would run its initialiser mid-`CNABlocks` and register types with null blocks.
- **ESL → Team Reborn Energy** (`teamreborn:energy`, jar-in-jar). `org.antarcticgardens.cna.energy` reimplements ESL's `SimpleEnergyStorage` (with its fluent setters), `EnergyHelper` and `ItemStackHolder`. Storages are registered once from `CreateNewAge` via each block entity's `registerEnergyStorage()`; ESL re-registered them from every constructor.
- **Network transactions**: `NetworkSnapshot` now only captures the network's own path conductivity. Consumers receive the transaction and roll themselves back. `SimpleNetworkEnergyStorage` (street light) also snapshots its stored energy, which upstream did not (an aborted test transaction kept what it moved).
- **Config**: `config/ModConfigSpec` is a small stand-in for the slice of NeoForge's API CNA uses, writing `config/create_new_age-{client,server}.toml`. Catnip's config has no doubles. The server config is not synced to clients (only tooltips read it client-side).
- **Datagen code** (`data/`, `*BlockStateGen`) is excluded from compilation, kept in the tree for upstream merges.
- **Energising recipe** is a record like Create Fly's processing recipes: `{"ingredient": ..., "results": [...], "energy_needed": n}`. The committed JSONs still use upstream's `ingredients` list: phase 4.
- The NeoForge entrypoints/platform code (`_neoforge_old/`) was deleted after phase 5; see upstream if needed.

## Server/client split (the biggest structural change)

Create Fly never asks a block entity for goggle lines or value boxes. It attaches **client behaviours** per block entity type on the first client tick of a `SmartBlockEntity`, and the goggle overlay / value box handler only look at those.

- `client/CNABlockEntityBehaviours` registers them (tooltips, kinetic audio, scroll value boxes). A registry nothing calls fails silently: it is called from `CreateNewAgeClient`.
- `client/tooltip/CNATooltipBehaviours` holds every former `addToGoggleTooltip`, moved unchanged. Block entities expose getters for what they show.
- `client/tooltip/CNAItemTooltips` holds every former `appendHoverText` (26.2 removed it from `Block`), via Create Fly's `TooltipModifier.REGISTRY`.
- Scroll values are pairs: server half in `content/motor/*ScrollValueBehaviour` and the street light's `ServerScrollValueBehaviour` (value, range, callback, clipboard); client half in `client/behaviour/*` (value box, board). The motor reuses Create Fly's `MotorValueBox`, which upstream had copied.
- The heat block entities became `SmartBlockEntity`s so they can carry behaviours, and all custom tickers go through `util/SmartTicker.wrap`, which runs the smart tick first. **Without the smart tick on the client, no client behaviour is ever attached** – this applies to every block with its own `getTicker`.

## Removal

26.2 split `onRemove`: `BlockEntity#preRemoveSideEffects` runs while the block entity still exists, `affectNeighborsAfterRemoval` after it is gone. Connector wire cleanup moved to `AbstractElectricalConnector.preRemoveSideEffects`. The reactor blocks' `IBE.onRemove` calls are gone: `SmartBlockEntity.preRemoveSideEffects` calls `destroy()` itself.

## Rendering (phase 3)

All client rendering lives in `client/render` and is registered from `CreateNewAgeClient`.

- **26.2 renders block entities in two passes**: `extractRenderState` (level readable) and `submit` (no level). Everything that reads the world, including the wires' per-section light, happens in extract. Wires are built into a `Wire.Mesh` there and written out through `submitCustomGeometry` with vanilla's `entityCutout` render type (upstream had its own "wire" render type).
- **`visual` vs `normal`** (Create Fly's `AllBlockEntityRenders`): `visual` skips the renderer while Flywheel draws, `normal` always runs it. Registrate's one-argument `visual(...)` kept the renderer running; the energiser beam, the brushes' coil and the stirling flywheel depend on that, so those use `normal` and skip their shaft themselves under Flywheel. The generator coil was `visual(..., false)` and is `visual` here.
- **Classtweaker**: `BlockEntityRenderState.blockState` must be opened in our own classtweaker; Create Fly's entry does not carry over.
- **Connected textures**: Create Fly reads one sprite per tile, `block/<name>_connected/<1..46>.png`, not Create's 8x8 sheet. `tools/split_ct_sheets.py` cuts the sheets; its mapping was verified pixel-for-pixel against Create's andesite casing sheet and Create Fly's shipped tiles (`--verify`). **Re-run it whenever a `*_connected.png` changes.** Without the tiles the blocks render as missing texture, with no log line.
- **Generator coil model**: upstream used NeoForge's OBJ loader. `tools/obj_to_json.py --quads` turns `tools/models/generator_coil.obj` into a `create_new_age:quads` model, baked by `client/model/QuadListModel` (Fabric `UnbakedModelDeserializer`). Plain elements cannot express the coil's parallelogram faces. The block itself uses `RenderShape.INVISIBLE` and is drawn by its renderer/visual.
- **Item models**: every item needs `assets/create_new_age/items/<id>.json` (`tools/gen_item_definitions.py`). The shaft items (motors, energisers, stirling engine, carbon brushes) are vanilla `composite` models; the shaft's transform reproduces upstream's `ItemShaftRenderer` exactly (X 90 degrees, Y 1 rad, then the offset).
- `generator_coil`, `street_light` and `electrical_connector` render beyond their block; the connector renderer returns `shouldRenderOffScreen`.

## Testing

- `./gradlew runServer` – dedicated server; checks data loading and that no client class is reached on the server. `run/eula.txt` is accepted (the user agreed).
- `./gradlew runClientGameTest` runs three client gametests (`src/gametest`), about 4 minutes:
  - `GameplayCheck` – working setups, checked through block entity state; logs `CNA-TEST PASS/FAIL` lines and fails the run if any check fails.
  - `MultiplayerCheck` – a dedicated server plus a connected client; screenshots of the goggle overlay and value box.
  - `RenderCheck` – see below.

  Test fixtures inject energy with `internalInsert`, which does not sync to clients by itself (real
  transfers go through transactions, whose final commit syncs), so a goggle overlay on such a block
  can read 0 while the server holds the energy.
- `RenderCheck` builds a scene with every block in a fresh world, drives some with creative motors, wires two connectors, and saves screenshots to `build/run/clientGameTest/screenshots`. It asserts nothing; look at the pictures. The run ends with a Flywheel shutdown-watchdog crash report after the test completes: Create Fly's worker threads do not stop in time. Not ours, and harmless.

## Things learned about Create Fly so far

- Addon hook: entrypoint `create_plugin` → `CreateRegisterPlugin`. Not needed here; registering in `onInitialize` works.
- Recipes: sequenced assembly steps are expanded into ordinary recipes, so `recipeAccess().getRecipeFor(type, SingleRecipeInput, level)` finds them; recipes only exist on the server.
- Inventories: a block exposes one by implementing `ItemInventoryProvider` (a `WorldlyContainerHolder`); hoppers, funnels and Fabric's transfer API read it. Used by the fuel acceptor.
- `KineticBlockEntity` subclasses need `KineticTooltipBehaviour`/`GeneratingKineticTooltipBehaviour` and `KineticAudioBehaviour` registered client-side, or they lose stress tooltips and sounds.

## 1.21.11 port

The branch `1.21.11` is `main` moved back to Minecraft 1.21.11 with Create Fly `1.21.11-6.0.9-5`.
Create Fly's own `1.21.11_obfuscation` and `26.1` branches differ mostly where vanilla does, so
`git diff` between them answers most "what was X called on 1.21.11" questions.

- **Build**: 1.21.11 is still obfuscated. Loom's `net.fabricmc.fabric-loom-remap` plugin with
  `loom.officialMojangMappings()`, `mod*` configurations, Java 21. The classtweaker became
  `create_new_age.accesswidener` (`accessWidener v2 named`; the jar carries it remapped to
  intermediary) and also opens `BlockEntityType`'s constructor and supplier, as on 26.1.
- **Dependencies**: Fabric API's creative tab module is `fabric-item-group-api-v1`; Team Reborn
  Energy 4.2.0 (5.0.0 needs 26.1). JEI is pinned to 27.7.0.30: every later 1.21.11 build was made
  with Loom 1.17, which Loom 1.16 refuses to read, and Loom 1.17 needs Gradle 9.5. The gametest run
  lists CC: Tweaked's libraries (night-config, cobalt, netty) itself; the dev runtime does not load
  the ones it nests.
- **Rendering**: Create Fly 1.21.11 has no `SuperByteBufferRenderState`,
  `SmartBlockEntityRenderer.extractBase`, `getTintColor`, `getRotateAngleWithoutBeOffset` or
  `cardinalLighting`. `client/render/KineticRenderSupport` rebuilds the first four: a `Part` keeps
  a cached `SuperByteBuffer` with its render type, light, colour and transform and applies them
  when the geometry is rendered (cached buffers are shared, and `renderInto` resets their
  transforms, so nothing may be set on them while extracting). Shafts and the flywheel use
  `solidMovingBlock`, the brushes' coil `cutoutMovingBlock`.
- **Chunk layers**: 1.21.11 does not derive them from the textures. `client/CNABlockLayers` puts
  the blocks upstream gave a cutout layer (Registrate's `addLayer`, and the NeoForge-only
  `render_type` in the motor, connector, street light and solar plate models) on `CUTOUT` through
  Fabric's `BlockRenderLayerMap`. A new block with see-through pixels needs an entry there.
- **Connected textures**: Create Fly 1.21.11 reads Create's 8x8 `*_connected.png` sheets again,
  not the per-tile folders. Both are in the tree; only the sheets are used here.
- **Models**: `QuadListModel` bakes with `BakedQuad(..., tintIndex, direction, sprite, shade,
  lightEmission)` and `baker.sprites().resolveSlot(...)`; there is no interner or
  `Material.Baked`.
- Vanilla and API names: `GuiGraphicsExtractor`→`GuiGraphics` (`text`→`drawString`,
  `item`→`renderItem`); `LightCoordsUtil`→`LightTexture`; `state.level.CameraRenderState`→
  `state.CameraRenderState`; `sendOverlayMessage(c)`→`displayClientMessage(c, true)`;
  `RecipeSerializer` is an interface (`codec()`, `streamCodec()`); `ItemStack.typeHolder()`→
  `getItem().builtInRegistryHolder()`; `ServerTickEvents.START_LEVEL_TICK`→`START_WORLD_TICK`;
  client gametests use `getClientWorld()`; plus the 26.1.2 differences (`getMainCamera()`,
  `WHITE_CONCRETE`, `COPPER_BLOCK`, `EntityType.CREEPER`, `TestServerConnection`).
- `fabric.mod.json` requires Minecraft `>=1.21.11 <1.21.12-`, Java 21, Fabric API `>=0.141.6`,
  Create `>=6.0.9-5`.

Changes on `main` usually carry over; renderers and anything touching the names above need a look.

## 1.21.10 port

The branch `1.21.10` is `1.21.11` moved back to Minecraft 1.21.10 with Create Fly `1.21.10-6.0.9-2`.
Create Fly's API is nearly the same on both. Loom keeps both Create Fly versions remapped to Mojang
names, with sources, under `.gradle/loom-cache/remapped_mods`; diffing those answers most questions.

- **Dependencies**: Fabric API 0.138.4+1.21.10, JEI 26.3.0.31. Fabric API 1.21.10 has no
  `ResourceLoader.registerBuiltinPack`; the monkey edition pack goes through
  `fabric-resource-loader-v0`'s `ResourceManagerHelper.registerBuiltinResourcePack`.
- **CC: Tweaked** has no 1.21.10 build. `cct_minecraft_version` keeps the compile-only API at
  1.21.11 / 1.117.1 (Loom remaps it like any mod; the API is a few interfaces), and the gametest run
  no longer loads CC or prints the peripheral lines.
- **Vanilla names**: `Identifier`→`ResourceLocation`; `rendertype.RenderTypes`→`RenderType`, with
  `solidMovingBlock`→`solid` and `cutoutMovingBlock`→`cutout` (what 1.21.10's
  `ItemBlockRenderTypes.getMovingBlockRenderType` returns); no JSpecify on the classpath, so
  `org.jetbrains.annotations.Nullable`; no environment attributes, the solar plate reads
  `getSunAngle(1.0F)` (radians) as upstream did.
- **Models**: `BakedQuad` takes packed vertex data (`int[]`, 8 ints per vertex as
  `FaceBakery.fillVertex` writes them) instead of positions and packed UVs, and
  `TextureSlots.parseTextureMap` takes the atlas (`TextureAtlas.LOCATION_BLOCKS`).
- **Create Fly**: `CreateRollableRecipe.assemble(input, random)` is called `craft`.
- `fabric.mod.json` requires Minecraft `>=1.21.10 <1.21.11-`, Fabric API `>=0.138.4`, Create
  `>=6.0.9-2`.

## 1.21.8 port

The branch `1.21.8` is `1.21.10` moved back to Minecraft 1.21.8 with Create Fly `1.21.8-6.0.9-2`.
Apart from rendering and the recipe viewer, the code compiled unchanged.

- **Rendering**: 1.21.8 has no render states and no `SubmitNodeCollector`; block entity renderers
  draw straight into a `MultiBufferSource` in `render(be, partialTicks, ms, buffers, light, overlay,
  cameraPos)`. Create Fly 1.21.8 still has `SafeBlockEntityRenderer`, `KineticBlockEntityRenderer`
  and `ShaftRenderer`, so the renderers are upstream's again (with the port's fixes: the coil on
  `cutout`, the stirling flywheel always drawn). `KineticRenderSupport` and the classtweaker entry
  for `BlockEntityRenderState` are gone. Wires are built into a `Wire.Mesh` and written out at once.
- **Registration**: Create Fly 1.21.8's `AllBlockEntityRenders.visual/normal/render` are private;
  `CNABlockEntityRenders` has its own copies (vanilla `BlockEntityRenderers.register` plus
  `SimpleBlockEntityVisualizer.builder`).
- **Value boxes**: `ValueBoxTransform` takes `(LevelAccessor, BlockPos, BlockState)`.
- **Recipe viewer: REI instead of JEI.** JEI 24.2 on Fabric 1.21.8 never receives the server's
  recipes (it warns "JEI is missing recipes"), Fabric API 1.21.8 has no `RecipeSynchronization`,
  and Create Fly 1.21.8 has no JEI support, only REI and EIV. `compat/rei` holds the display and its
  serializer (`rei_common`: REI builds displays on the server and syncs them), `client/compat/rei`
  the category on Create Fly's REI `CreateCategory` and the sequenced assembly step icon
  (`rei_client`). Compiled against `RoughlyEnoughItems-api-fabric`, Architectury and Cloth's
  `basic-math` (REI's `Rectangle`/`Point`, nested in Cloth Config).
  **Dev-only trap**: in `runClientGameTest`, opening Create Fly's sequenced assembly category with a
  repeat count crashes with `AbstractMethodError: TooltipWidget.children()`. Loom remapped Create Fly
  without REI on the classpath, so its override is still called `method_25396`. In a real (intermediary)
  game the names match; the render check therefore leaves that tab closed.
- **CC: Tweaked** 1.116.1 has a 1.21.8 build again, so the gametest run loads it (with the
  libraries its POM lists) and prints the peripheral lines.
- `fabric.mod.json` requires Minecraft `>=1.21.8 <1.21.9-`, Fabric API `>=0.136.1`, Create
  `>=6.0.9-2`.

## Vanilla 1.21.1 → 26.2 changes hit so far

`ResourceLocation`→`Identifier`; NBT via `ValueInput`/`ValueOutput` (`getXOr`, `childrenList`, `store` with codecs); `isClientSide` is a method; `DirectionProperty`→`EnumProperty<Direction>`; `neighborChanged` takes an `Orientation`; `updateShape` reordered with `ScheduledTickAccess`/`RandomSource`; `getCloneItemStack(LevelReader, pos, state, includeData)`; `ItemInteractionResult`→`InteractionResult` (`PASS_TO_DEFAULT_BLOCK_INTERACTION`→`TRY_WITH_EMPTY_HAND`); `Item.use` returns `InteractionResult`; `inventoryTick(stack, ServerLevel, entity, EquipmentSlot)`; `displayClientMessage`→`sendOverlayMessage`; `MobEffect.applyEffectTick(ServerLevel, ...)` (server only); `hurt`→`hurtServer`; `CONFUSION`→`NAUSEA`, `DIG_SLOWDOWN`→`MINING_FATIGUE`; `LEASH_KNOT_*`→`LEAD_TIED/UNTIED`; `getArmorSlots` gone; `BlockPos.getCenter`→`Vec3.atCenterOf`; sun angle via `environmentAttributes().getValue(SUN_ANGLE, pos)` (degrees); `Direction.getNearest(x,y,z)`→`getApproximateNearest`; tags of a state/stack via `builtInRegistryHolder()/typeHolder().tags()` (`TagKey.location()` unchanged); `FallingBlock.getDustColor` abstract; `Level.addParticle` has two booleans; entity type constants moved from `EntityType` to `EntityTypes`.

## Tools

The `tools/` folder is local only (ignored by git, not in the public repository).

- `tools/createfly-classes.txt` – every class in the Create Fly jar we compile against. Regenerate after a Create Fly bump.
- `tools/remap_imports.py` – maps Create/Catnip/Ponder/Flywheel references to Create Fly packages (dry run by default, `--write` to apply).
- `tools/show_errors.py <log> "<message part>" [lines]` – each distinct javac error with source context. Run with `PYTHONIOENCODING=utf-8`.
