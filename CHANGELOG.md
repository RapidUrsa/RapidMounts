# Changelog

## 1.6.0

- Added the normal gryphon as a fourth animated mount.
- Added native idle and walking animation switching for the gryphon.
- Added support for the gryphon's newer skeletal animation format while
  preserving its proportions at configurable scales.
- Added separately tuned Standard and Wide rider heights for the gryphon.
- Added configurable forward/back and left/right rider sway synchronized with
  the gryphon's walking cycle.
- Added independent gryphon scale and rider-position controls.
- Added a low-poly gryphon preview to the Mount Stable.
- Added the Gull pet item graphic as the gryphon mount button icon.

## 1.5.0

- Added a RuneLite sidebar button and Mount Stable panel.
- Added visual selection cards for the black unicorn, terrorbird, and lava dragon.
- Added bundled low-poly mount previews to each stable selection card.
- Recoloured the terrorbird preview to match its green, yellow, and red in-game palette.
- Kept the Wide pose locked to its clean, correctly seated frame 32.
- Added active-mount highlighting, Standard/Wide pose selection, and a
  mount/dismount control to the panel.
- Kept the movable in-game mount button available and synchronized.
- Added a bundled curved steel-grey horseshoe icon that is available immediately when
  RuneLite constructs the sidebar.

## 1.4.0

- Added selectable Standard and Wide riding poses.
- Added a purpose-built frozen wide riding stance using animation 7536, frame 32.
- Added independently tuned Wide positioning and motion for the black unicorn,
  terrorbird, and lava dragon.
- Kept existing tuning controls and behaviour unchanged in Standard mode.
- Refined the default Standard Terrorbird seat position.

## 1.3.0

- Added black unicorn, terrorbird, and lava dragon mounts.
- Added animated idle and walking cycles with mount-specific rider positioning.
- Added a movable mount/dismount control and optional hotkey.
- Added action-aware pausing and a cosmetic mount/dismount effect.
- Added an option to hide held weapon and shield models while mounted.
- Added safe rendering fallbacks for login, logout, world-hop, and model failures.
- Renamed the plugin to Rapid Mounts and prepared its Plugin Hub metadata.
- Marked the development launcher as an intentional non-JUnit test source for
  compatibility with Gradle 9.
- Relocated adapted appearance classes into Rapid Mounts' own namespace so the
  plugin can run alongside Follower Buddy without duplicate-class conflicts.
