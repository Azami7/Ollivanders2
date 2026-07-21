package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Astrology divination spell, divining the future from the relative positions of celestial bodies.
 * <p>
 * Astrology uses the positions of the sun, moon, and planets to predict future events or gain insight into a target's
 * personality, relationships, and health. This spell produces an {@link O2DivinationType#ASTROLOGY} prophecy about its
 * target.
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Astrology">Harry Potter Wiki - Astrology</a>
 */
public final class ASTROLOGIA extends Divination {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ASTROLOGIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ASTROLOGIA;
        divinationType = O2DivinationType.ASTROLOGY;

        flavorText = new ArrayList<>() {{
            add("\"Professor Trelawney did astrology with us! Mars causes accidents and burns and things like that, and when it makes an angle to Saturn, like now, that means people need to be extra careful when handling hot things.\" -Parvati Patil");
            add("\"My dears, it is time for use to consider the stars.\" -Sybill Trelawney");
            add("\"The movements of the planets and the mysterious portents they reveal only to those who understand the steps of the celestial dance. Human destiny may be deciphered by the planetary rays, which intermingle...\" -Sybill Trelawny");
        }};

        text = "Through the study of the position of celestial bodies, one may divine future events or gain insight in to the health or relationships of others.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ASTROLOGIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ASTROLOGIA;
        divinationType = O2DivinationType.ASTROLOGY;

        initSpell();
    }
}
