package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WEALTH;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the WEALTH effect.
 *
 * <p>WEALTH is a passive marker effect that periodically generates magical currency (Knuts, Sickles, and Galleons)
 * in the player's inventory. This test validates that the effect correctly triggers its coin-generation runnable
 * at the expected intervals and that the player receives coins as a result.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Initial coin generation - verifies coins appear after the effect is active for a sufficient duration</li>
 * <li>Repeated coin generation - verifies coins continue to be generated on subsequent intervals</li>
 * <li>Coin variety - checks that coins can be any of the three magical currency types (Knuts, Sickles, Galleons)</li>
 * </ul>
 *
 * @author Azami7
 * @see WEALTH for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public class WealthTest extends EffectTestSuper {
    /**
     * Create a WEALTH effect for testing.
     *
     * <p>Instantiates a new WEALTH effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new WEALTH effect targeting the specified player
     */
    @Override
    WEALTH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WEALTH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test that the WEALTH effect generates coins in the player's inventory.
     *
     * <p>This test validates two critical aspects of the WEALTH effect:</p>
     * <ol>
     * <li><strong>Initial Coin Generation:</strong>
     * <ul>
     * <li>Creates a player with a WEALTH effect (30 seconds duration)</li>
     * <li>Advances time by 10 seconds to allow the coin-generation runnable to execute</li>
     * <li>Verifies that at least one coin (of any type) has been added to the player's inventory</li>
     * </ul></li>
     * <li><strong>Repeated Coin Generation:</strong>
     * <ul>
     * <li>Advances time another 10 seconds to trigger the coin-generation runnable again</li>
     * <li>Verifies that the total coin count has increased, proving the effect generates coins repeatedly</li>
     * </ul></li>
     * </ol>
     *
     * <p>Note: The test checks all three coin types (Knuts, Sickles, Galleons) since the effect
     * may randomly generate any of them.</p>
     */
    @Override
    void checkEffectTest() {
        Ollivanders2.debug = true;

        PlayerMock target = mockServer.addPlayer();
        WEALTH wealth = (WEALTH) addEffect(target, Ollivanders2Common.ticksPerSecond * 30, false);
        // advance the server to register the effect and make sure it is running
        mockServer.getScheduler().performTicks(5);

        // advance 10 seconds to make sure the runnable executes the action
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 10);

        // check to see if the player now has at least one coin in their inventory
        // it could be any of the 3 coin types so we need to check all 3
        int knuts = TestCommon.amountInPlayerInventory(target, O2ItemType.KNUT.getMaterial(), O2ItemType.KNUT.getName());
        int sickles = TestCommon.amountInPlayerInventory(target, O2ItemType.SICKLE.getMaterial(), O2ItemType.SICKLE.getName());
        int galleons = TestCommon.amountInPlayerInventory(target, O2ItemType.GALLEON.getMaterial(), O2ItemType.GALLEON.getName());
        int totalCoins = knuts + sickles + galleons;
        assertTrue(totalCoins > 0, "Total coins in player inventory still 0 after WEALTH effect.");

        // advance 10 more seconds to make sure the runnable executes the action again
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 10);

        // check to see that the player has more coins than they did before
        knuts = TestCommon.amountInPlayerInventory(target, O2ItemType.KNUT.getMaterial(), O2ItemType.KNUT.getName());
        sickles = TestCommon.amountInPlayerInventory(target, O2ItemType.SICKLE.getMaterial(), O2ItemType.SICKLE.getName());
        galleons = TestCommon.amountInPlayerInventory(target, O2ItemType.GALLEON.getMaterial(), O2ItemType.GALLEON.getName());
        int newTotalCoins = knuts + sickles + galleons;
        assertTrue(newTotalCoins > totalCoins, "Total coins in player inventory did not increase after WEALTH effect.");
    }

    /**
     * Test WEALTH effect event handlers.
     *
     * <p>The WEALTH effect does not handle any events; all functionality is driven by scheduled runnables
     * that generate coins at regular intervals. This test is intentionally empty.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test WEALTH effect cleanup.
     *
     * <p>The WEALTH effect has no persistent state to clean up when removed. When the effect expires,
     * the player simply stops receiving coins. This test is intentionally empty.</p>
     */
    @Override
    void doRemoveTest() {}
}
