# EMI Ores

Easily view ore generation information from inside [EMI](https://modrinth.com/mod/emi).

[Modrinth](https://modrinth.com/mod/emi-ores) [CurseForge](https://www.curseforge.com/minecraft/mc-mods/emi-ores)

EMI Ores is a plugin for the recipe viewer EMI that displays information about how ores generate in the world, including:

- Y-level distribution
- Vein size
- Vein count per chunk
- Biome restrictions

The mod is required on both client and server in order to function; if present on only one side it will silently disable itself.

Owing to the fact that this mod gathers its data from data pack configured/placed features, modded ores should display correctly without additional configuration in the vast majority of cases.

## Screenshots

![ore_diamond](https://github.com/Abbie5/emi-ores/assets/43531981/3315b2ad-077c-417b-ba07-fcf0c8654b0c)
![ore_emerald](https://github.com/Abbie5/emi-ores/assets/43531981/a5cf1d4b-4178-4f00-9443-962b8b977a2f)
![ore_ancient_debris_large](https://github.com/Abbie5/emi-ores/assets/43531981/ba53c7ac-3636-4bfb-b98d-a2ac28070040)

## Comparison with existing ore generation recipe viewer plugins

| Feature | EMI Ores | [Just Enough Resources](https://modrinth.com/mod/just-enough-resources-jer) | [Roughly Enough Resources](https://www.curseforge.com/minecraft/mc-mods/roughly-enough-resources) |
| --- | --- | --- | --- |
| Display ore generation information | ✅ | ✅ | ✅ |
| Display non-ore world generation information | ❌ | ❌ | ✅ |
| Automatic modded ore support | ✅ | ❌ | ✅ |
| Display biome restrictions | ✅ | ✅ | ❌ |
| Combine stone and deepslate ores | ✅ | ✅ | ❌ |
| Data source | Data pack placed/configured features | Hard-coded | Scanned chunks |
| Recipe viewer support | EMI | [JEI](https://modrinth.com/mod/jei), EMI (via JEI) | [REI](https://modrinth.com/mod/rei) |
| Display loot tables | ❌[^1] | ✅ | ✅ |
| Display villager trades | ❌[^2] | ✅ | ❌ |
| Display enchantments available on items | ⭕[^3] | ✅ | ❌ |

Vanilla biome icons licensed [CC BY-NC-SA 3.0](https://creativecommons.org/licenses/by-nc-sa/3.0/) by the Minecraft Fandom Wiki contributors, taken from the [Biome wiki page](https://minecraft.fandom.com/wiki/Biome).

[^1]: See [EMI Loot](https://modrinth.com/mod/emi-loot).
[^2]: See [EMI Trades](https://modrinth.com/mod/emitrades).
[^3]: Basic functionality available in EMI, also see [EMI Enchanting](https://modrinth.com/mod/emi-enchanting).
