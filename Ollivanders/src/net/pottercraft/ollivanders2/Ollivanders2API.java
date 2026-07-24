package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.block.O2Blocks;
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
 * Public API for external plugins to interact with the Ollivanders2 plugin.
 *
 * <p>Every accessor here is only valid while Ollivanders2 is enabled and throws {@link IllegalStateException}
 * otherwise, so a plugin that uses this API should declare Ollivanders2 as a dependency in its plugin.yml to
 * guarantee load order.</p>
 *
 * <p>The objects returned are the plugin's live managers and are not thread-safe. Call them from the main server
 * thread only.</p>
 *
 * @author Azami7
 */
public final class Ollivanders2API {
    // TODO: make the common helper fields below private with accessors like the managers have. They are public and
    // mutable, so any plugin on the server can replace or null them out. This is a breaking change for anyone already
    // reading the fields, so it needs to wait for a major version.

    /**
     * Common player functions. Only set while the plugin is enabled.
     */
    public static O2PlayerCommon playerCommon;

    /**
     * Common functions shared across the whole plugin. Only set while the plugin is enabled.
     */
    public static Ollivanders2Common common;

    /**
     * Common entity functions. Only set while the plugin is enabled.
     */
    public static EntityCommon entityCommon;

    /**
     * Not instantiable, all members are static.
     */
    private Ollivanders2API() { }

    /**
     * Set up the common helpers. Must run before anything else in plugin startup because the managers and the data
     * access layer log through {@link #common}.
     *
     * @param p a callback to the plugin
     */
    static void init(@NotNull Ollivanders2 p) {
        // assign unconditionally so a reloaded plugin does not keep helpers pointing at the previous instance
        common = new Ollivanders2Common(p);
        playerCommon = new O2PlayerCommon(p);
        entityCommon = new EntityCommon(p);
    }

    /**
     * Drop every reference to the plugin's managers and helpers so that accessing the API after the plugin has been
     * disabled fails rather than handing out objects belonging to a dead plugin instance.
     *
     * <p>Must run at the end of plugin shutdown, after every manager has had its own shutdown.</p>
     */
    static void shutdown() {
        common = null;
        playerCommon = null;
        entityCommon = null;

        Ollivanders2.houses = null;
        Ollivanders2.players = null;
        Ollivanders2.books = null;
        Ollivanders2.spells = null;
        Ollivanders2.potions = null;
        Ollivanders2.stationarySpells = null;
        Ollivanders2.prophecies = null;
        Ollivanders2.items = null;
        Ollivanders2.owlPost = null;
        Ollivanders2.blocks = null;
    }

    /**
     * Check that a manager is available before handing it to a caller.
     *
     * @param manager the manager to check
     * @param name    the name of the manager, used in the failure message
     * @param <T>     the manager type
     * @return the manager
     * @throws IllegalStateException if the plugin is not enabled and the manager therefore does not exist
     */
    @NotNull
    private static <T> T requireEnabled(T manager, @NotNull String name) {
        if (manager == null)
            throw new IllegalStateException("Ollivanders2 is not enabled, the " + name + " manager is not available");

        return manager;
    }

    /**
     * Get the house management object.
     *
     * @return the house management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Houses getHouses() {
        return requireEnabled(Ollivanders2.houses, "houses");
    }

    /**
     * Get the player management object.
     *
     * @return the player management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Players getPlayers() {
        return requireEnabled(Ollivanders2.players, "players");
    }

    /**
     * Get the books management object.
     *
     * @return the book management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Books getBooks() {
        return requireEnabled(Ollivanders2.books, "books");
    }

    /**
     * Get the spells management object.
     *
     * @return the spells management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Spells getSpells() {
        return requireEnabled(Ollivanders2.spells, "spells");
    }

    /**
     * Get the potions management object.
     *
     * @return the potions management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Potions getPotions() {
        return requireEnabled(Ollivanders2.potions, "potions");
    }

    /**
     * Get the stationary spells management object.
     *
     * @return the stationary spells management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2StationarySpells getStationarySpells() {
        return requireEnabled(Ollivanders2.stationarySpells, "stationary spells");
    }

    /**
     * Get the prophecy management object.
     *
     * @return the prophecy management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Prophecies getProphecies() {
        return requireEnabled(Ollivanders2.prophecies, "prophecies");
    }

    /**
     * Get the item management object.
     *
     * @return the item management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Items getItems() {
        return requireEnabled(Ollivanders2.items, "items");
    }

    /**
     * Get the owl post management object.
     *
     * @return the owl post management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static Ollivanders2OwlPost getOwlPost() {
        return requireEnabled(Ollivanders2.owlPost, "owl post");
    }

    /**
     * Get the global block management object.
     *
     * @return the block management object
     * @throws IllegalStateException if the plugin is not enabled
     */
    @NotNull
    public static O2Blocks getBlocks() {
        return requireEnabled(Ollivanders2.blocks, "blocks");
    }
}
