package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the VERMILLIOUS_TRIA most powerful red sparks damage charm.
 *
 * <p>Provides test coverage for the strongest Red Sparks spell including:</p>
 * <ul>
 * <li><strong>Sound Verification:</strong> Verifies the spell plays a firework sound on cast</li>
 * <li><strong>Projectile Travel:</strong> Confirms the spell projectile travels to the target</li>
 * <li><strong>Damage Dealing:</strong> Validates that the spell damages entities upon impact with high damage modifier</li>
 * </ul>
 *
 * <p>Inherits core projectile and damage testing from {@link SparksTest}.</p>
 *
 * @author Azami7
 */
@Isolated
public class VermilliousTriaTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERMILLIOUS_TRIA;
    }


}
