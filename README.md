# [PlayerBiomes](https://www.spigotmc.org/resources/playerbiomes-1-16-x-1-19-x.108144/)
Find player's exact biome using a PlaceholderAPI placeholder
### What does it do different than the other PlaceholderAPI expansions?
It gives you the exact same name that the client sees which means it supports custom biomes (plugin and datapack)

### Supports [Terralith](https://www.curseforge.com/minecraft/mc-mods/terralith) datapack's reserved biome names

## Placeholders
#### `%playerbiomes_biome_raw%`: 
##### Gives the namespace of the biome

Examples: `minecraft:jungle`, `terralith:ominous_grove`, `terralith:zreserved/116/orchid_swamp`

#### `%playerbiomes_biome_name%`:
##### Gives the Capitalized name of the biome *(Removes the `zreserved/number/` and `caves/` parts from Terralith biomes)*

Examples: `minecraft:jungle` becomes "Jungle", `terralith:ominous_grove` becomes "Ominous Grove", `terralith:zreserved/116/orchid_swamp` becomes "Orchid Swamp"

#### `%playerbiomes_biome_namespace%`:
##### Gives the Capitalized nameSPACE of the biome

Examples: `minecraft:jungle` becomes "Minecraft", `terralith:ominous_grove` becomes "Terralith", `terralith:zreserved/116/orchid_swamp` becomes "Terralith"

#### `%playerbiomes_biome_formatted%`
##### Gives the full name of the biome in the format: `<Namespace>: <Biome Name Capitalized>`

Examples: `minecraft:jungle` becomes "Minecraft: Jungle", `terralith:ominous_grove` becomes "Terralith: Ominous Grove", `terralith:zreserved/116/orchid_swamp` becomes "Terralith: Orchid Swamp"

(this is **my first plugin** so pls no hate)
Thanks @mfnalex and @RoughlyUnderscore for helping with this plugin!
Thanks to Si6gma#0833 for help with string manipulation!

Utilizes the [JeffLib](https://github.com/JEFF-Media-GbR/JeffLib) library for initializing placeholders and getting the biome namespaced ID.

Building:
Build using `mvn install`
