package net.pottercraft.ollivanders2;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.effect.O2Effects;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.spell.APPARATE;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells;
import org.bukkit.World;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.potion.O2Potion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Ollivanders2 plugin
 *
 * @author Azami7
 */
public class Ollivanders2 extends JavaPlugin {
    /**
     * All active spell projectiles
     */
    final private List<O2Spell> projectiles = new ArrayList<>();

    /**
     * All blocks temporarily changed by spells
     */
    @Deprecated
    final private Map<Block, Material> tempBlocks = new HashMap<>();

    /**
     * All pending teleport events
     */
    private Ollivanders2TeleportActions teleportActions;

    /**
     * Spells
     */
    static O2Spells spells;

    /**
     * Potions
     */
    static O2Potions potions;

    /**
     * Books
     */
    static O2Books books;

    /**
     * Items
     */
    static O2Items items;

    /**
     * Houses
     */
    static O2Houses houses;

    /**
     * Players
     */
    static O2Players players;

    /**
     * Stationary Spells
     */
    static O2StationarySpells stationarySpells;

    /**
     * Prophecies
     */
    static O2Prophecies prophecies;

    /**
     * Owl Post
     */
    static Ollivanders2OwlPost owlPost;

    // file config
    /**
     * See <a href="https://https://github.com/Azami7/Ollivanders2/wiki/Configuration#chat-dropoff">https://https://github.com/Azami7/Ollivanders2/wiki/Configuration#chat-dropoff</a>
     */
    public static int chatDropoff = 15;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#chat-color">https://github.com/Azami7/Ollivanders2/wiki/Configuration#chat-color</a>
     */
    public static ChatColor chatColor;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#log-in-message">https://github.com/Azami7/Ollivanders2/wiki/Configuration#log-in-message</a>
     */
    public static boolean showLogInMessage;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#book-learning">https://github.com/Azami7/Ollivanders2/wiki/Configuration#book-learning</a>
     */
    public static boolean bookLearning;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#max-spell-level">https://github.com/Azami7/Ollivanders2/wiki/Configuration#max-spell-level</a>
     */
    public static boolean maxSpellLevel;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#non-verbal-spell-casting">https://github.com/Azami7/Ollivanders2/wiki/Configuration#non-verbal-spell-casting</a>
     */
    public static boolean enableNonVerbalSpellCasting;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#spell-journal">https://github.com/Azami7/Ollivanders2/wiki/Configuration#spell-journal</a>
     */
    public static boolean useSpellJournal;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#hostile-mob-animagi">https://github.com/Azami7/Ollivanders2/wiki/Configuration#hostile-mob-animagi</a>
     */
    public static boolean useHostileMobAnimagi;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#death-exp-loss">https://github.com/Azami7/Ollivanders2/wiki/Configuration#death-exp-loss</a>
     */
    public static boolean enableDeathExpLoss;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#apparate-locations">https://github.com/Azami7/Ollivanders2/wiki/Configuration#apparate-locations</a>
     */
    public static boolean apparateLocations;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#divination-1">https://github.com/Azami7/Ollivanders2/wiki/Configuration#divination-1</a>
     */
    public static int divinationMaxDays = 4;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#years">https://github.com/Azami7/Ollivanders2/wiki/Configuration#years</a>
     */
    public static boolean useYears;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#debug-mode">https://github.com/Azami7/Ollivanders2/wiki/Configuration#debug-mode</a>
     */
    public static boolean debug;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#override-mc-version-check">https://github.com/Azami7/Ollivanders2/wiki/Configuration#override-mc-version-check</a>
     */
    public static boolean overrideVersionCheck;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#floo-powder">https://github.com/Azami7/Ollivanders2/wiki/Configuration#floo-powder</a>
     */
    public static Material flooPowderMaterial;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#broomstick">https://github.com/Azami7/Ollivanders2/wiki/Configuration#broomstick</a>
     */
    public static Material broomstickMaterial;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#witch-wand-drop">https://github.com/Azami7/Ollivanders2/wiki/Configuration#witch-wand-drop</a>
     */
    public static boolean enableWitchDrop;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#hourly-backup">https://github.com/Azami7/Ollivanders2/wiki/Configuration#hourly-backup</a>
     */
    public static boolean hourlyBackup;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#archive-previous-backup">https://github.com/Azami7/Ollivanders2/wiki/Configuration#archive-previous-backup</a>
     */
    public static boolean archivePreviousBackup;

    /**
     * Whether to load localization strings from the config file
     */
    public static boolean useTranslations;

    /**
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#animagus-transformation-rules">https://github.com/Azami7/Ollivanders2/wiki/Configuration#animagus-transformation-rules</a>
     */
    public static boolean useStrictAnimagusConditions;

    /**
     * True if the WorldGuard plugin is found and enabled
     */
    public static boolean worldGuardEnabled = false;

    /**
     * True if the libsDisguises plugin is found and enabled
     */
    public static boolean libsDisguisesEnabled = false;

    /**
     * The WorldGuard management class
     */
    public static Ollivanders2WorldGuard worldGuardO2;

    /**
     * The plugin data directory on the server
     */
    public static final String pluginDir = "plugins/Ollivanders2/";

    /**
     * Cleanup when the Minecraft server shuts down.
     *
     * <p>Called when the Ollivanders2 plugin is being disabled. Orchestrates the complete shutdown
     * process by killing active spell projectiles, delegating cleanup to all resource managers,
     * saving persistent data, and reverting temporary world changes.</p>
     *
     * <p>Shutdown Operations:</p>
     * <ul>
     * <li>Kill all active spell projectiles to prevent dangling effects</li>
     * <li>Delegate shutdown to all resource managers (spells, potions, books, items, houses, players, stationary spells, prophecies, owl post)</li>
     * <li>Save APPARATE teleport locations</li>
     * <li>Revert all temporary block changes made by spells</li>
     * <li>Ensure plugin config file exists</li>
     * <li>Log plugin shutdown completion</li>
     * </ul>
     */
    public void onDisable() {
        // kill all spell projectiles running
        for (O2Spell proj : projectiles) {
            proj.kill();
        }

        // call on disable for all resources
        spells.onDisable();
        potions.onDisable();
        books.onDisable();
        items.onDisable();
        houses.onDisable();
        players.onDisable();
        stationarySpells.onDisable();
        prophecies.onDisable();
        owlPost.onDisable();

        APPARATE.saveApparateLocations();

        revertAllTempBlocks();
        savePluginConfig();

        getLogger().info(this + " is now disabled!");
    }

