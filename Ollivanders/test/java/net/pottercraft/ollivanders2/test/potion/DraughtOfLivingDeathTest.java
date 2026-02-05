package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.DRAUGHT_OF_LIVING_DEATH;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the Draught of Living Death potion effect.
 *
 * <p>Verifies that the Draught of Living Death potion correctly applies the SLEEPING effect to players
 * when consumed. Tests both the player feedback message and the effect application. Additionally,
 * tests the special behavior where players with the AWAKE effect are protected from the sleep effect.</p>
 *
 * @see PotionTestSuper for the base test infrastructure
 */
public class DraughtOfLivingDeathTest extends PotionTestSuper {
    /**
     * Set up the test by specifying the potion type to test.
     *
     * <p>Initializes the test to use the DRAUGHT_OF_LIVING_DEATH potion type and the SLEEPING effect.
     * This method is called before each test to ensure the correct potion is being tested by the
     * inherited drinkTest() method.</p>
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.DRAUGHT_OF_LIVING_DEATH;
        effectType = O2EffectType.SLEEPING;
    }

    /**
     * Test that the AWAKE effect prevents the Draught of Living Death from inducing sleep.
     *
     * <p>Verifies the special interaction between the Draught of Living Death and the AWAKE effect.
     * When a player with the AWAKE effect (immunity to sleep) drinks the Draught, the potion should
     * not apply the SLEEPING effect. Instead, the player should receive a special message indicating
     * they are protected from the sleep effect due to their awake state.</p>
     *
     * <p>Test procedure:</p>
     * <ol>
     *   <li>Create a mock player and add the AWAKE effect to them</li>
     *   <li>Have the player drink the Draught of Living Death</li>
     *   <li>Verify the player receives the special AWAKE effect message</li>
     *   <li>Verify that the SLEEPING effect was NOT applied to the player</li>
     * </ol>
     */
    @Test
    void awakeEffectTest() {
        assertNotNull(potionType, "potionType not set");
        DRAUGHT_OF_LIVING_DEATH potion = new DRAUGHT_OF_LIVING_DEATH(testPlugin);
        assertNotNull(potion, "Failed to get O2Potion");

        // add awake effect to player
        PlayerMock player = mockServer.addPlayer();
        AWAKE awake = new AWAKE(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(awake);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), awake.effectType));

        potion.drink(player);

        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getAwakeEffectMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not receive expected message after drinking potion");

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType), "SLEEPING effect added when player has AWAKE effect.");
    }
}
