package net.pottercraft.ollivanders2.listeners;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2OwlPost;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.Divination;
import net.pottercraft.ollivanders2.spell.O2SpellType;

import org.bukkit.Material;

import org.bukkit.block.Block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Primary listener for the plugin. Handles the events that are not owned by a specific subsystem: chat (spell
 * incantations and owl post), wand interactions, player join/quit/death, soul collection, invisibility targeting,
 * witch wand drops, and the spell journal.
 *
 * <p>Most handlers defer their work to the main thread by a short delay so that lower-priority listeners have a
 * chance to cancel the event first; each deferred action re-checks cancellation before acting.</p>
 */
public class OllivandersListener implements Listener {
    /**
     * Reference to the plugin
     */
    private final Ollivanders2 p;

    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * Number of ticks to delay thread start for
     */
    private static int threadDelay = (int) (Ollivanders2Common.ticksPerSecond * 0.5);

    /**
     * Constructor
     *
     * @param plugin reference to plugin
     */
    public OllivandersListener(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Reads the fastSpells config setting, which halves the delay used for all deferred event handling, and registers
     * this listener with the server.
     */
    public void onEnable() {
        // read config
        boolean useFastSpells = p.getConfig().getBoolean("fastSpells", false);

        if (useFastSpells)
            threadDelay = (int) (Ollivanders2Common.ticksPerSecond * 0.25);

        // register listeners
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Handles all actions related to players speaking.
     *
     * @param event the player chat event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        common.printDebugMessage("onPlayerChat: message = " + message, null, null, false);

        // Parse to see if they were casting a spell
        O2SpellType spellType = Ollivanders2API.getSpells().parseSpell(message);

        if (spellType != null) {
            updateRecipients(sender, spellType, event.getRecipients());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.isCancelled())
                       Ollivanders2API.getSpells().speakIncantation(sender, spellType, message.split(" "));
                }
            }.runTaskLater(p, threadDelay);
        }
    }

    /**
     * Limits who hears a spoken incantation. Non-verbal spells (apparating and divination) are heard by no one, all
     * other incantations only by players close enough to the caster.
     *
     * <p>Modifies the recipient set in place.</p>
     *
     * @param player     the player chatting
     * @param spellType  the spell type chatted
     * @param recipients the recipients for this chat, modified by this method
     */
    private void updateRecipients(@NotNull Player player, @NotNull O2SpellType spellType, @NotNull Set<Player> recipients) {
        // remove all recipients if this is not a "spoken" spell
        if (spellType == O2SpellType.APPARATE || Divination.divinationSpells.contains(spellType)) {
            recipients.clear();
            return;
        }

        // update chat recipients to only nearby players
        Ollivanders2Common.chatDropoff(recipients, Ollivanders2.chatDropoff, player.getLocation());
    }

    /**
     * Monitors chat events for the owl-post keywords and enacts the owl-post system
     *
     * @param event Chat event of type AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onOwlPost(@NotNull AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.toLowerCase().startsWith(Ollivanders2OwlPost.deliveryKeyword.toLowerCase()))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled())
                   Ollivanders2API.getOwlPost().processOwlPostRequest(event.getPlayer(), message);
            }
        }.runTaskLater(p, threadDelay);
    }

    /**
     * Handle events when player interacts with an item in their hand.
     *
     * @param event the player interact event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        common.printDebugMessage("onPlayerInteract: enter", null, null, false);

        //
        // A right or left click of the primary hand
        //
        if ((event.getHand() == EquipmentSlot.HAND)) {
            common.printDebugMessage("onPlayerInteract: primary hand right or left click", null, null, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    primaryHandInteractEvents(event);
                }
            }.runTaskLater(p, threadDelay);
        }
        else {
            common.printDebugMessage("onPlayerInteract: secondary hand action", null, null, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    secondaryHandInteractEvents(event);
                }
            }.runTaskLater(p, threadDelay);
        }
    }

    /**
     * Handle secondary hand interact events.
     *
     * @param event the event
     */
    private void secondaryHandInteractEvents(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        //
        // A right or left click that is not their primary hand
        //
        if (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
            if (Ollivanders2API.getItems().getWands().holdsWandInOff(player)) {
                Ollivanders2API.getSpells().rotateNonVerbalSpell(player, action);
            }
        }
    }

    /**
     * Handle primary hand interact events.
     *
     * @param event the interact event
     */
    private void primaryHandInteractEvents(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        //
        // A right or left click of the primary hand when holding a wand is used to make a magical action.
        //
        if ((Ollivanders2API.getItems().getWands().holdsWandInPrimary(player))) {
            common.printDebugMessage("primaryHandInteractEvents: player holding a wand", null, null, false);

            //
            // A left click of the primary hand is used to cast a spell
            //
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                common.printDebugMessage("primaryHandInteractEvents: left click action", null, null, false);
                Ollivanders2API.getSpells().castSpell(player);
            }

            //
            // A right click is used:
            // - to determine if the wand is the player's destined wand
            // - to brew a potion if they are holding a glass bottle in their off-hand and facing a cauldron
            //
            else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                common.printDebugMessage("primaryHandInteractEvents: right click action", null, null, false);

                rightClickWand(player, Ollivanders2Common.playerFacingBlockType(player, Material.WATER_CAULDRON));
            }
        }
    }

    /**
     * Handle a right click of a wand in the primary hand. A player facing a cauldron with an empty bottle in their off
     * hand is brewing a potion; anything else is waving the wand to see whether it is their destined wand.
     *
     * <p>Assumes: the player is holding a wand in their primary hand.</p>
     *
     * @param player   the player right clicking
     * @param cauldron the water cauldron the player is facing, null if they are not facing one
     */
    public void rightClickWand(@NotNull Player player, @Nullable Block cauldron) {
        if (cauldron != null && player.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE) {
            common.printDebugMessage("rightClickWand: brewing potion", null, null, false);
            Ollivanders2API.getPotions().brewPotion(player, cauldron);
            return;
        }

        common.printDebugMessage("rightClickWand: waving wand", null, null, false);
        Ollivanders2API.getItems().getWands().waveWand(player);
    }

    /**
     * Handle player joining event.
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());

        if (o2p == null) // new player
            o2p = p.getO2Player(player);
        else // existing player
        {
            // update player's display name in case it has changed
            if (!o2p.getPlayerName().equalsIgnoreCase(player.getName()))
                o2p.setPlayerName(player.getName());

            // add player to their house team
            Ollivanders2API.getHouses().addPlayerToHouseTeam(player);

            // do player join actions
            o2p.onJoin();
        }

        // re-add them to player list (in case they have changed from above actions)
        p.setO2Player(player, o2p);
    }

    /**
     * Handle player quitting.
     *
     * @param event the quit event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(event.getPlayer().getUniqueId());
        if (o2p == null)
            return;

        o2p.onQuit();
    }

    /**
     * Handle player death event.
     *
     * @param event the player death event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(event.getEntity().getUniqueId());

        if (o2p == null)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled()) {
                    o2p.onDeath();
                }
            }
        }.runTaskLater(p, threadDelay);
    }

    /**
     * This checks if a player kills another player, and if so, adds a soul to the attacking player
     *
     * @param event the entity damage event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!event.isCancelled()) {
                            if (damaged.getHealth() - event.getFinalDamage() <= 0)
                                p.getO2Player(attacker).addSoul();
                        }
                    }
                }.runTaskLater(p, threadDelay);
            }
        }
    }

    /**
     * Cancels any entity targeting of an invisible player, whether their invisibility comes from the Cloak of
     * Invisibility, a spell, or a potion.
     *
     * @param event the Entity Target Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCloakPlayer(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target instanceof Player) {
            if (O2PlayerCommon.hasPotionEffect((Player) target, PotionEffectType.INVISIBILITY)) {
                event.setCancelled(true);
                common.printDebugMessage("cloakPlayer: cancelling EntityTargetEvent", null, null, false);
            }
        }
    }

    /**
     * This drops a random wand when a witch dies
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#witch-wand-drop">https://github.com/Azami7/Ollivanders2/wiki/Configuration#witch-wand-drop</a>
     *
     * @param event the entity death event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWitchWandDrop(@NotNull EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.WITCH && Ollivanders2.enableWitchDrop) {
            ItemStack wand = Ollivanders2API.getItems().getWands().createRandomWand();
            if (wand == null) {
                common.printDebugMessage("OllivandersListener.witchWandDrop: wand is null", null, null, false);
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.isCancelled()) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), wand);
                    }
                }
            }.runTaskLater(p, threadDelay);
        }
    }

    /**
     * When a user holds their spell journal, replace it with an updated version of the book.
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#spell-journal">https://github.com/Azami7/Ollivanders2/wiki/Configuration#spell-journal</a>
     *
     * @param event the player item held event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpellJournalHold(@NotNull PlayerItemHeldEvent event) {
        // only run this if spellJournal is enabled
        if (!Ollivanders2.useSpellJournal)
            return;

        Player player = event.getPlayer();
        int slotIndex = event.getNewSlot();

        ItemStack heldItem = player.getInventory().getItem(slotIndex);
        if (heldItem != null && heldItem.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) heldItem.getItemMeta();
            if (bookMeta == null)
                return;

            String bookTitle = bookMeta.getTitle();
            if (bookTitle == null)
                return;

            if (bookMeta.getTitle().equalsIgnoreCase("Spell Journal")) {
                O2Player o2Player = p.getO2Player(player);
                ItemStack spellJournal = o2Player.getSpellJournal();

                player.getInventory().setItem(slotIndex, spellJournal);
            }
        }
    }

    /**
     * Get the number of ticks event handling is deferred by.
     *
     * @return the thread delay in ticks
     */
    public static int getThreadDelay() {
        return threadDelay;
    }
}