# Ollivanders2 Unit Testing Guidelines

This document outlines best practices for writing unit tests in the Ollivanders2 project.

## General Principles

### 1. Use MockBukkit for All Bukkit Plugin Tests
MockBukkit provides mock implementations of Bukkit classes, allowing tests without running a full Minecraft server.

```java
@BeforeEach
void setUp() {
    mockServer = MockBukkit.mock();
    testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/houses_config.yml"));
    testWorld = mockServer.addSimpleWorld("world");
}

@AfterEach
void tearDown() {
    MockBukkit.unmock();
}
```

### 2. Test Concrete Implementations, Not Abstract Classes
When testing abstract classes like `O2Book`, use real concrete implementations:
- ✅ `STANDARD_BOOK_OF_SPELLS_GRADE_1`
- ✅ `BOOK_OF_POTIONS`
- ❌ Creating test-only mock subclasses

This ensures you're testing real production code paths.

### 3. Consistent Test Structure
- Use `@BeforeEach` for setup
- Use `@AfterEach` for cleanup
- Keep test methods focused on a single concern
- Use `@Test` annotation on every test method

### 4. Descriptive Test Naming
Test method names should clearly describe what is being tested:
- ✅ `testBookItemIsWrittenBook()`
- ✅ `testSpellsTagContainsSpells()`
- ❌ `test1()`, `testBook()`, `testStuff()`

### 5. Add Javadoc Comments
Every test should have a comment explaining what it verifies:

```java
/**
 * Book item is the correct type
 */
@Test
void testBookItemIsWrittenBook() {
    // test code
}
```

### 6. Group Related Tests
Use comment headers to organize tests by category:

```java
/**
 * Basic Constructor & Initialization Tests
 */
@Test
void testBookCreation() { }

@Test
void testPotionBookCreation() { }

/**
 * Getter Method Tests
 */
@Test
void testGetTitle() { }
```

### 7. Avoid Magic Numbers and Hardcoded Values
Tests should not contain hardcoded values that may change when implementation changes. Add getter methods or use dynamic values.

**❌ Bad - Uses magic number:**
```java
@Test
void testBookContainsAllSpells() {
    ACHIEVEMENTS_IN_CHARMING book = new ACHIEVEMENTS_IN_CHARMING(testPlugin);
    BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();

    // If another spell is added, this test breaks
    assertTrue(meta.getPageCount() >= 3,
        "Book should have enough pages for 3 spells");
}
```

**✅ Good - Uses dynamic value:**
```java
@Test
void testBookContainsAllSpells() {
    ACHIEVEMENTS_IN_CHARMING book = new ACHIEVEMENTS_IN_CHARMING(testPlugin);
    BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
    int spellCount = book.getNumberOfSpells();

    // Test adapts automatically when spells are added/removed
    assertTrue(meta.getPageCount() >= spellCount,
        "Book should have enough pages for " + spellCount + " spells");
}
```

**When to add getter methods:**
- If a test needs to validate against an internal collection size
- If a value might change during normal development
- If the same value is needed across multiple tests

**When hardcoded values are acceptable:**
- External constraints that won't change (e.g., Minecraft's 50 page limit)
- Expected constants defined in specifications

## Assertion Best Practices

### 8. Prefer Specific Assertions
Use the most specific assertion available:

```java
// ✅ Good
assertEquals(expected, actual);
assertEquals(Material.WRITTEN_BOOK, bookItem.getType());

// ❌ Avoid
assertTrue(expected.equals(actual));
assertTrue(bookItem.getType() == Material.WRITTEN_BOOK);
```

### 9. Use Modern JUnit 5 Assertions

```java
// ✅ Good - Modern JUnit 5
assertInstanceOf(BookMeta.class, bookItem.getItemMeta());

// ❌ Avoid - Old style
assertTrue(bookItem.getItemMeta() instanceof BookMeta);
```

### 10. Test Null and Empty Cases

```java
assertNotNull(title);
assertFalse(title.isEmpty());
```

## Handling Deprecation

### 11. Suppress Deprecation Warnings in Tests
When testing against deprecated APIs (especially MockBukkit implementations), use `@SuppressWarnings`:

```java
@Test
@SuppressWarnings("deprecation")
void testBookHasTitlePage() {
    String firstPage = meta.getPage(1); // deprecated but functional in MockBukkit
    assertTrue(firstPage.contains("by"));
}
```

## Comprehensive Test Coverage

### 12. What to Test
For each class, ensure coverage of:

1. **Constructor & Initialization**
   - Object creation succeeds
   - Initial state is correct

2. **Getter Methods**
   - All getters return expected values
   - Values match the underlying data source

3. **Object Creation & Caching**
   - Objects are created correctly
   - Caching works (same instance returned)
   - Correct types/materials

4. **Metadata**
   - Metadata exists
   - All required fields are set
   - Values are correct

5. **Persistent Data (NBT Tags)**
   - NBT tags exist
   - Tag keys are correct
   - Tag values contain expected data

6. **Content Structure**
   - Content follows expected format
   - All required sections present
   - Special cases (opening pages, TOC, etc.)

7. **Edge Cases**
   - Limits are respected (e.g., page counts)
   - Empty/null handling

## Configuration

### 13. Use Realistic Test Configs
Load actual configuration files from `test/resources`:

```java
testPlugin = MockBukkit.loadWithConfig(
    Ollivanders2.class,
    new File("Ollivanders/test/resources/houses_config.yml")
);
```

## Example Test Class Structure

```java
package book;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {
    static ServerMock mockServer;
    static Ollivanders2 testPlugin;
    static World testWorld;

    @BeforeEach
    void setUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(
            Ollivanders2.class,
            new File("Ollivanders/test/resources/houses_config.yml")
        );
        testWorld = mockServer.addSimpleWorld("world");
    }

    /**
     * Group 1: Basic Tests
     */
    @Test
    void testCreation() {
        // Test object creation
    }

    /**
     * Group 2: Getter Tests
     */
    @Test
    void testGetValue() {
        // Test getter methods
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }
}
```

## Common Patterns

### Testing ItemStacks
```java
ItemStack item = getTestItem();
assertNotNull(item);
assertEquals(Material.WRITTEN_BOOK, item.getType());
assertEquals(1, item.getAmount());
assertTrue(item.hasItemMeta());
```

### Testing Persistent Data
```java
PersistentDataContainer container = meta.getPersistentDataContainer();
assertTrue(container.has(keyName, PersistentDataType.STRING));
String value = container.get(keyName, PersistentDataType.STRING);
assertEquals("EXPECTED_VALUE", value);
```

### Testing Book Content
```java
@SuppressWarnings("deprecation")
@Test
void testBookContent() {
    BookMeta meta = (BookMeta) book.getBookItem().getItemMeta();
    assertTrue(meta.getPageCount() > 0);
    String page = meta.getPage(1);
    assertTrue(page.contains("expected content"));
}
```

## Reference Implementation
See `test/java/book/O2BookTest.java` for a complete example following all these guidelines.