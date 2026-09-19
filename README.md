# Rapid Mounts

Rapid Mounts is a visual-only RuneLite plugin that lets your local player ride
four animated cosmetic mounts:

- Black unicorn
- Terrorbird
- Lava dragon
- Gryphon

The plugin changes only what is drawn by your own RuneLite client. Other
players cannot see the mount, and it does not change movement, pathing, speed,
clicks, combat, or any other gameplay.

## Features

- Animated idle and walking cycles for all four mounts.
- A reconstructed rider that keeps the local player's equipment and appearance.
- Selectable Standard and Wide riding poses, tuned independently for every mount.
- Fitted, animated saddles and reins for the Black unicorn and Gryphon.
- A sidebar toggle for showing or hiding supported saddle-and-reins sets.
- Weapons and shields are hidden from the cosmetic rider by default for a cleaner seated pose.
- Optional cape hiding and position adjustments to reduce clipping while mounted.
- Individually tuned scale, seat position, idle bounce, and walking motion.
- A movable mount/dismount button with a mount-specific icon.
- A Mount Stable sidebar panel for selecting mounts, choosing the riding pose,
  and mounting or dismounting.
- Optional keyboard shortcut for mounting and dismounting.
- A cosmetic dark-smoke effect when mounting or dismounting.
- Automatic action pause, revealing the normal player during combat, skilling,
  teleports, ladders, and other actions before remounting.
- Safe fallback behaviour: the original player remains visible unless both the
  cosmetic mount and reconstructed rider are ready.

## Controls

1. Open the horseshoe button in the RuneLite sidebar.
2. Select a mount and choose **Standard** or **Wide**.
3. For the Black unicorn or Gryphon, use **Saddle & reins** to show or hide the
   fitted tack. The unicorn set uses Wide pose and the gryphon set uses Standard.
4. Use the panel or the on-screen button to mount or dismount.
5. Hold **Alt** and drag the on-screen button to reposition it.
6. Optionally assign a **Mount/dismount hotkey** in the plugin settings.

The tested positions are supplied as defaults. Advanced rider and mount-specific
tuning is available in collapsed settings sections.

The Standard pose continues to use the advanced position controls. The Wide
pose uses separately tuned positions and motion for each mount so changing pose
does not overwrite the Standard setup.

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

### Terrorbird

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
Appearance-composition code derived from Follower Buddy remains subject to its
original licence and attribution requirements.

See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for attribution.

## Licence

BSD 2-Clause. See [LICENSE](LICENSE).
