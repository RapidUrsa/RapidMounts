# Rapid Mounts

Thanks for trying my plugin! This started as a plugin I really wanted, and it
grew from there! I'm so happy so many people seem to like it! I'm always looking
for new pose suggestions and mounts, so please send requests and I'll see what
I can do :)

## v2.5.0 — Flying dragon

The Flying dragon mount uses NPC 8075 and animation 7870 for both idle and
movement. The Extra Wide rider pose follows the dragon's animated back during
idle and movement. The rider anchor samples the central body around the back
rather than the wing tips. Dragon scale, flight height, rider position,
and mounted Rapid Holster position have adjustable controls in the Flying dragon
section. The stable shows a side view of the red dragon below Lava dragon, and
the quick button uses red dragonhide. The stable sidebar scrolls. The dragon
has no custom saddle.

The tested flying dragon defaults are NPC 8075, 75% scale, flight height 30,
idle and moving animation 7870, rider forward -56 / height 73 / sideways 0,
and mounted weapon forward 29 / height -55 / sideways 0.

The stable scrollbar uses a black thumb. The Battle-Ready Terrorbird keeps its
saddle mesh in place while the animated reins update separately, avoiding a
full saddle model replacement every walking frame.
During the split combat pose, the upper and lower rider share a scene tile and
the separate reins yield to the rider so the upper body stays visible.

## v2.4.0 — Battle-Ready Terrorbird

The Battle-Ready Terrorbird keeps its rider visible while the player fights.
The rider uses the player's attack animation and holds the equipped weapon.
Between attacks, one-handed weapons and shields use a seated ready pose;
two-handed weapons use their normal idle stance when Battle Ready is selected.
The separate, movable **Battle Ready** button appears only while mounted on the
Battle-Ready Terrorbird in the Standard pose. Press it to draw the weapon outside
combat, and press it again to return to riding. Other mounts still pause for
actions. Combat, targeting, timing and damage remain controlled by the game.

## v2.3.0 — TzRek-Zuk

This release adds the TzRek-Zuk form of the Jal-Nib-Rek pet as a scaled
cosmetic mount. It offers Standard, Cross-legged, and Extra Wide poses on one
shoulder, with Extra Wide selected by default. The TzRek-Zuk
settings let you adjust the pet NPC ID, model scale, the mount's forward/back
placement, separate rider positions for each pose, and the mounted Rapid
Holster position. The rider follows a group of
vertices on the animated shoulder when a mount animation is selected.

The pet's idle (7975) and walking (7977) animation IDs come from the
TzRek-Zuk entry in Rapid Companions. NPC ID 8009 and item ID 22319 are the
pet-form defaults. Adjust the shoulder controls while idle and walking;
the mount forward/back setting starts at -128 local units to bring the visible
model toward the player tile. The shipped rider positions for Standard,
Cross-legged, and Extra Wide and the mounted weapon position use the final
in-game fitting values.

## v2.2.0 — More mounts and riding polish

This release adds Big wolf, Catablepon, and Sheep? to the Mount Stable. It also
adds fitted tack and reins for the Battle-Ready Terrorbird and updates rider movement to
follow the bird's animated back. Sheep?, Catablepon, and Big wolf have Extra Wide riding
poses, with individual rider and mounted weapon positions.

Rapid Mounts can coordinate with Rapid Holster while riding: Holster hands its
weapon display to Mounts, and the mounted rider uses the saved Holster
placements. Mounts reads those placements without registering the Holster
settings page under Rapid Mounts.
Each supported mount-and-pose combination has its own mounted holster sideways,
height, and forward adjustments in that mount's settings section. These controls
do not change Rapid Holster's on-foot placements.

Rapid Mounts is a visual-only RuneLite plugin that lets your local player ride
thirteen cosmetic mounts:

- Battle-Ready Terrorbird
- Sheep?
- Catablepon
- Big wolf
- Black unicorn
- Gryphon
- Callisto
- Vorkath
- Lava dragon
- Flying dragon
- Araxxor
- Battle turtle
- TzRek-Zuk

The plugin changes only what is drawn by your own RuneLite client. Other
players cannot see the mount, and it does not change movement, pathing, speed,
clicks, combat, or any other gameplay.

## Features

