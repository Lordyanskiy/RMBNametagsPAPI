# RMBNametags 1.21 - 1.21.8





A simple Minecraft PLUGIN to hide player names and show them on right-click in actionbar
# INSTALL REQUIRED: [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

![Nickname pops up in actionbar when you press rmb](https://cdn.modrinth.com/data/cached_images/3232f03c8108ea611b1bdf8b42e6ce3320641d7c.png)

## Features

- Hiding player names
- Replacing player names with an invisible character
- Displaying a player's name when you right-click on it
- Enabling/disabling the visibility of individual player names
- Enabling/disabling the visibility of all player names
- Customizing the display format of the name
- Customizing the display time of the name
- Customizing the type of the invisible characters
- Customizing all plugin messages
- Integrating PlaceholderAPI

## Commands

- `/rmbnametags_reload` - Reload a plugin config
- `/rmbnametags_toggle [игрок]` - Enable/disable visibility of player name
- `/rmbnametags_toggleall` - Enable/disable visibility of all players name

## Permission

- `rmbnametags.reload` - Permission to use the command `/rmbnametags_reload`
- `rmbnametags.toggle` - Permission to use the command `/rmbnametags_toggle`
- `rmbnametags.toggle.all` - Permission to use the command `/rmbnametags_toggleall`

## Config

```yml
# Nickname display time
display-time: 3

# Nickname display format
# Use & for colors, and use %  for papi values (for nickname use %player_name%)
name-format: "&a&l%player_name%"

# Change player's name with an invisible character
use-invisible-character: false

# Type of invisible character (available options: ZWSP, NBSP, ZWNJ, ZWJ)
# ZWSP - Zero-Width Space (U+200B) - invisible zero-width gap
# NBSP - Non-Breaking Space (U+00A0) - unbreakable gap
# ZWNJ - Zero-Width Non-Joiner (U+200C) - invisible separator
# ZWJ - Zero-Width Joiner (U+200D) - invisible connector
invisible-character-type: ZWSP

# Hide player's name in tab (списке игроков)
hide-in-tab-list: false

# Message settings
messages:
  reload-success: "&aRMBNametags: Config was successfully reload!"
  name-visible: "&aPlayer name &f%player_name%&a now hidden."
  name-hidden: "&aPlayer name &f%player_name%&a now visible."
  all-names-visible: "&aAll player's name became hidden."
  all-names-hidden: "&aAll player's name became visible."
  player-not-found: "&cPlayer &f%player_name%&c not found or offline."
  no-permission: "&cYou don't have permission to use this command."
```

### bStats
[![bStats Graph Data](https://bstats.org/signatures/bukkit/RMBNametags.svg)](https://bstats.org/plugin/bukkit/RMBNametags)
