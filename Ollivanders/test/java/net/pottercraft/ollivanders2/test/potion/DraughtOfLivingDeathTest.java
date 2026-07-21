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
 * Unit tests for {@link DRAUGHT_OF_LIVING_DEATH}.
 *
 * @see PotionTestSuper
 */
public class DraughtOfLivingDeathTest extends PotionTestSuper {
    /**
     * Select the potion and effect under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.DRAUGHT_OF_LIVING_DEATH;
        effectType = O2EffectType.SLEEPING;
    }

    /**
     * A player with the AWAKE effect is protected: drinking the Draught sends the awake message and does not apply
     * SLEEPING.
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
