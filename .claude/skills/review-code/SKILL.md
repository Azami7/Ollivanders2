---
name: review-code
description: Use when the user asks to review, check, or audit code for issues. Covers logic bugs, javadoc, readability, abstraction, design, and test coverage in the Ollivanders2 project.
---

# Code Review Skill

Review the specified file(s) for issues. Read each file before reviewing. Read the broader file and system context — understand how the code fits into the surrounding codebase before flagging issues. Only report actual problems — do not mention things that are correct or working as intended. Be concise and focus on issues only.

**Review inline — do not fan out subagents.** Parallel finder agents each hit permission prompts, so several agents sit blocked on the user at once: the user becomes the bottleneck, the parallelism buys nothing, and the idle agents burn machine memory. Work through the checklist below sequentially yourself. Only fan out if the user explicitly asks for a multi-agent review.

When reviewing source code, also find and review the corresponding test class(es). Test files follow the naming convention `<ClassName>Test.java` and are located under `Ollivanders/test/`. Review both the source and test code together — verify the tests adequately cover the source code's behavior, including edge cases and failure paths.

## What to check

### Design
- Verify interactions between the code and the rest of the system are logical and well-integrated
- Check that the change belongs where it is — could it be better placed in a utility class, base class, or separate module?
- Evaluate whether the functionality is appropriate to add at this time and in this form

### Functionality and logic
- Verify that the code accomplishes its intended purpose — check that the implementation matches what the class/method name and documentation promise
- Inverted or incorrect boolean conditions
- Null pointer risks (e.g., using a value before null-checking it)
- Off-by-one errors, incorrect math
- Dead code or unreachable branches
- Edge cases that are unhandled or would produce incorrect results
- Concurrency issues — check for potential race conditions or deadlocks in any parallel or async code
- Security issues (command injection, XSS, SQL injection)

### Complexity
- Flag code that can't be understood quickly by a reader — if it takes significant effort to parse, it's too complex
- Watch for over-engineering: solving speculative future problems instead of the present one
- Each line, method, and class should be no more complex than necessary
- Prefer solving the problem at hand over building abstractions for hypothetical requirements

### Error handling and robustness
- Verify proper handling of exceptions and edge cases (null returns, empty collections, invalid input)
- Check for resource leaks (unclosed streams, connections, etc.)
- Verify that error logging uses the project's `common.printDebugMessage()` pattern where appropriate
- Ensure error paths don't silently swallow failures when the caller needs to know something went wrong

### Code structure and organization
- Evaluate the organization of methods within the class — related logic should be grouped together
- Check for appropriate use of data structures and algorithms
- Verify adherence to modularity and separation of concerns — a method should do one thing
- Check that design patterns are used appropriately (not over-engineered, not under-structured)

### Maintainability and readability
- Code should be clear and easy to follow — favor simple, straightforward designs over clever tricks or obscure syntax
- Non-obvious logic must have inline comments explaining the "why", not just the "what"
- Variable and method names should be descriptive enough to communicate purpose without being unnecessarily verbose
- Complex conditionals should be broken into named variables or helper methods for clarity
- Magic numbers should be named constants with a comment if the meaning isn't obvious from the name
- Code should be properly formatted with consistent indentation and meaningful whitespace
- Inconsistent use of class fields vs method calls that return the same value (e.g., using `caster.getWorld()` when `world` field is available)
- Follow existing project style consistently — don't introduce style changes alongside functional changes

### Abstraction and reuse
- Check Common classes (`Ollivanders2Common`, `EntityCommon`, `BlockCommon`, etc.) for existing helper methods that the code could use instead of reimplementing logic inline
- Look at sibling classes (e.g., other spells in the same package) for shared patterns that suggest the code should use a common base class method or utility
- Flag inline logic that duplicates functionality already available in the project
- Check for opportunities to use existing enums, type classes, or API methods (e.g., `O2WandWoodType`, `O2WandCoreType`, `Ollivanders2API`) instead of raw material/string checks

### Documentation

Judge comments and javadoc against `Ollivanders/src/JAVADOC_STANDARDS.md` (conciseness-first, contract-focused, design-by-contract). Flag both missing contract and needless verbosity.

Existence and correctness:
- Javadoc must exist for all public and protected classes and methods
- Javadoc descriptions must accurately reflect the current state of the code, not be stale or contradictory
- `<ul>` and `<ol>` lists must NOT be nested inside `<p>` tags — they must be placed outside
- No dangling or mismatched HTML tags (e.g., `</p>` without a matching `<p>`)
- `{@link}` or `@see` references must point to accessible members (e.g., not referencing a protected method from a class that cannot see it)
- `@param`, `@return`, `@throws` tags must match the method signature

