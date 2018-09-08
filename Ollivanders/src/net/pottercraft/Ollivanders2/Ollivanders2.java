package net.pottercraft.Ollivanders2;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

import Quidditch.Arena;

import net.pottercraft.Ollivanders2.Book.O2Books;
import net.pottercraft.Ollivanders2.House.O2Houses;
import net.pottercraft.Ollivanders2.House.O2HouseType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Player.O2Players;
import net.pottercraft.Ollivanders2.Player.O2PlayerCommon;
import net.pottercraft.Ollivanders2.Potion.O2Potions;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Potion.O2Potion;

import net.pottercraft.Ollivanders2.Spell.Transfiguration;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpells;

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
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Ollivanders2 plugin object
 *
 * @version Ollivanders2
 * @author cakenggt
 * @author lownes
 * @author Azami7
 * @author autumnwoz
 */

public class Ollivanders2 extends JavaPlugin
{
   private List<SpellProjectile> projectiles = new ArrayList<>();

   private Set<Prophecy> prophecy = new HashSet<>();
   private List<Block> tempBlocks = new ArrayList<>();
   private FileConfiguration fileConfig;
   public O2Houses houses;
   private O2Players o2Players;
   public O2Books books;
   private O2Potions potions;
   public O2StationarySpells stationarySpells;
   public Ollivanders2Common common;
   public O2PlayerCommon playerCommon;

   private static String mcVersion;
   public static Random random = new Random();
   public static boolean debug = false;
   public static boolean nonVerbalCasting = false;
   public static boolean hostileMobAnimagi = false;
   public static Ollivanders2WorldGuard worldGuardO2;
   public static boolean worldGuardEnabled = false;
   public static boolean libsDisguisesEnabled = false;
   public static ChatColor chatColor = ChatColor.AQUA;

   /**
    * onDisable runs when the Minecraft server is shutting down.
    *
    * Primary functions are to reset transfigured blocks back to their correct type and save plugin data to disk.
    */
   public void onDisable ()
   {
	   for (Block block : tempBlocks)
      {
         block.setType(Material.AIR);
      }
      for (SpellProjectile proj : projectiles)
      {
         if (proj instanceof Transfiguration)
         {
            getLogger().finest("Ended transfiguration");
            ((Transfiguration) proj).endTransfigure();
         }
         proj.revert();
      }

      stationarySpells.saveO2StationarySpells();

      houses.saveHouses();
	   o2Players.saveO2Players();

      getLogger().info(this + " is now disabled!");
   }

   /**
    * onEnable runs when the Minecraft server is starting up.
    *
    * Primary function is to set up static plugin data and load saved configs and data from disk.
    */
   public void onEnable ()
   {
      Listener playerListener = new OllivandersListener(this);
      getServer().getPluginManager().registerEvents(playerListener, this);
      common = new Ollivanders2Common(this);
      //loads data
	   if (new File("plugins/Ollivanders2/").mkdirs())
	   {
		   getLogger().info("File directory for Ollivanders2");
	   }
      projectiles = new ArrayList<>();
      prophecy = new HashSet<>();
      fileConfig = getConfig();

      random.setSeed(System.currentTimeMillis());

      //check version of server
      mcVersion = Bukkit.getBukkitVersion();
      if (!mcVersionCheck())
      {
         getLogger().warning("MC version " + mcVersion + ". Some features of Ollivanders2 require MC 1.12 and higher.");
      }

      // write the config.yml to the Ollivanders2 folder if there wasn't one already
      if (!new File(this.getDataFolder(), "config.yml").exists())
      {
         this.saveDefaultConfig();
      }

      // debug mode
      if (getConfig().getBoolean("debug"))
         debug = true;

      // chat color
      if (getConfig().isSet("chatColor"))
         chatColor = ChatColor.getByChar(getConfig().getString("chatColor"));

      OllivandersSchedule schedule = new OllivandersSchedule(this);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this, schedule, 20L, 1L);

