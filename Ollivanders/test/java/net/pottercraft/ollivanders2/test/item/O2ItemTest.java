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
 *
 */
public class O2ItemTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
    }

    /**
     *
     */
    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    O2Item getItemHelper(O2ItemType itemType) {
        O2Item item = new O2Item(testPlugin, itemType);
        assertNotNull(item, "new O2Item() returned null");

        return item;
    }

    /**
     *
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
     *
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
     *
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

    /**
     *
     */
    @AfterEach
    void tearDown() {
        Ollivanders2.debug = false;
    }

    /**
     *
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