Design by contract — flag expectations the annotations/types can't enforce but the javadoc omits:
- Preconditions not captured by `@NotNull`/types: value ranges, units (ticks vs seconds), required call order or state, constraints on collection contents
- Postconditions: whether a returned collection is a copy or live view, whether it may be empty, when a `@Nullable` return is actually null
- Side effects not obvious from the name: mutates a parameter, fires an event, registers/unregisters a listener, spawns/removes entities, writes to disk
- Failure modes: what it throws and when, or what it does with invalid input
- Conversely, flag redundant restatements of what annotations already enforce (e.g. "must not be null" on a `@NotNull` param)

Conciseness — flag comments that violate the standards:
- Comments that state the obvious or narrate straightforward code line-by-line (delete-worthy, not fix-worthy)
- Diff-narrative comments ("now uses X", "changed to...", "previously...") — should describe current behavior, not the change
- Design-history editorializing or references to decisions made and later changed
- GitHub/issue/task numbers in comments
- Out-of-scope/follow-up work written as prose instead of `// TODO:`
- Comment or javadoc lines exceeding 120 characters
- Inline comments should explain the "why" behind complex or non-obvious code — check that tricky logic has one and existing comments are still accurate

### Project-specific patterns (from this repo's PR review history)
- **Config lifecycle:** new config keys must be in `config.yml` with a safe default, and read once at plugin enable in the central config-loading spot — flag any per-action config read.
- **Placement:** spell-specific behavior in `Ollivanders2.java`, a listener, or the scheduler belongs in the spell class.
- **Player feedback:** a failed spell/command check must message the player, not silently no-op.
- **Scheduler termination:** anything in the per-tick/per-second scheduler needs a termination or dedup condition — flag work that repeats forever ("this will happen every second forever").
- **Repeated conditionals:** long Material/entity if-chains should be a static collection lookup, shared via `Ollivanders2Common` when used by more than one class.
- **Copy-paste residue:** for classes cloned from a sibling (the normal way spells are written), check identifiers, comments, and strings for stale references to the source class.
- **Explicit imports** — flag any `import x.y.*`.
- **Late-set nullable fields:** a field null until some other method sets it should be constructor-injected.

### Spell-specific patterns (for classes extending O2Spell)
- `doCheckEffect()`: verify the projectile lifecycle is handled correctly (hasHitTarget/kill/return flow)
- Verify `spellType` and `branch` are set in both constructors
- Verify `initSpell()` is called at the end of the casting constructor
- Check that WorldGuard flags are guarded by `if (Ollivanders2.worldGuardEnabled)`

### Spell patterns that are NOT bugs — don't flag these
Confirmed intentional by the maintainer; flagging them is review noise:
- Static configuration (`noProjectile`, `doFlair`, radius constants) assigned in the **casting constructor** — `doInitSpell()` is only for per-cast logic (e.g. skill-dependent values). Don't suggest moving constants there.
- The caster being skipped in the entity loop when `targetSelf` is false — that's the design; caster opt-in is explicit via `targetSelf`.
- No `noProjectile` check inside `doCheckEffect()` — `stopProjectile()` has already run upstream before `doCheckEffect()` is invoked.
- Raw `§` color codes in ItemMeta display names/lore — `Ollivanders2.chatColor` is for chat only; there is no itemMeta color helper.
- A full `checkEffect()` override (e.g. POINT_ME) is the OLD no-projectile pattern — suggest migrating to `noProjectile` + `doCheckEffect()`, but note the precedent exists; it's a cleanup suggestion, not a bug.

### Test-specific patterns (for test classes)
- Verify tests are correct, useful, and would actually fail if the code under test is broken
- Each test should make clear, focused assertions — not test too many things at once
- Test methods should be properly separated by concern
- Tests are code too — they need the same readability and maintainability standards
- Verify test state is cleaned up between sub-tests (removing dropped items, resetting blocks that spells destroy)
- Check that `caster.setLocation(location)` is called before each new cast if using `TestCommon.faceTarget()` (which causes caster drift)
- Verify different world coordinates are used across tests when block/entity persistence could cause interference
- Check for use of unimplemented MockBukkit methods (e.g., `EntityMock.isUnderWater()`)

## API references

When you need to verify Spigot or MockBukkit API behavior (method signatures, return types, class hierarchies, snapshot vs. live semantics, etc.), consult the official javadocs via WebFetch rather than guessing:
- Spigot: https://hub.spigotmc.org/javadocs/spigot/index.html
- MockBukkit (v1.21): https://javadoc.io/static/org.mockbukkit.mockbukkit/mockbukkit-v1.21/4.98.0/index.html

## Output format

For each issue found, report:
1. File path and line number
2. Brief description of the problem
3. Suggested fix (one line if possible)

If no issues are found, say so.