# PlayerBiomes
Find player's exact biome using a PlaceholderAPI placeholder
### What does it do different than the other PlaceholderAPI expansions?
It gives you the exact same name that the client sees which means it supports custom biomes (plugin and datapack)

### Supports [Terralith](https://www.curseforge.com/minecraft/mc-mods/terralith) datapack's reserved biome names

## Placeholders
#### `%playerbiomes_biome%`: 
##### Gives the namespace of the biome *(for now, without the `minecraft:` part or the plugin/datapack name)*

Examples: `minecraft:jungle` becomes "jungle", `terralith:ominous_grove` becomes "ominous_grove", `terralith:zreserved/116/orchid_swamp` becomes "zreserved/116/orchid_swamp"

#### `%playerbiomes_biome_capitalized%` *(Credit: Si6gma#0828)*:
##### Gives the Capitalized name of the biome *(Removes the `zreserved/number/` parts from Terralith biomes)*

Examples: `minecraft:jungle` becomes "Jungle", `terralith:ominous_grove` becomes "Ominous Grove", `terralith:zreserved/116/orchid_swamp` becomes "Orchid Swamp"

Currently **DOESN'T support PlaceholderAPI reloads**! Will be fixed soon

(this is **my first plugin** so pls no hate, thanks [@mfnalex](https://github.com/mfnalex) for helping with this plugin :P)
Utilizes the [JeffLib](https://github.com/JEFF-Media-GbR/JeffLib)

Building:
Build using `mvn install`
