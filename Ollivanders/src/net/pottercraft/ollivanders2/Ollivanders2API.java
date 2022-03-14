package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells;
import org.jetbrains.annotations.NotNull;

/**
 * API for allowing other plugins to interact with Ollivanders.
 */
public class Ollivanders2API
{
    /**
     * Common player functions
     */
    public static O2PlayerCommon playerCommon;

    /**
     * Common functions
     */
    public static Ollivanders2Common common;

    /**
     * Entity common functions
     */
    public static EntityCommon entityCommon;

    static void init(@NotNull Ollivanders2 p)
    {
        if (common == null)
            common = new Ollivanders2Common(p);
        if (playerCommon == null)
            playerCommon = new O2PlayerCommon(p);
        if (entityCommon == null)
            entityCommon = new EntityCommon(p);
    }

    /**
     * Get the house management object
     *
     * @return houses management object
     */
    @NotNull
    public static O2Houses getHouses()
    {
        return Ollivanders2.houses;
    }

    /**
     * Get the player management object.
     *
     * @return the player management object
     */
    @NotNull
    public static O2Players getPlayers()
    {
        return Ollivanders2.players;
    }

    /**
     * Get the books management object.
     *
     * @return the book management object
     */
    @NotNull
    public static O2Books getBooks()
    {
        return Ollivanders2.books;
    }

    /**
     * Get the spells management object.
     *
     * @return the spells management object
     */
    @NotNull
    public static O2Spells getSpells()
    {
        return Ollivanders2.spells;
    }

    /**
     * Get the potions management object.
     *
     * @return the potions management object
     */
    @NotNull
    public static O2Potions getPotions()
    {
        return Ollivanders2.potions;
    }

    /**
     * Get the stationary spells management object.
     *
     * @return the stationary spells management object
     */
    @NotNull
    public static O2StationarySpells getStationarySpells()
    {
        return Ollivanders2.stationarySpells;
    }

    /**
     * Get the prophecy management object.
     *
     * @return the prophecy management object
     */
    @NotNull
    public static O2Prophecies getProphecies()
    {
        return Ollivanders2.prophecies;
    }

    /**
     * Get the item management object.
     *
     * @return the item management object
     */
    @NotNull
    public static O2Items getItems()
    {
        return Ollivanders2.items;
    }

    /**
     * Get the owl post management object
     *
     * @return the owlPost management object
     */
    @NotNull
    public static Ollivanders2OwlPost getOwlPost()
    {
        return Ollivanders2.owlPost;
    }
}
