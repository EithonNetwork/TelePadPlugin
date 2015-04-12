# TelePadPlugin

A TelePad plugin for Minecraft.
A TelePad is a block that will throw away a user that steps on it in a certain direction.

## Prerequisits

* There must be a directory under CraftBukkit/plugins, named "TelePad". This is where this plugin will save information about all existing TelePads.

## Functionality

The plugin provides administrative commands for handling TelePads, a way of saving and loading the created TelePads to/from file storage and implementing the actual launching of players that step on a TelePad.

### Administrative commands

* add: Add a new named TelePad located to the STONE_PLATE block that is closest to the player
* target: Sets the target destination
* remove: Remove an existing TelePad
* goto: Teleport to an existing TelePad, temporarily stops the TelePad from working for this player so that he/she is not thrown away immediately
* list: Show a list with all existing TelePads

### The actual Jump

When a user steps on a TelePad, he/she will first be thrown up in the air (according to the parameters for that TelePad). When the user has reached the maximum height, he/she is shot away (according to the parameters for that TelePad) in the direction that the creator of the TelePad was looking when the TelePad was created.

### Restrictions

A player must have the "TelePad.jump" permission to use a TelePad. If he/she doesn't, then he/she will be informed that she needs to read the server rules first.

## Release history

### 2.0 (2015-04-12)

* NEW: Uses JSON.

### 1.4 (2015-04-04)

* NEW: Now uses EithonLibraryPlugin.

### 1.2 (2015-04-02)

* NEW: Now stores the direction the player is looking at when using add

### 1.0 (2015-03-15)

* NEW: Added SlownessTicks as a new configuration parameter
* NEW: Added "/teleport reload" command that loads the configuration file.
* CHANGE: Now stops the user before teleporting

### 0.3 (2015-03-15)

* CHANGE: Replaced the command "/telepad target <name>" with the command "/telepad link <pad name 1> < pad name 2>" that links two pads together. (Both jumps to the other pad).

### 0.2 (2015-03-10)

* First working version
