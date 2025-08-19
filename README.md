# RMBNametags
### 1.21 - 1.21.8





Простой плагин для Minecraft, который скрывает имена игроков и показывает их при нажатии правой кнопкой мыши в панели действий (actionbar).
ТРЕБУЕТСЯ УСТАНОВКА [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

![Ник появляется в панели действий при нажатии ПКМ](https://cdn.modrinth.com/data/cached_images/3232f03c8108ea611b1bdf8b42e6ce3320641d7c.png)

## Возможности

- Скрытие ников игроков
- Замена ников на невидимый символ
- Показ ника игрока при нажатии на него правой кнопкой мыши
- Возможность включать/отключать видимость ников отдельных игроков
- Возможность включать/отключать видимость ников всех игроков
- Настройка формата отображения ника
- Настройка времени отображения ника
- Настройка типа невидимого символа
- Настройка всех сообщений плагина
- Интеграция PlaceholderAPI

## Команды

- `/rmbnametags_reload` - Перезагрузить конфигурацию плагина
- `/rmbnametags_toggle [игрок]` - Включить/отключить видимость ника игрока
- `/rmbnametags_toggleall` - Включить/отключить видимость ников всех игроков

## Права

- `rmbnametags.reload` - Право на использование команды `/rmbnametags_reload`
- `rmbnametags.toggle` - Право на использование команды `/rmbnametags_toggle`
- `rmbnametags.toggle.all` - Право на использование команды `/rmbnametags_toggleall`

## Конфигурация

```yml
# Время отображения ника в секундах
display-time: 3

# Формат отображения ника
# Используйте & для цветов и разные papi значения (для ника используется %player_name%)
name-format: "&a&l%player_name%"

# Заменять ники игроков на невидимый символ
use-invisible-character: false

# Тип невидимого символа (доступные варианты: ZWSP, NBSP, ZWNJ, ZWJ)
# ZWSP - Zero-Width Space (U+200B) - невидимый пробел нулевой ширины
# NBSP - Non-Breaking Space (U+00A0) - неразрывный пробел
# ZWNJ - Zero-Width Non-Joiner (U+200C) - невидимый разделитель
# ZWJ - Zero-Width Joiner (U+200D) - невидимый соединитель
invisible-character-type: ZWSP

# Скрывать имя игрока в табе (списке игроков)
hide-in-tab-list: false

# Настройки сообщений
messages:
  reload-success: "&aRMBNametags: Конфигурация успешно перезагружена!"
  name-visible: "&aНик игрока &f%player_name%&a теперь виден."
  name-hidden: "&aНик игрока &f%player_name%&a теперь скрыт."
  all-names-visible: "&aНики всех игроков теперь видны."
  all-names-hidden: "&aНики всех игроков теперь скрыты."
  player-not-found: "&cИгрок &f%player_name%&c не найден или не в сети."
  no-permission: "&cУ вас нет прав для использования этой команды."
```

### bStats
[![bStats Graph Data](https://bstats.org/signatures/bukkit/RMBNametags.svg)](https://bstats.org/plugin/bukkit/RMBNametags)
