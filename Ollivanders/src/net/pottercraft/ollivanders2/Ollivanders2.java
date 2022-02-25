package net.pottercraft.ollivanders2;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.effect.O2Effects;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.spell.APPARATE;
import net.pottercraft.ollivanders2.spell.O2Spells;
import org.bukkit.World;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import quidditch.Arena;

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
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ollivanders2 plugin object
 *
 * @version Ollivanders2
 * @author cakenggt
 * @author lownes
 * @author Azami7
 * @author lil_miss_giggles
 */
public class Ollivanders2 extends JavaPlugin
{
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
   private Ollivanders2TeleportEvents teleportEvents;

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

   // file config
   public static int chatDropoff = 15;
   public static ChatColor chatColor;
   public static boolean showLogInMessage;
   public static boolean bookLearning;
   public static boolean maxSpellLevel;
   public static boolean enableNonVerbalSpellCasting;
   public static boolean useSpellJournal;
   public static boolean useHostileMobAnimagi;
   public static boolean enableDeathExpLoss;
   public static boolean apparateLocations;
   public static int divinationMaxDays = 4;
   public static boolean useYears;
   public static boolean debug;
   public static boolean overrideVersionCheck;
   public static Material flooPowderMaterial;
   public static Material broomstickMaterial;
   public static boolean enableWitchDrop;
   public static boolean hourlyBackup;
   public static boolean archivePreviousBackup;
   public static int purgeBackupsAfter = 0;
   public static boolean useTranslations;
   public static boolean useStrictAnimagusConditions;

   // other config
   public static boolean worldGuardEnabled = false;
   public static boolean libsDisguisesEnabled = false;
   public static Ollivanders2WorldGuard worldGuardO2;

   public static final String pluginDir = "plugins/Ollivanders2/";

   /**
    * onDisable runs when the Minecraft server is shutting down.
    *
    * Primary functions are to reset transfigured blocks back to their correct type and save plugin data to disk.
    */
   public void onDisable ()
   {
      for (O2Spell proj : projectiles)
      {
         proj.kill();
      }

      revertAllTempBlocks();
      savePluginConfig();
      savePluginData();

      getLogger().info(this + " is now disabled!");
   }

   /**
    * Purge Old Plugin Data
    */
   public void purgeBackups (@NotNull Integer args, @Nullable CommandSender sender) {
      File archiveDir = new File(Ollivanders2.pluginDir.toString() + "/archive");
      // If archiveDirectory is not found, do not delete.
      if(!archiveDir.exists()) {
         return;
      }
      File filesList[] = archiveDir.listFiles();
      Date date = new Date();
      int i = 0;
      // If less than 3 files exist, do not delete.
      if(filesList.length < 3) {
         return;
      }

      for(File file : filesList) {
         if(file.lastModified() + args*86400000 < date.getTime()) {
            file.delete();
            i++;
         }
      }
      if(i == 0) {
         getLogger().info("No files purged");
         if(sender != null) {
            sender.sendMessage(chatColor + "No files have been purged.");
         }
      }
      else {
         getLogger().info("Purged " + i + " files from archives.");
         if(sender != null) {
            sender.sendMessage(chatColor + "Purged " + i + " files from archives.");
         }
      } return;
   }


   /**
    * Save plugin data to disk
    */
   public void savePluginData ()
   {
      Ollivanders2API.saveStationarySpells();
      Ollivanders2API.saveProphecies();

      houses.saveHouses();
      players.saveO2Players();

      APPARATE.saveApparateLocations();
   }

   /**
    * Save plugin config
    */
   private void savePluginConfig ()
   {
      if (!(new File(pluginDir + "config.yml").exists()))
      {
         saveDefaultConfig();
      }
   }