- Animated idle and walking cycles for all thirteen mounts.
- A reconstructed rider that keeps the local player's equipment and appearance.
- Selectable Standard, Wide, Extra Wide, and Cross-legged riding poses, with
  mount-specific positioning controls.
- Fitted, animated tack for the Black unicorn, Battle-Ready Terrorbird, Gryphon, Battle turtle, and Callisto.
- Fitted saddle and reins on supported mounts and riding poses.
- Weapons and shields are hidden from the cosmetic rider by default for a cleaner seated pose.
- Optional cape hiding and position adjustments to reduce clipping while mounted.
- Individually tuned scale, seat position, idle bounce, and walking motion.
- A movable mount/dismount button with a mount-specific icon, plus the separate
  Battle Ready button for the Battle-Ready Terrorbird's Standard pose.
- A Mount Stable sidebar panel for selecting mounts, choosing the riding pose,
  and mounting or dismounting.
- Optional keyboard shortcut for mounting and dismounting.
- A cosmetic dark-smoke effect when mounting or dismounting.
- Automatic action pause for skilling, teleports, ladders and combat on other mounts.
- Safe fallback behaviour: the original player remains visible unless both the
  cosmetic mount and reconstructed rider are ready.

## Controls

1. Open the horseshoe button in the RuneLite sidebar.
2. Select a mount and choose **Standard**, **Wide**, **Extra Wide**, or
   **Cross-legged**.
3. Use the panel or the on-screen button to mount or dismount.
4. With the Battle-Ready Terrorbird in Standard pose, press **Battle Ready** to
   switch the rider's stance outside combat; press it again to return to riding.
5. Hold **Alt** and drag either on-screen button to position it independently.
6. Optionally assign a **Mount/dismount hotkey** in the plugin settings.

The tested positions are supplied as defaults. Advanced rider and mount-specific
tuning is available in collapsed settings sections.

Each pose uses its own tuned rider position where required, so changing pose
does not overwrite the fit of the others. Callisto also switches to its
dedicated ribbed saddle and handlebars when Extra Wide is selected.

**Hide held equipment** affects only the cosmetic rider. The real player,
weapon, and shield return normally whenever the mount is hidden or paused.

## Running locally

1. Install a compatible JDK.
2. Extract the project and open a terminal in its folder.
3. On Windows, run:

   ```bat
   gradlew.bat clean runClient
   ```

4. Enable **Rapid Mounts** in the developer RuneLite plugin list.

These instructions are intended for local development and release testing. The
published version can be installed normally through RuneLite's Plugin Hub.

## Privacy

Rapid Mounts does not collect, store, or transmit personal information. It has
no network functionality and writes no files at runtime.

## Tested defaults

### Black unicorn

- Scale: 105%
- Rider height: 41
- Rider forward/back: -2
- Rider sideways: 0
- Idle bounce: 2

### Battle-Ready Terrorbird

- Scale: 100%
- Rider height: 43
- Rider forward/back: -2
- Rider sideways: 0
- Idle animation: 6793
- Walk animation: 6796
- Idle bounce: 2
- Walk height adjustment: 0
- Walk forward adjustment: 25
- Stride follow: -6
- Seat bounce: 0
- Seat sway: 3

### Lava dragon

- Scale: 100%
- Rider height: 37
- Rider forward/back: -95
- Rider sideways: 6
- Idle animation: 90
- Walk animation: 79
- Idle bounce: 2
- Walk height adjustment: 5
- Walk forward adjustment: -12
- Stride follow: -3
- Seat bounce: 2
- Seat sway: -2

### Gryphon

- Scale: 98%
- Standard pose rider height: 44
- Wide pose rider height: 34
- Rider forward/back: 15
- Rider sideways: 0
- Idle animation: 12547
- Walk animation: 12549
- Idle bounce: 2
- Walk height adjustment: 0
- Walk forward adjustment: 0
- Seat sway: 5
- Left/right sway: 4
- Saddle scale: 64%
- Saddle forward/back: 22
- Saddle height: 123
- Saddle sideways: 0

## Compatibility and safety

Rapid Mounts does not send input, alter menu entries, communicate over the
network, or affect the game server. Mounts temporarily disappear during player
actions so normal gameplay animations remain clear.

The mount definitions and animations are loaded from the active OSRS cache.

See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for attribution.

## Licence

BSD 2-Clause. See [LICENSE](LICENSE).
