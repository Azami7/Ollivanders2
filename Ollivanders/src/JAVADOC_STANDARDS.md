# Javadoc Standards for Ollivanders2

This document outlines the javadoc conventions and standards used throughout the Ollivanders2 project to ensure consistency, clarity, and maintainability of code documentation.

## General guideance for code comments

- Explain only the necessary "why" for complex or surprising code. Do not comment code that speaks for itself, and never narrate straightforward lines (`// increment count` above `count++` is noise).
- Methods document their **contract** (what it does, inputs/constraints, return, side effects, failure modes), not their internal steps. The implementation should be free to change without the javadoc going stale.
- Classes document their **purpose and role**, not the algorithms or data structures inside.
- Write for a reader who was not involved in the change. No "the new approach", "as discussed", or history of decisions that were made and then changed. Will this comment make sense a year from now and when these commits are squashed?
- No comment should read as a diff between commits ("now uses X", "changed to...", "previously..."). Document the current behavior plainly.
- Put agreed follow-ups and out-of-scope work in `// TODO: <what and why>` comments.
- Do not reference github tasks, these will be meaningless if we ever change repositories
- Wrap comment and javadoc lines at 120 characters.
- Vocabulary: use "limit" (and "limited to"), not "clamp"/"clamped", when describing a value bounded to a min/max range.

When improving existing javadoc, fix comments that violate these rules rather than preserving them.

## Design by contract

Javadoc is where the contract lives that the type system and annotations cannot enforce. Do not repeat what they already express, but document everything they cannot — conciseness never justifies dropping a real contract term.

**Already enforced — do NOT restate in prose:**

- `@NotNull` / `@Nullable` on a parameter or return: the null contract *is* the annotation. Never write "must not be null" for a `@NotNull` parameter.
- The declared type: don't write "@param count an integer".

**Must be documented — annotations cannot express these:**

