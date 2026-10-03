------------------------------------------------------
Version 1.2.1-fly.26.2 (unofficial Fabric port)
------------------------------------------------------
First full release of the port.
#### Bug Fixes
- Charging an energy item on an energiser no longer creates energy
- Taking a half-charged item off an energiser no longer refunds its charge to the energiser
- An energiser charging an item keeps doing so after a reload
- Fix a server crash when a connector or street light is removed before its first tick
- Leaving a single player world no longer keeps it in memory
- The generator coil's efficiency no longer shows "NaN%"
- Declare the minimum Fabric API version (0.160.0)

------------------------------------------------------
Version 1.2.1-fly.26.2-beta.1 (unofficial Fabric port)
------------------------------------------------------
First release of the unofficial port to Create Fly (Fabric, Minecraft 26.2). Not made or
supported by Antarctic Gardens; please report problems with it to this port's issue tracker.
#### Changes from 1.2.1
- Runs on Fabric with Create Fly instead of NeoForge with Create
- Energy goes through Team Reborn Energy (bundled) instead of NeoForge's energy capability
- New logo

------------------------------------------------------
Version 1.2.1
------------------------------------------------------
#### Additions
- Added heat casing
#### Improvements
- Improve Reactor Statistics
- Cache wire networks and lazily reset wire connections - [!71 Thank you Superintendent](https://gitlab.com/antarcticgardens/create-new-age/-/merge_requests/71)
- Scale reactor heat vent extraction cap by overheatingMultiplier - [!72 Thank you Guk kis](https://gitlab.com/antarcticgardens/create-new-age/-/merge_requests/72)

------------------------------------------------------
Version 1.2.0
------------------------------------------------------
#### Additions
- Added Street Lights and Lamp Posts
- Added radiation effect & Geiger counter ticking - [!60 Thank you Auralyn](https://gitlab.com/antarcticgardens/create-new-age/-/merge_requests/60)
- Reactors now explode upon overheating
- Added CC:Tweaked compatibility (This is a work in progress feature please send us feedback)
- New Simplified Chinese translations from abandon0320
#### Improvements
- Improve wire rendering and stop crashes with Create Aeronautics - [!68 Thank you tmvkrpxl0](https://gitlab.com/antarcticgardens/create-new-age/-/merge_requests/68)

------------------------------------------------------
Version 1.1.7c
------------------------------------------------------
#### Bug Fixes
- Hopefully fix release

------------------------------------------------------
Version 1.1.7b
------------------------------------------------------
#### Bug Fixes
- Fix crash when removing CNA blocks from Electrodynamics' networks

------------------------------------------------------
Version 1.1.7a
------------------------------------------------------
Bug Fixes
- Fix compatibility with Mekanism

------------------------------------------------------
Version 0.0.0
------------------------------------------------------
Additions
- None

Changes
- None

Bug Fixes
- None