      //floo powder recipe
      ItemStack flooPowder = new ItemStack(Material.getMaterial(fileConfig.getString("flooPowder")), 8);
      ItemMeta fmeta = flooPowder.getItemMeta();
      fmeta.setDisplayName("Floo Powder");
      List<String> flore = new ArrayList<>();
      flore.add("Glittery, silver powder");
      fmeta.setLore(flore);
      flooPowder.setItemMeta(fmeta);

      //broomstick recipe
      ItemStack broomstick = new ItemStack(Material.getMaterial(fileConfig.getString("broomstick")));
      ItemMeta bmeta = broomstick.getItemMeta();
      bmeta.setDisplayName("Broomstick");
      List<String> blore = new ArrayList<>();
      blore.add("Flying vehicle used by magical folk");
      bmeta.setLore(blore);
      broomstick.setItemMeta(bmeta);
      Plugin plugin = Bukkit.getPluginManager().getPlugin("Ollivanders2");
      NamespacedKey recipeKey = new NamespacedKey(plugin, "broomstick");
      ShapedRecipe bRecipe = new ShapedRecipe(recipeKey, broomstick);
      bRecipe.shape("  S", " S ", "W  ");
      bRecipe.setIngredient('S', Material.STICK);
      bRecipe.setIngredient('W', Material.WHEAT);
      getServer().addRecipe(new FurnaceRecipe(flooPowder, Material.ENDER_PEARL));
      getServer().addRecipe(bRecipe);

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

      if (worldGuardEnabled)
      {
         getLogger().info("WorldGuard found, enabled WorldGuard features.");
      }
      else
      {
         getLogger().info("WorldGuard not found, disabled WorldGuard features.");
      }

