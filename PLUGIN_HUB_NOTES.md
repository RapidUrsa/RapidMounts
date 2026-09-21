# Plugin Hub review notes

Rapid Mounts is a visual-only cosmetic plugin.

## Behaviour

- Creates local `RuneLiteObject` instances for a selected mount and a
  reconstructed copy of the local player's appearance.
- Suppresses drawing of the original local player only after both cosmetic
  objects are active; otherwise the original player remains visible.
- Tracks the local player's location and orientation for rendering only.
- Optionally omits the weapon and shield slots when composing the cosmetic
  rider; it never modifies the real `PlayerComposition` or equipment.
- Temporarily removes the cosmetics while the real player performs an action.
- The overlay button and optional hotkey only toggle local cosmetic state.
- Includes six selectable mounts: black unicorn, terrorbird, lava dragon,
  gryphon, Battle turtle, and Battle Bear, with independently tuned riding poses.
- Adds fitted, animated tack for the Black unicorn, gryphon, Battle turtle, and
  Battle Bear, with user-facing visibility toggles.
- Provides cosmetic cape visibility and positioning controls to reduce clipping.
- Uses bundled classpath images for the Mount Stable previews and RuneLite's
  item image service for the movable mount button icons.

## Content identifiers

Mount NPCs, model sources, item icons, and spot animations use bundled defaults.
Raw animation-ID overrides used during development remain hidden in the public
settings. Version 1.8.1 safely migrates only the two known-bad Extra Wide values
from 1.8.0 and does not alter gameplay or make network requests.

## Capabilities deliberately absent

- No network access or data collection.
- No reflection or native code.
- No subprocess or external program execution.
- No menu-entry changes.
- No generated clicks, keyboard input, movement, or game actions.
- No modification of outgoing chat.
- No file writes at runtime.
- No gameplay simulation, rewards, mechanics, or server-visible state.

## Build

`runelite-plugin.properties` uses `build=standard`. Runtime functionality uses
RuneLite client APIs and Java only.

## Submission checklist

- Publish the release commit to `https://github.com/RapidUrsa/RapidMounts`.
- Confirm `gradlew.bat clean test` and `gradlew.bat clean runClient` succeed on
  the final public commit.
- Test mount/dismount, actions, teleporting, logout/login, and world hopping.
- Update `plugins/rapid-mounts` in the RuneLite Plugin Hub fork to the final
  40-character release commit hash.
- Keep the Plugin Hub pull request limited to that single manifest file.

Suggested manifest:

```properties
repository=https://github.com/RapidUrsa/RapidMounts.git
commit=YOUR-40-CHARACTER-COMMIT-HASH
```

Suggested v1.8.1 pull-request description:

> Updates Rapid Mounts to v1.8.1, correcting the Extra Wide idle and moving
> animation defaults and safely migrating the two incorrect v1.8.0 values.
> The plugin remains visual-only and does not alter movement, input, menus,
> gameplay, or server-visible state.
