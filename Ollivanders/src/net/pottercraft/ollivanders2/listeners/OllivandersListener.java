package net.pottercraft.ollivanders2.listeners;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2OwlPost;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerFoundWandEvent;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerNotDestinedWandEvent;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.APPARATE;
import net.pottercraft.ollivanders2.spell.Divination;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.spell.AMATO_ANIMO_ANIMATO_ANIMAGUS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2SplashPotion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Effect;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Primary listener for events from the plugin
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
    public static int threadDelay = (int) (Ollivanders2Common.ticksPerSecond * 0.5);

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
     * Listener set up on plugin enable.
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        common.printDebugMessage("onPlayerChat: message = " + message, null, null, false);

        //
        // Parse to see if they were casting a spell
        //
        O2SpellType spellType = parseSpell(message);

        if (spellType != null) {
            //
            // Handle removing recipients from chat
            //
            updateRecipients(sender, spellType, event.getRecipients());

            //
            // Handle spell casting
            //
            doSpellCasting(sender, spellType, message.split(" "));
        }

        common.printDebugMessage("onPlayerChat: return", null, null, false);
    }

    /**
     * Parse a spell from a chat
     *
     * @param message the words chatted by the player
     * @return a spell type if found, null otherwise
     */
    @Nullable
    private O2SpellType parseSpell(@NotNull String message) {
        O2SpellType spellType;

        // first try all the words as one spell name
        spellType = Ollivanders2API.getSpells().getSpellTypeByName(message);

        if (spellType != null)
            return spellType;

        String[] words = message.split(" ");

        StringBuilder spellName = new StringBuilder();
        for (String word : words) {
            spellName.append(word);
            spellType = Ollivanders2API.getSpells().getSpellTypeByName(spellName.toString());

            // we found a matching spell
            if (spellType != null)
                break;

            spellName.append(" ");
        }

        if (spellType != null) {
            common.printDebugMessage("Spell is " + spellType, null, null, false);
        }
        else {
            common.printDebugMessage("No spell found", null, null, false);
        }

        return spellType;
    }

    /**
     * Handle updating chat recipients
     *
     * @param player     the player chatting
     * @param spellType  the spell type chatted
     * @param recipients the recipients for this chat
     */
    private void updateRecipients(@NotNull Player player, @NotNull O2SpellType spellType, @NotNull Set<Player> recipients) {
        // remove all recipients if this is not a "spoken" spell
        if (spellType == O2SpellType.APPARATE || Divination.divinationSpells.contains(spellType)) {
            recipients.clear();
            return;
        }

        Ollivanders2Common.chatDropoff(recipients, Ollivanders2.chatDropoff, player.getLocation());
    }

    /**
     * Handle when a player says a spell
     *
     * @param player    the player casting the spell
     * @param spellType the spell cast
     * @param words     the args for this spell, if relevant
     */
    private void doSpellCasting(@NotNull Player player, @NotNull O2SpellType spellType, @NotNull String[] words) {
        if (p.canCast(player, spellType, true)) {
            if (Ollivanders2.bookLearning && p.getO2Player(player).getSpellCount(spellType) < 1) {
                // if bookLearning is set to true then spell count must be > 0 to cast this spell
                common.printDebugMessage("doSpellCasting: bookLearning enforced", null, null, false);
                player.sendMessage(Ollivanders2.chatColor + "You do not know that spell yet. To learn a spell, you'll need to read a book about that spell.");

                return;
            }

            boolean castSuccess = true;

            if (!Ollivanders2API.getItems().getWands().holdsWand(player)) {
                // if they are not holding their destined wand, casting success is reduced
                common.printDebugMessage("doSpellCasting: player not holding destined wand", null, null, false);

                int uses = p.getO2Player(player).getSpellCount(spellType);
                castSuccess = Math.random() < (1.0 - (100.0 / (uses + 101.0)));
            }

            // wandless spells
            if (O2Spells.wandlessSpells.contains(spellType)) {
                common.printDebugMessage("doSpellCasting: allow wandless casting of " + spellType, null, null, false);
                castSuccess = true;
            }

            if (castSuccess) {
                common.printDebugMessage("doSpellCasting: begin casting " + spellType, null, null, false);

                if (spellType == O2SpellType.APPARATE)
                    addSpellProjectile(player, new APPARATE(p, player, 1.0, words));
                else if (spellType == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
                    addSpellProjectile(player, new AMATO_ANIMO_ANIMATO_ANIMAGUS(p, player, 1.0));
                else if (Divination.divinationSpells.contains(spellType)) {
                    divine(spellType, player, words);
                }
                else {
                    O2Player o2p = p.getO2Player(player);
                    o2p.setWandSpell(spellType);
                }
            }
        }
        else {
            common.printDebugMessage("doSpellCasting: Either no spell cast attempted or not allowed to cast", null, null, false);
        }
    }

    /**
     * Add the spell and increment cast count
     *
     * @param player the player who cast the spell
     * @param spell  the spell cast
     */
    private void addSpellProjectile(@NotNull Player player, @NotNull O2Spell spell) {
        p.addProjectile(spell);

        p.incrementSpellCount(player, spell.spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FAST_LEARNING))
            p.incrementSpellCount(player, spell.spellType);
    }

    /**
     * Monitors chat events for the owl-post keywords and enacts the owl-post system
     *
     * @param event Chat event of type AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void owlPost(@NotNull AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.toLowerCase().startsWith(Ollivanders2OwlPost.deliveryKeyword.toLowerCase()))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Ollivanders2API.getOwlPost().processOwlPostRequest(event.getPlayer(), message);
            }
        }.runTaskLater(p, threadDelay);
    }

    /**
     * This creates the spell projectile.
     *
     * @param player the player that cast the spell
     * @param name   the name of the spell cast
     * @param wandC  the wand check value for the held wand
     */
    @Nullable
    private O2Spell createSpellProjectile(@NotNull Player player, @NotNull O2SpellType name, double wandC) {
        common.printDebugMessage("OllivandersListener.createSpellProjectile: enter", null, null, false);

        //spells go here, using any of the three types of magic
        String spellClass = "net.pottercraft.ollivanders2.spell." + name;

        Constructor<?> c;
        try {
            c = Class.forName(spellClass).getConstructor(Ollivanders2.class, Player.class, Double.class);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell constructor", e, null, true);
            return null;
        }

        O2Spell spell;

        try {
            spell = (O2Spell) c.newInstance(p, player, wandC);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell", e, null, true);
            return null;
        }

        return spell;
    }

    /**
     * Action by player to cast a spell
     *
     * @param player the player casting the spell
     */
    private void castSpell(@NotNull Player player) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null) {
            common.printDebugMessage("Unable to find o2player casting spell.", null, null, false);
            return;
        }

        O2SpellType spellType = o2p.getWandSpell();

        // if no spell set, check to see if they have a master spell
        boolean nonverbal = false;
        if (spellType == null && Ollivanders2.enableNonVerbalSpellCasting) {
            spellType = o2p.getMasterSpell();
            nonverbal = true;
        }

        if (spellType != null) {
            double wandCheck;
            boolean playerHoldsWand = Ollivanders2API.getItems().getWands().holdsWand(player, EquipmentSlot.HAND);
            if (playerHoldsWand) {
                common.printDebugMessage("OllivandersListener:castSpell: player holds a wand in their primary hand", null, null, false);
                wandCheck = Ollivanders2API.playerCommon.wandCheck(player, EquipmentSlot.HAND);
            }
            else {
                common.printDebugMessage("OllivandersListener:castSpell: player does not hold a wand in their primary hand", null, null, false);
                return;
            }

            O2Spell castSpell = createSpellProjectile(player, spellType, wandCheck);
            if (castSpell == null) {
                return;
            }

            addSpellProjectile(player, castSpell);

            o2p.setSpellRecentCastTime(spellType);
            if (!nonverbal) {
                o2p.setPriorIncantatem(spellType);
            }

            common.printDebugMessage("OllivandersListener:castSpell: " + player.getName() + " cast " + castSpell.getName(), null, null, false);

            o2p.setWandSpell(null);
        }
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
                    //if (!event.isCancelled())
                    primaryHandInteractEvents(event);
                }
            }.runTaskLater(p, threadDelay);
        }
        else {
            common.printDebugMessage("onPlayerInteract: secondary hand action", null, null, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    //if (!event.isCancelled())
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
            if (Ollivanders2API.getItems().getWands().holdsWand(player, EquipmentSlot.OFF_HAND)) {
                rotateNonVerbalSpell(player, action);
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
        if ((Ollivanders2API.getItems().getWands().holdsWand(player, EquipmentSlot.HAND))) {
            common.printDebugMessage("primaryHandInteractEvents: player holding a wand", null, null, false);

            //
            // A left click of the primary hand is used to cast a spell
            //
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                common.printDebugMessage("primaryHandInteractEvents: left click action", null, null, false);
                castSpell(player);
            }

            //
            // A right click is used:
            // - to determine if the wand is the player's destined wand
            // - to brew a potion if they are holding a glass bottle in their off-hand and facing a cauldron
            //
            else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (!Ollivanders2API.getItems().getWands().holdsWand(player)) {
                    return;
                }

                common.printDebugMessage("primaryHandInteractEvents: right click action", null, null, false);

                Block cauldron = (Ollivanders2Common.playerFacingBlockType(player, Material.WATER_CAULDRON));
                if ((cauldron != null) && (player.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE)) {
                    common.printDebugMessage("primaryHandInteractEvents: brewing potion", null, null, false);
                    brewPotion(player, cauldron);
                    return;
                }

                common.printDebugMessage("primaryHandInteractEvents: waving wand", null, null, false);

                // play a sound and visual effect when they right-click their destined wand with no spell
                if (Ollivanders2API.playerCommon.wandCheck(player, EquipmentSlot.HAND) < 2) {
                    Location location = player.getLocation();
                    location.setY(location.getY() + 1.6);
                    player.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
                    player.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    p.getO2Player(player).setFoundWand(true);

                    OllivandersPlayerFoundWandEvent wandEvent = new OllivandersPlayerFoundWandEvent(player);
                    p.getServer().getPluginManager().callEvent(wandEvent);
                    common.printDebugMessage("fired found wand event", null, null, false);
                }
                else {
                    OllivandersPlayerNotDestinedWandEvent wandEvent = new OllivandersPlayerNotDestinedWandEvent(player);
                    p.getServer().getPluginManager().callEvent(wandEvent);
                }
            }
        }
    }

    /**
     * If non-verbal spell casting is enabled, selects a new spell from mastered spells.
     *
     * @param player the player rotating spells
     * @param action the player action
     */
    private void rotateNonVerbalSpell(@NotNull Player player, @NotNull Action action) {
        if (!Ollivanders2.enableNonVerbalSpellCasting)
            return;

        common.printDebugMessage("Rotating mastered spells for non-verbal casting.", null, null, false);

        if (!Ollivanders2API.getItems().getWands().holdsWand(player, EquipmentSlot.OFF_HAND))
            return;

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
            return;

        boolean reverse = false;
        // right click rotates through spells backwards
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
            reverse = true;

        o2p.shiftMasterSpell(reverse);
        O2SpellType spell = o2p.getMasterSpell();
        if (spell != null) {
            String spellName = Ollivanders2Common.firstLetterCapitalize(Ollivanders2Common.enumRecode(spell.toString()));
            player.sendMessage(Ollivanders2.chatColor + "Wand master spell set to " + spellName);
        }
        else {
            if (Ollivanders2.debug) {
                player.sendMessage(Ollivanders2.chatColor + "You have not mastered any spells.");
            }
        }
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        playerQuit(event.getPlayer());
    }

    /**
     * Handle player being kicked from the server.
     *
     * @param event the kick event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(@NotNull PlayerKickEvent event) {
        playerQuit(event.getPlayer());
    }

    /**
     * Upkeep when a player leaves the game.
     *
     * @param player the player
     */
    private void playerQuit(@NotNull Player player) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
            return;

        // do player quit actions
        o2p.onQuit();
    }

    /**
     * Handle player death event.
     *
     * @param event the player death event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        if (Ollivanders2.enableDeathExpLoss) {
            O2Player o2p = Ollivanders2API.getPlayers().getPlayer(event.getEntity().getUniqueId());

            if (o2p == null)
                return;

            o2p.onDeath();

            p.setO2Player(event.getEntity(), o2p);
        }
    }

    /**
     * This checks if a player kills another player, and if so, adds a soul to the attacking player
     *
     * @param event the entity damage event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(@NotNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();
                if (damaged.getHealth() - event.getDamage() <= 0)
                    p.getO2Player(attacker).addSoul();
            }
        }
    }

    /**
     * If a block is broken that is temporary, prevent it from dropping anything.
     *
     * @param event the block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTemporaryBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        if (p.isTempBlock(block)) {
            event.setDropItems(false);
        }
    }

    /**
     * Cancels any targeting of players with the Cloak of Invisibility.
     *
     * @param event the Entity Target Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void cloakPlayer(@NotNull EntityTargetEvent event) {
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
     * <p>Reference: https://github.com/Azami7/Ollivanders2/wiki/Configuration#witch-wand-drop</p>
     *
     * @param event the entity death event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void witchWandDrop(@NotNull EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.WITCH && Ollivanders2.enableWitchDrop) {
            ItemStack wand = Ollivanders2API.getItems().getWands().createRandomWand();
            if (wand == null) {
                common.printDebugMessage("OllivandersListener.witchWandDrop: wand is null", null, null, false);
                return;
            }

            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), wand);
        }
    }

    /**
     * When a player consumes something, see if it was a potion and apply the effect if it was.
     *
     * @param event the player item consume event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDrink(@NotNull PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.POTION) {
            Player player = event.getPlayer();
            common.printDebugMessage(player.getDisplayName() + " drank a potion.", null, null, false);

            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;

            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(O2Potions.potionTypeKey, PersistentDataType.STRING) || meta.hasLore()) {
                O2Potion potion = Ollivanders2API.getPotions().findPotionByItemMeta(meta);

                if (potion != null) {
                    if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType()))
                        return;

                    potion.drink(player);
                }
            }
        }
    }

    /**
     * When a user holds their spell journal, replace it with an updated version of the book.
     *
     * <p>Reference: https://github.com/Azami7/Ollivanders2/wiki/Configuration#spell-journal</p>
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
     * Handle potion making actions.
     *
     * @param event the player toggle sneak event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPotionBrewing(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // is the player sneaking
        if (!event.isSneaking())
            return;

        Block cauldron = Ollivanders2Common.playerFacingBlockType(player, Material.WATER_CAULDRON);
        if (cauldron == null)
            return;

        // check that the item held is in their left hand
        ItemStack heldItem = player.getInventory().getItemInOffHand();
        if (heldItem.getType() == Material.AIR || heldItem.getAmount() == 0)
            return;

        ItemMeta meta = heldItem.getItemMeta();
        if (meta == null)
            return;

        String ingredientName = heldItem.getItemMeta().getDisplayName();

        // put the item in the player's off-hand in to the cauldron
        Location spawnLoc = cauldron.getLocation();
        World world = cauldron.getWorld();

        Item item = world.dropItem(spawnLoc.add(+0.5, +0.5, +0.5), heldItem.clone());
        player.sendMessage(Ollivanders2.chatColor + "Added " + ingredientName);

        item.setVelocity(new Vector(0, 0, 0));
        player.getInventory().setItemInOffHand(null);
    }

    /**
     * Brew a potion from the ingredients in a cauldron.
     *
     * @param player   the player brewing the potion
     * @param cauldron the cauldron of ingredients
     * @apiNote assumes player is holding a glass bottle in their off-hand and will set off-hand item to null
     */
    private void brewPotion(@NotNull Player player, @NotNull Block cauldron) {
        common.printDebugMessage("OllivandersListener:brewPotion: brewing potion", null, null, false);

        Block under = cauldron.getRelative(BlockFace.DOWN);
        if (Ollivanders2Common.hotBlocks.contains(under.getType())) {
            ItemStack potion = Ollivanders2API.getPotions().brewPotion(cauldron, player);

            if (potion == null) {
                player.sendMessage(Ollivanders2.chatColor + "The cauldron appears unchanged. Perhaps you should check your recipe");
                return;
            }

            // remove ingredients from cauldron
            for (Entity e : cauldron.getWorld().getNearbyEntities(cauldron.getLocation(), 1, 1, 1)) {
                if (e instanceof Item)
                    e.remove();
            }

            player.getWorld().playEffect(cauldron.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);

            // put potion in player's hand
            ItemStack emptyBottle = player.getInventory().getItemInOffHand();
            player.getInventory().remove(emptyBottle);
            player.getInventory().setItemInOffHand(potion);
        }
        else
            common.printDebugMessage("Cauldron is not over a hot block", null, null, false);
    }

    /**
     * Handle effects for O2SplashPotion throws
     *
     * @param event the potion splash event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onSplashPotion(@NotNull PotionSplashEvent event) {
        ThrownPotion thrown = event.getEntity();
        ItemMeta meta = thrown.getItem().getItemMeta();
        if (meta == null)
            return;

        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemMeta(meta);

        if (potion != null) {
            if (potion instanceof O2SplashPotion)
                ((O2SplashPotion) potion).thrownEffect(event);
        }
    }

    /**
     * Handle casting a divination spell
     *
     * @param spellType the type of divination spell
     * @param sender    the player doing the spell
     * @param words     the additional args to the spell
     * @return true if spell was successfully created, false otherwise
     */
    private boolean divine(@NotNull O2SpellType spellType, @NotNull Player sender, @NotNull String[] words) {
        common.printDebugMessage("Casting divination spell", null, null, false);

        // parse the words for the target player's name
        if (words.length < 2) {
            sender.sendMessage(Ollivanders2.chatColor + "You must say the name of the player. Example: 'astrologia steve'.");
            return false;
        }

        // name should be the last word the player said
        String targetName = words[words.length - 1];
        Player target = p.getServer().getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find player named " + targetName + ".");
            return false;
        }

        O2Spell spell = createSpellProjectile(sender, spellType, 1.0);

        if (!(spell instanceof Divination))
            return false;

        ((Divination) spell).setTarget(target);
        addSpellProjectile(sender, spell);

        return true;
    }
}