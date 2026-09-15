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

## Fixed content

All NPC, model, animation, item-icon, and spot-animation identifiers are fixed
in source. Users cannot supply arbitrary content identifiers.

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

- Publish this project in a public GitHub repository, ideally named
  `rapid-mounts`.
- Confirm `gradlew.bat clean test` and `gradlew.bat clean runClient` succeed on
  the final public commit.
- Test mount/dismount, actions, teleporting, logout/login, and world hopping.
- Copy the final 40-character commit hash into `plugins/rapid-mounts` in a fork
  of the RuneLite Plugin Hub repository.
- Keep the Plugin Hub pull request limited to that single manifest file.

Suggested manifest:

```properties
repository=https://github.com/YOUR-GITHUB-USERNAME/rapid-mounts.git
commit=YOUR-40-CHARACTER-COMMIT-HASH
```

Suggested pull-request description:

> Adds Rapid Mounts, a visual-only cosmetic plugin that renders an animated
> black unicorn, terrorbird, or lava dragon beneath the local player. It does
> not alter movement, input, menus, gameplay, or server-visible state. The
> original player is restored during actions and whenever cosmetic rendering
> is unavailable.
