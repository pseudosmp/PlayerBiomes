# PlayerBiomes

![SpigotMC Downloads](https://img.shields.io/spiget/downloads/108144?style=for-the-badge&logo=spigotmc&link=https%3A%2F%2Fwww.spigotmc.org%2Fresources%2Fplayerbiomes-1-16-2.108144)&ensp;![Modrinth Downloads](https://img.shields.io/modrinth/dt/playerbiomes?style=for-the-badge&logo=modrinth&link=https%3A%2F%2Fmodrinth.com%2Fplugin%2Fplayerbiomes) 

- Use **/whatbiome** to find what biome you are in
- Send **player-specific translations** of biome names to players
- Get the player's biome name formatted in various ways using **PlaceholderAPI placeholders**
- Gives you the exact same name that the client sees which means it supports **custom biomes (datapacks like Terralith, Incendium, etc.)**

![image](https://github.com/user-attachments/assets/56b59b53-6a60-466f-b182-1566a60c344e)

## Commands

#### `/playerbiomes reload` (alias `/pb reload`):

&ensp;&ensp;Permission: `playerbiomes.command.reload` (false by default)
<br>
&ensp;&ensp;Description: Reloads the configuration of PlayerBiomes, and refreshes the translation cache.

#### `/whatbiome` (alias `/whereami`):

&ensp;&ensp;Permission: `playerbiomes.command.whatbiome` (true by default)
<br>
&ensp;&ensp;Description: Displays the name of the current biome of the player running the command.

<br>

![image](https://github.com/user-attachments/assets/b45720dc-3d3b-436a-9efc-773309b01075)

## Configuration: (config.yml)

Options are described in comments beside them. Here is the latest [config.yml](https://github.com/pseudoforceyt/PlayerBiomes/blob/main/src/main/resources/config.yml)

Inside the `plugins/PlayerBiomes/` folder, a folder named `lang` can be created and in it, can be multiple json files named `<locale>.json` that contain translations of biome names for that locale. You can either [extract these from the Minecraft Java client `.jar` file
](https://chatgpt.com/share/685ad29a-6b90-8006-9bae-f6a8d432931c), or download these on the fly from a website that hosts those files. Downloading is disabled by default. Read the config for more info.

## Placeholders

![image](https://github.com/user-attachments/assets/451f4467-4bc4-4cfc-a107-39cf7e4085fe)

[Fabric Mod [Caxton](https://modrinth.com/mod/caxton) used on Client to render the Tamil font ([Resource Pack Used](https://pseudosmp.github.io/rp/java/caxton_demo_Catamaran.zip)). Check it out!]

#### `%playerbiomes_biome_raw%`: 

&ensp;&ensp;Gives the complete namespaced ID of the biome

&ensp;&ensp;**Examples:**<br>
&ensp;&ensp;&ensp;&ensp;"minecraft:jungle"<br>
&ensp;&ensp;&ensp;&ensp;"terralith:moonlight_grove"<br>
&ensp;&ensp;&ensp;&ensp;"terralith:caves/deep_caves"<br>
&ensp;&ensp;&ensp;&ensp;"incendium:weeping_valley"

#### `%playerbiomes_biome_name%` and `%playerbiomes_biome_name_english%`:

&ensp;&ensp;Gives the Capitalized name of the biome (Removes the internal path of biomes from the Namespaced ID)

&ensp;&ensp;**Examples:**<br>
&ensp;&ensp;&ensp;&ensp;`minecraft:jungle` becomes "Jungle"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:moonlight_grove` becomes "Moonlight Grove"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:caves/deep_caves` becomes "Deep Caves"<br>
&ensp;&ensp;&ensp;&ensp;`incendium:weeping_valley` becomes "Weeping Valley"

#### `%playerbiomes_biome_namespace%`:

&ensp;&ensp;Gives the Capitalized nameSPACE of the biome

&ensp;&ensp;**Examples:**<br>
&ensp;&ensp;&ensp;&ensp;`minecraft:jungle` becomes "Minecraft"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:moonlight_grove` becomes "Terralith"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:caves/deep_caves` becomes "Terralith"<br>
&ensp;&ensp;&ensp;&ensp;`incendium:weeping_valley` becomes "Incendium".

#### `%playerbiomes_biome_formatted%` and `%playerbiomes_biome_formatted_english%`:

&ensp;&ensp;Gives the full namespaced ID of the biome in the format: <Namespace>: <Biome Name Capitalized>

&ensp;&ensp;**Examples:**<br>
&ensp;&ensp;&ensp;&ensp;`minecraft:jungle` becomes "Minecraft: Jungle"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:moonlight_grove` becomes "Terralith: Moonlight Grove"<br>
&ensp;&ensp;&ensp;&ensp;`terralith:caves/deep_caves` becomes "Terralith: Deep Caves"<br>
&ensp;&ensp;&ensp;&ensp;`incendium:weeping_valley` becomes "Incendium: Weeping Valley"

**Placeholders ending with `_english` use the old method that gets the name from the namespaced key directly using string manipulation.**
***

Support and Feature Requests in [SpigotMC Resource Discussion Tab](https://www.spigotmc.org/threads/playerbiomes-1-16-2.592358/) / [Discord](https://dsc.gg/pseudoforceyt) only! Do NOT use the issues tab for this.

Thanks [@mfnalex](https://github.com/mfnalex/) and [@RoughlyUnderscore](https://github.com/RoughlyUnderscore) for helping with the initial version of this plugin! Thanks to @si6gma (Discord) for help with string manipulation (< 6.0.0)!

The plugin utilizes the [JeffLib](https://github.com/JEFF-Media-GbR/JeffLib) library for getting the biome namespaced key for game versions older than 1.19.3.

Building:
1. Clone the project (the version/branch of your choice)
2. Build using `mvn install`

***

[bStats - PlayerBiomes](https://bstats.org/plugin/bukkit/PlayerBiomes/17782)<br><br>
![image](https://bstats.org/signatures/bukkit/PlayerBiomes.svg)
