package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the PETRIFICUS_TOTALUS spell.
 *
 * <p>PETRIFICUS_TOTALUS is a full body-bind curse that temporarily paralyzes the target player's
 * entire body, preventing all movement and rotation. The spell can target any player without
 * restrictions, making it universally applicable as an immobilization curse.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Spell targeting and effect application (inherited from ImmobilizePlayerSuperTest)</li>
 * <li>Effect duration calculation (inherited)</li>
 * <li>Full immobilization behavior (inherited)</li>
 * <li>No target validation (any player can be targeted)</li>
 * <li>No additional effects beyond FULL_IMMOBILIZE</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.PETRIFICUS_TOTALUS for the spell implementation
 * @see ImmobilizePlayerTest for inherited test framework
 */
@Isolated
public class PetrificusTotalusTest extends ImmobilizePlayerTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PETRIFICUS_TOTALUS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PETRIFICUS_TOTALUS;
    }
}
