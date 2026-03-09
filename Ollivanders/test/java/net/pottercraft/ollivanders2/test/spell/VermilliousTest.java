package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the VERMILLIOUS red sparks emergency signal charm.
 *
 * <p>Provides test coverage for the Red Sparks spell including:</p>
 * <ul>
 * <li><strong>Sound Verification:</strong> Verifies the spell plays a firework sound on cast</li>
 * <li><strong>Projectile Travel:</strong> Confirms the spell projectile travels to the target</li>
 * <li><strong>No Damage:</strong> Validates that the spell does not damage entities (non-damaging variant)</li>
 * </ul>
 *
 * <p>Inherits core projectile and sound testing from {@link SparksTest}.</p>
 *
 * @author Azami7
 */
@Isolated
public class VermilliousTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERMILLIOUS;
    }
}
