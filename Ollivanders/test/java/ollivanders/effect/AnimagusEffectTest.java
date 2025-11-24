package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.EntityType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for ANIMAGUS_EFFECT
 * TODO: mock out libsDisguises so we can test things that require it
 */
public class AnimagusEffectTest extends PermanentEffectTestSuper {
    ANIMAGUS_EFFECT createEffect(int durationInTicks, boolean isPermanent) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());

        // we need to make the player an animagus so that the effect can work
        assertNotNull(o2p, "Ollivanders2API.getPlayers().getPlayer(target.getUniqueId()) is null");
        o2p.setAnimagusForm(EntityType.COW);

        return new ANIMAGUS_EFFECT(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    void checkEffectTest() { }

    /**
     * doRemove() in AnimagusEffect does not do anything
     */
    void doRemoveTest() {}

    void eventHandlerTests() {
        // doOnPlayerInteractEvent

        // doOnPlayerToggleFlightEvent

        // doOnPlayerPickupItemEvent

        // doOnPlayerItemHeldEvent

        // doOnPlayerItemConsumeEvent

        // doOnPlayerDropItemEvent
    }
}
