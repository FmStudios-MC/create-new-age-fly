# Porting notes: Create: New Age → Create Fly (Fabric, MC 26.2)

Working document. Read *State* first.

## State

**Phase 3 (rendering) done, 2026-09-29.** Verified in game with the automated render check
(`./gradlew runClientGameTest`, see *Testing*): every block renders, kinetic parts spin, connected
textures join, wires hang between connectors, items show in the inventory, no render errors.
A dedicated server (`runServer`) loads all data and reaches `Done` without errors.

Not yet checked in game: goggle tooltips, value boxes, energy flow, heat, reactor, recipes in use,
the energiser's beam (only drawn while processing), the stirling engine's miniature flywheel.

| | |
|---|---|
| Upstream | https://gitlab.com/antarcticgardens/create-new-age, branch `1.21.1` (NeoForge) |
| Port branch | `fly/26.2` |
| Target | Minecraft 26.2, Fabric Loader 0.19.3, Fabric API 0.155.0+26.2 |
| Create Fly | `maven.modrinth:create-fly:26.2-rc-2-6.0.9-1` (latest 26.2 release on Modrinth) |
| JDK | 25, pinned via `org.gradle.java.home` in `gradle.properties` |
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
5. Ponders, JEI, ComputerCraft
6. Play-test gameplay: energy networks, motors, heat, reactor, recipes; multiplayer

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
- `_neoforge_old/` keeps the NeoForge entrypoints/platform code for reference; delete once phase 5 is done.

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
- `./gradlew runClientGameTest` – `src/gametest/.../RenderCheck` builds a scene with every block in a fresh world, drives some with creative motors, wires two connectors, and saves screenshots to `build/run/clientGameTest/screenshots`. It asserts nothing; look at the pictures. The run ends with a Flywheel shutdown-watchdog crash report after the test completes: Create Fly's worker threads do not stop in time. Not ours, and harmless.

## Things learned about Create Fly so far

- Addon hook: entrypoint `create_plugin` → `CreateRegisterPlugin`. Not needed here; registering in `onInitialize` works.
- Recipes: sequenced assembly steps are expanded into ordinary recipes, so `recipeAccess().getRecipeFor(type, SingleRecipeInput, level)` finds them; recipes only exist on the server.
- Inventories: a block exposes one by implementing `ItemInventoryProvider` (a `WorldlyContainerHolder`); hoppers, funnels and Fabric's transfer API read it. Used by the fuel acceptor.
- `KineticBlockEntity` subclasses need `KineticTooltipBehaviour`/`GeneratingKineticTooltipBehaviour` and `KineticAudioBehaviour` registered client-side, or they lose stress tooltips and sounds.

## Vanilla 1.21.1 → 26.2 changes hit so far

`ResourceLocation`→`Identifier`; NBT via `ValueInput`/`ValueOutput` (`getXOr`, `childrenList`, `store` with codecs); `isClientSide` is a method; `DirectionProperty`→`EnumProperty<Direction>`; `neighborChanged` takes an `Orientation`; `updateShape` reordered with `ScheduledTickAccess`/`RandomSource`; `getCloneItemStack(LevelReader, pos, state, includeData)`; `ItemInteractionResult`→`InteractionResult` (`PASS_TO_DEFAULT_BLOCK_INTERACTION`→`TRY_WITH_EMPTY_HAND`); `Item.use` returns `InteractionResult`; `inventoryTick(stack, ServerLevel, entity, EquipmentSlot)`; `displayClientMessage`→`sendOverlayMessage`; `MobEffect.applyEffectTick(ServerLevel, ...)` (server only); `hurt`→`hurtServer`; `CONFUSION`→`NAUSEA`, `DIG_SLOWDOWN`→`MINING_FATIGUE`; `LEASH_KNOT_*`→`LEAD_TIED/UNTIED`; `getArmorSlots` gone; `BlockPos.getCenter`→`Vec3.atCenterOf`; sun angle via `environmentAttributes().getValue(SUN_ANGLE, pos)` (degrees); `Direction.getNearest(x,y,z)`→`getApproximateNearest`; tags of a state/stack via `builtInRegistryHolder()/typeHolder().tags()` (`TagKey.location()` unchanged); `FallingBlock.getDustColor` abstract; `Level.addParticle` has two booleans; entity type constants moved from `EntityType` to `EntityTypes`.

## Tools

- `tools/createfly-classes.txt` – every class in the Create Fly jar we compile against. Regenerate after a Create Fly bump.
- `tools/remap_imports.py` – maps Create/Catnip/Ponder/Flywheel references to Create Fly packages (dry run by default, `--write` to apply).
- `tools/show_errors.py <log> "<message part>" [lines]` – each distinct javac error with source context. Run with `PYTHONIOENCODING=utf-8`.