    /**
     * Save plugin data
     */
    public void savePluginData() {
        if (stationarySpells != null)
            stationarySpells.saveO2StationarySpells();
        if (prophecies != null)
            prophecies.saveProphecies();
        if (houses != null)
            houses.saveHouses();
        if (players != null) {
            players.saveO2Players();
            if (players.playerEffects != null)
                players.playerEffects.saveEffects();
        }

        APPARATE.saveApparateLocations();
    }

    /**
     * Save plugin config
     */
    private void savePluginConfig() {
        if (!(new File(pluginDir + "config.yml").exists())) {
            saveDefaultConfig();
        }
    }

    /**
     * onEnable runs when the Minecraft server is starting up.
     * <p>
     * Primary function is to set up static plugin data and load saved configs and data from disk.
     */
    public void onEnable() {
        spells = new O2Spells(this);
        potions = new O2Potions(this);
        books = new O2Books(this);
        items = new O2Items(this);
        houses = new O2Houses(this);
        players = new O2Players(this);
        stationarySpells = new O2StationarySpells(this);
        prophecies = new O2Prophecies(this);
        owlPost = new Ollivanders2OwlPost(this);

        Ollivanders2API.init(this);

        // check for plugin data directory and config
        if (new File(pluginDir).mkdirs())
            getLogger().info("Creating directory for Ollivanders2");

        // read configuration
        initConfig();

        // set up event listeners
        OllivandersListener ollivandersListener = new OllivandersListener(this);
        ollivandersListener.onEnable();

        // set up scheduler
        OllivandersSchedule schedule = new OllivandersSchedule(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, schedule, 20L, 1L);

        // set up dependencies
        loadDependencyPlugins();

        // set up players
        players.onEnable();

        // set up houses
        houses.onEnable();

        // set up items
        items.onEnable();

        // stationary spells
        stationarySpells.onEnable();

        // spells
        spells.onEnable();

        // potions
        potions.onEnable();

        // books
        books.onEnable();

        // teleport events
        teleportActions = new Ollivanders2TeleportActions(this);

        getLogger().info(this + " is now enabled!");
    }

    /**
     * Load plugin config. If there is a config.yml file, load any config
     * set there and set everything else to default values.
     */
    private void initConfig() {
        //
        // chatDropoff
        //
        if (getConfig().isSet("chatDropoff"))
            chatDropoff = getConfig().getInt("chatDropoff");
        if (chatDropoff <= 0)
            chatDropoff = 15;

        //
        // chatColor
        //
        if (getConfig().isSet("chatColor"))
            chatColor = ChatColor.getByChar(Objects.requireNonNull(getConfig().getString("chatColor")));
        if (chatColor == null)
            chatColor = ChatColor.AQUA;
        getLogger().info("Setting plugin message color to " + chatColor.toString());

        //
        // showLogInMessage
        //
        showLogInMessage = getConfig().getBoolean("showLogInMessage");
        if (showLogInMessage)
            getLogger().info("Enabling player log in message.");

        //
        // bookLearning
        //
        bookLearning = getConfig().getBoolean("bookLearning");
        if (bookLearning)
            getLogger().info("Enabling book learning.");

        //
        // maxSpells, can only be enabled when bookLearning is off.
        //
        if (!bookLearning)
            maxSpellLevel = getConfig().getBoolean("maxSpellLevel");
        else
            maxSpellLevel = false;

        if (maxSpellLevel)
            getLogger().info("Max spell level on.");

        //
        // nonVerbalSpellCasting
        //
        enableNonVerbalSpellCasting = getConfig().getBoolean("nonVerbalSpellCasting");
        if (enableNonVerbalSpellCasting)
            getLogger().info("Enabling non-verbal spell casting.");

        //
        // spellJournal
        //
        useSpellJournal = getConfig().getBoolean("spellJournal");
        if (useSpellJournal)
            getLogger().info("Enabling spell journal.");

        //
        // hostileMobAnimagi
        //
        useHostileMobAnimagi = getConfig().getBoolean("hostileMobAnimagi");
        if (useHostileMobAnimagi)
            getLogger().info("Enabling hostile mob types for animagi.");

        //
        // deathExpLoss
        //
        enableDeathExpLoss = getConfig().getBoolean("deathExpLoss");
        if (enableDeathExpLoss)
            getLogger().info("Enabling death experience loss.");

        //
        // apparate locations, when true apparate will only work to named locations, not by coordinates
        //
        apparateLocations = getConfig().getBoolean("apparateLocations");
        if (apparateLocations)
            getLogger().info("Enabling apparate locations - apparation by coordinates disabled.");

        //
        // divinationMaxDays
        //
        if (getConfig().isSet("divinationMaxDays"))
            divinationMaxDays = getConfig().getInt("divinationMaxDays");
        if (divinationMaxDays <= 0)
            divinationMaxDays = 4;

        //
        // years
        //
        useYears = getConfig().getBoolean("years");
        if (useYears)
            getLogger().info("Enabling school years.");

        //
        // flooPowder
        //
        if (getConfig().isSet("flooPowder"))
            flooPowderMaterial = Material.getMaterial(Objects.requireNonNull(getConfig().getString("flooPowder")));
        if (flooPowderMaterial == null)
            flooPowderMaterial = Material.REDSTONE;
        O2ItemType.FLOO_POWDER.setMaterial(flooPowderMaterial);

        //
        // broomstick
        //
        if (getConfig().isSet("broomstick"))
            broomstickMaterial = Material.getMaterial(Objects.requireNonNull(getConfig().getString("broomstick")));
        if (broomstickMaterial == null)
            broomstickMaterial = Material.STICK;
        O2ItemType.BASIC_BROOM.setMaterial(broomstickMaterial);
        O2ItemType.BROOMSTICK.setMaterial(broomstickMaterial);

        //
        // witchDrop
        //
        enableWitchDrop = getConfig().getBoolean("witchDrop");
        if (enableWitchDrop)
            getLogger().info("Enabling witch wand drop");

        //
        // debug and experimental
        //
        debug = getConfig().getBoolean("debug");
        if (debug)
            getLogger().info("Enabling debug mode.");

        overrideVersionCheck = getConfig().getBoolean("overrideVersionCheck");
        if (overrideVersionCheck)
            getLogger().info("Experimental - disabling version checks. This version of Ollivanders2 may not be compatible with you MC server api.");

        //
        // Save options
        //
        hourlyBackup = getConfig().getBoolean("hourlyBackup");
        if (hourlyBackup)
            getLogger().info("Enabling hourly backups.");

        archivePreviousBackup = getConfig().getBoolean("archivePreviousBackup");
        if (archivePreviousBackup)
            getLogger().info("Enabling backup archiving.");

        //
        // Translations
        //
        useTranslations = getConfig().getBoolean("useTranslations");
        if (useTranslations)
            getLogger().info("Enabling string translations.");

        //
        // Strict animagus potion consume conditions
        //
        useStrictAnimagusConditions = getConfig().getBoolean("useStrictAnimagusConditions");
        if (useStrictAnimagusConditions)
            getLogger().info("Enabling strict animagus conditions.");
    }