      // set up players
      try
      {
         o2Players = new O2Players(this);
         o2Players.loadO2Players();
         playerCommon = new O2PlayerCommon(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up players.");
         e.printStackTrace();
      }

      // set up houses
      try
      {
         houses = new O2Houses(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up houses.");
         e.printStackTrace();
      }

      // set up potions
      try
      {
         potions = new O2Potions(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up potions.");
         e.printStackTrace();
      }

      // set up stationary spells
      stationarySpells = new O2StationarySpells(this);

      // create books
      try
      {
         books = new O2Books(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up books.");
         e.printStackTrace();
      }

      // set up WorldGuard manager
      Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
      if (wg == null)
      {
         worldGuardEnabled = false;
      }
      else
      {
         try
         {
            if (wg instanceof WorldGuardPlugin)
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

      nonVerbalCasting = getConfig().getBoolean("nonVerbalSpellCasting");
      if (nonVerbalCasting)
         getLogger().info("Enabling non-verbal spell casting.");

      hostileMobAnimagi = getConfig().getBoolean("hostileMobAnimagi");
      if (hostileMobAnimagi)
         getLogger().info("Enabling hostile mob types for animagi.");

      getLogger().info(this + " is now enabled!");
   }

   /**
    * Handle command events
    *
    * @param sender the player who issued the command
    * @param cmd the command entered by the player
    * @param commandLabel required arg for the plugin onCommand call from the server
    * @param args the arguments to the command, if any
    * @return true if the command was successful, false otherwise
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public boolean onCommand (CommandSender sender, Command cmd, String commandLabel, String[] args)
   {
      if (cmd.getName().equalsIgnoreCase("Ollivanders2") || cmd.getName().equalsIgnoreCase("Olli"))
      {
         return runOllivanders(sender, cmd, args);
      }
      else if (cmd.getName().equalsIgnoreCase("Quidd"))
      {
         if (!isOp(sender))
         {
            return true;
         }
         return runQuidd(sender, args);
      }

      return false;
   }

   /**
    * Verify the command sender is a game admin.
    *
    * @param sender the command sender
    * @return true if sender is a game admin, false otherwise
    */
   private boolean isOp (CommandSender sender)
   {
      Player player = null;
      if (sender instanceof Player)
      {
         player = (Player) sender;
      }

      if (player != null)
      {
         if (!player.isOp())
         {
            return false;
         }
      }
      else
         return false;

      return true;
   }

   /**
    * The main Ollivanders2 command.
    *
    * @param sender the player who issued the command
    * @param cmd the command the player issued
    * @param args the arguments for the command, if any
    * @return true if the /ollivanders command worked, false otherwise
    */
   private boolean runOllivanders (CommandSender sender, Command cmd, String[] args)
   {
      if (!isOp(sender))
      {
         return playerSummary(sender);
      }

      // parse args
      if (args.length >= 1)
      {
         String subCommand = args[0];

         if (debug)
         {
            String argString = "";
            for (String arg : args)
            {
               argString = argString + arg + " ";
            }

            getLogger().info("runOllivanders: command = " + cmd.toString() + " " + argString);
         }

         if (subCommand.equalsIgnoreCase("help"))
         {
            usageMessageOllivanders(sender);
            return true;
         }
         else if (subCommand.equalsIgnoreCase("wands"))
         {
            if (args.length == 1)
               return okitWands((Player) sender);
            else
            {
               Player target = getServer().getPlayer(args[1]);
               return giveRandomWand(target);
            }
         }
         else if (subCommand.equalsIgnoreCase("reload"))
            return runReloadConfigs(sender);
         else if (subCommand.equalsIgnoreCase("items"))
            return okitItems((Player) sender);
         else if (subCommand.equalsIgnoreCase("house") || subCommand.equalsIgnoreCase("houses"))
            return runHouse(sender, args);
         else if (subCommand.equalsIgnoreCase("debug"))
            return toggleDebug(sender);
         else if (subCommand.equalsIgnoreCase("floo"))
         {
            if (args.length == 1)
            {
               return giveFlooPowder((Player)sender);
            }
            else
            {
               Player target = getServer().getPlayer(args[1]);
               return giveFlooPowder(target);
            }
         }
         else if (subCommand.equalsIgnoreCase("books") || subCommand.equalsIgnoreCase("book"))
         {
            return runBooks(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("summary"))
         {
            return playerSummary(sender);
         }
         else if (subCommand.equalsIgnoreCase("potions"))
         {
            return runPotions(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("year"))
         {
            return runYear(sender, args);
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
   private boolean playerSummary (CommandSender sender)
   {
      if (debug)
         getLogger().info("Running playerSummary");

      Player player = getServer().getPlayer(sender.getName());
      O2Player o2Player = o2Players.getPlayer(player.getUniqueId());

      String summary = "Ollivanders2 player summary:\n\n";

      // wand type
      if (o2Player.foundWand())
      {
         String wandlore = o2Player.getDestinedWandLore();
         summary = summary + "\nWand Type: " + wandlore;

         Spells masterSpell = o2Player.getMasterSpell();
         if (masterSpell != null)
         {
            summary = summary + "\nMaster Spell: " + common.enumRecode(masterSpell.toString().toLowerCase());
         }

         summary = summary + "\n";
      }

      // sorted
      if (houses.isSorted(player))
      {
         String house = houses.getHouse(player).getName();
         summary = summary + "\nHouse: " + house + "\n";
      }
      else
      {
         summary = summary + "\nYou have not been sorted.\n";
      }

      //year
      summary = summary + "Year: " + O2PlayerCommon.yearToInt(o2Player.getYear()) + "\n";

      // spells
      Map<Spells, Integer> spells = o2Player.getKnownSpells();

      if (spells.size() > 0)
      {
         summary = summary + "\nKnown Spells and Spell Level:";

         for (Map.Entry<Spells, Integer> e : spells.entrySet())
         {
            summary = summary + "\n" + e.getKey().toString() + " " + e.getValue().toString();
         }
      }
      else
      {
         summary = summary + "\nYou have not learned any spells.";
      }

      sender.sendMessage(chatColor + summary);

      return true;
   }

   /**
    * Usage message for the /ollivanders command.
    *
    * @param sender the command sender
    */
   private void usageMessageOllivanders (CommandSender sender)
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
    * @param args the arguments to the command, if any
    * @return true if no error occurred
    */
   private boolean runHouse (CommandSender sender, String[] args)
   {
      if (!getConfig().getBoolean("houses"))
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
   private void usageMessageHouse (CommandSender sender)
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
    * @param args the arguments for the command, if any
    * @return true if no error occurred
    */
   private boolean runListHouse (CommandSender sender, String args[])
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
            ArrayList<String> members = houses.getHouseMembers(house);
            String memberStr = "";

            if (members.isEmpty())
               memberStr = "no members";
            else
            {
               for (String p : members)
               {
                  memberStr = memberStr + p + " ";
               }
            }

            sender.sendMessage(chatColor + "Members of " + targetHouse + " are:\n" + memberStr);

            return true;
         }

         sender.sendMessage(chatColor + "Invalid house name '" + targetHouse + "'");
      }

      String houseNames = "";
      ArrayList<String> h = houses.getAllHouseNames();

      for (String name : h)
      {
         houseNames = houseNames + name + " ";
      }

      sender.sendMessage(chatColor
            + "Ollivanders2 House are:\n" + houseNames + "\n"
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
   private boolean runSort (CommandSender sender, String targetPlayer, String targetHouse, boolean forcesort)
   {
      if (debug)
         getLogger().info("Running house sort");

      if (targetPlayer == null || targetPlayer.length() < 1 || targetHouse == null || targetHouse.length() < 1)
         usageMessageHouseSort(sender);

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
         success = houses.sort(player, house);

      if (success)
      {
         sender.sendMessage(chatColor + targetPlayer + " has been successfully sorted in to " + targetHouse);
      }
      else
      {
         String curHouse = houses.getHouse(player).getName();
         if (curHouse == null)
         {
            sender.sendMessage(chatColor + "Oops, something went wrong with the sort.  If this persists, check your server logs.");
         }
         else
         {
            sender.sendMessage(chatColor + targetPlayer + " is already a member of " + houses.getHouse(player).getName());
         }
      }

      return true;
   }

   /**
    * Usage message for /ollivanders house sort
    * @param sender the player that issued the command
    */
   private void usageMessageHouseSort (CommandSender sender)
   {
      sender.sendMessage(chatColor
            + "Usage: /ollivanders2 house sort [player] [house]"
            + "\nFor example '/ollivanders2 house sort Harry Gryffindor");
   }

   /**
    * Manage house points.
    *
    * @param sender the player that issued the command
    * @param args the arguments for the command, if any
    * @return true unless an error occurs
    */
   private boolean runHousePoints (CommandSender sender, String[] args)
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
   private void usageMessageHousePoints (CommandSender sender)
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
    * @param args the arguments for the command, if any
    * @return true unless an error occurred
    */

   private boolean runYear (CommandSender sender, String[] args)
   {
      if (!getConfig().getBoolean("years"))
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
         else if (args.length == 2) {
            String p = args[1];
            if (p == null || p.length() < 1) {
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
            sender.sendMessage(chatColor + "Player " + p + " is in year " + O2PlayerCommon.yearToInt(o2p.getYear()));
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
   private void usageMessageYearSet (CommandSender sender)
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
   private void usageMessageYearPromote (CommandSender sender)
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
   private void usageMessageYearDemote (CommandSender sender)
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
   private void usageMessageYear (CommandSender sender)
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
    * @param sender the player that issued the command
    * @param targetPlayer the player to set the year for
    * @param targetYear the year to set for the player
    * @return true unless an error occurred
    */
   private boolean runYearSet (CommandSender sender, String targetPlayer, String targetYear)
   {
      if (targetPlayer == null || targetPlayer.length() < 1 || targetYear == null || targetYear.length() < 1)
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
      int year;
      try
      {
         year = Integer.parseInt(targetYear);
      }
      catch (NumberFormatException e)
      {
         usageMessageYearSet(sender);
         return true;
      }

      if (year < 1 || year > 7)
      {
         usageMessageYearSet(sender);
         return true;
      }
      o2p.setYear(O2PlayerCommon.intToYear(year));
      return true;
   }

   /**
    * Run promote and demote year commands
    *
    * @param sender the player that issued the command
    * @param targetPlayer the player to promote or demote
    * @param yearChange the year to change the player to
    * @return true unless an error occurred
    */
   private boolean runYearChange (CommandSender sender, String targetPlayer, int yearChange)
   {
      Player player = getServer().getPlayer(targetPlayer);
      if (player == null)
      {
         sender.sendMessage(chatColor + "Unable to find a player named " + targetPlayer + ".\n");
         return true;
      }
      O2Player o2p = getO2Player(player);
      int year = O2PlayerCommon.yearToInt(o2p.getYear()) + yearChange;
      if (year > 0 && year < 8)
      {
         o2p.setYear(O2PlayerCommon.intToYear(year));
      }
      return true;
   }

   /**
    * The quidditch setup command.
    *
    * @param sender the player that issued the command
    * @param args the arguments for the command, if any
    * @return true unless an error occurred
    */
   private boolean runQuidd (CommandSender sender, String[] args)
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
   private boolean toggleDebug(CommandSender sender)
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
   private boolean runReloadConfigs(CommandSender sender)
   {
      reloadConfig();
      fileConfig = getConfig();
      sender.sendMessage(chatColor + "Config reloaded");

      return true;
   }

   /**
    * Give a player all the items.
    *
    * @param player the player to give items to
    * @return true unless an error occurred
    */
   private boolean okitItems (Player player)
   {
      List<ItemStack> kit = new ArrayList<>();

      //Elder Wand
      ItemStack wand = new ItemStack(Material.BLAZE_ROD);
      List<String> lore = new ArrayList<>();
      lore.add("Blaze and Ender Pearl");
      ItemMeta meta = wand.getItemMeta();
      meta.setLore(lore);
      meta.setDisplayName("Elder Wand");
      wand.setItemMeta(meta);
      kit.add(wand);

      //Cloak of Invisibility
      ItemStack cloak = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
      List<String> cloakLore = new ArrayList<>();
      cloakLore.add("Silvery Transparent Cloak");
      ItemMeta cloakMeta = cloak.getItemMeta();
      cloakMeta.setLore(cloakLore);
      cloakMeta.setDisplayName("Cloak of Invisibility");
      cloak.setItemMeta(cloakMeta);
      kit.add(cloak);

      givePlayerKit(player, kit);

      return true;
   }

   /**
    * Give a player a random wand.
    */
   private boolean giveRandomWand (Player player)
   {
      String[] woodArray = {"Spruce", "Jungle", "Birch", "Oak"};
      String[] coreArray = {"Spider Eye", "Bone", "Rotten Flesh", "Gunpowder"};

      String wood = woodArray[Math.abs(random.nextInt() % woodArray.length)];
      String core = coreArray[Math.abs(random.nextInt() % coreArray.length)];

      List<ItemStack> kit = new ArrayList<>();
      ItemStack wand = new ItemStack(Material.STICK);
      List<String> lore = new ArrayList<>();
      lore.add(wood + " and " + core);
      ItemMeta meta = wand.getItemMeta();
      meta.setLore(lore);
      meta.setDisplayName("Wand");
      wand.setItemMeta(meta);
      wand.setAmount(1);
      kit.add(wand);

      givePlayerKit(player, kit);

      return true;
   }

   /**
    * Give a player all the wands.
    *
    * @param player the player to give the wands to
    * @return true unless an error occurred
    */
   private boolean okitWands (Player player)
   {
      String[] woodArray = {"Spruce", "Jungle", "Birch", "Oak"};
      String[] coreArray = {"Spider Eye", "Bone", "Rotten Flesh", "Gunpowder"};

      List<ItemStack> kit = new ArrayList<>();

      for (String wood : woodArray)
      {
         for (String core : coreArray)
         {
            ItemStack wand = new ItemStack(Material.STICK);
            List<String> lore = new ArrayList<>();
            lore.add(wood + " and " + core);
            ItemMeta meta = wand.getItemMeta();
            meta.setLore(lore);
            meta.setDisplayName("Wand");
            wand.setItemMeta(meta);
            wand.setAmount(1);
            kit.add(wand);
         }
      }

      givePlayerKit(player, kit);

      return true;
   }

   private void givePlayerKit (Player player, List<ItemStack> kit)
   {
      Location loc = player.getEyeLocation();
      ItemStack[] kitArray = kit.toArray(new ItemStack[kit.size()]);
      HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kitArray);
      for (ItemStack item : leftover.values())
      {
         player.getWorld().dropItem(loc, item);
      }
   }

   /**
    * Get the chat dropoff distance configuration.
    *
    * @return the chatDropoff config value
    */
   public int getChatDistance ()
   {
      return fileConfig.getInt("chatDropoff");
   }

   /**
    * Get all the active spell projectiles
    *
    * @return a list of all active spell projectiles
    */
   public List<SpellProjectile> getProjectiles ()
   {
      return projectiles;
   }

   /**
    * Add a new spell projectile
    *
    * @param spell the spell projectile
    */
   public void addProjectile (SpellProjectile spell)
   {
      projectiles.add(spell);
   }

   /**
    * Remove a spell projectile. This will make it stop if it has not already been killed.
    *
    * @param spell the spell projectile
    */
   public void remProjectile (SpellProjectile spell)
   {
      projectiles.remove(spell);
   }

   /**
    * Get the spell use count for the player for this spell
    *
    * @param player the player to get the count for
    * @param spell the spell to get the count for
    * @return the spell count
    */
   public int getSpellNum (Player player, Spells spell)
   {
      O2Player o2p = o2Players.getPlayer(player.getUniqueId());

      return o2p.getSpellCount(spell);
   }

   /**
    * Set the spell use count for a player.
    *
    * @param player the player to set the spell count for
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellNum (Player player, Spells spell, int count)
   {
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      o2p.setSpellCount(spell, count);

      o2Players.updatePlayer(pid, o2p);
   }

   /**
    * Increment the spell use count for a player.
    *
    * @param player the player to increment the count for
    * @param spell the spell to increment
    * @return the incremented use count for this player for this spell
    */
   public int incSpellCount (Player player, Spells spell)
   {
      //returns the incremented spell count
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      o2p.incrementSpellCount(spell);
      o2Players.updatePlayer(pid, o2p);

      return o2p.getSpellCount(spell);
   }

   /**
    * Increment the potion use count for a player.
    *
    * @param player the player to increment the count for
    * @param potion the potion to increment
    */
   public void incPotionCount (Player player, String potion)
   {
      //returns the incremented potion count
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      o2p.incrementPotionCount(potion);
      o2Players.updatePlayer(pid, o2p);
   }

   /**
    * Gets the O2Player associated with the Player
    *
    * @param player the player to get
    * @return O2Player object for this player
    */
   public O2Player getO2Player (Player player)
   {
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      if (o2p == null)
      {
         o2Players.addPlayer(pid, player.getDisplayName());

         o2p = o2Players.getPlayer(pid);
      }

      return o2p;
   }

   /**
    * Sets the player's OPlayer by their playername
    *
    * @param player      the player
    * @param o2p the OPlayer associated with the player
    */
   public void setO2Player (Player player, O2Player o2p)
   {
      if (!(player instanceof NPC))
      {
         o2Players.updatePlayer(player.getUniqueId(), o2p);
      }
   }

   /**
    * Gets the set of prophecy objects
    *
    * @return Set of prophecy objects in server
    */
   public Set<Prophecy> getProphecy ()
   {
      return prophecy;
   }

   /**
    * Gets the tempBlocks list.
    *
    * @return tempBlocks Block list.
    */
   public List<Block> getTempBlocks ()
   {
      return tempBlocks;
   }

   /**
    * Gets the set of all player UUIDs.
    *
    * @return a list of all player MC UUIDs
    */
   public ArrayList<UUID> getO2PlayerIDs ()
   {
      return o2Players.getPlayerIDs();
   }

   /**
    * Can this player cast this spell?
    *
    * @param player Player to check
    * @param spell Spell to check
    * @param verbose Whether or not to inform the player of why they cannot cast a spell
    * @return True if the player can cast this spell, false if not
    */
   public boolean canCast (Player player, Spells spell, boolean verbose)
   {
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

      O2Player p = getO2Player(player);
      boolean coolDown = System.currentTimeMillis() < p.getSpellLastCastTime(spell);

      if (coolDown)
      {
         if (verbose)
         {
            spellCoolDownMessage(player);
         }
         return false;
      }

      boolean cast = canLive(player.getLocation(), spell);

      if (!cast && verbose)
      {
         spellCannotBeCastMessage(player);
      }
      return cast;
   }

   /**
    * Determine if this spell can exist here
    *
    * @param loc the location of the spell
    * @param spell the spell to check
    * @return true if the spell can exist, false otherwise
    */
   public boolean canLive (Location loc, Spells spell)
   {
      boolean cast = true;
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();
      if (fileConfig.contains("zones"))
      {
         ConfigurationSection config = fileConfig.getConfigurationSection("zones");
         for (String zone : config.getKeys(false))
         {
            String prefix = zone + ".";
            String type = config.getString(prefix + "type");
            String world = config.getString(prefix + "world");
            String areaString = config.getString(prefix + "area");
            boolean allAllowed = false;
            boolean allDisallowed = false;
            List<Spells> allowedSpells = new ArrayList<>();
            for (String spellString : config.getStringList(prefix + "allowed-spells"))
            {
               if (spellString.equalsIgnoreCase("ALL"))
               {
                  allAllowed = true;
               }
               else
               {
                  allowedSpells.add(Spells.decode(spellString));
               }
            }
            List<Spells> disallowedSpells = new ArrayList<>();
            for (String spellString : config.getStringList(prefix + "disallowed-spells"))
            {
               if (spellString.equalsIgnoreCase("ALL"))
               {
                  allDisallowed = true;
               }
               else
               {
                  disallowedSpells.add(Spells.decode(spellString));
               }
            }
            if (type.equalsIgnoreCase("World"))
            {
               if (loc.getWorld().getName().equals(world))
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
               List<String> areaStringList = Arrays.asList(areaString.split(" "));
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
               if (loc.getWorld().getName().equals(world))
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
      }
      return cast;
   }

   /**
    * Get the file configuration
    *
    * @return FileConfiguration
    */
   @Deprecated
   public FileConfiguration getFileConfig ()
   {
      return fileConfig;
   }

   /**
    * SLAPI = Saving/Loading API
    * API for Saving and Loading Objects.
    *
    * @author Tomsik68
    */
   @Deprecated
   public static class SLAPI
   {
      public static void save (Object obj, String path) throws Exception
      {
         ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
         oos.writeObject(obj);
         oos.flush();
         oos.close();
      }

      public static Object load (String path) throws Exception
      {
         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
         Object result = ois.readObject();
         ois.close();
         return result;
      }
   }

   /**
    * Check to see if we're running MC version 1.12 or higher, which many Ollivanders2 features depend on.
    *
    * @return true of version string is 1.12, false otherwise.
    */
   public static boolean mcVersionCheck ()
   {
      if (mcVersion.startsWith("1.12"))
         return true;

      return false;
   }

   /**
    * Give floo powder to player.
    *
    * @since 2.2.4
    * @param player the player to give the floo powder to
    * @return true if successful, false otherwise
    */
   private boolean giveFlooPowder (Player player)
   {
      ItemStack flooPowder = new ItemStack(Material.REDSTONE);
      List<String> lore = new ArrayList<>();
      lore.add("Glittery, silver powder");
      flooPowder.getItemMeta().setLore(lore);
      flooPowder.getItemMeta().setDisplayName("Floo Powder");
      flooPowder.setAmount(8);

      List<ItemStack> fpStack = new ArrayList<>();
      fpStack.add(flooPowder);

      givePlayerKit(player, fpStack);

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
   private boolean runBooks (CommandSender sender, String[] args)
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
         bookStack = books.getAllBooks();

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

      givePlayerKit(targetPlayer, bookStack);

      return true;
   }

   /**
    * Get the book
    * @param args the arguments for the book command
    * @param sender the player that issued the command
    * @return true unless an error occurred
    */
   private ItemStack getBookFromArgs (String[] args, CommandSender sender)
   {
      String title = common.stringArrayToString(args);

      ItemStack bookItem = books.getBookByTitle(title);

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
    * @since 2.2.4
    * @param sender the player that issued the command
    */
   private void usageMessageBooks (CommandSender sender)
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
   public void spellCannotBeCastMessage (Player player)
   {
      player.sendMessage(chatColor + "A powerful protective magic prevents you from casting this spell here.");
   }

   /**
    * When a spell is not allowed be cast, such as from WorldGuard protection, send a message.
    * This is not the message to use for bookLearning enforcement.
    *
    * @since 2.2.5
    * @param player the player to send the cooldown message to
    */
   public void spellCoolDownMessage (Player player)
   {
      player.sendMessage(chatColor + "You are too tired to cast this spell right now.");
   }

   public boolean runPotions (CommandSender sender, String[] args)
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
                  return givePotionIngredient((Player)sender, common.stringArrayToString(subArgs));
               }
            }
         }
         else if (subCommand.equalsIgnoreCase("all"))
         {
            return giveAllPotions((Player)sender);
         }
         //TODO implement givePotion
         /*
         else if (subCommand.equalsIgnoreCase("give"))
         {
            if (args.length > 3)
            {
               // potions give fred memory potion
               Player targetPlayer = getServer().getPlayer(args[2]);
               String [] subArgs = Arrays.copyOfRange(args, 3, args.length);
               givePotion((Player)sender, common.stringArrayToString(subArgs));
            }
         }
         else
         {
            // potions memory potion
            String [] subArgs = Arrays.copyOfRange(args, 1, args.length);
            givePotion((Player)sender, common.stringArrayToString(subArgs));
         }
         */
      }

      usageMessagePotions(sender);
      return true;
   }

   private void usageMessagePotions (CommandSender sender)
   {
      sender.sendMessage(chatColor
            + "Usage: /olli potions"
            + "\ningredient list - lists all potions ingredients"
            + "\ningredient <ingredient name> - give you the ingredient with this name, if it exists"
            + "\nall - gives all Ollivanders2 potions, this may not fit in your inventory"
            //+ "\n<potion name> - gives you the potion with this name, if it exists"
            //+ "\ngive <player> <potion name> - gives target player the potion with this name, if it exists\n"
            + "\nExample: /ollivanders2 potions all"
            + "\nExample: /ollivanders2 potions ingredient list");
   }

   /**
    * List all the potions ingredients
    *
    * @param player the player to display the list to
    * @return true
    */
   private boolean listAllIngredients (Player player)
   {
      List<String> ingredientList = potions.getAllIngredientNames();
      String displayString = "Ingredients:";

      for (String name : ingredientList)
      {
         displayString = displayString + "\n" + name;
      }
      displayString = displayString + "\n";

      player.sendMessage(chatColor + displayString);

      return true;
   }

   /**
    * Give a potion ingredient to a player.
    *
    * @param player the player to give the ingredient to
    * @param name the name of the ingredient
    * @return true
    */
   private boolean givePotionIngredient(Player player, String name)
   {
      List<ItemStack> kit = new ArrayList<>();
      ItemStack ingredient = potions.getIngredientByName(name);

      if (ingredient != null)
      {
         kit.add(ingredient);
         givePlayerKit(player, kit);
      }

      return true;
   }

   /**
    * Gives the specified player 1 of every Ollivanders2 potion.
    *
    * @param player the player to give the potions to
    * @return true unless an error occurred
    */
   private boolean giveAllPotions (Player player)
   {
      if (debug)
         getLogger().info("Running givePotions...");

      List<ItemStack> kit = new ArrayList<>();

      for (O2Potion potion : potions.getAllPotions())
      {
         ItemStack brewedPotion = potion.brew();

         if (debug)
            getLogger().info("Adding " + potion.getName());

         kit.add(brewedPotion);
      }

      givePlayerKit(player, kit);

      return true;
   }

   /**
    * Get the O2Potions class managing all the potions loaded in the game.
    *
    * @return the O2Potions class for this plugin
    */
   public O2Potions getO2Potions ()
   {
      return potions;
   }

   /**
    * Show a list of all Ollivanders2 books
    *
    * @param player the player to display the list to
    */
   public void listAllBooks (Player player)
   {
      String titleList = "Book Titles:";
      for (String bookTitle : books.getAllBookTitles())
      {
         titleList = titleList + "\n" + bookTitle;
      }

      player.sendMessage(chatColor + titleList);
   }

}