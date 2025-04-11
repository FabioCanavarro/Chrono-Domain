# ChronoDomain

## Overview
ChronoDomain is a Minecraft Fabric mod that allows you to manipulate time for entities, blocks, and entire chunks. Speed up or slow down various game elements, creating unique gameplay experiences.

## Features

### Time Crystals
- **Time Crystal**: Accelerates the target entity, making it move faster than normal.
- **Slow Time Crystal**: Slows down the target entity, making it move in slow motion.

### Time Station
The Time Station block creates a time field that affects entities and blocks within the same chunk:
- Place the Time Station block and activate it with a Time Crystal
- All entities in the chunk will move at accelerated speed
- Random block ticks and block entities will also operate at accelerated speed
- Visual particles mark the boundaries of the affected area

## Commands
ChronoDomain adds several useful commands:

- `/GetEntityRegistered` - Shows all entities currently affected by time fields
- `/GetTimeFactor` - Shows the current time factors for speed-up and slow-down effects
- `/SetTimeFactor <value>` - Set the acceleration factor (default: 10.0)
- `/SetSlowTimeFactor <value>` - Set the slow-down factor (default: 0.1)

## Time Effects
When an entity or chunk is affected by a time field:

### Speed-Up Effects (Time Crystal)
- Entities move faster
- Animation speeds increase
- Block ticks occur more frequently
- Projectiles travel faster

#### Examples:
- Arrows fly faster
- Entities move at an increased speed
- Blocks like Furnaces, Brewing Stand, Hopper, etc. will work faster
- Fires will spread faster
- Crops will grow faster

### Slow-Down Effects (Slow Time Crystal)
- Entities move in slow motion
- Physics slow down
- Block ticks occur less frequently
- Projectiles travel slower

## Technical Details
ChronoDomain uses Minecraft's mixin system to modify core game functionality:

- `EntityTickMixin` - Controls entity tick rates and movement speeds
- `BlockEntityMixin` - Modifies block entity ticking behavior
- `ProjectileTickMixin` - Adjusts projectile velocities based on time factors
- `RandomTickMixin` - Changes block random tick rates in affected chunks

The mod keeps track of affected entities and chunks using:
- `TimeFieldManager` - Manages individual entity time factors
- `ChunkTimeManager` - Handles chunk-level time manipulation

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
2. Download the ChronoDomain mod JAR file
3. Place the JAR file in your Minecraft mods folder
4. Launch Minecraft with Fabric profile

## Compatibility
ChronoDomain is a Fabric mod and requires:
- Minecraft (check mod version for compatible game versions)
- Fabric Loader
- Fabric API