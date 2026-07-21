package net.pottercraft.ollivanders2.test.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.O2Item;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link O2Item}.
 */
public class O2ItemTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, reloaded before each test method for isolation.
     */
    Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    O2Item getItemHelper(O2ItemType itemType) {
        O2Item item = new O2Item(testPlugin, itemType);
        assertNotNull(item, "new O2Item() returned null");

        return item;
    }

    /**
     * getItem() returns a stack of the requested amount.
     */
    @Test
    void getItemTest() {
        O2Item item = getItemHelper(O2ItemType.DEW_DROP);

        int count = 1;
        ItemStack itemStack = item.getItem(count);
        assertNotNull(itemStack, "item.getItem(1) returned null");
        assertEquals(count, itemStack.getAmount(), "itemStack.getAmount() amount unexpected");

        count = 5;
        itemStack = item.getItem(count);
        assertNotNull(itemStack, "item.getItem() returned null");
        assertEquals(count, itemStack.getAmount(), "itemStack.getAmount() amount unexpected");
    }

    /**
     * getType() returns the type the item was created with.
     */
    @Test
    void getItemTypeTest() {
        O2ItemType itemType = O2ItemType.DEW_DROP;
        O2Item item = getItemHelper(itemType);
        assertNotNull(item);
        assertEquals(itemType, item.getType(), "ItemType was wrong");

        itemType = O2ItemType.BROOMSTICK;
        item = getItemHelper(itemType);
        assertNotNull(item);
        assertEquals(itemType, item.getType(), "ItemType for 2nd item was wrong");
    }

    /**
     * The static O2Item.getItemType(ItemStack) recovers the type from a created item stack.
     */
    @Test
    void getStaticItemTypeTest() {
        O2ItemType itemType = O2ItemType.DEW_DROP;
        O2Item item = getItemHelper(itemType);
        assertNotNull(item);
        ItemStack itemStack = item.getItem(1);
        assertNotNull(itemStack);
        assertEquals(itemType, O2Item.getItemType(itemStack), "O2Item.getItemType(itemStack) did not return expected type");

        itemType = O2ItemType.BROOMSTICK;
        item = getItemHelper(itemType);
        assertNotNull(item);
        itemStack = item.getItem(1);
        assertNotNull(itemStack);
        assertEquals(itemType, O2Item.getItemType(itemStack), "O2Item.getItemType(itemStack) did not return expected type");
    }

    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
