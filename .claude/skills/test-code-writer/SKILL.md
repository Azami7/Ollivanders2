---
name: test-code-writer
description: Use when the user asks to write, add, or improve unit tests for the Ollivanders2 project. Covers project test conventions, MockBukkit constraints, the parallel-execution model, and coverage practices.
---

# Test Code Writer Skill

Write JUnit Jupiter unit tests for the Ollivanders2 project. Read the production class under test before writing tests — understand what it does, what its public surface is, and how it integrates with the surrounding system. Read at least one existing sibling test in the same package to match the project's conventions before writing anything new.

## Project conventions

### Test class naming
For most production classes, the test class is `<ClassName>Test.java`. **Exception:** classes that act as implementations of an enumerated type — spells, effects, stationary spells, potions, etc. — use PascalCase based on the SCREAMING_SNAKE_CASE production name, not the raw class name. Examples:
- `PACK.java` → `PackTest.java`
- `WINGARDIUM_LEVIOSA.java` → `WingardiumLeviosaTest.java`
- `ItemToItemTransfiguration.java` → `ItemToItemTransfigurationTest.java` (already PascalCase, no transform)

### Test method naming
Test methods are named `<scenarioOrAspect>Test()` — e.g. `doCheckEffectTest()`, `radiusTest()`, `upkeepTest()`, `invalidTargetTest()`. Not `test<Thing>()` and not bare camelCase verbs. The base class typically declares an abstract `doCheckEffectTest()` (or equivalent) that subclasses must implement; additional scenario tests follow the same `<aspect>Test()` shape.

### Package layout
Test packages mirror the production code package layout exactly. A class at `Ollivanders/src/net/pottercraft/ollivanders2/spell/PACK.java` has its test at `Ollivanders/test/java/net/pottercraft/ollivanders2/test/spell/PackTest.java`. The `test` segment is inserted between `ollivanders2` and the leaf package; everything below that mirrors prod.

### Javadoc
Test classes and test methods get Javadoc, same as production code. Document what the test verifies and why — not just what it does mechanically. Follow `JAVADOC_STANDARDS.md`.

### Magic numbers and constants
Any literal whose meaning isn't obvious from context needs an inline comment explaining where it comes from. Examples: `1728 // 27 slots × 64 stack depth — https://minecraft.fandom.com/wiki/Chest`, `20 // ticks per second`. Prefer a named local variable when the number is reused.

### World naming
Worlds created via `mockServer.addSimpleWorld(...)` use a short, descriptive name — typically the spell or effect name, with a scenario suffix when one test class needs several worlds. The first/canonical test in a spell test class commonly passes `getSpellType().getSpellName()`; sibling methods append a scenario tag (e.g. `"PackEnderChest"`, `"PackShulkerBox"`).

## Test design

### Don't hard-code production values
Never copy a constant from production code into a test. If the test needs a value that lives in production (e.g. a spell's `maxDuration`, a radius, a cooldown), add a **copy-returning getter** to the production class and call it from the test. The production code becomes the single source of truth, and the test stays correct when the value changes.

Strict rules for these getters:
- Read-only access only. **Never** add a setter purely to satisfy a test.
- If the value is a mutable object (List, Map, ItemStack, Location, etc.), the getter MUST return a defensive copy or unmodifiable view. A getter that hands out the live internal collection lets the test mutate production state, which is worse than the duplication it was trying to avoid.

### Plugin lifecycle: `@BeforeAll` vs `@BeforeEach`
Spell tests load the plugin once via a static `@BeforeAll` and reuse it across all methods in the class (`O2SpellTestSuper.globalSetUp`). Effect tests load a fresh plugin per method via `@BeforeEach` (`EffectTestSuper.setUp`). The split exists because effects mutate plugin-scoped state during a method, while spell tests are read-only against the plugin. When writing a new test class, match whichever base class you extend — don't reinvent the lifecycle. When designing a new base class, choose `@BeforeEach` if your category mutates plugin state, `@BeforeAll` otherwise (it's much faster).

### Inheritance over duplication
When multiple test classes share setup or assertions, push the common code up into a base class. Look at existing bases like `O2SpellTestSuper` for the pattern: shared MockBukkit lifecycle, shared `castSpell` helpers, abstract methods that subclasses must implement. New test classes should extend the appropriate base when one exists, and you should propose a new base when you see a third copy of the same setup emerging.

### TestCommon for cross-cutting helpers
Helpers that are useful across many *unrelated* test classes belong in `TestCommon` (or another `*Common` test utility), not duplicated per test. Examples: `createBlockBase`, `faceTarget`, `setPlayerSpellExperience`. Helpers that are only useful to one test class or to one test family stay local to that class or its base.

### Useful existing helpers
Before writing new fixture or assertion code, check whether one of these already exists:

