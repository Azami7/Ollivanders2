package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The stationary spells available in Ollivanders2. Each constant pairs a spell name with its implementation class and
 * the magic level required to cast it.
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
     * Protego Diabolica: Surrounds the caster in a blue flame that will harm anyone not in their house who crosses it
     */
    PROTEGO_DIABOLICA(PROTEGO_DIABOLICA.class, MagicLevel.EXPERT),
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
    REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, MagicLevel.NEWT),
    /**
     * Tranquillus: pacifies an area and prevents combat
     *
     * @see TRANQUILLUS
     */
    TRANQUILLUS(TRANQUILLUS.class, MagicLevel.NEWT);

    /**
     * The {@link O2StationarySpell} subclass that implements this spell.
     */
    private final Class<?> className;

    /**
     * The magic level required to cast this spell; also gates which counter-spells can dispel it.
     */
    private final MagicLevel level;

    /**
     * @param className the implementation class for this spell type
     * @param level     the magic level required to cast this spell
     */
    O2StationarySpellType(@NotNull Class<?> className, @NotNull MagicLevel level) {
        this.className = className;
        this.level = level;
    }

    /**
     * Get the implementation class for this spell type.
     *
     * @return the class implementing this spell
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the magic level required to cast this spell. A counter-spell must be of equal or higher level to dispel it.
     *
     * @return the magic level of this spell
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Look up a stationary spell type by its enum name, case-insensitively.
     *
     * @param name the spell name, e.g. "muffliato" or "MUFFLIATO"
     * @return the matching spell type, or null if the name matches no spell
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
     * Get the human-readable name for this spell type, e.g. {@link #PROTEGO_MAXIMA} becomes "Protego Maxima".
     *
     * @return the formatted spell name
     */
    @NotNull
    public String getSpellName() {
        String spellTypeString = this.toString().toLowerCase();

        return Ollivanders2Common.firstLetterCapitalize(spellTypeString.replace("_", " "));
    }

    /**
     * Check whether a player is within the radius of any stationary spell of this type.
     *
     * @param player the player to check
     * @return true if the player is inside at least one stationary spell of this type, false otherwise
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