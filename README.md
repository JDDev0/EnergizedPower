# EnergizedPower
This is a technology Forge/NeoForge/Fabric mod with many machines.<br>
Download on CurseForge: [Energized Power](https://www.curseforge.com/minecraft/mc-mods/energized-power)<br>
Download on Modrinth: [Energized Power](https://modrinth.com/mod/energized-power)

## Features

### Machines

- Powered Furnace
- Auto Crafter (Can handle recipe conflicts, that is recipes with the same inputs)
- Crusher
- Sawmill
- Compressor
- Plant Growth Chamber
- Block Placer (Supports placing blocks below and above it; The rotation of the placed block depends on the rotation of the block placer)
- Fluid Filler (**In next release (v2.0.0)**)
- Fluid Drainer (**In next release (v2.0.0)**)
- Charger
- Advanced Charger (3 slots) (**In next release (v2.0.0)**)
- Uncharger
- Advanced Uncharger (3 slots) (**In next release (v2.0.0)**)
- Minecart Charger
- Advanced Minecart Charger (**In next release (v2.0.0)**)
- Minecart Uncharger
- Advanced Minecart Uncharger (**In next release (v2.0.0)**)
- Charging Station
- Energizer
- Coal Engine
- Heat Generator
- Thermal Generator (**In next release (v2.0.0)**)
- Lightning Generator
- Solar Panels (5 Tiers)
- Transformers (Can be used to connect different tiers of cables):
  - 3 tiers: MV, HV (**In next release (v2.0.0)**), and EHV (**In next release (v2.0.0)**)
  - 3 types (for each tier): 1 to n, 3 to 3, and n to 1
- Battery Box
- Advanced Battery Box (**In next release (v2.0.0)**)
- Weather Controller
- Time Controller

Every machine supports auto-insert and auto-extract of items from all block faces.

### Energy transportation

- Cables (5 Tiers: Copper, Gold, Energized Copper, Energized Gold, and Energized Crystal Matrix (**In next release (v2.0.0)**))
- Minecart with Battery Box
- Minecart with Advanced Battery Box (**In next release (v2.0.0)**)

### Energy Items

- Energy Analyzer (Can be used to debug energy blocks)
- Fluid Analyzer (Can be used to debug blocks which contain fluids) (**In next release (v2.0.0)**)
- Batteries (8 Tiers)
- Inventory Coal Engine
- Inventory Charger (3 slots) (**In next release (v2.0.0)**)

### Fluid transport

- Fluid Pipe (**In next release (v2.0.0)**)

### Integrations

- JEI (*Forge/NeoForge editions only*), EMI, and REI support
- Curios API (*Forge/NeoForge edition only*)

### World generation

- Electrician villager
- Electrician villager house (Can be found in every vanilla village)
- Factory building (Can be found in Forest and Flower Forest biomes)
- Small solar farm (Can be found in Desert, Badlands, and Savanna biomes)

### Guide book

- The Energized Power Book contains information for all the blocks and items of this mod

### Configurations (**In next release (v2.0.0)**)

- Configurations are located in the `config/energizedpower` directory:
  - `client.conf`: Contains config values which are only used for the client
  - `server.conf`: Contains config values which are only used for the server (Currently none)
  - `common.conf`: Contains config values which are used for both the client and the server
    - **IMPORTANT**: Values from the `common.conf` are **not** synced from the server to the client!<br>You must provide players of the server the `common.conf` file. 

### Translations

- English
- German
- Chinese (Simplified)
- Italian

## Information

**The fabric edition of this mod requires the Fabric API.**

*Feel free to include this mod in your ModPack :-)*

## Discord

Invite Link: [https://discord.gg/sAKDNAU7yH](https://discord.gg/sAKDNAU7yH)

## Credits

- Energized Copper/Gold Ingot textures ([flashbulbs](https://github.com/flashbulbs))
- Battery textures ([flashbulbs](https://github.com/flashbulbs))
- Silicon texture ([flashbulbs](https://github.com/flashbulbs))
- Energy Analyzer texture ([flashbulbs](https://github.com/flashbulbs))
- Chinese (Simplified) translation: ([HanJiang-cn](https://github.com/HanJiang-cn))
- Italian translation: ([Roby1164](https://github.com/Roby1164))

## Tutorial
A tutorial world showcasing all the features of this mod can be downloaded on CurseForge: [Energized Power [Tutorial World]](https://www.curseforge.com/minecraft/worlds/energized-power-tutorial-world)

## Supported versions:
Mod version: MC version
- 1.7.x: 1.20.1, 1.20, 1.19.4, 1.19.3, 1.19.2, 1.18.2
- 1.6.x: 1.19.4, 1.19.3, 1.19.2, 1.18.2
- 1.5.x: 1.19.4, 1.19.3, 1.19.2, 1.18.2
- 1.4.x: 1.19.4, 1.19.3, 1.19.2, 1.18.2, 1.17.1
- 1.3.x: 1.19.3, 1.19.2, 1.18.2, 1.17.1
- 1.2.x: 1.19.3
- 1.1.x: 1.19.3
- 1.0.x: 1.19.3
