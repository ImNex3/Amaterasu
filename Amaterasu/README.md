# Amaterasu (Paper 1.21.1)

Fire/Amaterasu PvP abilities with **inventory-based upgrades**. Bind-friendly commands, first-join roll, invincible special items, action-bar cooldowns, and Eternal Dragon synergy.

## Features
- 6 abilities (base) with **Corrupted (Amaterasu)** forms.
- **Upgrader (Heart of the Sea)** in inventory => Corrupted versions.
- **Dragon Egg + Upgrader** => **Eternal Dragon's Wrath**.
- **Reroller (Echo Shard)**: select active ability (GUI placeholder), droppable but **invincible** (never despawns/burns/explodes).
- First-join ability roll with tutorial chat and “incomplete states” warning.
- Action-bar cooldown timer above the hotbar.
- Crafting:
  - **Upgrader**: Diamonds in the corners, Dragon's Breath on the 4 edges, Sculk Catalyst in center.
  - **Reroller**: Wither Skeleton Skull in center fully surrounded by Netherite Ingots.

## Build
```bash
mvn -B package
```
Drop `target/amaterasu-1.0.0-shaded.jar` into your Paper server `plugins/` folder.

## Commands
```
/devilsfootprint
/plasmablessing
/dragonwrath
/hellflame
/flarerebirth
/piercingspike
```
Bind them with your BindCommand mod, e.g. `/bind c dragonwrath`.

## GitHub (push this project)
```bash
git init
git branch -M main
git add .
git commit -m "Initial commit: Amaterasu plugin"
git remote add origin https://github.com/<your-username>/Amaterasu.git
git push -u origin main
```

## Notes
- Upgrader/Reroller are **droppable** but never despawn or take damage.
- On gaining Upgrader: `You feel the corrupted course through your veins... your ability has reached a new height.`
- On losing Upgrader: `You have downgraded back to your ability’s weakened state.`

## Blockbench animations (optional)
Export your animated JSON and spawn via ArmorStands or a custom entity in ability casts. This project leaves hooks in `AbilityManager` to add model/particle calls.
