# Javadoc Standards for Ollivanders2

This document outlines the javadoc conventions and standards used throughout the Ollivanders2 project to ensure consistency, clarity, and maintainability of code documentation.

## Table of Contents

1. [Class-Level Documentation](#class-level-documentation)
2. [Method and Constructor Documentation](#method-and-constructor-documentation)
3. [Field Documentation](#field-documentation)
4. [Inline Comments](#inline-comments)
5. [Reference Links](#reference-links)
6. [Common Patterns](#common-patterns)

---

## Class-Level Documentation

Class-level javadoc should provide comprehensive context about the class's purpose, design, and usage.

### Structure

```java
/**
 * [Brief one-line description of the class purpose]
 * <p>
 * [More detailed explanation of what the class does and why it exists]
 * </p>
 * <p>
 * [Additional context about design patterns, relationships to other classes, or important concepts]
 * </p>
 *
 * @author [Author Name]
 * @since [Version]
 * @see <a href="url">Descriptive Label</a>
 */
public class MyClass {
```

### Guidelines

- **First line**: Brief, clear statement of what the class is and does
- **Paragraph 1**: Expanded explanation providing context and purpose
- **Paragraph 2+**: Additional details like:
  - How this class fits into the broader system
  - Key concepts or patterns used
  - Relationships to parent classes or interfaces
  - Design rationale
- **Never leave the purpose ambiguous** - A reader should understand what this class does after reading the javadoc

### Example: O2Color.java

```java
/**
 * Color representation for Ollivanders2 supporting multiple Minecraft color formats.
 * <p>
 * Minecraft handles colors inconsistently across different contexts - chat messages use ChatColor and format codes (§),
 * items use Color, and blocks use DyeColor. This enum provides unified access to all color representations,
 * allowing seamless conversion between formats.
 * </p>
 * <p>
 * <strong>When to use each color representation:</strong>
 * <ul>
 * <li>{@link #getBukkitColor()} - For items and potions (Color class)</li>
 * <li>{@link #getChatColor()} - For chat messages in commands and player messages (ChatColor enum)</li>
 * <li>{@link #getChatColorCode()} - For text formatting in books and signs using color codes like "§c" for red</li>
 * <li>{@link #getDyeColor()} - For dyeable blocks like wool, concrete, and glass (DyeColor enum)</li>
 * </ul>
 * </p>
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
 * [Clear action verb]: [What the method does and why]
 * <p>
 * [Additional details about behavior, algorithm, or design considerations]
 * </p>
 *
 * @param paramName [Description of what this parameter represents and expected range/constraints]
 * @return [Description of return value and what it represents]
 */
public ReturnType methodName(Type paramName) {
```

### Guidelines

- **Start with action verbs**: "Get", "Create", "Transform", "Filter", "Check", etc.
- **Explain the "why"**: Don't just describe implementation, explain purpose
- **Parameter descriptions**: Include constraints, valid ranges, null handling
- **Return descriptions**: Explain what the returned value represents and any special cases
- **Use `{@link}` for cross-references**: Link to related classes/methods

### Example: Constructor Documentation

```java
/**
 * Constructor that initializes a tarot cartomancy divination prophecy.
 * <p>
 * Creates a new tarot cartomancy divination instance and populates it with major arcana card prophecy prefixes.
 * Sets the divination type to CARTOMANCY_TAROT with a maximum accuracy of 35 points (higher than regular
 * cartomancy at 25 points). The prophecy prefixes reference the 22 major arcana cards and their archetypal
 * symbolic meanings, which represent significant life principles and transformative forces.
 * </p>
 *
 * @param plugin     a callback to the plugin for accessing configuration and other resources
 * @param prophet    the player who is performing the divination (casting the tarot cartomancy spell)
 * @param target     the player who is the subject of the divination prophecy
 * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
 */
public CARTOMANCY_TAROT(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
```

### Example: Method with Algorithm Explanation

```java
/**
 * Get a dyeable color based on the provided seed.
 * <p>
 * Returns one of the 16 available dye colors that can be applied to dyeable blocks like wool, concrete, and glass.
 * The seed is used with modulo arithmetic to select from the available colors.
 * </p>
 *
 * @param seed the base value to determine which color is selected (using modulo)
 * @return a dyeable color from the seed value
 */
@NotNull
public static O2Color getRandomDyeableColor(int seed) {
    // Modulo 16 distributes the seed value evenly across the 16 available dye colors (0-15)
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
 * The Bukkit Color object for this color.
 * Used for item coloring (e.g., potion colors). Access via {@link #getBukkitColor()}.
 */
final Color bukkitColor;

/**
 * The chat format code for this color (e.g., "§c" for red).
 * Used for text formatting in books, signs, and other text-based UI elements.
 * Format is the section symbol (§) followed by a hexadecimal character. Access via {@link #getChatColorCode()}.
 */
final String chatColorCode;
```

### Example: Ollivanders2Common.java

```java
/**
 * Global Random instance for generating random numbers across the plugin.
 * <p>
 * Seeded with {@link System#currentTimeMillis()} in the constructor to ensure different sequences
 * across plugin reloads. All plugin components should use this shared instance rather than creating
 * their own to maintain consistency in random number generation.
 * </p>
 */
public final static Random random = new Random();
```

---

## Inline Comments

Inline comments explain non-obvious code patterns, design decisions, or complex logic.

### When to Use Inline Comments

- **Non-obvious algorithms**: Explain why an unusual approach was chosen
- **Modulo operations**: Document why specific divisors were chosen (e.g., "Modulo 16 because there are 16 dye colors")
- **Initialization blocks**: Explain the purpose of large initialization sections
- **Design pattern justifications**: Why create a copy before modifying? Why check for null?
- **Workarounds or hacks**: Explain any non-obvious code patterns

### Guidelines

- **Be specific**: Don't just say "check if valid", explain what valid means
- **Reference the "why"**: Why is this necessary? What would go wrong without it?
- **Keep comments current**: Update comments when code changes
- **One comment per concept**: Don't clutter code with excessive comments

### Example: Initialization Block Comment

```java
// Populate prophecy prefixes with tarot major arcana cards. These prefixes are randomly selected
// when generating a prophecy and combined with divination text to create the final prophecy message.
// The prefixes reference the 22 major arcana cards from a traditional tarot deck.
// Each major arcana card represents a fundamental life principle or archetypal force:
// - The Fool: new beginnings, innocence, risk
// - The Magician: manifestation, resourcefulness, power
// - The High Priestess: intuition, mystery, inner knowledge
// ... (list continues)
prophecyPrefix.add("The cards have revaled that");
prophecyPrefix.add("The reading of the cards says that");
// ... (remaining prefixes)
```

### Example: Algorithm Explanation

```java
// X and Z form the horizontal plane (radius from Y-axis), azimuth determines direction
double x = radius * Math.sin(inclusion) * Math.cos(azimuth);
double z = radius * Math.sin(inclusion) * Math.sin(azimuth);
// Y is the vertical component; cos(inclusion) ranges from -1 to 1 as inclusion goes 0 to π
double y = radius * Math.cos(inclusion);
```

### Example: Design Pattern Explanation

```java
// Random offset prevents aligned grid patterns; starts at random point instead of 0
for (double inclusion = (Math.random() * Math.PI) / intensity; inclusion < Math.PI; inclusion += Math.PI / intensity) {
```

### Example: Copy Before Modification

```java
// A copy of the original recipient set is iterated to safely remove players from the original set
// without causing concurrent modification exceptions.
Set<Player> temp = new HashSet<>(recipients);
for (Player recipient : temp) {
    if (!Ollivanders2Common.isInside(location, recipient.getLocation(), dropoff)) {
        recipients.remove(recipient);
    }
}
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

Enums should document each constant and explain what they represent.

```java
/**
 * Enumeration of standard times of day in Minecraft, based on day-relative ticks.
 * <p>
 * [Detailed explanation of the concept and usage]
 * </p>
 */
public enum TimeCommon {
    /**
     * Midnight (18000 ticks) - the start of the night cycle, when darkness is complete.
     */
    MIDNIGHT(18000),

    /**
     * Dawn (23000 ticks) - early morning when the sun is just beginning to rise.
     */
    DAWN(23000),
```

### Divination Class Pattern

For divination spell implementations, document:
1. What type of divination (astrology, cartomancy, etc.)
2. How it generates prophecies (combines prefixes with text)
3. Accuracy level and why it differs from other types
4. What the prefixes represent

```java
/**
 * [Divination Type] divination spell implementation using [specific method].
 * <p>
 * [Definition and context about the divination type]
 * </p>
 * <p>
 * This class implements the [type] divination method, generating randomized prophecies based on
 * [method-specific details]. The prophecies are created by combining randomly selected prefixes
 * (which reference [what prefixes represent]) with divination text from the parent {@link O2Divination} class.
 * [Divination type] has [accuracy level] maximum accuracy compared to [other types],
 * [explanation of why].
 * </p>
 *
 * @author Azami7
 * @see <a href="url">Reference Link</a>
 */
```

### String Transformation Documentation

For methods that transform strings, explain both the input and output format with examples.

```java
/**
 * Transform an enum name string to a human-readable format.
 * <p>
 * Converts the input to lowercase, splits on underscores, and joins the parts with spaces.
 * For example: "AVADA_KEDAVRA" → "avada kedavra"
 * </p>
 *
 * @param s the enum name as a string (typically in CONSTANT_CASE format)
 * @return a space-separated lowercase string with underscores removed
 */
```

### Coordinate System Documentation

For methods dealing with spatial coordinates, document the coordinate system and reference frames.

```java
/**
 * Convert spherical coordinates to a Cartesian vector.
 * <p>
 * Transforms spherical coordinates (inclination and azimuth) into a 3D Cartesian vector
 * relative to the origin. The inclination angle is the polar angle measured from the positive Y-axis,
 * and the azimuth angle is measured from the positive X-axis in the horizontal plane.
 * </p>
 *
 * @param sphere a 2-element array where:
 *               - sphere[0] is the inclination (polar angle in radians, 0 to π)
 *               - sphere[1] is the azimuth (horizontal angle in radians, 0 to 2π)
 * @param radius the radius of the sphere, determining the distance from the origin
 * @return a Vector representing the 3D position in Cartesian coordinates
 */
```

---

## Checklist for Javadoc Review

When reviewing or writing javadoc, verify:

- [ ] **Class**: Does the javadoc explain what the class does and why it exists?
- [ ] **Purpose**: Is the purpose clear to someone unfamiliar with the code?
- [ ] **Methods**: Does each method explain what it does, not just how it does it?
- [ ] **Parameters**: Are parameter descriptions clear about constraints and expectations?
- [ ] **Returns**: Are return values clearly explained?
- [ ] **Links**: Are external references in `@see` tags with descriptive labels?
- [ ] **Format**: No spaces around `=` in href attributes?
- [ ] **Examples**: Are complex operations explained with examples?
- [ ] **Consistency**: Does the style match other similar classes in the project?

---

## References

- [Oracle Javadoc Style Guide](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Ollivanders2 Project Implementation Examples