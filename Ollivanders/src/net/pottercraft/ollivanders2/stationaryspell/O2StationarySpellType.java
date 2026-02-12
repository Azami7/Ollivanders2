package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of all available stationary spells in Ollivanders2.
 *
 * <p>Each stationary spell type encapsulates the implementation class and magic level required
 * to cast the spell. Stationary spells are area-of-effect spells that persist in a location
 * and affect players within their radius.</p>
 *
 * <p>This enum provides utility methods for looking up spells by name, checking if a player is
 * within a spell's radius, and retrieving spell metadata such as class and magic level.</p>
 *
 * @author Azami7
 * @version Ollivanders2
 */
public enum O2StationarySpellType {
    /**
     * Aliquam Floo: A Floo Network destination spell for stationary networks.
     *
     * @see ALIQUAM_FLOO
     */
    ALIQUAM_FLOO(ALIQUAM_FLOO.class, MagicLevel.EXPERT),
    /**
     * Cave Inimicum: A protective barrier spell that repels enemies.
     *
     * @see CAVE_INIMICUM
     */
    CAVE_INIMICUM(CAVE_INIMICUM.class, MagicLevel.NEWT),
    /**
     * Colloportus: Seals a passage magically. Requires expert level and can only be undone with Alohomora.
     *
     * @see COLLOPORTUS
     */
    COLLOPORTUS(COLLOPORTUS.class, MagicLevel.EXPERT),
    /**
     * Harmonia Nectere Passus: A spell that creates harmony and binding effects.
     *
     * @see HARMONIA_NECTERE_PASSUS
     */
    HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, MagicLevel.EXPERT),
    /**
     * Herbicide: Kills plants and vegetation in an area.
     *
     * @see HERBICIDE
     */
    HERBICIDE(HERBICIDE.class, MagicLevel.BEGINNER),
    /**
     * Horcrux: Creates or manages Horcrux objects.
     *
     * @see HORCRUX
     */
    HORCRUX(HORCRUX.class, MagicLevel.EXPERT),
    /**
     * Lumos Fervens: Creates intense light and heat in an area.
     *
     * @see LUMOS_FERVENS
     */
    LUMOS_FERVENS(LUMOS_FERVENS.class, MagicLevel.OWL),
    /**
     * Molliare: A cushioning spell that softens impacts.
     *
     * @see MOLLIARE
     */
    MOLLIARE(MOLLIARE.class, MagicLevel.OWL),
    /**
     * Muffliato: Mutes conversations within a specified area.
     *
     * @see MUFFLIATO
     */
    MUFFLIATO(MUFFLIATO.class, MagicLevel.NEWT),
    /**
     * Nullum Apparebit: Makes objects or effects invisible or undetectable.
     *
     * @see NULLUM_APPAREBIT
     */
    NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, MagicLevel.EXPERT),
    /**
     * Nullum Evanescunt: Causes objects or effects to vanish from an area.
     *
     * @see NULLUM_EVANESCUNT
     */
    NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, MagicLevel.EXPERT),
    /**
     * Protego Horribilis: A protection curse that defends against attacks.
     *
     * @see PROTEGO_HORRIBILIS
     */
    PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, MagicLevel.EXPERT),
    /**
     * Protego Maxima: A maximum-strength protective barrier spell.
     *
     * @see PROTEGO_MAXIMA
     */
    PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, MagicLevel.EXPERT),
    /**
     * Protego Totalum: A comprehensive protective spell covering an entire area.
     *
     * @see PROTEGO_TOTALUM
     */
    PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, MagicLevel.EXPERT),
    /**
     * Repello Muggleton: Repels non-magical people from a protected area.
     *
     * @see REPELLO_MUGGLETON
     */
    REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, MagicLevel.NEWT);

    /**
     * The implementation class for this stationary spell.
     *
     * <p>Used to instantiate new instances of this spell type and access spell-specific behavior.</p>
     */
    private final Class<?> className;

    /**
     * The magic level required to cast this stationary spell.
     *
     * <p>Used to determine compatibility with counter-spells and other magic interactions that depend
     * on spell power levels.</p>
     */
    private final MagicLevel level;

    /**
     * Constructs a new stationary spell type with the specified implementation class and magic level.
     *
     * @param className the implementation class for this spell type (not null)
     * @param level     the magic level required to cast this spell (not null)
     */
    O2StationarySpellType(@NotNull Class<?> className, @NotNull MagicLevel level) {
        this.className = className;
        this.level = level;
    }

    /**
     * Gets the implementation class for this stationary spell type.
     *
     * @return the Class object representing the implementation of this spell (never null)
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Gets the magic level required to cast this stationary spell.
     *
     * <p>The level is used to determine compatibility with counter-spells and defensive magic.
     * A counter-spell must be of equal or higher level to successfully dispel this spell.</p>
     *
     * @return the magic level of this spell (never null)
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Looks up a stationary spell type by its enum name (case-insensitive).
     *
     * <p>The method converts the input string to uppercase and attempts to match it against
     * the enum constant names. For example, "muffliato" or "MUFFLIATO" both resolve to
     * {@link #MUFFLIATO}. Invalid names gracefully return null without throwing an exception.</p>
     *
     * @param name the name of the spell to look up (not null)
     * @return the matching spell type, or null if no spell with that name exists
     */
    @Nullable
    public static O2StationarySpellType getStationarySpellTypeFromString(@NotNull String name) {
        O2StationarySpellType spellType = null;

        try {
            spellType = O2StationarySpellType.valueOf(name.toUpperCase());
        }
        catch (Exception e) {
            // do nothing, this is expected if they send a string that is not a valid spell
        }

        return spellType;
    }

    /**
     * Gets the human-readable name for this spell type.
     *
     * <p>Transforms the enum constant name by replacing underscores with spaces and capitalizing
     * the first letter of each word. For example, {@link #MUFFLIATO} becomes "Muffliato" and
     * {@link #PROTEGO_MAXIMA} becomes "Protego Maxima".</p>
     *
     * @return the formatted spell name (never null)
     */
    @NotNull
    public String getSpellName() {
        String spellTypeString = this.toString().toLowerCase();

        return Ollivanders2Common.firstLetterCapitalize(spellTypeString.replace("_", " "));
    }

    /**
     * Checks whether a player is within the radius of an active stationary spell of this type.
     *
     * <p>Retrieves all active stationary spells at the player's current location and checks if any
     * are of this spell type. A player may be affected by multiple spell types simultaneously.</p>
     *
     * @param player the player to check (not null)
     * @return true if the player is within the radius of at least one active spell of this type,
     *         false otherwise
     */
    public boolean isPlayerInsideStationarySpell(@NotNull Player player) {
        Location location = player.getLocation();

        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell.getSpellType() == this)
                return true;
        }

        return false;
    }
}