package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents allowable stationary spells.
 */
public enum O2StationarySpellType
{
    ALIQUAM_FLOO(ALIQUAM_FLOO.class, MagicLevel.EXPERT),
    COLLOPORTUS(COLLOPORTUS.class, MagicLevel.EXPERT), // colloportus can only be undone with alohomora so we level to max to ensure other spells cannot undo it
    HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, MagicLevel.EXPERT),
    HORCRUX(HORCRUX.class, MagicLevel.EXPERT),
    MOLLIARE(MOLLIARE.class, MagicLevel.OWL),
    MUFFLIATO(MUFFLIATO.class, MagicLevel.NEWT),
    NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, MagicLevel.EXPERT),
    NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, MagicLevel.EXPERT),
    PROTEGO(PROTEGO.class, MagicLevel.OWL),
    PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, MagicLevel.EXPERT),
    PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, MagicLevel.EXPERT),
    PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, MagicLevel.EXPERT),
    REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, MagicLevel.NEWT);

    /**
     * The class of stationary spell this creates
     */
    private final Class<?> className;

    /**
     * The level of this stationary spell, for use with counter-spells
     */
    private final MagicLevel level;

    /**
     * Constructor
     *
     * @param className the class this type represents
     * @param level     the level of this stationary spell
     */
    O2StationarySpellType(@NotNull Class<?> className, @NotNull MagicLevel level)
    {
        this.className = className;
        this.level = level;
    }

    /**
     * Get the class for this stationary spell
     *
     * @return the classname for this spell
     */
    @NotNull
    public Class<?> getClassName()
    {
        return className;
    }

    /**
     * Get the level of magic for this spell, for counter-spells
     *
     * @return the level of this spell
     */
    @NotNull
    public MagicLevel getLevel()
    {
        return level;
    }

    /**
     * Get the spell by name
     *
     * @param name the name of the spell
     * @return the spell type or null if not found
     */
    @Nullable
    public static O2StationarySpellType getStationarySpellTypeFromString(@NotNull String name)
    {
        O2StationarySpellType spellType = null;

        try
        {
            spellType = O2StationarySpellType.valueOf(name);
        }
        catch (Exception e)
        {
            // do nothing, this is expected if they send a string that is not a valid spell
        }

        return spellType;
    }

    /**
     * Get the spell name for this spell type.
     *
     * @return the spell name for this spell type.
     */
    @NotNull
    public String getSpellName()
    {
        String spellTypeString = this.toString().toLowerCase();

        return Ollivanders2Common.firstLetterCapitalize(spellTypeString.replace("_", " "));
    }

    /**
     * Is a player inside the radius of an instance of this stationary spell type?
     *
     * @param player the player to check
     * @return true if the player is within the radius of a stationary spell of this type, false otherwise
     */
    public boolean isPlayerInsideStationary(Player player)
    {
        Location location = player.getLocation();

        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location))
        {
            if (stationarySpell.getSpellType() == this)
                return true;
        }

        return false;
    }
}