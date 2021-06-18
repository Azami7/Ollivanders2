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

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import quidditch.Arena;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.O2WandCoreType;
import net.pottercraft.ollivanders2.player.O2WandWoodType;
import net.pottercraft.ollivanders2.player.Year;
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
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
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
   final private Map<Block, Material> tempBlocks = new HashMap<>();

   /**
    * All pending teleport events
    */
   private Ollivanders2TeleportEvents teleportEvents;

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
   public static int divinationMaxDays = 4;
   public static boolean useHouses;
   public static boolean displayMessageOnSort;
   public static boolean useYears;
   public static boolean debug;
   public static boolean overrideVersionCheck;
   public static Material flooPowderMaterial;
   public static Material broomstickMaterial;
   public static boolean enableWitchDrop;
   private ConfigurationSection zoneConfig;
   public static boolean hourlyBackup;
   public static boolean archivePreviousBackup;

   // other config
   public static boolean worldGuardEnabled = false;
   public static boolean libsDisguisesEnabled = false;
   public static Ollivanders2WorldGuard worldGuardO2;

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

      savePluginData();

      getLogger().info(this + " is now disabled!");
   }

   /**
    * Save plugin data to disk
    */
   public void savePluginData ()
   {
      Ollivanders2API.saveStationarySpells();
      Ollivanders2API.saveProphecies();
      if (useHouses)
      {
         Ollivanders2API.saveHouses();
      }
      Ollivanders2API.savePlayers();
   }

   /**
    * onEnable runs when the Minecraft server is starting up.
    *
    * Primary function is to set up static plugin data and load saved configs and data from disk.
    */
   public void onEnable ()
   {
      Ollivanders2API.init(this);

      // set up event listeners
      Listener playerListener = new OllivandersListener(this);
      getServer().getPluginManager().registerEvents(playerListener, this);

      // check for plugin data directory
      if (new File("plugins/Ollivanders2/").mkdirs())
      {
         getLogger().info("Creating directory for Ollivanders2");
         this.saveDefaultConfig();
      }

      // read configuration
      initConfig();

      // check MC version
      if (!mcVersionCheck())
      {
         Bukkit.getPluginManager().disablePlugin(this);
         return;
      }

      // set up scheduler
      OllivandersSchedule schedule = new OllivandersSchedule(this);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this, schedule, 20L, 1L);

      // set up dependencies
      loadDependenciesPlugins();

      // set up players
      try
      {
         Ollivanders2API.initPlayers(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up players.");
         e.printStackTrace();
      }

      // set up houses
      try
      {
         Ollivanders2API.initHouses(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up houses.");
         e.printStackTrace();
      }

      // set up items
      try
      {
         Ollivanders2API.initItems(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up items.");
         e.printStackTrace();
      }

      // set up potions
      try
      {
         Ollivanders2API.initPotions(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up potions.");
         e.printStackTrace();
      }

      // set up spells
      try
      {
         Ollivanders2API.initSpells(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up spells.");
         e.printStackTrace();
      }

      // set up stationary spells
      Ollivanders2API.initStationarySpells(this);

      // create books
      try
      {
         Ollivanders2API.initBooks(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up books.");
         e.printStackTrace();
      }

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
      // houses
      //
      useHouses = getConfig().getBoolean("houses");
      if (useHouses)
      {
         getLogger().info("Enabling school houses.");
      }
      displayMessageOnSort = getConfig().getBoolean("displayMessageOnSort");

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
      // Zones
      //
      zoneConfig = getConfig().getConfigurationSection("zones");

      //
      // Save options
      //
      hourlyBackup = getConfig().getBoolean("hourlyBackup");
      if (hourlyBackup)
         getLogger().info("Enabling hourly backups.");

      archivePreviousBackup = getConfig().getBoolean("archivePreviousBackup");
      if (archivePreviousBackup)
         getLogger().info("Enabling backup archiving.");
   }

   /**
    * Load dependency plugins or turn of the features that require them if
    * they are not present.
    */
   private void loadDependenciesPlugins ()
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
      ItemStack broomstick = Ollivanders2API.getItems(this).getItemByType(O2ItemType.BROOMSTICK, 1);
      if (broomstick != null)
      {
         ShapedRecipe bRecipe = new ShapedRecipe(new NamespacedKey(this, "broomstick"), broomstick);
         bRecipe.shape("  S", " S ", "W  ");
         bRecipe.setIngredient('S', Material.STICK);
         bRecipe.setIngredient('W', Material.WHEAT);
         getServer().addRecipe(bRecipe);
      }

      //floo powder recipe
      ItemStack flooPowder = Ollivanders2API.getItems(this).getItemByType(O2ItemType.FLOO_POWDER, 8);
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
      } else if (cmd.getName().equalsIgnoreCase("Quidd"))
      {
         if (sender.isOp())
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
      if (!sender.isOp())
      {
         return playerSummary(sender, (Player) sender);
      }

      // parse args
      if (args.length >= 1)
      {
         String subCommand = args[0];

         if (subCommand.equalsIgnoreCase("help"))
         {
            usageMessageOllivanders(sender);
            return true;
         }
         else if (subCommand.equalsIgnoreCase("wands") || subCommand.equalsIgnoreCase("wand"))
         {
            if (args.length == 1)
               return okitWands((Player) sender);
            else
            {
               Player target = getServer().getPlayer(args[1]);
               if (target != null)
                  return giveRandomWand(target);
            }
         }
         else if (subCommand.equalsIgnoreCase("reload"))
            return runReloadConfigs(sender);
         else if (subCommand.equalsIgnoreCase("items") || subCommand.equalsIgnoreCase("item"))
         {
            return runItems((Player) sender, args);
         }
         else if (subCommand.equalsIgnoreCase("house") || subCommand.equalsIgnoreCase("houses"))
            return runHouse(sender, args);
         else if (subCommand.equalsIgnoreCase("debug"))
            return toggleDebug(sender);
         else if (subCommand.equalsIgnoreCase("floo"))
         {
            if (args.length == 1)
            {
               return giveFlooPowder((Player) sender);
            }
            else
            {
               Player target = getServer().getPlayer(args[1]);
               if (target != null)
                  return giveFlooPowder(target);
            }
         }
         else if (subCommand.equalsIgnoreCase("books") || subCommand.equalsIgnoreCase("book"))
         {
            return runBooks(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("summary"))
         {
            if (args.length == 1)
            {
               return playerSummary(sender, (Player) sender);
            }
            else
            {
               Player target = getServer().getPlayer(args[1]);
               if (target != null)
                  return playerSummary(sender, target);
               else
               {
                  usageMessageOllivanders(sender);
                  return true;
               }
            }
         }
         else if (subCommand.equalsIgnoreCase("potions") || subCommand.equalsIgnoreCase("potion"))
         {
            return runPotions(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("year") || subCommand.equalsIgnoreCase("years"))
         {
            return runYear(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("effect") || subCommand.equalsIgnoreCase("effects"))
         {
            return runEffect(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("prophecy") || subCommand.equalsIgnoreCase("prophecies"))
         {
            return runProphecies(sender);
         }
      }

      usageMessageOllivanders(sender);

      return true;
   }

   /**
    * Displays a summary of player info for the Ollivanders2 plugin
    *
    * @param sender the player who issued the command
    * @return true if no error occurred
    */
   public boolean playerSummary(@NotNull CommandSender sender, @NotNull Player player)
   {
      if (debug)
         getLogger().info("Running playerSummary");

      O2Player o2p = getO2Player(player);
      if (o2p == null)
      {
         sender.sendMessage(chatColor + "Unable to find player.");
         return true;
      }

      StringBuilder summary = new StringBuilder();

      summary.append("Player summary:\n\n");

      // wand type
      if (o2p.foundWand())
      {
         String wandlore = o2p.getDestinedWandLore();
         summary.append("\nWand Type: ").append(wandlore);

         O2SpellType masterSpell = o2p.getMasterSpell();
         if (masterSpell != null)
         {
            summary.append("\nMaster Spell: ").append(Ollivanders2API.common.enumRecode(masterSpell.toString().toLowerCase()));
         }

         summary.append("\n");
      }

      // sorted
      if (useHouses)
      {
         if (Ollivanders2API.getHouses(this).isSorted(player))
         {
            String house = Ollivanders2API.getHouses(this).getHouse(player).getName();
            summary.append("\nHouse: ").append(house).append("\n");
         }
         else
         {
            summary.append("\nYou have not been sorted.\n");
         }
      }

      //year
      if (useYears)
         summary.append("\nYear: ").append(o2p.getYear().getIntValue()).append("\n");

      //animagus
      if (o2p.isAnimagus())
      {
         EntityType animagusForm = o2p.getAnimagusForm();
         if (animagusForm != null)
         {
            summary.append("\nAnimagus Form: ").append(Ollivanders2API.common.enumRecode(animagusForm.toString()));
         }
      }

      // effects
      if (sender.isOp())
      {
         List<O2EffectType> effects = Ollivanders2API.getPlayers(this).playerEffects.getEffects(o2p.getID());
         summary.append("\n\nAffected by:\n");

         if (effects.isEmpty())
         {
            summary.append("Nothing");
         }
         else
         {
            for (O2EffectType effectType : effects)
            {
               summary.append(Ollivanders2API.common.enumRecode(effectType.toString())).append("\n");
            }
         }

         summary.append("\n");
      }

      // spells
      Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();

      if (knownSpells.size() > 0)
      {
         summary.append("\n\nKnown Spells and Spell Level:");

         for (O2SpellType spellType : O2SpellType.values())
         {
            if (knownSpells.containsKey(spellType) && Ollivanders2API.getSpells(this).isLoaded(spellType))
            {
               summary.append("\n* ").append(spellType.getSpellName()).append(" ").append(knownSpells.get(spellType).toString());
            }
         }
      }
      else
      {
         summary.append("\n\nYou have not learned any spells.");
      }

      Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
      if (!knownPotions.isEmpty())
      {
         summary.append("\n\nKnown potions and Potion Level:");

         for (O2PotionType potionType : O2PotionType.values())
         {
            if (knownPotions.containsKey(potionType) && Ollivanders2API.getPotions(this).isLoaded(potionType))
            {
               summary.append("\n* ").append(potionType.getPotionName()).append(" ").append(knownPotions.get(potionType).toString());
            }
         }
      }
      else
      {
         summary.append("\n\nYou have not learned any potions.");
      }

      sender.sendMessage(chatColor + summary.toString());

      return true;
   }

   /**
    * Usage message for the /ollivanders command.
    *
    * @param sender the command sender
    */
   private void usageMessageOllivanders(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "You are running Ollivanders2 version " + this.getDescription().getVersion() + "\n"
              + "\nOllivanders2 commands:"
              + "\nwands - gives a complete set of wands"
              + "\nbooks - gives a complete set of spell books"
              + "\nitems - gives a complete set of items"
              // + "\nquidd - creates a quidditch pitch"
              + "\nhouse - view and manage houses and house points"
              + "\nyear - view and manage player years"
              + "\nsummary - gives a summary of wand type, house, year, and known spells"
              + "\nreload - reload the Ollivanders2 configs"
              + "\ndebug - toggles Ollivanders2 plugin debug output\n"
              + "\n" + "To run a command, type '/ollivanders2 [command]'."
              + "\nFor example, '/ollivanders2 wands");
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
      if (!useHouses)
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
            return Ollivanders2API.getHouses(this).reset();
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

         O2HouseType house = Ollivanders2API.getHouses(this).getHouseType(targetHouse);
         if (house != null)
         {
            List<String> members = Ollivanders2API.getHouses(this).getHouseMembers(house);
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
      List<String> h = Ollivanders2API.getHouses(this).getAllHouseNames();

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

      O2HouseType house = Ollivanders2API.getHouses(this).getHouseType(targetHouse);

      if (house == null)
      {
         sender.sendMessage(chatColor + targetHouse + " is not a valid house name.");

         return true;
      }

      boolean success;
      if (forcesort)
      {
         Ollivanders2API.getHouses(this).forceSetHouse(player, house);
         success = true;
      }
      else
      {
         success = Ollivanders2API.getHouses(this).sort(player, house);
      }

      if (success)
      {
         sender.sendMessage(chatColor + targetPlayer + " has been successfully sorted in to " + targetHouse);
      }
      else
      {
         sender.sendMessage(chatColor + targetPlayer + " is already a member of " + Ollivanders2API.getHouses(this).getHouse(player).getName());
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
            return Ollivanders2API.getHouses(this).resetHousePoints();
         }

         if (args.length > 4)
         {
            String h = args[3];

            if (debug)
               getLogger().info("runHousePoints: house = " + h);

            O2HouseType houseType = null;
            try
            {
               houseType = Ollivanders2API.getHouses(this).getHouseType(h);
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
               return Ollivanders2API.getHouses(this).addHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("subtract"))
            {
               return Ollivanders2API.getHouses(this).subtractHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("set"))
            {
               return Ollivanders2API.getHouses(this).setHousePoints(houseType, value);
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
    * The year subCommand for managing everything related to years.
    *
    * @param sender the player that issued the command
    * @param args   the arguments for the command, if any
    * @return true unless an error occurred
    */

   private boolean runYear(@NotNull CommandSender sender, @NotNull String[] args)
   {
      if (!useYears)
      {
         sender.sendMessage(chatColor
                 + "Years are not currently enabled for your server."
                 + "\nTo enable years, update the Ollivanders2 config.yml setting to true and restart your server."
                 + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki");

         return true;
      }

      // parse args
      if (args.length >= 2)
      {
         String subCommand = args[1];

         if (subCommand.equalsIgnoreCase("set"))
         {
            if (args.length < 4)
            {
               usageMessageYearSet(sender);
               return true;
            }

            return runYearSet(sender, args[2], args[3]);
         }
         else if (subCommand.equalsIgnoreCase("promote"))
         {
            if (args.length < 3)
            {
               usageMessageYearPromote (sender);
               return true;
            }

            return runYearChange(sender, args[2], 1);
         }
         else if (subCommand.equalsIgnoreCase("demote"))
         {
            if (args.length < 3)
            {
               usageMessageYearDemote(sender);
               return true;
            }

            return runYearChange(sender, args[2], -1);
         }
         else if (args.length == 2)
         {
            String p = args[1];
            if (p.length() < 1)
            {
               usageMessageHouseSort(sender);
               return true;
            }
            Player player = getServer().getPlayer(p);
            if (player == null)
            {
               sender.sendMessage(chatColor + "Unable to find a player named " + p + ".\n");
               return true;
            }

            O2Player o2p = getO2Player(player);
            if (o2p != null)
               sender.sendMessage(chatColor + "Player " + p + " is in year " + o2p.getYear().getIntValue());
            else
               sender.sendMessage(chatColor + "Unable to find player.");

            return true;
         }
      }

      usageMessageYear(sender);

      return true;
   }

   /**
    * Display the usage message for /ollivanders2 year set
    *
    * @param sender the player that issued the command
    */
   private void usageMessageYearSet(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Usage: /ollivanders2 year set [player] [year]"
              + "\nyear - must be a number between 1 and 7"
              + "\nExample: /ollivanders2 year set Harry 5");
   }

   /**
    * Display the usage message for /ollivanders2 year promote
    *
    * @param sender the player that issued the command
    */
   private void usageMessageYearPromote(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Usage: /ollivanders2 year promote [player]"
              + "\nExample: /ollivanders2 year promote Harry");
   }

   /**
    * Display the usage message for /ollivanders2 year demote
    *
    * @param sender the player that issued the command
    */
   private void usageMessageYearDemote(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Usage: /ollivanders2 year demote [player]"
              + "\nExample: /ollivanders2 year demote Harry");
   }

   /**
    * Usage message for Year subcommands.
    *
    * @param sender the player that issued the command
    */
   private void usageMessageYear(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Year commands: "
              + "\nset - sets a player's year, years must be between 1 and 7"
              + "\npromote - increases a player's year by 1 year"
              + "\ndemote - decreases a player's year by 1 year"
              + "\n<player> - tells you the year or a player\n");
   }

   /**
    * Run the command to set a player's year
    *
    * @param sender       the player that issued the command
    * @param targetPlayer the player to set the year for
    * @param targetYear   the year to set for the player
    * @return true unless an error occurred
    */
   private boolean runYearSet(@NotNull CommandSender sender, @NotNull String targetPlayer, @NotNull String targetYear)
   {
      if (targetPlayer.length() < 1 || targetYear.length() < 1)
      {
         usageMessageYearSet(sender);
         return true;
      }
      Player player = getServer().getPlayer(targetPlayer);
      if (player == null)
      {
         sender.sendMessage(chatColor + "Unable to find a player named " + targetPlayer + ".\n");
         return true;
      }
      O2Player o2p = getO2Player(player);
      int y;
      try
      {
         y = Integer.parseInt(targetYear);
      }
      catch (NumberFormatException e)
      {
         usageMessageYearSet(sender);
         return true;
      }

      if (y < 1 || y > 7)
      {
         usageMessageYearSet(sender);
         return true;
      }
      Year year = O2PlayerCommon.intToYear(y);
      if (year != null)
         o2p.setYear(year);

      return true;
   }

   /**
    * Run promote and demote year commands
    *
    * @param sender       the player that issued the command
    * @param targetPlayer the player to promote or demote
    * @param yearChange   the year to change the player to
    * @return true unless an error occurred
    */
   private boolean runYearChange(@NotNull CommandSender sender, @NotNull String targetPlayer, int yearChange)
   {
      Player player = getServer().getPlayer(targetPlayer);
      if (player == null)
      {
         sender.sendMessage(chatColor + "Unable to find a player named " + targetPlayer + ".\n");
         return true;
      }
      O2Player o2p = getO2Player(player);
      if (o2p == null)
      {
         sender.sendMessage(chatColor + "Unable to find player.");
         return true;
      }

      int y = o2p.getYear().getIntValue() + yearChange;
      if (y > 0 && y < 8)
      {
         Year year = O2PlayerCommon.intToYear(y);

         if (year != null)
            o2p.setYear(year);
      }
      return true;
   }

   /**
    * The effects command, this is for testing purposes only and is not listed in the usage message.
    *
    * @param sender the player that issued the command
    * @param args   the arguments for the command, if any
    * @return true unless an error occurred
    */
   private boolean runEffect(@NotNull CommandSender sender, @NotNull String[] args)
   {
      // olli effect <effect>
      if (args.length >= 2)
      {
         String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
         String effectName = Ollivanders2API.common.stringArrayToString(subArgs).toUpperCase();

         O2EffectType effectType;
         try
         {
            effectType = O2EffectType.valueOf(effectName);
         }
         catch (Exception e)
         {
            sender.sendMessage(chatColor + "No effect named " + effectName + ".\n");
            return true;
         }

         if (Ollivanders2API.getPlayers(this).playerEffects.hasEffect(((Player) sender).getUniqueId(), effectType))
         {
            Ollivanders2API.getPlayers(this).playerEffects.removeEffect(((Player) sender).getUniqueId(), effectType);
            sender.sendMessage(chatColor + "Removed " + effectName + " from " + sender + ".\n");
         }
         else
         {
            Class<?> effectClass = effectType.getClassName();
            getLogger().info("Trying to add effect " + effectClass);

            O2Effect effect;

            try
            {
               effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(this, 1200, ((Player) sender).getUniqueId());
            }
            catch (Exception e)
            {
               sender.sendMessage(chatColor + "Failed to add effect " + effectName + " to " + sender + ".\n");
               e.printStackTrace();
               return true;
            }

            Ollivanders2API.getPlayers(this).playerEffects.addEffect(effect);
            sender.sendMessage(chatColor + "Added " + effectName + " to " + sender + ".\n");
         }
      }
      else
      {
         sender.sendMessage(chatColor + "Not enough arguments to /olli effect.\n");
      }

      return true;
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
         player.sendMessage("You need to include the name of the item.");
         return true;
      }

      if (args[1].equals("list"))
      {
         player.sendMessage("\n" + listAllItems());
         return true;
      }

      String itemName = Ollivanders2API.common.stringArrayToString(Arrays.copyOfRange(args, 1, args.length));
      ItemStack item = Ollivanders2API.getItems(this).getItemStartsWith(itemName, 1);

      if (item == null)
      {
         player.sendMessage("Unable to find an item with that name.");
         return true;
      }

      kit.add(item);
      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
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

      for (String item : Ollivanders2API.getItems(this).getAllItems())
      {
         stringBuilder.append("   ");
         stringBuilder.append(item);
         stringBuilder.append("\n");
      }

      return stringBuilder.toString();
   }

   /**
    * Give a player a random wand.
    */
   private boolean giveRandomWand(@NotNull Player player)
   {
      String wood = O2WandWoodType.getAllWoodsByName().get(Math.abs(Ollivanders2Common.random.nextInt() % O2WandWoodType.getAllWoodsByName().size()));
      String core = O2WandCoreType.getAllCoresByName().get(Math.abs(Ollivanders2Common.random.nextInt() % O2WandCoreType.getAllCoresByName().size()));

      List<ItemStack> kit = new ArrayList<>();
      kit.add(Ollivanders2API.common.makeWands(wood, core, 1));

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
   }

   /**
    * Give a player all the wands.
    *
    * @param player the player to give the wands to
    * @return true unless an error occurred
    */
   private boolean okitWands(@NotNull Player player)
   {
      List<ItemStack> kit = Ollivanders2API.common.getAllWands();

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
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
    * @param from player's original location
    * @param to   teleport destination
    */
   public void addTeleportEvent(@NotNull Player p, @NotNull Location from, @NotNull Location to)
   {
      addTeleportEvent(p, from, to, false);
   }

   /**
    * Add a teleport event with an explosion effect
    *
    * @param p                   the player to teleport
    * @param from                player's original location
    * @param to                  teleport destination
    * @param explosionOnTeleport whether or not to do an explosion effect
    */
   public void addTeleportEvent(@NotNull Player p, @NotNull Location from, @NotNull Location to, boolean explosionOnTeleport)
   {
      teleportEvents.addTeleportEvent(p, from, to, explosionOnTeleport);
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

      Ollivanders2API.getPlayers(this).updatePlayer(pid, o2p);
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
      Ollivanders2API.getPlayers(this).updatePlayer(pid, o2p);

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
      Ollivanders2API.getPlayers(this).updatePlayer(pid, o2p);
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
      O2Player o2p = Ollivanders2API.getPlayers(this).getPlayer(pid);

      if (o2p == null)
      {
         Ollivanders2API.getPlayers(this).addPlayer(pid, player.getDisplayName());

         o2p = Ollivanders2API.getPlayers(this).getPlayer(pid);
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
         Ollivanders2API.getPlayers(this).updatePlayer(player.getUniqueId(), o2p);
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
      return Ollivanders2API.getPlayers(this).getPlayerIDs();
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
      if (Ollivanders2API.getPlayers(this).playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT))
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

      O2Player p = Ollivanders2API.getPlayers(this).getPlayer(player.getUniqueId());
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

      boolean cast = isSpellTypeAllowed(player.getLocation(), spell);

      if (!cast && verbose)
      {
         spellCannotBeCastMessage(player);
      }
      return cast;
   }

   /**
    * Determine if this spell can exist here based on zone config
    *
    * @param loc   the location the spell was cast
    * @param spell the spell to check
    * @return true if the spell can exist, false otherwise
    */
   public boolean isSpellTypeAllowed(@NotNull Location loc, @NotNull O2SpellType spell)
   {
      if (zoneConfig == null)
      {
         getLogger().info("isSpellTypeAllowed: no zone config");
         return true;
      }

      boolean cast = true;
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();

      for (String zone : zoneConfig.getKeys(false))
      {
         String prefix = zone + ".";
         String type = zoneConfig.getString(prefix + "type");
         String world = zoneConfig.getString(prefix + "world");
         String areaString = zoneConfig.getString(prefix + "area");

         if (type == null || world == null || areaString == null)
            return true;

         boolean allAllowed = false;
         boolean allDisallowed = false;
         List<O2SpellType> allowedSpells = new ArrayList<>();
         for (String spellString : zoneConfig.getStringList(prefix + "allowed-spells"))
         {
            if (spellString.equalsIgnoreCase("ALL"))
            {
               allAllowed = true;
            } else
            {
               allowedSpells.add(Ollivanders2API.getSpells(this).getSpellTypeByName(spellString));
            }
         }
         List<O2SpellType> disallowedSpells = new ArrayList<>();
         for (String spellString : zoneConfig.getStringList(prefix + "disallowed-spells"))
         {
            if (spellString.equalsIgnoreCase("ALL"))
            {
               allDisallowed = true;
            }
            else
            {
               disallowedSpells.add(Ollivanders2API.getSpells(this).getSpellTypeByName(spellString));
            }
         }
         if (type.equalsIgnoreCase("World"))
         {
            if (loc.getWorld() != null && loc.getWorld().getName().equals(world))
            {
               if (allowedSpells.contains(spell) || allAllowed)
               {
                  return true;
               }
               if (disallowedSpells.contains(spell) || allDisallowed)
               {
                  cast = false;
               }
            }
         }
         if (type.equalsIgnoreCase("Cuboid"))
         {
            String[] areaStringList = areaString.split(" ");
            List<Integer> area = new ArrayList<>();
            for (String a : areaStringList)
            {
               area.add(Integer.parseInt(a));
            }
            if (area.size() < 6)
            {
               for (int i = 0; i < 6; i++)
               {
                  area.set(i, 0);
               }
            }
            if (loc.getWorld() != null && loc.getWorld().getName().equals(world))
            {
               if ((area.get(0) < x) && (x < area.get(3)))
               {
                  if ((area.get(1) < y) && (y < area.get(4)))
                  {
                     if ((area.get(2) < z) && (z < area.get(5)))
                     {
                        if (allowedSpells.contains(spell) || allAllowed)
                        {
                           return true;
                        }
                        if (disallowedSpells.contains(spell) || allDisallowed)
                        {
                           cast = false;
                        }
                     }
                  }
               }
            }
         }
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

      if (versionString.startsWith("1.16") || versionString.startsWith("1.17"))
      {
         return true;
      }
      else // anything lower than 1.14 set to 0 because this version of the plugin cannot run on < 1.14
      {
         getLogger().warning("MC version " + versionString + ". This version of Ollivanders2 requires 1.16 or higher.");
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
      ItemStack flooPowder = new ItemStack(Ollivanders2.flooPowderMaterial);
      List<String> lore = new ArrayList<>();

      lore.add("Glittery, silver powder");
      ItemMeta meta = flooPowder.getItemMeta();

      if (meta == null)
         return false;

      meta.setLore(lore);
      meta.setDisplayName("Floo Powder");
      flooPowder.setItemMeta(meta);

      flooPowder.setAmount(8);

      List<ItemStack> fpStack = new ArrayList<>();
      fpStack.add(flooPowder);

      Ollivanders2API.common.givePlayerKit(player, fpStack);

      return true;
   }

   /**
    * Run the books subcommands
    *
    * @since 2.2.4
    * @param sender the player that issued the command
    * @param args the arguments to the book command
    * @return true if successful, false otherwise
    */
   private boolean runBooks (@NotNull CommandSender sender, @NotNull String[] args)
   {
      Player targetPlayer = (Player) sender;

      if (args.length < 2)
      {
         usageMessageBooks(sender);
         return true;
      }


      List<ItemStack> bookStack = new ArrayList<>();
      if (args[1].equalsIgnoreCase("allbooks"))
      {
         bookStack = Ollivanders2API.getBooks(this).getAllBooks();

         if (bookStack.isEmpty())
         {
            sender.sendMessage(chatColor + "There are no Ollivanders2 books.");

            return true;
         }
      }
      else if (args[1].equalsIgnoreCase("list"))
      {
         // olli books list
         listAllBooks(targetPlayer);
         return true;
      }
      else if (args[1].equalsIgnoreCase("give"))
      {
         // olli books give <player> <book name>
         if (args.length < 4)
         {
            usageMessageBooks(sender);
         }

         //next arg is the target player
         String targetName = args[2];
         targetPlayer = getServer().getPlayer(targetName);
         if (targetPlayer == null)
         {
            sender.sendMessage(chatColor + "Did not find player \"" + targetName + "\".\n");

            return true;
         }
         else
         {
            if (debug)
               getLogger().info("player to give book to is " + targetName);
         }

         // args after "book give <player>" are book name
         String [] subArgs = Arrays.copyOfRange(args, 3, args.length);
         ItemStack bookItem = getBookFromArgs(subArgs, sender);

         if (bookItem == null)
         {
            return true;
         }

         bookStack.add(bookItem);
      }
      else
      {
         String [] subArgs = Arrays.copyOfRange(args, 1, args.length);
         ItemStack bookItem = getBookFromArgs(subArgs, sender);
         if (bookItem == null)
         {
            return true;
         }

         bookStack.add(bookItem);
      }

      Ollivanders2API.common.givePlayerKit(targetPlayer, bookStack);

      return true;
   }

   /**
    * Get the book
    *
    * @param args   the arguments for the book command
    * @param sender the player that issued the command
    * @return true unless an error occurred
    */
   @Nullable
   private ItemStack getBookFromArgs(@NotNull String[] args, @NotNull CommandSender sender)
   {
      String title = Ollivanders2API.common.stringArrayToString(args);

      ItemStack bookItem = Ollivanders2API.getBooks(this).getBookByTitle(title);

      if (bookItem == null)
      {
         sender.sendMessage(chatColor + "No book named \"" + title + "\".\n");
         usageMessageBooks(sender);
      }

      return bookItem;
   }

   /**
    * Usage message for book subcommands.
    *
    * @param sender the player that issued the command
    * @since 2.2.4
    */
   private void usageMessageBooks(@NotNull CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Usage: /olli books"
              + "\nlist - gives a book that lists all available books"
              + "\nallbooks - gives all Ollivanders2 books, this may not fit in your inventory"
              + "\n<book title> - gives you the book with this title, if it exists"
              + "\ngive <player> <book title> - gives target player the book with this title, if it exists\n"
              + "\nExample: /ollivanders2 book standard book of spells grade 1");
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
                  return listAllIngredients((Player)sender);
               }
               else
               {
                  // potion ingredient mandrake leaf
                  String [] subArgs = Arrays.copyOfRange(args, 2, args.length);
                  return giveItem((Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
               }
            }
         }
         else if (subCommand.equalsIgnoreCase("list"))
         {
            return listAllPotions((Player) sender);
         }
         else if (subCommand.equalsIgnoreCase("all"))
         {
            return giveAllPotions((Player) sender);
         }
         else if (subCommand.equalsIgnoreCase("give"))
         {
            if (args.length > 3)
            {
               // potions give fred memory potion
               Player targetPlayer = getServer().getPlayer(args[2]);

               if (targetPlayer != null)
               {
                  String[] subArgs = Arrays.copyOfRange(args, 3, args.length);
                  return givePotion(sender, targetPlayer, Ollivanders2API.common.stringArrayToString(subArgs));
               }
            }
         }
         else
         {
            // potions memory potion
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return givePotion(sender, (Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
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
   public boolean givePotion(@NotNull CommandSender sender, @NotNull Player player, @NotNull String potionName)
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

      O2Potion potion = Ollivanders2API.getPotions(this).getPotionFromType(potionType);

      if (potion == null)
         return true;

      ItemStack brewedPotion = potion.brew((Player)sender, false);
      List<ItemStack> kit = new ArrayList<>();
      kit.add(brewedPotion);

      Ollivanders2API.common.givePlayerKit(player, kit);

      return true;
   }

   private void usageMessagePotions (CommandSender sender)
   {
      sender.sendMessage(chatColor
              + "Usage: /olli potions"
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
    * @param player the player to display the list to
    * @return true
    */
   private boolean listAllIngredients(@NotNull Player player)
   {
      List<String> ingredientList = O2Potions.getAllIngredientNames(this);
      StringBuilder displayString = new StringBuilder();
      displayString.append("Ingredients:");

      for (String name : ingredientList)
      {
         displayString.append("\n").append(name);
      }
      displayString.append("\n");

      player.sendMessage(chatColor + displayString.toString());

      return true;
   }

   /**
    * Lists all the potions active in the game.
    *
    * @param player the player to display the list to
    * @return true
    */
   private boolean listAllPotions(@NotNull Player player)
   {
      StringBuilder displayString = new StringBuilder();
      displayString.append("Potions:");

      List<String> potionNames = Ollivanders2API.getPotions(this).getAllPotionNames();
      for (String name : potionNames)
      {
         displayString.append("\n").append(name);
      }
      displayString.append("\n");

      player.sendMessage(chatColor + displayString.toString());

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
      ItemStack item = Ollivanders2API.getItems(this).getItemStartsWith(name, 1);

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

      for (O2Potion potion : Ollivanders2API.getPotions(this).getAllPotions())
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
    * Show a list of all Ollivanders2 books
    *
    * @param player the player to display the list to
    */
   public void listAllBooks(@NotNull Player player)
   {
      StringBuilder titleList = new StringBuilder();
      titleList.append("Book Titles:");

      for (String bookTitle : Ollivanders2API.getBooks(this).getAllBookTitles())
      {
         titleList.append("\n").append(bookTitle);
      }

      player.sendMessage(chatColor + titleList.toString());
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