    /**
     * Load dependency plugins or turn of the features that require them if
     * they are not present.
     */
    private void loadDependencyPlugins() {
        // set up libDisguises
        Plugin libsDisguises = Bukkit.getServer().getPluginManager().getPlugin("LibsDisguises");
        if (libsDisguises != null) {
            libsDisguisesEnabled = true;
            getLogger().info("LibsDisguises found, enabled entity transfiguration spells.");
        }
        else
            getLogger().info("LibsDisguises not found, disabling entity transfiguration spells.");

        // set up WorldGuard manager
        Plugin worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null)
            worldGuardEnabled = false;
        else {
            try {
                if (worldGuard instanceof WorldGuardPlugin) {
                    worldGuardO2 = new Ollivanders2WorldGuard(this);
                    worldGuardEnabled = true;
                }
            }
            catch (Exception e) {
                worldGuardEnabled = false;
            }
        }

        if (worldGuard != null)
            getLogger().info("WorldGuard found, enabled WorldGuard features.");
        else
            getLogger().info("WorldGuard not found, disabled WorldGuard features.");
    }

    /**
     * Handle command events
     *
     * @param sender       the player who issued the command
     * @param cmd          the command entered by the player
     * @param commandLabel required arg for the plugin onCommand call from the server
     * @param args         the arguments to the command, if any
     * @return true if the command was successful, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("Ollivanders2") || cmd.getName().equalsIgnoreCase("Olli"))
            return runOllivanders(sender, args);

        return false;
    }

    /**
     * The main Ollivanders2 command.
     *
     * @param sender the player who issued the command
     * @param args   the arguments for the command, if any
     * @return true if the /ollivanders command worked, false otherwise
     */
    private boolean runOllivanders(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            usageMessageOllivanders(sender);
            return true;
        }

        String subCommand = args[0];

        //
        // Help
        //
        if (subCommand.equalsIgnoreCase("help")) {
            usageMessageOllivanders(sender);
            return true;
        }
        //
        // Summary
        //
        else if (subCommand.equalsIgnoreCase("summary"))
            return Ollivanders2API.getPlayers().runSummary(sender, args);

        //
        //
        // Admin only permissions beyond this point
        //
        //
        if (sender.hasPermission("Ollivanders2.admin")) {
            //
            // Wands
            //
            if (subCommand.equalsIgnoreCase("wands") || subCommand.equalsIgnoreCase("wand")) {
                if (args.length == 1 && sender instanceof Player)
                    return okitWands((Player) sender);
                else if (args.length > 1) {
                    Player target = getServer().getPlayer(args[1]);
                    if (target != null)
                        return Ollivanders2API.getItems().getWands().giveRandomWand(target);
                    else {
                        usageMessageWand(sender);
                        return true;
                    }
                }
                else
                    return false;
            }
            //
            // Reload
            //
            else if (subCommand.equalsIgnoreCase("reload"))
                return runReloadConfigs(sender);
                //
                // Items
                //
            else if (subCommand.equalsIgnoreCase("items") || subCommand.equalsIgnoreCase("item")) {
                if (sender instanceof Player)
                    return runItems((Player) sender, args);
                else
                    return false;
            }
            //
            // House
            //
            else if (subCommand.equalsIgnoreCase("house") || subCommand.equalsIgnoreCase("houses"))
                return runHouse(sender, args);
                //
                // Debug
                //
            else if (subCommand.equalsIgnoreCase("debug"))
                return toggleDebug(sender);
                //
                // Floo
                //
            else if (subCommand.equalsIgnoreCase("floo")) {
                if (args.length == 1 && sender instanceof Player)
                    return giveFlooPowder((Player) sender);
                else if (args.length > 1) {
                    Player target = getServer().getPlayer(args[1]);
                    if (target != null)
                        return giveFlooPowder(target);
                    else {
                        sender.sendMessage(Ollivanders2.chatColor + "Unable to find player.");
                        return true;
                    }
                }
                else
                    return false;
            }
            //
            // Books
            //
            else if (subCommand.equalsIgnoreCase("books") || subCommand.equalsIgnoreCase("book"))
                return books.runCommand(sender, args);
                //
                // Year
                //
            else if (subCommand.equalsIgnoreCase("year") || subCommand.equalsIgnoreCase("years"))
                return Ollivanders2API.getPlayers().runYear(sender, args);
                //
                // Animagus
                //
            else if (subCommand.equalsIgnoreCase("animagus"))
                return Ollivanders2API.getPlayers().runAnimagus(sender, args);
                //
                // Potions
                //
            else if (subCommand.equalsIgnoreCase("potions") || subCommand.equalsIgnoreCase("potion"))
                return runPotions(sender, args);
                //
                // Effects
                //
            else if (subCommand.equalsIgnoreCase("effect") || subCommand.equalsIgnoreCase("effects"))
                return O2Effects.runCommand(sender, args, this);
                //
                // Prophecy
                //
            else if (subCommand.equalsIgnoreCase("prophecy") || subCommand.equalsIgnoreCase("prophecies"))
                return runProphecies(sender);
                //
                // Apparate locations
                //
            else if (subCommand.equalsIgnoreCase("apparate") || subCommand.equalsIgnoreCase("apparateLoc"))
                return runApparateLocation(sender, args);
        }
        else
            usageMessageOllivanders(sender);

        return true;
    }

    /**
     * Usage message for the /ollivanders command.
     *
     * @param sender the command sender
     */
    private void usageMessageOllivanders(@NotNull CommandSender sender) {
        if (sender.hasPermission("Ollivanders2.admin")) {
            sender.sendMessage(chatColor
                    + "You are running Ollivanders2 version " + this.getDescription().getVersion() + "\n"
                    + "\nOllivanders2 commands:"
                    + "\nwands - wand commands"
                    + "\nbooks - books commands"
                    + "\nitems - item commands"
                    + "\npotions - potions commands"
                    // + "\nquidd - creates a quidditch pitch"
                    + "\nhouse - house commands"
                    + "\nyear - year commands"
                    + "\neffect - effects commands"
                    + "\nprophecy - list all active prophecies"
                    + "\nsummary - gives a summary of wand type, house, year, and known spells"
                    + "\napparateLoc - apparation location commands"
                    + "\nanimagus - make a player an animagus"
                    + "\nreload - reload the Ollivanders2 configs"
                    + "\ndebug - toggles Ollivanders2 plugin debug output\n"
                    + "\n" + "To run a command, type '/ollivanders2 [command]'."
                    + "\nFor example, '/ollivanders2 wands\n");
        }
        else
            players.usageSummaryPlayer(sender);
    }

    /**
     * The house subCommand for managing everything related to houses.
     *
     * @param sender the player that issued the command
     * @param args   the arguments to the command, if any
     * @return true if no error occurred
     */
    private boolean runHouse(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (!O2Houses.useHouses) {
            sender.sendMessage(chatColor
                    + "House are not currently enabled for your server."
                    + "\nTo enable houses, update the Ollivanders2 config.yml setting to true and restart your server."
                    + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki");

            return true;
        }

        // parse args
        if (args.length >= 2) {
            String subCommand = args[1];

            if (subCommand.equalsIgnoreCase("sort") || subCommand.equalsIgnoreCase("forcesort")) {
                // sort player in to a house
                if (args.length < 4) {
                    usageMessageHouseSort(sender);
                    return true;
                }

                return runSort(sender, args[2], args[3], subCommand.equalsIgnoreCase("forcesort"));
            }
            else if (subCommand.equalsIgnoreCase("list")) {
                // list houses

                return runListHouse(sender, args);
            }
            else if (subCommand.equalsIgnoreCase("points")) {
                // update house points

                return runHousePoints(sender, args);
            }
            else if (subCommand.equalsIgnoreCase("reset"))
                return houses.reset();
        }

        usageMessageHouse(sender);

        return true;
    }

    /**
     * Usage message for /ollivanders house
     *
     * @param sender the command sender
     */
    private void usageMessageHouse(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Usage: /ollivanders2 house points [option]"
                + "\n\nOptions to '/ollivanders2 house':"
                + "\nlist - lists Ollivanders2 houses and house membership"
                + "\nsort - sort a player in to a house"
                + "\npoints - manage house points");
    }

    /**
     * List all houses or the members of a house
     *
     * @param sender the player who issued the command
     * @param args   the arguments for the command, if any
     * @return true if no error occurred
     */
    private boolean runListHouse(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        // list houses
        if (debug)
            getLogger().info("Running list houses");

        if (args.length > 2) {
            String targetHouse = args[2];

            O2HouseType house = houses.getHouseType(targetHouse);
            if (house != null) {
                List<String> members = houses.getHouseMembers(house);
                StringBuilder memberStr = new StringBuilder();

                if (members.isEmpty())
                    memberStr.append("no members");
                else {
                    for (String p : members) {
                        memberStr.append(p).append(" ");
                    }
                }

                sender.sendMessage(chatColor + "Members of " + targetHouse + " are:\n" + memberStr);

                return true;
            }

            sender.sendMessage(chatColor + "Invalid house name '" + targetHouse + "'");
        }

        StringBuilder houseNames = new StringBuilder();
        List<String> h = houses.getAllHouseNames();

        for (String name : h) {
            houseNames.append(name).append(" ");
        }

        sender.sendMessage(chatColor
                + "Ollivanders2 House are:\n" + houseNames + "\n"
                + "\nTo see the members of a specific house, run the command /ollivanders2 house list [house]"
                + "\nFor example, /ollivanders2 list Hufflepuff");

        return true;
    }

    /**
     * Sorts a player in to a specific house. The player will not be sorted if:
     * a) the player is not online
     * b) an invalid house name is specified
     * c) they have already been sorted
     *
     * @param sender       the player that issued the command
     * @param targetPlayer the player to sort
     * @param targetHouse  the house to sort the player to
     * @param forcesort    should the sort happen even if the player is already sorted
     * @return true unless an error occurred
     */
    private boolean runSort(@NotNull CommandSender sender, @NotNull String targetPlayer, @NotNull String targetHouse, boolean forcesort) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (debug)
            getLogger().info("Running house sort");

        if (targetPlayer.isEmpty() || targetHouse.isEmpty()) {
            usageMessageHouseSort(sender);
            return (true);
        }

        Player player = getServer().getPlayer(targetPlayer);
        if (player == null) {
            sender.sendMessage(chatColor
                    + "Unable to find a player named " + targetPlayer + " logged in to this server."
                    + "\nPlayers must be logged in to be sorted.");

            return true;
        }

        O2HouseType house = houses.getHouseType(targetHouse);

        if (house == null) {
            sender.sendMessage(chatColor + targetHouse + " is not a valid house name.");

            return true;
        }

        boolean success;
        if (forcesort) {
            houses.forceSetHouse(player, house);
            success = true;
        }
        else
            success = houses.sort(player, house);

        if (success)
            sender.sendMessage(chatColor + targetPlayer + " has been successfully sorted in to " + targetHouse);
        else {
            O2HouseType houseType = houses.getHouse(player);
            if (houseType == null) {
                sender.sendMessage(chatColor + " Failed to sort " + targetPlayer);
                Ollivanders2API.common.printDebugMessage("Null house in Ollivanders2.runSort", null, null, true);
            }
            else
                sender.sendMessage(chatColor + targetPlayer + " is already a member of " + houseType.getName());
        }

        return true;
    }

    /**
     * Usage message for /ollivanders house sort
     *
     * @param sender the player that issued the command
     */
    private void usageMessageHouseSort(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Usage: /ollivanders2 house sort [player] [house]"
                + "\nFor example '/ollivanders2 house sort Harry Gryffindor");
    }

    /**
     * Manage house points.
     *
     * @param sender the player that issued the command
     * @param args   the arguments for the command, if any
     * @return true unless an error occurs
     */
    private boolean runHousePoints(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (debug)
            getLogger().info("Running house points");

        if (args.length > 2) {
            String option = args[2];
            if (debug)
                getLogger().info("runHousePoints: option = " + option);

            if (option.equalsIgnoreCase("reset"))
                return houses.resetHousePoints();

            if (args.length > 4) {
                String h = args[3];

                if (debug)
                    getLogger().info("runHousePoints: house = " + h);

                O2HouseType houseType = null;
                try {
                    houseType = houses.getHouseType(h);
                }
                catch (Exception e) {
                    // nom nom nom
                    if (debug)
                        getLogger().warning("runHousePoints: Exception getting house type.\n");
                }

                if (houseType == null) {
                    if (debug)
                        getLogger().info("runHousePoints: invalid house name '" + h + "'");

                    usageMessageHousePoints(sender);
                    return true;
                }

                int value;

                try {
                    value = Integer.parseInt(args[4]);

                    if (debug)
                        getLogger().info("runHousePoints: value = " + value);
                }
                catch (Exception e) {
                    if (debug)
                        getLogger().warning("runHousePoints: unable to parse int from " + args[4]);

                    usageMessageHousePoints(sender);
                    return true;
                }

                if (option.equalsIgnoreCase("add"))
                    return houses.addHousePoints(houseType, value);
                else if (option.equalsIgnoreCase("subtract"))
                    return houses.subtractHousePoints(houseType, value);
                else if (option.equalsIgnoreCase("set"))
                    return houses.setHousePoints(houseType, value);
            }
        }

        usageMessageHousePoints(sender);

        return true;
    }

    /**
     * Display the usage message for /ollivanders2 house points
     *
     * @param sender the player that issued the command
     */
    private void usageMessageHousePoints(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Usage: /ollivanders2 house points [option] [house] [value]"
                + "\n\nOptions to '/ollivanders2 house points':"
                + "\nadd - increase points for a house by specific value"
                + "\nsubtract - decrease points for a house by specified value"
                + "\nset - set the points for a house to specified value"
                + "\nreset - reset all house points to 0"
                + "\n\nExample: /ollivanders2 house points add Slytherin 5"
                + "\nExample: /ollivanders2 house points reset");
    }

    /**
     * Toggle debug mode.
     *
     * @param sender the player that issued the command
     * @return true unless and error occurred
     */
    private boolean toggleDebug(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        debug = !debug;

        if (debug) {
            getLogger().info("Debug mode enabled.");
            sender.sendMessage(chatColor + "Ollivanders2 debug mode enabled.");
        }
        else {
            getLogger().info("Debug mode disabled.");
            sender.sendMessage(chatColor + "Ollivanders2 debug mode disabled.");
        }

        return true;
    }

    /**
     * Reload the game configs if the command caller is an op.
     *
     * @param sender the player that issued the command
     * @return true unless an error occurred
     */
    private boolean runReloadConfigs(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        reloadConfig();
        initConfig();

        sender.sendMessage(chatColor + "Config reloaded");
        return true;
    }

    /**
     * Give a player an item.
     *
     * @param sender the player to give item to
     * @return true unless an error occurred
     */
    private boolean runItems(@NotNull Player sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        List<ItemStack> kit = new ArrayList<>();

        if (args.length < 2)
            usageMessageItem(sender);
        else if (args[1].equals("list"))
            sender.sendMessage("\n" + listAllItems());
        else if (args[1].equals("give") && args.length >= 4) {
            int amount = 0;
            try {
                amount = Integer.parseInt(args[2]);
            }
            catch (Exception e) {
                sender.sendMessage("Unable to parse amount " + args[2]);
            }

            if (amount > 64)
                amount = 64;

            String itemName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            ItemStack item = items.getItemStartsWith(itemName, amount);

            if (item == null) {
                sender.sendMessage("Unable to find an item \"" + itemName + "\"");
                return true;
            }

            kit.add(item);
            O2PlayerCommon.givePlayerKit(sender, kit);
        }
        else
            usageMessageItem(sender);

        return true;
    }

    /**
     * Usage message for Item subcommands.
     *
     * @param sender the player that issued the command
     */
    private void usageMessageItem(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Options to '/ollivanders2 item':"
                + "\nlist - lists all items"
                + "\ngive <count> <item_name> - gives the specified amount of the item");
    }

    /**
     * Build a list of all O2Items
     *
     * @return the list of items
     */
    @NotNull
    private String listAllItems() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Item list:\n");

        for (String item : items.getAllItems()) {
            stringBuilder.append("   ");
            stringBuilder.append(item);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    /**
     * Give a player all the wands.
     *
     * @param player the player to give the wands to
     * @return true unless an error occurred
     */
    private boolean okitWands(@NotNull Player player) {
        List<ItemStack> kit = Ollivanders2API.getItems().getWands().getAllWands();

        O2PlayerCommon.givePlayerKit(player, kit);

        return true;
    }

    /**
     * Usage message for Wand subcommands.
     *
     * @param sender the player that issued the command
     */
    private void usageMessageWand(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "/ollivanders2 wand - gives you one of every wand"
                + "\n/ollivanders2 wand player_name - gives named player a random wand");
    }

    /**
     * Get all the active spell projectiles
     *
     * @return a list of all active spell projectiles
     */
    @NotNull
    public List<O2Spell> getProjectiles() {
        return projectiles;
    }

    /**
     * Add a new spell projectile
     *
     * @param spell the spell projectile
     */
    public void addProjectile(@NotNull O2Spell spell) {
        projectiles.add(spell);
    }

    /**
     * Remove a spell projectile. This will make it stop if it has not already been killed.
     *
     * @param spell the spell projectile
     */
    public void removeProjectile(@NotNull O2Spell spell) {
        projectiles.remove(spell);
    }

    /**
     * Get all pending teleport actions.
     *
     * @return a list of teleport actions
     */
    @NotNull
    public List<Ollivanders2TeleportActions.O2TeleportAction> getTeleportActions() {
        return teleportActions.getTeleportActions();
    }

    /**
     * Add a teleport action to the queue
     *
     * @param p  the player to teleport
     * @param to teleport destination
     */
    public void addTeleportAction(@NotNull Player p, @NotNull Location to) {
        addTeleportAction(p, to, false);
    }

    /**
     * Add a teleport action with an explosion effect
     *
     * @param p                   the player to teleport
     * @param to                  teleport destination
     * @param explosionOnTeleport whether to do an explosion effect
     */
    public void addTeleportAction(@NotNull Player p, @NotNull Location to, boolean explosionOnTeleport) {
        teleportActions.addTeleportEvent(p, p.getLocation(), to, explosionOnTeleport);
    }

    /**
     * Remove a teleport action.
     *
     * @param action the action to remove
     */
    public void removeTeleportAction(@NotNull Ollivanders2TeleportActions.O2TeleportAction action) {
        teleportActions.removeTeleportEvent(action);
    }

    /**
     * Get the spell use count for the player for this spell
     *
     * @param player the player to get the count for
     * @param spell  the spell to get the count for
     * @return the spell count
     */
    public int getSpellCount(@NotNull Player player, @NotNull O2SpellType spell) {
        O2Player o2p = getO2Player(player);

        return o2p.getSpellCount(spell);
    }

    /**
     * Set the spell use count for a player.
     *
     * @param player the player to set the spell count for
     * @param spell  the spell to set the count for
     * @param count  the count to set
     */
    public void setSpellCount(@NotNull Player player, @NotNull O2SpellType spell, int count) {
        UUID pid = player.getUniqueId();
        O2Player o2p = getO2Player(player);

        o2p.setSpellCount(spell, count);

        Ollivanders2API.getPlayers().updatePlayer(pid, o2p);
    }

    /**
     * Increment the spell use count for a player.
     *
     * @param player the player to increment the count for
     * @param spell  the spell to increment
     * @return the incremented use count for this player for this spell
     */
    public int incrementSpellCount(@NotNull Player player, @NotNull O2SpellType spell) {
        //returns the incremented spell count
        UUID pid = player.getUniqueId();
        O2Player o2p = getO2Player(player);

        o2p.incrementSpellCount(spell);
        Ollivanders2API.getPlayers().updatePlayer(pid, o2p);

        return o2p.getSpellCount(spell);
    }

    /**
     * Gets the O2Player associated with the Player
     *
     * @param player the player to get
     * @return O2Player object for this player
     */
    @NotNull
    public O2Player getO2Player(@NotNull Player player) {
        UUID pid = player.getUniqueId();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(pid);

        if (o2p == null) {
            o2p = Ollivanders2API.getPlayers().addPlayer(pid, player.getDisplayName());
        }

        return o2p;
    }

    /**
     * Sets the player's OPlayer by their player name
     *
     * @param player the player
     * @param o2p    the OPlayer associated with the player
     */
    public void setO2Player(@NotNull Player player, @NotNull O2Player o2p) {
        if (!(player instanceof NPC))
            Ollivanders2API.getPlayers().updatePlayer(player.getUniqueId(), o2p);
    }

    /**
     * Gets the set of all player UUIDs.
     *
     * @return a list of all player MC UUIDs
     */
    @NotNull
    public ArrayList<UUID> getO2PlayerIDs() {
        return Ollivanders2API.getPlayers().getPlayerIDs();
    }

    /**
     * Can this player cast this spell?
     *
     * @param player  player to check
     * @param spell   spell to check
     * @param verbose whether to inform the player of why they cannot cast a spell
     * @return True if the player can cast this spell, false if not
     */
    public boolean canCast(@NotNull Player player, @NotNull O2SpellType spell, boolean verbose) {
        // players cannot cast spells when in animagus form, except the spell to change form
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT)) {
            if (spell == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
                return true;
            else
                player.sendMessage(Ollivanders2.chatColor + "You cannot cast spells while in your animagus form.");
            return false;
        }

        if (player.isPermissionSet("Ollivanders2." + spell)) {
            if (!player.hasPermission("Ollivanders2." + spell)) {
                if (verbose)
                    player.sendMessage(chatColor + "You do not have permission to use " + spell);
                return false;
            }
        }

        O2Player p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (p == null)
            return false;

        boolean coolDown = System.currentTimeMillis() < p.getSpellLastCastTime(spell);

        if (coolDown) {
            if (verbose)
                spellCoolDownMessage(player);
            return false;
        }

        boolean cast = spells.isSpellTypeAllowed(player.getLocation(), spell);

        if (!cast && verbose)
            spellCannotBeCastMessage(player);

        return cast;
    }

    /**
     * Give floo powder to player.
     *
     * @param player the player to give the floo powder to
     * @return true if successful, false otherwise
     */
    private boolean giveFlooPowder(@NotNull Player player) {
        ItemStack flooPowder = Ollivanders2API.getItems().getItemByType(O2ItemType.FLOO_POWDER, 8);

        if (flooPowder == null)
            return false;

        List<ItemStack> fpStack = new ArrayList<>();
        fpStack.add(flooPowder);

        O2PlayerCommon.givePlayerKit(player, fpStack);

        return true;
    }

    /**
     * When a spell is not allowed be cast, such as from WorldGuard protection, send a message.
     * This is not the message to use for bookLearning enforcement.
     *
     * @param player the player that cast the spell
     */
    public void spellCannotBeCastMessage(@NotNull Player player) {
        player.sendMessage(chatColor + "A powerful protective magic prevents you from casting this spell here.");
    }

    /**
     * When a spell cannot be used in a location, such as from WorldGuard protection, send a message.
     * This is not the message to use for bookLearning enforcement.
     *
     * @param player the player that cast the spell
     */
    public void spellFailedMessage(@NotNull Player player) {
        player.sendMessage(chatColor + "A powerful protective magic blocks your spell.");
    }

    /**
     * When a spell is not allowed be cast, such as from WorldGuard protection, send a message.
     * This is not the message to use for bookLearning enforcement.
     *
     * @param player the player to send the cooldown message to
     */
    public void spellCoolDownMessage(@NotNull Player player) {
        player.sendMessage(chatColor + "You are too tired to cast this spell right now.");
    }

    /**
     * Potions subcommand
     *
     * @param sender the command sender
     * @param args   args to the command
     * @return true if the command succeeded, false otherwise
     */
    public boolean runPotions(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (args.length > 1) {
            String subCommand = args[1];

            if (subCommand.equalsIgnoreCase("ingredient")) {
                if (args.length > 2) {
                    if (args[2].equalsIgnoreCase("list"))
                        return listAllIngredients(sender);
                    else if (sender instanceof Player) {
                        // potion ingredient mandrake leaf
                        String[] subArgs = Arrays.copyOfRange(args, 2, args.length);
                        return giveItem((Player) sender, String.join(" ", subArgs));
                    }
                    else
                        return false;
                }
            }
            else if (subCommand.equalsIgnoreCase("list"))
                return listAllPotions(sender);
            else if (subCommand.equalsIgnoreCase("all")) {
                if (sender instanceof Player)
                    return giveAllPotions((Player) sender);
                else
                    return false;
            }
            else if (subCommand.equalsIgnoreCase("give")) {
                if (sender instanceof Player) {
                    if (args.length > 3) {
                        // potions give fred memory potion
                        Player targetPlayer = getServer().getPlayer(args[2]);

                        if (targetPlayer != null) {
                            String[] subArgs = Arrays.copyOfRange(args, 3, args.length);
                            return givePotion((Player) sender, targetPlayer, String.join(" ", subArgs));
                        }
                    }
                }
                else
                    return false;
            }
            else {
                if (sender instanceof Player) {
                    // potions memory potion
                    String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                    return givePotion((Player) sender, (Player) sender, String.join(" ", subArgs));
                }
                else
                    return false;
            }
        }

        usageMessagePotions(sender);
        return true;
    }

    /**
     * Give a specific potion to a player.
     *
     * @param sender     the player who did the command
     * @param player     the player to give the potion to
     * @param potionName the potion to give the player
     * @return true if the command succeeded, false otherwise
     */
    public boolean givePotion(@NotNull Player sender, @NotNull Player player, @NotNull String potionName) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        O2PotionType potionType = null;

        // need to iterate rather than call potions.getPotionTypeByName so we can do startsWith
        for (O2PotionType p : O2PotionType.values()) {
            if (p.getPotionName().toLowerCase().startsWith(potionName.toLowerCase())) {
                potionType = p;
                break;
            }
        }

        if (potionType == null) {
            sender.sendMessage(chatColor + "Unable to find potion " + potionName);

            return true;
        }

        O2Potion potion = potions.getPotionFromType(potionType);

        if (potion == null)
            return true;

        ItemStack brewedPotion = potion.brew(sender, false);
        List<ItemStack> kit = new ArrayList<>();
        kit.add(brewedPotion);

        O2PlayerCommon.givePlayerKit(player, kit);

        return true;
    }

    /**
     * Usage message for potion subcommands
     *
     * @param sender the command sender
     */
    private void usageMessagePotions(CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Usage: /ollivanders2 potions"
                + "\ningredient list - lists all potions ingredients"
                + "\ningredient <ingredient name> - give you the ingredient with this name, if it exists"
                + "\nall - gives all Ollivanders2 potions, this may not fit in your inventory"
                + "\n<potion name> - gives you the potion with this name, if it exists"
                + "\ngive <player> <potion name> - gives target player the potion with this name, if it exists\n"
                + "\nExample: /ollivanders2 potions wiggenweld potion"
                + "\nExample: /ollivanders2 potions ingredient list");
    }

    /**
     * List all the potions ingredients
     *
     * @param sender the player to display the list to
     * @return true
     */
    private boolean listAllIngredients(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        List<String> ingredientList = O2Potions.getAllIngredientNames();
        StringBuilder displayString = new StringBuilder();
        displayString.append("Ingredients:");

        for (String name : ingredientList) {
            displayString.append("\n").append(name);
        }
        displayString.append("\n");

        sender.sendMessage(chatColor + displayString.toString());

        return true;
    }

    /**
     * Lists all the potions active in the game.
     *
     * @param sender the player to display the list to
     * @return true
     */
    private boolean listAllPotions(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        StringBuilder displayString = new StringBuilder();
        displayString.append("Potions:");

        List<String> potionNames = potions.getAllPotionNames();
        for (String name : potionNames) {
            displayString.append("\n").append(name);
        }
        displayString.append("\n");

        sender.sendMessage(chatColor + displayString.toString());

        return true;
    }

    /**
     * Give a potion ingredient to a player.
     *
     * @param player the player to give the ingredient to
     * @param name   the name of the ingredient
     * @return true
     */
    private boolean giveItem(@NotNull Player player, @NotNull String name) {
        List<ItemStack> kit = new ArrayList<>();
        ItemStack item = items.getItemStartsWith(name, 1);

        if (item != null) {
            kit.add(item);
            O2PlayerCommon.givePlayerKit(player, kit);
        }

        return true;
    }

    /**
     * Gives the specified player 1 of every Ollivanders2 potion.
     *
     * @param player the player to give the potions to
     * @return true unless an error occurred
     */
    private boolean giveAllPotions(@NotNull Player player) {
        if (debug)
            getLogger().info("Running givePotions...");

        List<ItemStack> kit = new ArrayList<>();

        for (O2Potion potion : potions.getAllPotions()) {
            ItemStack brewedPotion = potion.brew(player, false);

            if (debug)
                getLogger().info("Adding " + potion.getName());

            kit.add(brewedPotion);
        }

        O2PlayerCommon.givePlayerKit(player, kit);

        return true;
    }

    /**
     * Prophecies sub-command
     *
     * @param sender the command sender
     * @return true unless an error occurred
     */
    public boolean runProphecies(@NotNull CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        StringBuilder output = new StringBuilder();
        List<String> allProphecies = prophecies.getProphecies();

        if (!allProphecies.isEmpty()) {
            output.append("Prophecies:\n");

            for (String prophecy : allProphecies) {
                output.append(prophecy).append("\n");
            }
        }
        else
            output.append("There are no unfulfilled prophecies.");

        sender.sendMessage(chatColor + output.toString());

        return true;
    }

    /**
     * Run the apparate location subcommand
     *
     * @param sender the command sender
     * @param args   the arguments to this command
     * @return true if the command succeeded, false otherwise
     */
    public boolean runApparateLocation(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin") || !(sender instanceof Player))
            return false;

        if (!apparateLocations) {
            sender.sendMessage(chatColor
                    + "Apprate locations are not currently enabled for your server."
                    + "\nTo enable apparate locations, update the Ollivanders2 config.yml setting to true and restart your server."
                    + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki/Configuration#apparate-locations"
            );

            return true;
        }

        if (args.length >= 2) {
            String subCommand = args[1];

            if (subCommand.equalsIgnoreCase("list")) {
                APPARATE.listApparateLocations((Player) sender);
                return true;
            }
            else if (subCommand.equalsIgnoreCase("add") || subCommand.equalsIgnoreCase("update")) {
                if (args.length < 6) {
                    usageMessageApparateLocation(sender);
                    return true;
                }

                String locationName = args[2];

                if (subCommand.equalsIgnoreCase("add") && APPARATE.doesLocationExist(locationName)) {
                    sender.sendMessage(chatColor + "An apparate location with that name already exists. If you want to update the location coordinates, use /ollivanders2 apparateLoc update.");
                    return true;
                }
                else if (subCommand.equalsIgnoreCase("update") && !APPARATE.doesLocationExist(locationName)) {
                    sender.sendMessage(chatColor + "An apparate location with that name does not exist. If you want to add a location coordinates, use /ollivanders2 apparateLoc add.");
                    return true;
                }

                String xCoord = args[3];
                String yCoord = args[4];
                String zCoord = args[5];
                double x;
                double y;
                double z;
                try {
                    x = Double.parseDouble(xCoord);
                    y = Double.parseDouble(yCoord);
                    z = Double.parseDouble(zCoord);
                }
                catch (Exception e) {
                    sender.sendMessage(chatColor + "Invalid coordinates for location.");
                    usageMessageApparateLocation(sender);
                    return true;
                }

                World world = ((Player) sender).getWorld();

                APPARATE.addLocation(locationName, world, x, y, z);

                sender.sendMessage(chatColor + "Apparate location " + locationName + " set. \nTo list all locations, use /ollivanders2 apparateLoc list.");
                return true;
            }
            else if (subCommand.equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    usageMessageApparateLocation(sender);
                    return true;
                }

                String locationName = args[2];

                APPARATE.removeLocation(locationName);
                sender.sendMessage(chatColor + "Removed apparate location " + locationName);

                return true;
            }
        }

        usageMessageApparateLocation(sender);
        return true;
    }

    /**
     * Usage message for apparate location subcommands
     *
     * @param sender the command sender
     */
    private void usageMessageApparateLocation(CommandSender sender) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return;

        sender.sendMessage(chatColor
                + "Usage: /ollivanders2 apparateLoc"
                + "\nlist - lists all apparate locations"
                + "\nadd <location name> <x> <y> <z> - adds an apparate location"
                + "\nupdate <location name> <x> <y> <z> - updates named location to new coordinates"
                + "\nremove <location name> - removes the specified apparate location"
                + "\nExample: /ollivanders2 apparateLoc add hogsmeade 200 70 350"
                + "\nExample: /ollivanders2 remove hogsmeade");
    }

    /**
     * Add a temporary block. A temp block is a block that has been temporarily changed to a different type.
     *
     * @param block            the block to add
     * @param originalMaterial the original material of this block
     */
    @Deprecated
    public void addTempBlock(@NotNull Block block, @NotNull Material originalMaterial) {
        tempBlocks.put(block, originalMaterial);
    }

    /**
     * Reverts a temp block back to its original type.
     *
     * @param block the block to remove
     */
    @Deprecated
    public void revertTempBlock(@NotNull Block block) {
        if (tempBlocks.containsKey(block)) {
            revertBlockType(block);

            tempBlocks.remove(block);
        }
    }

    /**
     * Revert a temporarily changed block back to its original type.
     *
     * @param block the changed block
     */
    @Deprecated
    private void revertBlockType(@NotNull Block block) {
        if (tempBlocks.containsKey(block)) {
            Material material = tempBlocks.get(block);

            try {
                block.setType(material);
            }
            catch (Exception e) {
                // in case the block does not exist anymore.
            }
        }
    }

    /**
     * Is a block a temp block or not.
     *
     * @param block the block to check
     * @return true if it is a temp block, false otherwise
     */
    @Deprecated
    public boolean isTempBlock(@NotNull Block block) {
        return tempBlocks.containsKey(block);
    }

    /**
     * Returns the original material for a temp block.
     *
     * @param block the temp block
     * @return the material, if found, null otherwise
     */
    @Deprecated
    public Material getTempBlockOriginalMaterial(@NotNull Block block) {
        return tempBlocks.getOrDefault(block, null);
    }

    /**
     * Revert all temporary blocks to their original type.
     */
    @Deprecated
    private void revertAllTempBlocks() {
        for (Block block : tempBlocks.keySet()) {
            revertBlockType(block);
        }

        tempBlocks.clear();
    }
}