# Rapid Mounts

Rapid Mounts is a visual-only RuneLite plugin that lets your local player ride
three animated cosmetic mounts:

- Black unicorn
- Terrorbird
- Lava dragon

The plugin changes only what is drawn by your own RuneLite client. Other
players cannot see the mount, and it does not change movement, pathing, speed,
clicks, combat, or any other gameplay.

## Features

- Animated idle and walking cycles for all three mounts.
- A reconstructed rider that keeps the local player's equipment and appearance.
- Weapons and shields are hidden from the cosmetic rider by default for a cleaner seated pose.
- Individually tuned scale, seat position, idle bounce, and walking motion.
- A movable mount/dismount button with a mount-specific icon.
- Optional keyboard shortcut for mounting and dismounting.
- A cosmetic dark-smoke effect when mounting or dismounting.
- Automatic action pause, revealing the normal player during combat, skilling,
  teleports, ladders, and other actions before remounting.
- Safe fallback behaviour: the original player remains visible unless both the
  cosmetic mount and reconstructed rider are ready.

## Controls

1. Select a mount in the **General** settings section.
2. Left-click the on-screen button to mount or dismount.
3. Hold **Alt** and drag the button to reposition it.
4. Optionally assign a **Mount/dismount hotkey**.

The tested positions are supplied as defaults. Advanced rider and mount-specific
tuning is available in collapsed settings sections.

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

RuneLite does not support installing an unpublished Plugin Hub plugin by copying
its JAR into the normal client. Until accepted into the Plugin Hub, Rapid Mounts
must be run through the development client.

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
- Rider height: 39
- Rider forward/back: 2
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
