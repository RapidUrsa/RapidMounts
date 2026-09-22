# Changelog

## 1.9.0

- Removed Extra Wide from the approved Terrorbird and Lava Dragon pose lists.
- Hid the remaining Black Unicorn fitting controls and restored all Battle Bear
  controls for the final adjustment pass.
- Added mount-specific pose lists to the Mount Stable selector.
- Disabled the selector automatically for Gryphon and Battle Turtle because
  each currently supports one approved pose.
- Added per-mount pose memory with safe fallbacks for unsupported old choices.
- Set Battle Bear's fresh/default riding style to Extra Wide.
- Set Black unicorn's fresh/default riding style to Wide and made its Standard
  style bareback.
- Added a Battle Bear-only No Saddle style using the Extra Wide animation with
  independent rider height, forward/back, and sideways controls, defaulted to
  the approved -51 forward/back, 99 height, and -3 sideways fit.
- Added dark leather reins to Battle Bear's Extra Wide saddle, running from
  its black hand grips to cached animated head vertices so the reins follow
  the bear during idle and walking animations.
- Added live hand-end, head-target, sag, spread, and thickness controls for
  tuning the first Battle Bear rein test.
- Moved the default Battle Bear rein target from the ears to the mouth and
  added an animated black mouth bar with thickness and extension controls.
- Corrected the Battle Bear head controls so forward/back, height, and spread
  apply real offsets after animated mouth anchoring, and added a separate
  sideways control for moving the complete mouth bar left or right.
- Curved the final section of each Battle Bear rein outward around the neck,
  with an adjustable neck-clearance control and smoother five-section paths.
- Anchored the complete Gryphon saddle to an animated back vertex and applied
  the same skeleton delta to the rider, while retaining the independent
  mouth-vertex anchoring used by its reins and gold bit.
- Exposed live Gryphon saddle up/down and left/right controls for final
  skeleton-lock positioning without changing the selected back anchor.
- Locked the approved final Gryphon and Battle Bear rider, saddle, rein,
  mouth-bar, shield, warspear, and attachment values into the release build.
- Reduced every mount-specific settings section to its useful scale control;
  development fitting, bob, pose-offset, and saddle geometry controls remain
  available internally but are hidden from the release menus.
- Added a one-time v1.9 migration that removes old local fitting overrides so
  upgraded installations receive the same approved defaults as fresh installs.
- Kept the Battle Bear's proven procedural backing capacity so adding reins
  cannot prevent the complete saddle model from loading.
- Made fitted saddles and reins automatic and removed their sidebar toggle.
- Kept Terrorbird and Lava Dragon saddle-free.

## 1.8.1

- Corrected the Extra Wide pose's default idle animation ID to 1461 and moving
  animation ID to 1462 so fresh installations use the intended sled poses.
- Added a safe migration that replaces only the two incorrect v1.8.0 Extra
  Wide animation overrides while preserving any other custom values.

## 1.8.0

- Added the Battle turtle and Battle Bear as fully selectable mounts.
- Added the Cross-legged riding pose and mount-specific positioning controls.
- Added the universal Extra Wide riding pose using the sled's idle and moving
  animations, with independent height, forward/back, and sideways controls for
  every mount.
- Added live Extra Wide idle and moving animation ID controls under Rider for
  rapid in-game animation testing.
- Added a dedicated Extra Wide Battle Bear saddle with the regular backrest,
  V's Shield, and separate front blocks removed.
- Removed the regular saddle's raised orange cushions and upright side pads
  from the Extra Wide Battle Bear variant for a clean, open seat profile.
- Added a tapered, profiled ribbed leather seat beneath the Battle Bear's Extra
  Wide rider, with a narrow nose, raised rear and shallow rider pocket.
- Added independent width, length, thickness, body height, forward/back,
  vertical position, and sideways controls for the Extra Wide saddle.
- Corrected the Extra Wide saddle's top-face winding so its padded upper
  surface renders solidly from overhead instead of appearing hollow.
- Corrected the remaining side, underside, nose, and rear face winding so the
  Extra Wide saddle is fully enclosed, and recoloured it muted dark leather.
- Deepened the Extra Wide saddle to a charcoal brown-grey leather with
  near-black ribs, and added a fitted V-shaped front handlebar and hand grips.
- Fixed the Extra Wide saddle measurement variables being declared in the
  wrong model builder, which prevented the handlebar build from compiling.
- Added independent forward/back, height, sideways, spread, and thickness
  controls for the Extra Wide Battle Bear handlebars and grips.
- Recoloured the Extra Wide handlebars black and added an angle control for
  tilting both outer grips upward or downward.
- Locked in the approved final Battle Bear and Extra Wide rider, saddle,
  handlebar, seat, and warspear defaults for release.
- Added a separate Extra Wide spear-bar forward/back setting, defaulted to 2,
  while preserving the regular Battle Bear saddle's setting at 13.
- Polished the release documentation and user-facing setting descriptions for
  the final Plugin Hub submission.
- Hid raw NPC IDs, animation IDs, loop-frame controls, and the Battle Bear
  anchor probe tools from the release settings while retaining their approved
  internal defaults.
- Built a fitted Fremennik battle saddle for the Battle Bear with V's Shield,
  genuine Guthan's warspears, adjustable seat layers, and spear holders.
- Saved the approved final Battle Bear, saddle, seat, shield, warspear, and spear-holder defaults.
- Locked the Battle Bear rider to the saddle's animated upper-torso skeleton anchor so both follow the exact same movement during idle and walking animations.
- Kept the rider position controls as independent fine-tuning offsets from the animated seat anchor.
- Added matching transparent Battle turtle and Battle Bear previews to the Mount Stable sidebar.
- Reordered the Mount Stable cards to Terrorbird, Black unicorn, Gryphon,
  Battle Bear, Lava dragon, and Battle turtle.

- Increased the procedural backing-mesh capacity so the curved Battle Bear saddle,
  rear shield and cache-backed warspears always construct instead of leaving
  the rider floating above an empty mount.
- Replaced the Battle Bear's handmade side spears with Guthan's genuine equipped
  warspear model loaded from the live game cache.
- Rebuilt the Battle Bear's saddle seat and backrest from layered curved profiles for a
  softer Fremennik silhouette, with muted hide colours and V's Shield behind
  the rider.

## 1.7.0

- Added fitted, animation-synchronised saddles and reins for the Black unicorn
  and normal gryphon.
- Added a curved black-and-red unicorn saddle, deep-red reins and a steel-grey
  mouth bit tuned to follow the unicorn's full idle and walking motion.
- Added the ivory-and-gold Skybound gryphon saddle, layered wing guards and an
  Armadyl shield mounted to its tall backrest.
- Anchored the gryphon's curved reins and rounded gold mouth bit to animated
  beak vertices so they follow the gryphon's head movement.
- Locked the unicorn tack to the Wide pose and gryphon tack to the Standard pose
  for their fitted rider positions.
- Added a Saddle & reins toggle directly beneath the pose selector in the Mount
  Stable, with matching options in each supported mount's settings section.
- Added mounted cape visibility and position controls to reduce clipping without
  changing the real player's equipment.
- Linked each saddle, reins and rider to the mount-specific idle and walking
  motion for a cohesive seated animation.

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