   /**
    * onEnable runs when the Minecraft server is starting up.
    *
    * Primary function is to set up static plugin data and load saved configs and data from disk.
    */
   public void onEnable ()
   {
      spells = new O2Spells(this);
      potions = new O2Potions(this);
      books = new O2Books(this);
      items = new O2Items(this);
      houses = new O2Houses(this);
      players = new O2Players(this);

      Ollivanders2API.init(this);

      // check for plugin data directory and config
      if (new File(pluginDir).mkdirs())
      {
         getLogger().info("Creating directory for Ollivanders2");
      }

      // read configuration
      initConfig();

      // check MC version
      if (!mcVersionCheck())
      {
         Bukkit.getPluginManager().disablePlugin(this);
         return;
      }

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

      // set up stationary spells
      Ollivanders2API.initStationarySpells(this);

      // spells
      spells.onEnable();

      // potions
      potions.onEnable();

      // books
      books.onEnable();

      // set up prophecies
      try
      {
         Ollivanders2API.initProphecies(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up prophecies.");
         e.printStackTrace();
      }

      // set up owl post
      try
      {
         Ollivanders2API.initOwlPost(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up owl post.");
         e.printStackTrace();
      }

      // set up all plugin crafting recipes
      initRecipes();

      // teleport events
      teleportEvents = new Ollivanders2TeleportEvents(this);

      getLogger().info(this + " is now enabled!");
   }

   /**
    * Load plugin config. If there is a config.yml file, load any config
    * set there and set everything else to default values.
    */
   private void initConfig ()
   {
      //
      // chatDropoff
      //
      if (getConfig().isSet("chatDropoff"))
      {
         chatDropoff = getConfig().getInt("chatDropoff");
      }
      if (chatDropoff <= 0)
      {
         chatDropoff = 15;
      }

      //
      // chatColor
      //
      if (getConfig().isSet("chatColor"))
      {
         chatColor = ChatColor.getByChar(Objects.requireNonNull(getConfig().getString("chatColor")));
      }
      if (chatColor == null)
      {
         chatColor = ChatColor.AQUA;
      }
      getLogger().info("Setting plugin message color to " + chatColor.toString());

      //
      // showLogInMessage
      //
      showLogInMessage = getConfig().getBoolean("showLogInMessage");
      if (showLogInMessage)
      {
         getLogger().info("Enabling player log in message.");
      }

      //
      // bookLearning
      //
      bookLearning = getConfig().getBoolean("bookLearning");
      if (bookLearning)
      {
         getLogger().info("Enabling book learning.");
      }

      //
      // maxSpells, can only be enabled when bookLearning is off.
      //
      if (!bookLearning)
      {
         maxSpellLevel = getConfig().getBoolean("maxSpellLevel");
      }
      else
         maxSpellLevel = false;

      if (maxSpellLevel)
      {
         getLogger().info("Max spell level on.");
      }

      //
      // nonVerbalSpellCasting
      //
      enableNonVerbalSpellCasting = getConfig().getBoolean("nonVerbalSpellCasting");
      if (enableNonVerbalSpellCasting)
      {
         getLogger().info("Enabling non-verbal spell casting.");
      }

      //
      // spellJournal
      //
      useSpellJournal = getConfig().getBoolean("spellJournal");
      if (useSpellJournal)
      {
         getLogger().info("Enabling spell journal.");
      }

      //
      // hostileMobAnimagi
      //
      useHostileMobAnimagi = getConfig().getBoolean("hostileMobAnimagi");
      if (useHostileMobAnimagi)
      {
         getLogger().info("Enabling hostile mob types for animagi.");
      }

      //
      // deathExpLoss
      //
      enableDeathExpLoss = getConfig().getBoolean("deathExpLoss");
      if (enableDeathExpLoss)
      {
         getLogger().info("Enabling death experience loss.");
      }

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
      {
         divinationMaxDays = getConfig().getInt("divinationMaxDays");
      }
      if (divinationMaxDays <= 0)
      {
         divinationMaxDays = 4;
      }

      //
      // years
      //
      useYears = getConfig().getBoolean("years");
      if (useYears)
      {
         getLogger().info("Enabling school years.");
      }

      //
      // flooPowder
      //
      if (getConfig().isSet("flooPowder"))
      {
         flooPowderMaterial = Material.getMaterial(Objects.requireNonNull(getConfig().getString("flooPowder")));
      }
      if (flooPowderMaterial == null)
      {
         flooPowderMaterial = Material.REDSTONE;
      }
      O2ItemType.FLOO_POWDER.setMaterial(flooPowderMaterial);

      //
      // broomstick
      //
      if (getConfig().isSet("broomstick"))
      {
         broomstickMaterial = Material.getMaterial(Objects.requireNonNull(getConfig().getString("broomstick")));
      }
      if (broomstickMaterial == null)
      {
         broomstickMaterial = Material.STICK;
      }
      O2ItemType.BROOMSTICK.setMaterial(broomstickMaterial);

      //
      // witchDrop
      //
      enableWitchDrop = getConfig().getBoolean("witchDrop");
      if (enableWitchDrop)
      {
         getLogger().info("Enabling witch wand drop");
      }

      //
      // debug and experimental
      //
      debug = getConfig().getBoolean("debug");
      if (debug)
      {
         getLogger().info("Enabling debug mode.");
      }

      overrideVersionCheck = getConfig().getBoolean("overrideVersionCheck");
      if (overrideVersionCheck)
      {
         getLogger().info("Experimental - disabling version checks. This version of Ollivanders2 may not be compatible with you MC server api.");
      }

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
      // Purging Backups
      //
      if(getConfig().isSet("purgeBackupsAfter"))
      {
         purgeBackupsAfter = getConfig().getInt("purgeBackupsAfter");
         if (purgeBackupsAfter <= 1)
         {
            getLogger().info("Enabling purging older backups.");
         }
      }
      else {
         purgeBackupsAfter = 0;
      }

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
   private void loadDependencyPlugins()
   {
      // set up libDisguises
      Plugin libsDisguises = Bukkit.getServer().getPluginManager().getPlugin("LibsDisguises");
      if (libsDisguises != null)
      {
         libsDisguisesEnabled = true;
         getLogger().info("LibsDisguises found, enabled entity transfiguration spells.");
      }
      else
      {
         getLogger().info("LibsDisguises not found, disabling entity transfiguration spells.");
      }

      // set up WorldGuard manager
      Plugin worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
      if (worldGuard == null)
      {
         worldGuardEnabled = false;
      }
      else
      {
         try
         {
            if (worldGuard instanceof WorldGuardPlugin)
            {
               worldGuardO2 = new Ollivanders2WorldGuard(this);
               worldGuardEnabled = true;
            }
         }
         catch (Exception e)
         {
            worldGuardEnabled = false;
         }
      }

      if (worldGuard != null)
      {
         getLogger().info("WorldGuard found, enabled WorldGuard features.");
      }
      else
      {
         getLogger().info("WorldGuard not found, disabled WorldGuard features.");
      }
   }

   /**
    * Set up all the Ollivanders2 crafting recipes
    */
   private void initRecipes ()
   {
      //broomstick recipe
      ItemStack broomstick = items.getItemByType(O2ItemType.BROOMSTICK, 1);
      if (broomstick != null)
      {
         ShapedRecipe bRecipe = new ShapedRecipe(new NamespacedKey(this, "broomstick"), broomstick);
         bRecipe.shape("  S", " S ", "W  ");
         bRecipe.setIngredient('S', Material.STICK);
         bRecipe.setIngredient('W', Material.WHEAT);
         getServer().addRecipe(bRecipe);
      }

      //floo powder recipe
      ItemStack flooPowder = items.getItemByType(O2ItemType.FLOO_POWDER, 8);
      if (flooPowder != null)
      {
         getServer().addRecipe(new FurnaceRecipe(new NamespacedKey(this, "floo_powder"), flooPowder, Material.ENDER_PEARL, 2, (5 * Ollivanders2Common.ticksPerSecond)));
      }
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
   @EventHandler(priority = EventPriority.HIGHEST)
   public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, @NotNull String[] args)
   {
      if (cmd.getName().equalsIgnoreCase("Ollivanders2") || cmd.getName().equalsIgnoreCase("Olli"))
      {
         return runOllivanders(sender, args);
      }
      else if (cmd.getName().equalsIgnoreCase("Quidd"))
      {
         if (sender.hasPermission("Ollivanders2.admin"))
         {
            return true;
         }
         return runQuidd(sender, args);
      }

      return false;
   }

   /**
    * The main Ollivanders2 command.
    *
    * @param sender the player who issued the command
    * @param args   the arguments for the command, if any
    * @return true if the /ollivanders command worked, false otherwise
    */
   private boolean runOllivanders(@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (args.length < 1)
      {
         usageMessageOllivanders(sender);
         return true;
      }

      String subCommand = args[0];

      //
      // Help
      //
      if (subCommand.equalsIgnoreCase("help"))
      {
         usageMessageOllivanders(sender);
         return true;
      }
      //
      // Wands
      //
      else if (subCommand.equalsIgnoreCase("wands") || subCommand.equalsIgnoreCase("wand"))
      {
         if (args.length == 1 && sender instanceof Player)
            return okitWands((Player) sender);
         else if (args.length > 1)
         {
            Player target = getServer().getPlayer(args[1]);
            if (target != null)
               return Ollivanders2API.getItems().getWands().giveRandomWand(target);
            else
               usageMessageWand(sender);
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
      else if (subCommand.equalsIgnoreCase("items") || subCommand.equalsIgnoreCase("item"))
      {
         if (sender instanceof Player)
            return runItems((Player) sender, args);
         else
            return false;
      }
      //
      // Purge
      //
      else if (subCommand.equalsIgnoreCase("purge"))
      {
         Integer num = purgeBackupsAfter;
         if(args.length > 2) {
            num = Integer.parseInt(args[1]);
         }
         purgeBackups(num, sender);
         return true;
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
      else if (subCommand.equalsIgnoreCase("floo"))
      {
         if (args.length == 1 && sender instanceof Player)
         {
            return giveFlooPowder((Player) sender);
         }
         else if (args.length > 1)
         {
            Player target = getServer().getPlayer(args[1]);
            if (target != null)
               return giveFlooPowder(target);
         }
         else
            return false;
      }
      //
      // Books
      //
      else if (subCommand.equalsIgnoreCase("books") || subCommand.equalsIgnoreCase("book"))
      {
         return books.runCommand(sender, args);
      }
      //
      // Player related commands
      //
      else if (subCommand.equalsIgnoreCase("summary"))
         return Ollivanders2API.getPlayers().runSummary(sender, args);
      else if (subCommand.equalsIgnoreCase("year") || subCommand.equalsIgnoreCase("years"))
         return Ollivanders2API.getPlayers().runYear(sender, args);
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

      return true;
   }

   /**
    * Usage message for the /ollivanders command.
    *
    * @param sender the command sender
    */
   private void usageMessageOllivanders(@NotNull CommandSender sender)
   {
      if (sender.hasPermission("Ollivanders2.admin"))
      {
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
   private boolean runHouse(@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (!O2Houses.useHouses)
      {
         sender.sendMessage(chatColor
                 + "House are not currently enabled for your server."
                 + "\nTo enable houses, update the Ollivanders2 config.yml setting to true and restart your server."
                 + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki");

         return true;
      }

      // parse args
      if (args.length >= 2)
      {
         String subCommand = args[1];

         if (subCommand.equalsIgnoreCase("sort") || subCommand.equalsIgnoreCase("forcesort"))
         {
            // sort player in to a house
            if (args.length < 4)
            {
               usageMessageHouseSort(sender);
               return true;
            }

            boolean forcesort = false;
            if (subCommand.equalsIgnoreCase("forcesort"))
               forcesort = true;

            return runSort(sender, args[2], args[3], forcesort);
         }
         else if (subCommand.equalsIgnoreCase("list"))
         {
            // list houses

            return runListHouse(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("points"))
         {
            // update house points

            return runHousePoints(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("reset"))
         {
            return houses.reset();
         }
      }

      usageMessageHouse(sender);

      return true;
   }

   /**
    * Usage message for /ollivanders house
    *
    * @param sender the command sender
    */
   private void usageMessageHouse(@NotNull CommandSender sender)
   {
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
   private boolean runListHouse(@NotNull CommandSender sender, @NotNull String[] args)
   {
      // list houses
      if (debug)
         getLogger().info("Running list houses");

      if (args.length > 2)
      {
         String targetHouse = args[2];

         O2HouseType house = houses.getHouseType(targetHouse);
         if (house != null)
         {
            List<String> members = houses.getHouseMembers(house);
            StringBuilder memberStr = new StringBuilder();

            if (members.isEmpty())
               memberStr.append("no members");
            else
            {
               for (String p : members)
               {
                  memberStr.append(p).append(" ");
               }
            }

            sender.sendMessage(chatColor + "Members of " + targetHouse + " are:\n" + memberStr.toString());

            return true;
         }

         sender.sendMessage(chatColor + "Invalid house name '" + targetHouse + "'");
      }

      StringBuilder houseNames = new StringBuilder();
      List<String> h = houses.getAllHouseNames();

      for (String name : h)
      {
         houseNames.append(name).append(" ");
      }

      sender.sendMessage(chatColor
              + "Ollivanders2 House are:\n" + houseNames.toString() + "\n"
              + "\nTo see the members of a specific house, run the command /ollivanders2 house list [house]"
              + "\nFor example, /ollivanders2 list Hufflepuff");

      return true;
   }

   /**
    * Sorts a player in to a specific house.  The player will not be sorted if:
    * a) the player is not online
    * b) an invalid house name is specified
    * c) they have already been sorted
    *
    * @param sender the player that issued the command
    * @param targetPlayer the player to sort
    * @param targetHouse the house to sort the player to
    * @param forcesort should the sort happen even if the player is already sorted
    * @return true unless an error occurred
    */
   private boolean runSort(@NotNull CommandSender sender, @NotNull String targetPlayer, @NotNull String targetHouse, boolean forcesort)
   {
      if (debug)
         getLogger().info("Running house sort");

      if (targetPlayer.length() < 1 || targetHouse.length() < 1)
      {
         usageMessageHouseSort(sender);
         return (true);
      }

      Player player = getServer().getPlayer(targetPlayer);
      if (player == null)
      {
         sender.sendMessage(chatColor
                 + "Unable to find a player named " + targetPlayer + " logged in to this server."
                 + "\nPlayers must be logged in to be sorted.");

         return true;
      }

      O2HouseType house = houses.getHouseType(targetHouse);

      if (house == null)
      {
         sender.sendMessage(chatColor + targetHouse + " is not a valid house name.");

         return true;
      }

      boolean success;
      if (forcesort)
      {
         houses.forceSetHouse(player, house);
         success = true;
      }
      else
      {
         success = houses.sort(player, house);
      }

      if (success)
      {
         sender.sendMessage(chatColor + targetPlayer + " has been successfully sorted in to " + targetHouse);
      }
      else
      {
         sender.sendMessage(chatColor + targetPlayer + " is already a member of " + houses.getHouse(player).getName());
      }

      return true;
   }

   /**
    * Usage message for /ollivanders house sort
    *
    * @param sender the player that issued the command
    */
   private void usageMessageHouseSort(@NotNull CommandSender sender)
   {
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
   private boolean runHousePoints(@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (debug)
         getLogger().info("Running house points");

      if (args.length > 2)
      {
         String option = args[2];
         if (debug)
            getLogger().info("runHousePoints: option = " + option);

         if (option.equalsIgnoreCase("reset"))
         {
            return houses.resetHousePoints();
         }

         if (args.length > 4)
         {
            String h = args[3];

            if (debug)
               getLogger().info("runHousePoints: house = " + h);

            O2HouseType houseType = null;
            try
            {
               houseType = houses.getHouseType(h);
            }
            catch (Exception e)
            {
               // nom nom nom
               if (debug)
                  getLogger().warning("runHousePoints: Exception getting house type.\n");
            }

            if (houseType == null)
            {
               if (debug)
                  getLogger().info("runHousePoints: invalid house name '" + h + "'");

               usageMessageHousePoints(sender);
               return true;
            }

            int value;

            try
            {
               value = Integer.parseInt(args[4]);

               if (debug)
                  getLogger().info("runHousePoints: value = " + value);
            }
            catch (Exception e)
            {
               if (debug)
                  getLogger().warning("runHousePoints: unable to parse int from " + args[4]);

               usageMessageHousePoints(sender);
               return true;
            }

            if (option.equalsIgnoreCase("add"))
            {
               return houses.addHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("subtract"))
            {
               return houses.subtractHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("set"))
            {
               return houses.setHousePoints(houseType, value);
            }
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
   private void usageMessageHousePoints(@NotNull CommandSender sender)
   {
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
    * The quidditch setup command.
    *
    * @param sender the player that issued the command
    * @param args   the arguments for the command, if any
    * @return true unless an error occurred
    */
   private boolean runQuidd(@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (args.length >= 1)
      {
         Player player;
         if (sender instanceof Player)
         {
            player = (Player) sender;
            Arena arena = new Arena(args[0], player.getLocation(), Arena.Size.MEDIUM);
            sender.sendMessage(chatColor + "The following arena was made: " + arena.toString());
         }
         else
         {
            sender.sendMessage(chatColor + "Only players can use the /Quidd command.");
         }
      }
      else
      {
         sender.sendMessage(chatColor + "Please include a name for your arena.");
      }
      return true;
   }

   /**
    * Toggle debug mode.
    *
    * @param sender the player that issued the command
    * @return true unless and error occurred
    */
   private boolean toggleDebug(@NotNull CommandSender sender)
   {
      debug = !debug;

      if (debug)
      {
         getLogger().info("Debug mode enabled.");
         sender.sendMessage(chatColor + "Ollivanders2 debug mode enabled.");
      }
      else
      {
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
   private boolean runReloadConfigs(@NotNull CommandSender sender)
   {
      reloadConfig();
      initConfig();

      sender.sendMessage(chatColor + "Config reloaded");
      return true;
   }

   /**
    * Give a player an item.
    *
    * @param player the player to give item to
    * @return true unless an error occurred
    */
   private boolean runItems(@NotNull Player player, @NotNull String[] args)
   {
      List<ItemStack> kit = new ArrayList<>();

      if (args.length < 2)
      {
         usageMessageItem(player);
      }
      else if (args[1].equals("list"))
      {
         player.sendMessage("\n" + listAllItems());
      }
      else if (args[1].equals("give") && args.length >= 4)
      {
         int amount = 0;
         try
         {
            amount = Integer.parseInt(args[2]);
         }
         catch (Exception e)
         {
            player.sendMessage("Unable to parse amount " + args[2]);
         }

         if (amount > 64)
            amount = 64;

         String itemName = Ollivanders2API.common.stringArrayToString(Arrays.copyOfRange(args, 3, args.length));
         ItemStack item = items.getItemStartsWith(itemName, amount);

         if (item == null)
         {
            player.sendMessage("Unable to find an item \"" + itemName + "\'");
            return true;
         }

         kit.add(item);
         Ollivanders2API.common.givePlayerKit(player, kit);
      }
      else
      {
         usageMessageItem(player);
      }

      return true;
   }

   /**
    * Usage message for Item subcommands.
    *
    * @param sender the player that issued the command
    */
   private void usageMessageItem(@NotNull CommandSender sender)
   {
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
   private String listAllItems()
   {
      StringBuilder stringBuilder = new StringBuilder();

      stringBuilder.append("Item list:\n");

      for (String item : items.getAllItems())
      {
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
   private boolean okitWands(@NotNull Player player)
   {
      List<ItemStack> kit = Ollivanders2API.getItems().getWands().getAllWands();

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
   }

   /**
    * Usage message for Wand subcommands.
    *
    * @param sender the player that issued the command
    */
   private void usageMessageWand(@NotNull CommandSender sender)
   {
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
   public List<O2Spell> getProjectiles()
   {
      return projectiles;
   }

   /**
    * Add a new spell projectile
    *
    * @param spell the spell projectile
    */
   public void addProjectile(@NotNull O2Spell spell)
   {
      projectiles.add(spell);
   }

   /**
    * Remove a spell projectile. This will make it stop if it has not already been killed.
    *
    * @param spell the spell projectile
    */
   public void removeProjectile(@NotNull O2Spell spell)
   {
      projectiles.remove(spell);
   }

   /**
    * Get all pending teleport events.
    *
    * @return an array of teleport events
    */
   @NotNull
   public List<Ollivanders2TeleportEvents.O2TeleportEvent> getTeleportEvents()
   {
      return teleportEvents.getTeleportEvents();
   }

   /**
    * Add a teleport event to the queue
    *
    * @param p    the player to teleport
    * @param to   teleport destination
    */
   public void addTeleportEvent(@NotNull Player p, @NotNull Location to)
   {
      addTeleportEvent(p, to, false);
   }

   /**
    * Add a teleport event with an explosion effect
    *
    * @param p                   the player to teleport
    * @param to                  teleport destination
    * @param explosionOnTeleport whether or not to do an explosion effect
    */
   public void addTeleportEvent(@NotNull Player p, @NotNull Location to, boolean explosionOnTeleport)
   {
      teleportEvents.addTeleportEvent(p, p.getLocation(), to, explosionOnTeleport);
   }

   /**
    * Remove a teleport event.
    *
    * @param event the event to remove
    */
   public void removeTeleportEvent(@NotNull Ollivanders2TeleportEvents.O2TeleportEvent event)
   {
      teleportEvents.removeTeleportEvent(event);
   }

   /**
    * Get the spell use count for the player for this spell
    *
    * @param player the player to get the count for
    * @param spell  the spell to get the count for
    * @return the spell count
    */
   public int getSpellCount(@NotNull Player player, @NotNull O2SpellType spell)
   {
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
   public void setSpellCount(@NotNull Player player, @NotNull O2SpellType spell, int count)
   {
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
   public int incrementSpellCount(@NotNull Player player, @NotNull O2SpellType spell)
   {
      //returns the incremented spell count
      UUID pid = player.getUniqueId();
      O2Player o2p = getO2Player(player);

      o2p.incrementSpellCount(spell);
      Ollivanders2API.getPlayers().updatePlayer(pid, o2p);

      return o2p.getSpellCount(spell);
   }

   /**
    * Increment the potion use count for a player.
    *
    * @param player     the player to increment the count for
    * @param potionType the potion to increment
    */
   public void incrementPotionCount(@NotNull Player player, @NotNull O2PotionType potionType)
   {
      //returns the incremented potion count
      UUID pid = player.getUniqueId();
      O2Player o2p = getO2Player(player);

      o2p.incrementPotionCount(potionType);
      Ollivanders2API.getPlayers().updatePlayer(pid, o2p);
   }

   /**
    * Gets the O2Player associated with the Player
    *
    * @param player the player to get
    * @return O2Player object for this player
    */
   public O2Player getO2Player(@NotNull Player player)
   {
      UUID pid = player.getUniqueId();
      O2Player o2p = Ollivanders2API.getPlayers().getPlayer(pid);

      if (o2p == null)
      {
         Ollivanders2API.getPlayers().addPlayer(pid, player.getDisplayName());

         o2p = Ollivanders2API.getPlayers().getPlayer(pid);
      }

      return o2p;
   }

   /**
    * Sets the player's OPlayer by their player name
    *
    * @param player      the player
    * @param o2p the OPlayer associated with the player
    */
   public void setO2Player (@NotNull Player player, @NotNull O2Player o2p)
   {
      if (!(player instanceof NPC))
      {
         Ollivanders2API.getPlayers().updatePlayer(player.getUniqueId(), o2p);
      }
   }

   /**
    * Gets the set of all player UUIDs.
    *
    * @return a list of all player MC UUIDs
    */
   @NotNull
   public ArrayList<UUID> getO2PlayerIDs()
   {
      return Ollivanders2API.getPlayers().getPlayerIDs();
   }

   /**
    * Can this player cast this spell?
    *
    * @param player  Player to check
    * @param spell   Spell to check
    * @param verbose Whether or not to inform the player of why they cannot cast a spell
    * @return True if the player can cast this spell, false if not
    */
   public boolean canCast(@NotNull Player player, @NotNull O2SpellType spell, boolean verbose)
   {
      // players cannot cast spells when in animagus form, except the spell to change form
      if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT))
      {
         if (spell == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
         {
            return true;
         }
         else
         {
            player.sendMessage(Ollivanders2.chatColor + "You cannot cast spells while in your animagus form.");
            return false;
         }
      }

      if (player.isPermissionSet("Ollivanders2." + spell.toString()))
      {
         if (!player.hasPermission("Ollivanders2." + spell.toString()))
         {
            if (verbose)
            {
               player.sendMessage(chatColor + "You do not have permission to use " + spell.toString());
            }
            return false;
         }
      }

      O2Player p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
      if (p == null)
         return false;

      boolean coolDown = System.currentTimeMillis() < p.getSpellLastCastTime(spell);

      if (coolDown)
      {
         if (verbose)
         {
            spellCoolDownMessage(player);
         }
         return false;
      }

      boolean cast = spells.isSpellTypeAllowed(player.getLocation(), spell);

      if (!cast && verbose)
      {
         spellCannotBeCastMessage(player);
      }
      return cast;
   }

   /**
    * Check to see what MC version is being run to determine what Ollivanders2 features are supported.
    */
   private boolean mcVersionCheck()
   {
      if (overrideVersionCheck)
         return true;

      String versionString = Bukkit.getBukkitVersion();

      if (versionString.startsWith("1.17") || versionString.startsWith("1.18"))
      {
         return true;
      }
      else // anything lower than 1.14 set to 0 because this version of the plugin cannot run on < 1.14
      {
         getLogger().warning("MC version " + versionString + ". This version of Ollivanders2 requires 1.17 or higher.");
         return false;
      }
   }

   /**
    * Give floo powder to player.
    *
    * @param player the player to give the floo powder to
    * @return true if successful, false otherwise
    * @since 2.2.4
    */
   private boolean giveFlooPowder(@NotNull Player player)
   {
      ItemStack flooPowder = Ollivanders2API.getItems().getItemByType(O2ItemType.FLOO_POWDER, 8);

      if (flooPowder == null)
         return false;

      List<ItemStack> fpStack = new ArrayList<>();
      fpStack.add(flooPowder);

      Ollivanders2API.common.givePlayerKit(player, fpStack);

      return true;
   }

   /**
    * When a spell is not allowed be cast, such as from WorldGuard protection, send a message.
    * This is not the message to use for bookLearning enforcement.
    *
    * @since 2.2.5
    * @param player the player that cast the spell
    */
   public void spellCannotBeCastMessage(@NotNull Player player)
   {
      player.sendMessage(chatColor + "A powerful protective magic prevents you from casting this spell here.");
   }

   /**
    * When a spell cannot be used in a location, such as from WorldGuard protection, send a message.
    * This is not the message to use for bookLearning enforcement.
    *
    * @param player the player that cast the spell
    * @since 2.3
    */
   public void spellFailedMessage(@NotNull Player player)
   {
      player.sendMessage(chatColor + "A powerful protective magic blocks your spell.");
   }

   /**
    * When a spell is not allowed be cast, such as from WorldGuard protection, send a message.
    * This is not the message to use for bookLearning enforcement.
    *
    * @since 2.2.5
    * @param player the player to send the cooldown message to
    */
   public void spellCoolDownMessage(@NotNull Player player)
   {
      player.sendMessage(chatColor + "You are too tired to cast this spell right now.");
   }

   /**
    * Potions subcommand
    *
    * @param sender the command sender
    * @param args   args to the command
    * @return true
    */
   public boolean runPotions (@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (args.length > 1)
      {
         String subCommand = args[1];

         if (subCommand.equalsIgnoreCase("ingredient"))
         {
            if (args.length > 2)
            {
               if (args[2].equalsIgnoreCase("list"))
               {
                  return listAllIngredients(sender);
               }
               else if (sender instanceof Player)
               {
                  // potion ingredient mandrake leaf
                  String [] subArgs = Arrays.copyOfRange(args, 2, args.length);
                  return giveItem((Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
               }
               else
                  return false;
            }
         }
         else if (subCommand.equalsIgnoreCase("list"))
         {
            return listAllPotions(sender);
         }
         else if (subCommand.equalsIgnoreCase("all"))
         {
            if (sender instanceof Player)
            {
               return giveAllPotions((Player) sender);
            }
            else
               return false;
         }
         else if (subCommand.equalsIgnoreCase("give"))
         {
            if (sender instanceof Player)
            {
               if (args.length > 3)
               {
                  // potions give fred memory potion
                  Player targetPlayer = getServer().getPlayer(args[2]);

                  if (targetPlayer != null)
                  {
                     String[] subArgs = Arrays.copyOfRange(args, 3, args.length);
                     return givePotion((Player) sender, targetPlayer, Ollivanders2API.common.stringArrayToString(subArgs));
                  }
               }
            }
            else
               return false;
         }
         else
         {
            if (sender instanceof Player)
            {
               // potions memory potion
               String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
               return givePotion((Player)sender, (Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
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
    * @param player     the player to give the potion to
    * @param potionName the potion to give the player
    */
   public boolean givePotion(@NotNull Player sender, @NotNull Player player, @NotNull String potionName)
   {
      O2PotionType potionType = null;

      // need to iterate rather than call potions.getPotionTypeByName so we can do startsWith
      for (O2PotionType p : O2PotionType.values())
      {
         if (p.getPotionName().toLowerCase().startsWith(potionName.toLowerCase()))
         {
            potionType = p;
            break;
         }
      }

      if (potionType == null)
      {
         sender.sendMessage(chatColor + "Unable to find potion " + potionName);

         return true;
      }

      O2Potion potion = potions.getPotionFromType(potionType);

      if (potion == null)
         return true;

      ItemStack brewedPotion = potion.brew(sender, false);
      List<ItemStack> kit = new ArrayList<>();
      kit.add(brewedPotion);

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
   }

   /**
    * Usage message for potion subcommands
    *
    * @param sender the command sender
    */
   private void usageMessagePotions (CommandSender sender)
   {
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
   private boolean listAllIngredients(@NotNull CommandSender sender)
   {
      List<String> ingredientList = O2Potions.getAllIngredientNames(this);
      StringBuilder displayString = new StringBuilder();
      displayString.append("Ingredients:");

      for (String name : ingredientList)
      {
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
   private boolean listAllPotions(@NotNull CommandSender sender)
   {
      StringBuilder displayString = new StringBuilder();
      displayString.append("Potions:");

      List<String> potionNames = potions.getAllPotionNames();
      for (String name : potionNames)
      {
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
    * @param name the name of the ingredient
    * @return true
    */
   private boolean giveItem(@NotNull Player player, @NotNull String name)
   {
      List<ItemStack> kit = new ArrayList<>();
      ItemStack item = items.getItemStartsWith(name, 1);

      if (item != null)
      {
         kit.add(item);
         Ollivanders2API.common.givePlayerKit(player, kit);
      }

      return true;
   }

   /**
    * Gives the specified player 1 of every Ollivanders2 potion.
    *
    * @param player the player to give the potions to
    * @return true unless an error occurred
    */
   private boolean giveAllPotions (@NotNull Player player)
   {
      if (debug)
         getLogger().info("Running givePotions...");

      List<ItemStack> kit = new ArrayList<>();

      for (O2Potion potion : potions.getAllPotions())
      {
         ItemStack brewedPotion = potion.brew(player, false);

         if (debug)
            getLogger().info("Adding " + potion.getName());

         kit.add(brewedPotion);
      }

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
   }

   /**
    * Prophecies sub-command
    *
    * @param sender the command sender
    * @return true unless an error occurred
    */
   public boolean runProphecies (@NotNull CommandSender sender)
   {
      StringBuilder output = new StringBuilder();
      List<String> prophecies = Ollivanders2API.getProphecies(this).getProphecies();

      if (prophecies.size() > 0)
      {
         output.append("Prophecies:\n");

         for (String prophecy : prophecies)
         {
            output.append(prophecy).append("\n");
         }
      }
      else
      {
         output.append("There are no unfulfilled prophecies.");
      }

      sender.sendMessage(chatColor + output.toString());

      return true;
   }

   /**
    * Run the apparate location subcommand
    *
    * @param sender the command sender
    * @param args
    * @return
    */
   public boolean runApparateLocation (@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (!(sender instanceof Player))
         return false;

      if (!apparateLocations)
      {
         sender.sendMessage(chatColor
                 + "Apprate locations are not currently enabled for your server."
                 + "\nTo enable apparate locations, update the Ollivanders2 config.yml setting to true and restart your server."
                 // TODO update wiki and uncomment
                 //+ "\nFor help, see our documentation at ..."
         );

         return true;
      }

      if (args.length >= 2)
      {
         String subCommand = args[1];

         if (subCommand.equalsIgnoreCase("list"))
         {
            APPARATE.listApparateLocations((Player)sender);
            return true;
         }
         else if (subCommand.equalsIgnoreCase("add") || subCommand.equalsIgnoreCase("update"))
         {
            if (args.length < 6)
            {
               usageMessageApparateLocation(sender);
               return true;
            }

            String locationName = args[2];

            if (subCommand.equalsIgnoreCase("add") && APPARATE.doesLocationExist(locationName))
            {
               sender.sendMessage(chatColor + "An apparate location with that name already exists. If you want to update the location coordinates, use /ollivanders2 apparateLoc update.");
               return true;
            }
            else if (subCommand.equalsIgnoreCase("update") && !APPARATE.doesLocationExist(locationName))
            {
               sender.sendMessage(chatColor + "An apparate location with that name does not exist. If you want to add a location coordinates, use /ollivanders2 apparateLoc add.");
               return true;
            }

            String xCoord = args[3];
            String yCoord = args[4];
            String zCoord = args[5];
            double x;
            double y;
            double z;
            try
            {
               x = Double.parseDouble(xCoord);
               y = Double.parseDouble(yCoord);
               z = Double.parseDouble(zCoord);
            }
            catch (Exception e)
            {
               sender.sendMessage(chatColor + "Invalid coordinates for location.");
               usageMessageApparateLocation(sender);
               return true;
            }

            World world = ((Player) sender).getWorld();

            APPARATE.addLocation(locationName, world, x, y, z);

            sender.sendMessage(chatColor + "Apparate location " + locationName + " set. \nTo list all locations, use /ollivanders2 apparateLoc list.");
            return true;
         }
         else if (subCommand.equalsIgnoreCase("remove"))
         {
            if (args.length < 3)
            {
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
   private void usageMessageApparateLocation (CommandSender sender)
   {
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
   public void addTempBlock(@NotNull Block block, @NotNull Material originalMaterial)
   {
      tempBlocks.put(block, originalMaterial);
   }

   /**
    * Reverts a temp block back to its original type.
    *
    * @param block the block to remove
    */
   public void revertTempBlock(@NotNull Block block)
   {
      if (tempBlocks.containsKey(block))
      {
         revertBlockType(block);

         tempBlocks.remove(block);
      }
   }

   /**
    * Revert a temporarily changed block back to its original type.
    *
    * @param block the changed block
    */
   private void revertBlockType(@NotNull Block block)
   {
      if (tempBlocks.containsKey(block))
      {
         Material material = tempBlocks.get(block);

         try
         {
            block.setType(material);
         }
         catch (Exception e)
         {
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
   public boolean isTempBlock(@NotNull Block block)
   {
      return tempBlocks.containsKey(block);
   }

   /**
    * Returns the original material for a temp block.
    *
    * @param block the temp block
    * @return the material, if found, null otherwise
    */
   public Material getTempBlockOriginalMaterial (@NotNull Block block)
   {
      return tempBlocks.getOrDefault(block, null);
   }

   /**
    * Revert all temporary blocks to their original type.
    */
   private void revertAllTempBlocks ()
   {
      for (Block block : tempBlocks.keySet())
      {
         revertBlockType(block);
      }

      tempBlocks.clear();
   }
}