**Test-side utilities:**
- **Block fixtures (`TestCommon`):** `createBlockBase(location, size)`, `createNorthSouthBlockWall(location, size)`, `createEastWestBlockWall(location, size)`. Use these instead of hand-rolled nested loops.
- **Inventory queries (`TestCommon`):** `getPlayerInventoryItem(player, material)`, `getPlayerInventoryItem(player, material, name)`, `amountInPlayerInventory(player, material, name)`. These handle null safety and book/display-name edge cases.
- **Chat / message handling (`TestCommon`):** `cleanChatMessage(message)` to strip color codes before assertion, `clearMessageQueue(player)` to drain pending messages before re-checking output.
- **Wand correctness (`O2PlayerCommon`):** `rightWand`, `wrongWand`, `elderWand` constants. Pass these to `castSpell(...)` overloads instead of magic doubles when testing wand-dependent behavior.

**Production `*Common` utilities — also for tests:**
Tests can and should call the production helper classes (`Ollivanders2Common`, `EntityCommon`, `BlockCommon`, `O2PlayerCommon`, etc.) directly when they need the same logic the production code uses. Don't reimplement entity radius scans, block adjacency checks, distance math, player metadata lookups, etc. — find the existing helper first. If a test needs to assert "the spell affected the right entities", the most accurate check is usually "compare against what `EntityCommon.getEntitiesInRadius(...)` returns at the same location and radius". Reimplementing the logic in the test risks the test agreeing with itself but disagreeing with prod.

### Coverage targets
Aim for high line coverage of meaningful behavior. Skip:
- Trivial getters and setters that just read/write a field with no logic.
- Generated code, enum boilerplate.

Always cover:
- Happy paths.
- Branch boundaries (off-by-one, min/max clamps, empty collections, null inputs where allowed).
- Failure paths the production code is expected to handle gracefully.

## MockBukkit constraints

### Parallel execution + shared game state
JUnit Jupiter runs test methods in parallel by default. MockBukkit's `ServerMock` is a shared static instance across the methods of a test class (set up in `@BeforeAll`), and the world/entity/scheduler state inside it is **global**. If two parallel test methods both mutate that shared state — spawning entities, changing blocks, casting spells, advancing the scheduler — they will interfere with each other in nondeterministic ways.

**Rule:** any sequence of operations that depends on a specific state evolution must live inside a **single** `@Test` method, not split across methods. Use sub-sections within one method (with comments delimiting them) when you need multiple scenarios that share state assumptions. Splitting only makes sense when the scenarios are genuinely independent (different worlds, different players, no overlap).

When in doubt, keep related scenarios together. The `doCheckEffectTest` pattern in the existing spell tests is the canonical example.

### Wrapping unimplemented MockBukkit methods
Some Bukkit/Paper APIs are not implemented in MockBukkit and will throw `UnimplementedOperationException` (or similar) if called from a test. Examples seen so far: `World.createExplosion`, certain entity state queries.

When production code needs to call one of these, wrap the call so it's skipped under test:

```java
if (!Ollivanders2.testMode)
    world.createExplosion(location, power);
```

This sacrifices coverage of the wrapped line in exchange for letting the rest of the method be tested. Keep the wrapped block as small as possible — only the actually-unimplemented call goes inside the guard, not the surrounding logic.

`Ollivanders2.testMode` is set to `true` in the `@BeforeAll` of `O2SpellTestSuper` (and equivalents). Production never sets it.

### Bukkit events must be fired manually
MockBukkit does not synthesize Bukkit events the way real Paper does — gameplay actions in a test won't automatically generate the corresponding event. To exercise an event handler, construct the event yourself, call `mockServer.getPluginManager().callEvent(event)`, then `performTicks(...)` afterward to give the handler time to run.

### Snapshot vs live state in MockBukkit
MockBukkit's `BlockState` snapshots can diverge from production Paper semantics for some `Container` types (notably `Chest`). When a test reads inventory state that the production code has just written, prefer re-fetching the state via `block.getState()` immediately before the assertion rather than reusing a stale snapshot reference. If a test still fails despite a fresh snapshot, suspect a MockBukkit-vs-Paper API divergence and check the API references below.

## API references

You are pre-authorized to read the local Spigot Javadoc for any read-only operation (Read, Glob, Grep, Bash with grep/cat/find — not modifications). It lives at:

```
/Users/kristin/minecraft/spigot javadoc/index.html
```

Class pages follow the standard javadoc layout: `org/bukkit/block/Chest.html`, `org/bukkit/inventory/Inventory.html`, etc. Use Read or Grep against these files when you need to verify a method signature, return type, class hierarchy, or snapshot/live semantics — do **not** guess at the API. Note that `WebFetch` does not accept `file://` URLs; use Read or Bash for local files.

For MockBukkit-specific behavior, the published javadocs are at:
- https://javadoc.io/static/org.mockbukkit.mockbukkit/mockbukkit-v1.21/4.98.0/index.html

## Workflow

1. Read the production class under test.
2. Read the most-similar existing test class to match conventions.
3. Identify the abstract base test class to extend (if any).
4. Identify any existing `TestCommon` helpers you can reuse.
5. Identify any production values you'll need — add copy-returning getters first if they don't exist.
6. Write the test, grouping state-dependent scenarios into single methods.
7. Run only the affected test class first (`./gradlew test --tests <FQCN>`) to get fast feedback.
8. Run the full test suite once everything passes locally.