package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WEALTH;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link WEALTH}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public class WealthTest extends EffectTestSuper {
    @Override
    WEALTH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new WEALTH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * WEALTH adds magical coins (Knuts, Sickles, or Galleons) to the target's inventory, and keeps adding more on each
     * interval.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        addEffect(target, Ollivanders2Common.ticksPerSecond * 30, false);
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

    @Override
    void eventHandlerTests() {}

    @Override
    void doRemoveTest() {}
}
