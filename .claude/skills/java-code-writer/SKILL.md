---
name: java-code-writer
description: Use when writing or modifying Java production code in the Ollivanders2 project — new spells, effects, potions, listeners, commands, or changes to existing classes. Covers the project's repo-specific conventions distilled from its PR review history: config read-once-at-enable with safe defaults, spell logic stays in the spell class, player-facing failure messages, per-tick scheduler self-termination, shared Material collections in Ollivanders2Common, the copy-paste audit for cloned spell classes, explicit imports, boolean naming polarity, and the mandatory pre-diff self-review checklist. Javadoc rules live in javadoc-writer; test conventions in test-code-writer; review methodology in review-code.
---

# Java Code Writer Skill

Conventions for writing production Java in Ollivanders2, distilled from the project's PR review history and the maintainer's standards when reviewing contributor PRs. Read the class you're modifying and at least one sibling (e.g. another spell in the same package) before writing anything.

## Before presenting any diff — mandatory self-review

Run this as an explicit pass over the diff, not a mental nod:

1. **Copy-paste audit.** If the class started as a clone of a sibling (the normal way to write a new spell), audit every identifier, comment, string, and javadoc for stale references to the source class ("these still say foundWand", "comments cut and paste from Diffindo" are real review quotes). Also check for duplicated lines left by merges.
2. **Comment/javadoc audit.** Every comment passes the javadoc-writer bar: contract not implementation, no diff-narrative, no obvious restatements. Public/protected members have javadoc.
3. **Naming audit.** Booleans named so conditions read correctly (`materialIsASign` — polarity bugs have shipped from misnamed booleans); getters say what they get (`getSpellLastCastTime`, not `getSpellCastTime` for a last-cast timestamp); enum constants ALL_CAPS.
4. **Imports.** Explicit only — never `import x.y.*`.
5. **Placement audit.** Spell-specific behavior lives in the spell class, not `Ollivanders2.java` or a listener. Shared logic goes in the owning `*Common` class.
6. **Tests.** Every new spell/effect/behavior gets a test per test-code-writer; the current expectation is test-per-spell even though no reviewer will chase you for it.

## Config lifecycle — read once at enable, safe defaults

New config keys:
- Go in `config.yml` **with a safe default**, in the same commit as the code that reads them.
- Are read **once at plugin enable**, in the central config-loading spot, and stored — never re-read per action. ("Just in this one new feature you have it reading the config file 4 times" is the review this avoids.)

## Spell logic stays in the spell class

Behavior specific to one spell — its checks, its config, its side effects — belongs in that spell's class, not in `Ollivanders2.java`, the listener, or the scheduler. The plugin main class wires things up; it doesn't host feature logic.

## Player-facing failure feedback

A spell or command that fails a check (out of range, wrong target, insufficient level) must **tell the player why**, not silently no-op or die. "We probably also want to tell the player that they cannot go that far rather than have the command just die." Use the established message patterns (`Ollivanders2.chatColor` for chat).

## Scheduler tasks must be self-terminating

Anything running in the per-tick/per-second scheduler (`Ollivanders2Schedule`) needs a termination or dedup condition — "unless the item lore is cleaned up, this will happen every second forever." Before adding scheduled work, state what makes it stop. Also avoid needless allocation and string concatenation inside these loops.

## Shared collections over repeated conditionals

A long if-chain over Materials (or entity types, etc.) becomes a static `Set`/`List` lookup — and if more than one class needs it, the collection lives in `Ollivanders2Common` (or the relevant `*Common`), not copied per class. Check the `*Common` classes for an existing collection or helper before writing a new check.

## Constructor injection over late-set nullable fields

A field that stays null until some other method happens to set it is a review flag ("where does p get set to something besides null?"). Pass required collaborators through the constructor so the object is valid on construction.

## Spell-class patterns (extending O2Spell)

- Static configuration (`noProjectile`, `doFlair`, radius constants) is set in the **casting constructor**. `doInitSpell()` is only for per-cast logic (e.g. anything depending on the caster's skill level).
- `spellType` and `branch` are set in **both** constructors; `initSpell()` is called at the end of the casting constructor.
- The current no-projectile idiom is `noProjectile = true` in the constructor + logic in `doCheckEffect()`. Don't override `checkEffect()` (old pattern — bypasses base-class kill checks/aging); don't re-check `noProjectile` inside `doCheckEffect()` (handled upstream).
- WorldGuard interactions are guarded by `if (Ollivanders2.worldGuardEnabled)`.
- Probabilistic rolls that tests must control get an `Ollivanders2.testMode` guard in production (see test-code-writer for the preferred form) — not test-side workarounds.
- Raw `§` color codes are correct for ItemMeta display names/lore; `Ollivanders2.chatColor` is for chat only.

## Game content: canon and Material correctness

New spells, potions, and ingredients are checked against Harry Potter canon (name, effect, ingredients) — be ready to cite a source for content choices. Verify chosen Bukkit `Material`s actually render sensibly as the item they represent (some, like nether wart block vs. item forms, don't behave as expected).