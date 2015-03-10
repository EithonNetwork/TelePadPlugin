# JumpPadPlugin

A JumpPad plugin for Minecraft.
A JumpPad is a block that will throw away a user that steps on it in a certain direction.

## Prerequisits

* There must be a directory under CraftBukkit/plugins, named "JumpPad". This is where this plugin will save information about all existing jumppads.

## Functionality

The plugin provides administrative commands for handling JumpPads, a way of saving and loading the created JumpPads to/from file storage and implementing the actual launching of players that step on a JumpPad.

### Administrative commands

* add: Add a new named JumpPad located to the STONE_PLATE block that is closest to the player
* edit: Edit the parameters of an existing JumpPad
* remove: Remove an existing JumpPad
* goto: Teleport to an existing JumpPad, temporarily stops the JumpPad from working for this player so that he/she is not thrown away immediately
* list: Show a list with all existing JumpPads

### The actual Jump

When a user steps on a JumpPad, he/she will first be thrown up in the air (according to the parameters for that JumpPad). When the user has reached the maximum height, he/she is shot away (according to the parameters for that JumpPad) in the direction that the creator of the JumpPad was looking when the JumpPad was created.

### Restrictions

A player must have the "jumppad.jump" permission to use a JumpPad. If he/she doesn't, then he/she will be informed that she needs to read the server rules first.

## Release history

### 1.4 (2014-01-12)

* NEW: Uses PluginTools.PlayerInfo
* CHANGE: Now requires a STONE_PLATE as jumppad.

### 1.3 (2014-01-03)

* CHANGE: Now jumps as an arc instead of first straigh up and then forward.
* CHANGE: Removed "implements" for interfaces that were not implemented.

### 1.2.1 (2014-12-27)

* CHANGE: Updated to be compatible with Minecraft 1.8 and Spigot

### 1.2 (2014-09-26)

* CHANGE: Refactoring

### 1.1 (2014-09-22)

* CHANGE: The command edit now requires the player to be on a jumppad first.
* CHANGE: The player now jumps straight up, before being sent off with forward velocity.

### 1.0.1 (2014-09-16)

* NEW: Nudges the user up in the air before sending him away, to avoid speed differences due to friction differens beteween air and ground.
* CHANGE: Shorter usage text, showing detailed usage text only if a sub command is issued
* CHANGE: Now stores the jump pad locations as the block location, not the creators location.

### 1.0 (2014-09-16)

* The /jumppad command supports add, remove, edit, goto and list