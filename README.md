# [PlayerBiomes on SpigotMC](https://www.spigotmc.org/resources/playerbiomes-1-16-x-1-19-x.108144/)
- Find player's exact biome using a PlaceholderAPI placeholder
- Use /whereami to find what biome you are in
- Gives you the exact same name that the client sees which means it supports **custom biomes (datapacks like Terralith, Incendium, etc.)**

## Commands
#### `/whereami`:
##### Permission: playerbiomes.command.whereami (True by default) 
Description: Displays the name of the biome the player is currently in.
![image](https://github.com/pseudoforceyt/PlayerBiomes/assets/70620481/72ba4a09-9e06-479d-b43a-3ebe7bc0d179)

## Configuration: (config.yml)
Options are described in comments beside them
[Latest](https://github.com/pseudoforceyt/PlayerBiomes/blob/main/src/main/resources/config.yml) | [v4.3.0-legacy](https://raw.githubusercontent.com/pseudoforceyt/PlayerBiomes/v4.3.0-legacy/src/main/resources/config.yml)

## Placeholders
![image](https://github.com/pseudoforceyt/PlayerBiomes/assets/70620481/2402d591-c9bf-4e2e-b81e-807c3fab2365)

#### `%playerbiomes_biome_raw%`: 
Gives the complete namespaced ID of the biome
##### Examples:
"minecraft:jungle"
"terralith:moonlight_grove"
"terralith:caves/deep_caves"
"incendium:weeping_valley"

#### `%playerbiomes_biome_name%`:
Gives the Capitalized name of the biome (Removes the internal path of biomes from the Namespaced ID)
##### Examples:
`minecraft:jungle` becomes "Jungle"
`terralith:moonlight_grove` becomes "Moonlight Grove"
`terralith:caves/deep_caves` becomes "Deep Caves"
`incendium:weeping_valley` becomes "Weeping Valley"

#### `%playerbiomes_biome_namespace%`:
Gives the Capitalized nameSPACE of the biome
##### Examples:
`minecraft:jungle` becomes "Minecraft"
`terralith:moonlight_grove` becomes "Terralith"
`terralith:caves/deep_caves` becomes "Terralith"
`incendium:weeping_valley` becomes "Incendium".

#### `%playerbiomes_biome_formatted%`
Gives the full namespaced ID of the biome in the format: <Namespace>: <Biome Name Capitalized>
##### Examples:
`minecraft:jungle` becomes "Minecraft: Jungle"
`terralith:moonlight_grove` becomes "Terralith: Moonlight Grove"
`terralith:caves/deep_caves` becomes "Terralith: Deep Caves"
`incendium:weeping_valley` becomes "Incendium: Weeping Valley"

Support and Feature Requests in [SpigotMC Resource Discussion Tab](https://www.spigotmc.org/threads/playerbiomes-1-16-3-1-20-x.592358/) / [Discord](https://dsc.gg/pseudoforceyt) only! Do NOT use the issues tab for this.
(this is **my first plugin** so pls no hate)
Thanks @mfnalex and @RoughlyUnderscore for helping with this plugin!
Thanks to @si6gma (Discord) for help with string manipulation!

Versions <= 4.3.0 utilize the [JeffLib](https://github.com/JEFF-Media-GbR/JeffLib) library for initializing placeholders and getting the biome namespaced ID.

Building:
1. Clone the project (the version/branch of your choice)
2. Build using `mvn install`

[bStats - PlayerBiomes](https://bstats.org/plugin/bukkit/PlayerBiomes/17782)
![image](https://bstats.org/signatures/bukkit/PlayerBiomes.svg)