- **Preconditions** the caller must satisfy: value ranges and units (e.g. radius must be `>= 0`; duration is in ticks, not seconds), required call order or state (e.g. only valid after `initSpell()` has run), and constraints on contents (e.g. the list must be non-empty; the location's chunk must be loaded).
- **Postconditions / guarantees**: whether a returned collection is a defensive copy or a live view, whether it may be empty, when and why a `@Nullable` return is null, and any invariant the method upholds.
- **Side effects**: mutates a parameter, fires an event, registers or unregisters a listener, spawns or removes entities, writes to disk — anything the caller didn't obviously ask for.
- **Failure modes**: what it throws and when, or what it does with input the annotations don't reject (throws vs. returns null vs. no-ops).

If a caller could misuse the member without the compiler or an annotation stopping them, the javadoc must tell them how not to. Example:

```java
/**
 * Get the players within a radius of this spell's projectile.
 *
 * @param radius the search radius in blocks; must be >= 0
 * @return the players in range, excluding the caster; empty if none, never null
 */
@NotNull
public List<Player> getNearbyPlayers(double radius) {
```

## Table of Contents

1. [Class-Level Documentation](#class-level-documentation)
2. [Method and Constructor Documentation](#method-and-constructor-documentation)
3. [Field Documentation](#field-documentation)
4. [Inline Comments](#inline-comments)
5. [Reference Links](#reference-links)
6. [Common Patterns](#common-patterns)

---

## Class-Level Documentation

Class-level javadoc should state the class's purpose and role — why it exists and how it fits in — not its internal design.

### Structure

```java
/**
 * [Brief one-line description of the class purpose]
 * <p>
 * [More detailed explanation of what the class does and why it exists, if it is not clear from the one-line description.]
 * </p>
 * <p>
 * [Additional context about design patterns, relationships to other classes, or important concepts if they are not 
 * obvious. Also add any Spigot contraints or requirements that influence the class' design and are not obvious]
 * </p>
 *
 * @author [Author Name]
 * @see <a href="url">Descriptive Label</a>
 */
public class MyClass {
```

### Guidelines

- **First line**: brief, clear statement of what the class is for. For many classes this is the whole comment.
- **Further paragraphs**: add only when the purpose genuinely needs them (a non-obvious role, an important relationship). Explain why the class exists, not how it works inside. Do not pad with design rationale or history.
- **Never leave the purpose ambiguous** - a reader should understand what this class does after reading the javadoc.

### Example: O2Color.java

Most classes need only the one-line form:

```java
/**
 * Color representation for Ollivanders2 supporting Minecraft's several color formats (ChatColor, Color, DyeColor).
 *
 * @author Azami7
 */
```

Add a list only when it earns its place — e.g. an enum whose accessors each serve a distinct, non-obvious purpose:

```java
/**
 * Color representation for Ollivanders2 supporting Minecraft's several color formats.
 * <p>
 * Minecraft exposes color differently per context, so this enum offers each representation:
 * </p>
 * <ul>
 * <li>{@link #getBukkitColor()} - items and potions</li>
 * <li>{@link #getChatColor()} - chat messages</li>
 * <li>{@link #getChatColorCode()} - text in books and signs</li>
 * <li>{@link #getDyeColor()} - dyeable blocks</li>
 * </ul>
 *
 * @author Azami7
 */
```

---

## Method and Constructor Documentation

Method and constructor javadoc should clearly explain what the method does, what parameters it expects, and what it returns.

### Structure

```java
/**
 * [Clear action verb]: [what the method does — the contract, not the steps]
 *
 * @param paramName [what it represents and any constraints/range/null handling]
 * @return [what the value represents and any special cases]
 */
public ReturnType methodName(Type paramName) {
```

Add a `<p>` body paragraph only when the contract needs it (a non-obvious side effect, failure mode, or constraint). Most methods need just the summary line and tags.

### Guidelines

- **Start with action verbs**: "Get", "Create", "Transform", "Filter", "Check", etc.
- **Document the contract, not the implementation**: state what it does and returns, not the internal steps.
- **Parameter descriptions**: include constraints, valid ranges, null handling.
- **Return descriptions**: explain what the returned value represents and any special cases.
- **Use `{@link}` for cross-references**: link to related classes/methods.

### Example: Constructor Documentation

Keep it to the contract — the summary line plus params. Don't restate what the body obviously does:

```java
/**
 * Create a tarot cartomancy divination that generates a prophecy about the target.
 *
 * @param plugin     a callback to the plugin
 * @param prophet    the player performing the divination
 * @param target     the player the prophecy is about
 * @param experience the prophet's experience, which affects prophecy accuracy
 */
public CARTOMANCY_TAROT(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
```

### Example: Method Documentation

The contract is the summary line and tags; the seed-to-index mapping is an ordinary idiom that needs no comment:

```java
/**
 * Get a dyeable color selected from the given seed.
 *
 * @param seed any value; determines which dye color is returned
 * @return one of the dyeable colors
 */
@NotNull
public static O2Color getRandomDyeableColor(int seed) {
    int rand = Math.abs(seed) % dyeableColors.length;
    return dyeableColors[rand];
}
```

---

## Field Documentation

Field javadoc should explain the purpose and usage of the field.

### Structure

```java
/**
 * [What this field represents and why it exists]
 */
private Type fieldName;
```

### Guidelines

- **Be concise** - Usually one sentence unless the field represents a complex concept
- **Explain the purpose** - Why does this field exist? What does it represent?
- **Note any constraints** - Valid range, null-safety, initialization timing
- **For complex fields**: Reference related methods that use/populate the field

### Example: O2Color.java

```java
/**
 * This color for items and potions. Access via {@link #getBukkitColor()}.
 */
final Color bukkitColor;

/**
 * The chat format code for this color, e.g. "§c" for red. Access via {@link #getChatColorCode()}.
 */
final String chatColorCode;
```

### Example: Ollivanders2Common.java

```java
/**
 * Shared Random for the whole plugin. All components should use this rather than creating their own.
 */
public final static Random random = new Random();
```

---

## Inline Comments

Inline comments explain non-obvious code patterns, design decisions, or complex logic.

### When to Use Inline Comments

Comment only code a competent reader wouldn't immediately understand. Standard idioms — a `% length` index, copying a collection before iterating to remove from it, a null guard — need no comment.

- **Non-obvious algorithms or magic values**: why an unusual approach or a specific constant was chosen
- **Workarounds**: why a Spigot/MockBukkit limitation or bug forces this shape
- **Non-obvious domain rules**: a constraint or interaction the code enforces that isn't self-evident

### Guidelines

- **Explain the "why"**: what would go wrong without it, or why it's done this way — not what the line does
- **Be specific**: "reject locations outside the arena" beats "check if valid"
- **Keep comments current**: update them when the code changes; delete them when they stop adding value

### Example: Initialization Block Comment

One line stating the purpose is enough — don't catalog the data the code already contains:

```java
// prophecy prefixes, chosen at random and prepended to the divination text
prophecyPrefix.add("The cards have revealed that");
prophecyPrefix.add("The reading of the cards says that");
// ... (remaining prefixes)
```

### Counter-example: don't narrate standard math

Standard math and library calls explain themselves. The spherical-to-Cartesian conversion below needs no commentary — a reader recognizes it:

```java
double x = radius * Math.sin(inclination) * Math.cos(azimuth);
double y = radius * Math.cos(inclination);
double z = radius * Math.sin(inclination) * Math.sin(azimuth);
```

### Example: a genuinely non-obvious "why"

The comment earns its place — nothing in the code says why the loop starts at a random offset:

```java
// random start offset so repeated casts don't align into a visible grid
for (double inclination = (Math.random() * Math.PI) / intensity; inclination < Math.PI; inclination += Math.PI / intensity) {
```

### Example: a non-obvious domain rule

The comment explains a constraint the code enforces but that isn't self-evident:

```java
// maxSpellLevel and bookLearning are mutually exclusive progression systems; only one may be on
if (!bookLearning)
    maxSpellLevel = getConfig().getBoolean("maxSpellLevel");
else
    maxSpellLevel = false;
```

---

## Reference Links

All external references and documentation links should use `@see` tags with proper formatting.

### Format

```java
@see <a href="url">Descriptive Label</a>
```

### Guidelines

- **No spaces around `=`**: Use `href="url"`, not `href = "url"`
- **Use descriptive labels**: Reference what the link contains (e.g., "Harry Potter Wiki - Astrology")
- **Avoid repeating URLs**: Don't use the full URL as the label
- **Place in javadoc footer**: After `@author`, `@since`, and other tags
- **Only external references**: Use `{@link}` for internal class/method references

### Examples

```java
/**
 * Astrology divination spell implementation...
 *
 * @author Azami7
 * @since 2.2.9
 * @see <a href="http://harrypotter.wikia.com/wiki/Astrology">Harry Potter Wiki - Astrology</a>
 */

/**
 * Cartomancy Tarot divination...
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Cartomancy">Harry Potter Wiki - Cartomancy</a>
 */
```

---

## Common Patterns

### Enum Documentation

Document each constant with what it represents and its value; the class comment states the enum's purpose.

```java
/**
 * The standard Minecraft times of day, as day-relative ticks.
 */
public enum TimeCommon {
    /**
     * Midnight, 18000 ticks.
     */
    MIDNIGHT(18000),

    /**
     * Dawn, 23000 ticks.
     */
    DAWN(23000),
```

### Divination Class Pattern

Document what the divination is and what makes it distinct from its siblings (e.g. its accuracy). The prophecy-generation mechanics live in the parent {@link O2Divination} — don't restate them here.

```java
/**
 * [Divination type] divination — [one line on what makes it distinct, e.g. its accuracy relative to other types].
 *
 * @author Azami7
 * @see <a href="url">Reference Link</a>
 */
```

### String Transformation Documentation

Show the transformation with an example rather than narrating the steps:

```java
/**
 * Transform an enum name into a human-readable string, e.g. "AVADA_KEDAVRA" -> "avada kedavra".
 *
 * @param s the enum name, typically in CONSTANT_CASE
 * @return the name as lowercase words separated by spaces
 */
```

### Coordinate System Documentation

For methods dealing with spatial coordinates, document only what the caller can't get from the types — which value is which and the units. Don't explain what the coordinate system is; the reader knows. (A dash list inside a `@param` is also not valid javadoc — it renders as a run-on line, so keep it prose.)

```java
/**
 * Convert spherical coordinates to a Cartesian vector relative to the origin.
 *
 * @param sphere the {@code [inclination, azimuth]} angles, in radians
 * @param radius the distance from the origin
 * @return the equivalent Cartesian vector
 */
```

---

## Checklist for Javadoc Review

When reviewing or writing javadoc, verify:

- [ ] **Contract, not implementation**: Do methods/classes document what and why, not the internal steps?
- [ ] **No diff narrative**: Does it read as current behavior, not "changed to / now uses / previously"? No task numbers.
- [ ] **Purpose**: Is the purpose clear to someone unfamiliar with the code and not part of the change?
- [ ] **Parameters/Returns**: Are constraints, ranges, null handling, and special cases covered?
- [ ] **Links**: Are external references in `@see` tags with descriptive labels, no spaces around `=`?
- [ ] **Width**: Are lines wrapped at 120 characters?
- [ ] **Follow-ups**: Are out-of-scope items captured as `// TODO:` (no task numbers), not narrated in prose?

---

## References

- [Oracle Javadoc Style Guide](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Ollivanders2 Project Implementation Examples