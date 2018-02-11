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
import java.util.Map.Entry;
import java.util.UUID;

import Quidditch.Arena;

import net.pottercraft.Ollivanders2.Book.O2Books;
import net.pottercraft.Ollivanders2.House.O2Houses;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.Spell.Spells;

import net.pottercraft.Ollivanders2.Spell.Transfiguration;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import net.pottercraft.Ollivanders2.Book.Books;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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
 * @author Lil_Miss_Giggles
 */
public class Ollivanders2 extends JavaPlugin
{
   private List<SpellProjectile> projectiles = new ArrayList<>();
   private List<StationarySpellObj> stationary = new ArrayList<>();

   private Set<Prophecy> prophecy = new HashSet();
   private Listener playerListener;
   private OllivandersSchedule schedule;
   private List<Block> tempBlocks = new ArrayList<>();
   private FileConfiguration fileConfig;
   private O2Houses houses;
   private O2Players o2Players;
   private O2Books books;

   private static String mcVersion;
   public static Random random = new Random();
   public static boolean debug = false;
   public static boolean nonVerbalCasting = false;
   public static boolean hostileMobAnimagi = false;
   public static Ollivanders2WorldGuard worldGuardO2;
   public static boolean worldGuardEnabled = false;
   public static boolean libsDisguisesEnabled = false;
   public PotionParser potionParser;

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
      for (StationarySpellObj stat : stationary)
      {
         stat.active = true;
      }

      try
      {
         SLAPI.save(stationary, "plugins/Ollivanders2/stationary.bin");
         getLogger().finest("Saved stationary.bin");
      } catch (Exception e)
      {
         getLogger().warning("Could not save stationary.bin");
      }
      try
      {
         SLAPI.save(prophecy, "plugins/Ollivanders2/prophecy.bin");
         getLogger().finest("Saved prophecy.bin");
      } catch (Exception e)
      {
         getLogger().warning("Could not save prophecy.bin");
      }

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
      playerListener = new OllivandersListener(this);
      getServer().getPluginManager().registerEvents(playerListener, this);
      //loads data
	   if (new File("plugins/Ollivanders2/").mkdirs())
	   {
		   getLogger().info("File directory for Ollivanders2");
	   }
      projectiles = new ArrayList<>();
      stationary = new ArrayList<>();
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

      try
      {
         stationary = (List<StationarySpellObj>) SLAPI.load("plugins/Ollivanders2/stationary.bin");
         getLogger().info("Loaded save file stationary.bin");
      }
      catch (Exception e)
      {
         getLogger().warning("Did not find stationary.bin");
      }
      try
      {
         prophecy = (HashSet<Prophecy>) SLAPI.load("plugins/Ollivanders2/prophecy.bin");
         getLogger().info("Loaded save file prophecy.bin");
      }
      catch (Exception e)
      {
         getLogger().warning("Did not find prophecy.bin");
      }

      this.schedule = new OllivandersSchedule(this);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.schedule, 20L, 1L);

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
      o2Players = new O2Players(this);
      o2Players.loadO2Players();

      // set up houses
      houses = new O2Houses(this);

      // create books
      books = new O2Books(this);

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
         catch (NoClassDefFoundError e)
         {
            worldGuardEnabled = false;
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

      // potions
      potionParser = new PotionParser(this);

      getLogger().info(this + " is now enabled!");
   }

   /**
    * Handle command events
    *
    * @param sender
    * @param cmd
    * @param commandLabel
    * @param args
    * @return true if the command was successful, false otherwise
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public boolean onCommand (CommandSender sender, Command cmd, String commandLabel, String[] args)
   {
      if (cmd.getName().equalsIgnoreCase("Ollivanders2") || cmd.getName().equalsIgnoreCase("Olli"))
      {
         return runOllivanders(sender, cmd, commandLabel, args);
      }
      else if (cmd.getName().equalsIgnoreCase("Okit"))
      {
         // okit command deprecated
         return runOllivanders(sender, cmd, commandLabel, args);
      }
      else if (cmd.getName().equalsIgnoreCase("Quidd"))
      {
         if (!isOp(sender))
         {
            return true;
         }
         return runQuidd(sender, cmd, commandLabel, args);
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
    * @param sender
    * @param cmd
    * @param commandLabel
    * @param args
    * @return
    */
   private boolean runOllivanders (CommandSender sender, Command cmd, String commandLabel, String[] args)
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
            String argString = new String();
            for (int i = 0; i < args.length; i++)
            {
               argString = argString + args[i] + " ";
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
            return runHouse(sender, cmd, commandLabel, args);
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
            return givePotions((Player)sender);
         }
      }

      usageMessageOllivanders(sender);

      return true;
   }

   /**
    * Displays a summary of player info for the Ollivanders2 plugin
    *
    * @param sender
    * @return true if the command worked, false otherwise.
    */
   private boolean playerSummary (CommandSender sender)
   {
      if (debug)
         getLogger().info("Running playerSummary");

      Player player = getServer().getPlayer(sender.getName());
      O2Player o2Player = o2Players.getPlayer(player.getUniqueId());

      String summary = new String("Ollivanders2 player summary:\n\n");

      // wand type
      if (o2Player.foundWand())
      {
         String wandlore = o2Player.getDestinedWandLore();
         summary = summary + "\nWand Type: " + wandlore;

         Spells masterSpell = o2Player.getMasterSpell();
         if (masterSpell != null)
         {
            summary = summary + "\nMaster Spell: " + Spells.recode(masterSpell);
         }

         summary = summary + "\n";
      }

      // sorted
      if (houses.isSorted(player))
      {
         String house = houses.getHouseName(houses.getHouse(player));
         summary = summary + "\nHouse: " + house + "\n";
      }
      else
      {
         summary = summary + "\nYou have not been sorted.\n";
      }

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

      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + summary);

      return true;
   }

   /**
    * Usage message for the /ollivanders command.
    *
    * @param sender the command sender
    */
   private void usageMessageOllivanders (CommandSender sender)
   {
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
            + "You are running Ollivanders2 version " + this.getDescription().getVersion() + "\n"
            + "\nOllivanders2 commands:"
            + "\nwands - gives a complete set of wands"
            + "\nbooks - gives a complete set of spell books"
            + "\nitems - gives a complete set of items"
            // + "\nquidd - creates a quidditch pitch"
            + "\nhouse - view and manage houses and house points"
            + "\nsummary - gives a summary of wand type, house, and known spells"
            + "\nreload - reload the Ollivanders2 configs"
            + "\ndebug - toggles Ollivanders2 plugin debug output\n"
            + "\n" + "To run a command, type '/ollivanders2 [command]'."
            + "\nFor example, '/ollivanders2 wands");
   }

   /**
    * The house subCommand for managing everything related to houses.
    *
    * @param sender
    * @param cmd
    * @param commandLabel
    * @param args
    * @return
    */
   private boolean runHouse (CommandSender sender, Command cmd, String commandLabel, String[] args)
   {
      if (getConfig().getBoolean("houses") == false)
      {
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
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
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
            + "Usage: /ollivanders2 house points [option]"
            + "\n\nOptions to '/ollivanders2 house':"
            + "\nlist - lists Ollivanders2 houses and house membership"
            + "\nsort - sort a player in to a house"
            + "\npoints - manage house points");
   }

   /**
    * List all houses or the members of a house
    *
    * @param sender
    * @param args
    * @return true if the command succeeds.
    */
   private boolean runListHouse (CommandSender sender, String args[])
   {
      // list houses
      if (debug)
         getLogger().info("Running list houses");

      if (args.length > 2)
      {
         String targetHouse = args[2];

         O2Houses.O2HouseType house = houses.getHouseType(targetHouse);
         if (house != null)
         {
            ArrayList<String> members = houses.getHouseMembers(house);
            String memberStr = new String();

            if (members.isEmpty())
               memberStr = "no members";
            else
            {
               for (String p : members)
               {
                  memberStr = memberStr + p + " ";
               }
            }

            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                  + "Members of " + targetHouse + " are:\n" + memberStr);

            return true;
         }

         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + "Invalid house name '" + targetHouse + "'");
      }

      String houseNames = new String();
      ArrayList<String> h = houses.getAllHouseNames();

      for (String name : h)
      {
         houseNames = houseNames + name + " ";
      }

      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
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
    * @param sender
    * @param targetPlayer
    * @param targetHouse
    * @param forcesort should the sort happen even if the player is already sorted
    * @return true unless an error occurs
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
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + "Unable to find a player named " + targetPlayer + " logged in to this server."
               + "\nPlayers must be logged in to be sorted.");

         return true;
      }

      O2Houses.O2HouseType house = houses.getHouseType(targetHouse);

      if (house == null)
      {
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + targetHouse + " is not a valid house name.");

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
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + targetPlayer + " has been successfully sorted in to " + targetHouse);
      }
      else
      {
         String curHouse = houses.getHouseName(houses.getHouse(player));
         if (curHouse == null)
         {
            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                + "Oops, something went wrong with the sort.  If this persists, check your server logs.");
         }
         else
         {
            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                  + targetPlayer + " is already a member of " + houses.getHouseName(houses.getHouse(player)));
         }
      }

      return true;
   }

   /**
    * Usage message for /ollivanders house sort
    * @param sender
    */
   private void usageMessageHouseSort (CommandSender sender)
   {
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
            + "Usage: /ollivanders2 house sort [player] [house]"
            + "\nFor example '/ollivanders2 house sort Harry Gryffindor");
   }

   /**
    * Manage house points.
    *
    * @param sender
    * @param args
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

            O2Houses.O2HouseType houseType = null;
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

            int value = 0;

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
    * @param sender
    */
   private void usageMessageHousePoints (CommandSender sender)
   {
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
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
    * @param sender
    * @param cmd
    * @param commandLabel
    * @param args
    * @return
    */
   private boolean runQuidd (CommandSender sender, Command cmd, String commandLabel, String[] args)
   {
      if (args.length >= 1)
      {
         Player player = null;
         if (sender instanceof Player)
         {
            player = (Player) sender;
            Arena arena = new Arena(args[0], player.getLocation(), Arena.Size.MEDIUM);
            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + "The following arena was made: " + arena.toString());
         }
         else
         {
            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + "Only players can use the /Quidd command.");
         }
      }
      else
      {
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + "Please include a name for your arena.");
      }
      return true;
   }

   /**
    * Toggle debug mode.
    *
    * @param sender
    * @return true
    */
   private boolean toggleDebug(CommandSender sender)
   {
      debug = !debug;

      if (debug)
      {
         getLogger().info("Debug mode enabled.");
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + "Ollivanders2 debug mode enabled.");
      }
      else
      {
         getLogger().info("Debug mode disabled.");
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + "Ollivanders2 debug mode disabled.");
      }

      return true;
   }

   /**
    * Reload the game configs if the command caller is an op.
    *
    * @param sender
    * @return
    */
   private boolean runReloadConfigs(CommandSender sender)
   {
      reloadConfig();
      fileConfig = getConfig();
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + "Config reloaded");

      return true;
   }

   /**
    * Give a player all the items.
    *
    * @param player
    * @return
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

      //TODO resurrection stone

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
    * @param player
    * @return true
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

   public int getChatDistance ()
   {
      return fileConfig.getInt("chatDropoff");
   }

   public List<SpellProjectile> getProjectiles ()
   {
      return projectiles;
   }

   public void addProjectile (SpellProjectile s)
   {
      projectiles.add(s);
   }

   public void remProjectile (SpellProjectile s)
   {
      projectiles.remove(s);
   }

   /**
    * Get the spell use count for the player for this spell
    *
    * @param player
    * @param spell
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
    * @param player
    * @param spell
    * @param i
    */
   public void setSpellNum (Player player, Spells spell, int i)
   {
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      o2p.setSpellCount(spell, i);

      o2Players.updatePlayer(pid, o2p);
   }

   /**
    * Increment the spell use count for a player.
    *
    * @param player
    * @param s
    * @return the incremented use count for this player for this spell
    */
   public int incSpellCount (Player player, Spells s)
   {
      //returns the incremented spell count
      UUID pid = player.getUniqueId();
      O2Player o2p = o2Players.getPlayer(pid);

      o2p.incrementSpellCount(s);
      o2Players.updatePlayer(pid, o2p);

      return o2p.getSpellCount(s);
   }

   /**
    * Gets the OPlayer associated with the Player
    *
    * @param player Player
    * @return OPlayer of playername s
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
    * Gets the list of stationary spell objects
    *
    * @return List of stationary spell objects in server
    */
   public List<StationarySpellObj> getStationary ()
   {
      return stationary;
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
    * Adds a stationary spell object to plugin stationary spell ojbect list
    *
    * @param s - StationarySpellObj to be added to list. Cannot be null.
    */
   public void addStationary (StationarySpellObj s)
   {
      stationary.add(s);
   }

   /**
    * Removes a stationary spell object from plugin's stationary list
    *
    * @param s - StationarySpellObj to be removed. Cannot be null.
    */
   public void remStationary (StationarySpellObj s)
   {
      stationary.remove(s);
   }

   /**
    * Checks if the location is within one or more stationary spell objects, regardless of whether or not they are active.
    *
    * @param location - location to check
    * @return List of StationarySpellObj that the location is inside
    */
   public List<StationarySpellObj> checkForStationary (Location location)
   {
      List<StationarySpellObj> stationaries = getStationary();
      List<StationarySpellObj> inside = new ArrayList<>();
      for (StationarySpellObj stationary : stationaries)
      {
         if (stationary.location.getWorldUUID().equals(location.getWorld().getUID()))
         {
            if (stationary.location.distance(location) < stationary.radius)
            {
               inside.add(stationary);
            }
         }
      }
      return inside;
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
    * Determine if the location is inside of a stationary spell area.
    *
    * @param statName
    * @param loc
    * @return
    */
   public boolean isInsideOf (StationarySpells statName, Location loc)
   {
      for (StationarySpellObj stat : getStationary())
      {
         if (stat.name == statName)
         {
            if (stat.isInside(loc) && stat.active)
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Gets the set of all player UUIDs.
    *
    * @return a copy of the OPlayer player IDs.
    */
   public ArrayList<UUID> getOPlayerKeys ()
   {
      return o2Players.getPlayerIDs();
   }

   /**
    * Can this player cast this spell?
    *
    * @param player  - Player to check
    * @param spell   - Spell to check
    * @param verbose - Whether or not to inform the player of why they cannot cast a spell
    * @return True if yes, false if not
    */
   public boolean canCast (Player player, Spells spell, boolean verbose)
   {
      if (player.isPermissionSet("Ollivanders2." + spell.toString()))
      {
         if (!player.hasPermission("Ollivanders2." + spell.toString()))
         {
            if (verbose)
            {
               player.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                     + "You do not have permission to use " + spell.toString());
            }
            return false;
         }
      }
      boolean cast = canLive(player.getLocation(), spell);
      if (!cast && verbose)
      {
         spellCannotBeCastMessage(player);
      }
      return cast;
   }

   public boolean canLive (Location loc, Spells spell)
   {
		/**
		 * cast is whether or not the spell can be cast.
		 * set to false whenever it can't be
		 * return true whenever it is in allowed-spells
		 */
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
            String region = config.getString(prefix + "region");
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
               for (int i = 0; i < areaStringList.size(); i++)
               {
                  area.add(Integer.parseInt(areaStringList.get(i)));
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
            /*
            if (type.equalsIgnoreCase("WorldGuard"))
            {
               WorldGuardPlugin worldGuard = getWorldGuard();
               if (worldGuard != null)
               {
                  RegionManager regionManager = worldGuard.getRegionManager(Bukkit.getWorld(world));
                  if (regionManager != null)
                  {
                     ProtectedRegion protRegion = regionManager.getRegion(region);
                     if (protRegion != null)
                     {
                        if (protRegion.contains(BukkitUtil.toVector(loc)))
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
            */
         }
      }
      return cast;
   }

   /**
    * Get the file configuration
    *
    * @return FileConfiguration
    */
   public FileConfiguration getFileConfig ()
   {
      return fileConfig;
   }

   /**
    * Does the player hold a wand item?
    *
    * @param player - Player to check.
    * @return True if the player holds a wand. False if not or if player is null.
    */
   public boolean holdsWand (Player player)
   {
      if (player != null && player.getInventory().getItemInMainHand() != null)
      {
         ItemStack held = player.getInventory().getItemInMainHand();
         return isWand(held);
      }
      else
      {
         return false;
      }
   }

   /**
    * Is this item stack a wand?
    *
    * @param stack - stack to be checked
    * @return true if yes, false if no
    */
   public boolean isWand (ItemStack stack)
   {
      if (stack != null)
      {
         if (stack.getType() == Material.STICK || stack.getType() == Material.BLAZE_ROD)
         {
            if (stack.getItemMeta().hasLore())
            {
               String itemName = stack.getItemMeta().getDisplayName();
               List<String> lore = stack.getItemMeta().getLore();
               //TODO refactor this - item name should contain "wand" and lore should not just contain "and" but have the correct components
               if (lore.get(0).split(" and ").length == 2)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Is this itemstack the player's destined wand?
    *
    * @param player - Player to check the stack against.
    * @param stack  - Itemstack to be checked
    * @return true if yes, false if no
    */
   public boolean destinedWand (Player player, ItemStack stack)
   {
      if (isWand(stack))
      {
         O2Player o2Player = o2Players.getPlayer(player.getUniqueId());

         if (o2Player == null)
         {
            getLogger().warning(player.getDisplayName() + " not found.");
            return false;
         }

         return o2Player.isDestinedWand(stack);
      }
      else
      {
         return false;
      }
   }

   /**
    * Finds out if an item is a broom.
    *
    * @param item - Item in question.
    * @return True if yes.
    */
   public boolean isBroom (ItemStack item)
   {
      if (item.getType() == Material.getMaterial(fileConfig.getString("broomstick")))
      {
         if (item.containsEnchantment(Enchantment.PROTECTION_FALL))
         {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore())
            {
               List<String> lore = meta.getLore();
               if (lore.contains("Flying vehicle used by magical folk"))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Checks what kind of wand a player holds. Returns a value based on the
    * wand and it's relation to the player.
    *
    * @assumes player not null, player holding a wand
    * @param player - Player being checked. The player must be holding a wand.
    * @return 2 - The wand is not player's type AND/OR is not allied to player.<p>
    * 1 - The wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player.<p>
    * 0.5 - The wand is the elder wand and it is allied to player.
    */
   public double wandCheck (Player player)
   {
      ItemStack item = player.getInventory().getItemInMainHand();

      List<String> lore = item.getItemMeta().getLore();
      if (lore.get(0).equals("Blaze and Ender Pearl")) // elder wand
      {
         if (lore.size() == 2 && lore.get(1).equals(player.getUniqueId().toString()))
         {
            // wand is Elder Wand and allied to player
            return 0.5;
         }
      }
      else // not the elder wand
      {
         if (!destinedWand(player, player.getInventory().getItemInMainHand()))
         {
            // not the player's destined wand
            return 2;
         }
      }

      return 1;
   }

   /**
    * SLAPI = Saving/Loading API
    * API for Saving and Loading Objects.
    *
    * @author Tomsik68
    */
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
    * Determine if this is the Cloak of Invisibility.
    *
    * @param held
    * @return
    */
   public static boolean isInvisibilityCloak (ItemStack held)
   {
      if (held.getType() == Material.CHAINMAIL_CHESTPLATE)
      {
         if (held.getItemMeta().hasLore())
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore.get(0).equals("Silvery Transparent Cloak"))
            {
               return true;
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
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
    * @param player
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
    * @param sender
    * @param args
    * @return true if successful, false otherwise
    */
   private boolean runBooks (CommandSender sender, String[] args)
   {
      if (args.length < 2)
      {
         usageMessageBooks(sender);
         return true;
      }
      else if (args.length >= 2)
      {
         List<ItemStack> bookStack = new ArrayList<>();
         if (args[1].equalsIgnoreCase("allbooks"))
         {
            bookStack = books.getAllBooks();

            if (bookStack.isEmpty())
            {
               sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                     + "There are no Ollivanders2 books.");

               return true;
            }
         }
         else if (args[1].equalsIgnoreCase("list"))
         {
            ItemStack bookItem = books.getReadingList();
            bookStack.add(bookItem);
         }
         else if (args[1].equalsIgnoreCase("give"))
         {
            if (args.length < 4)
            {
               usageMessageBooks(sender);
            }

            //next arg is the target player
            String targetName = args[2];
            Player targetPlayer = getServer().getPlayer(targetName);
            if (targetPlayer == null)
            {
               sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                     + "Did not find player \"" + targetName + "\".\n");

               return true;
            }

            // args after "book give [player]" are book name
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
            ItemStack bookItem = getBookFromArgs(args, sender);
            if (bookItem == null)
            {
               return true;
            }

            bookStack.add(bookItem);
         }

         givePlayerKit((Player) sender, bookStack);
      }
      else
      {
         usageMessageBooks(sender);
      }

      return true;
   }

   /**
    * Get the book
    * @param args
    * @param sender
    * @return
    */
   private ItemStack getBookFromArgs (String[] args, CommandSender sender)
   {
      ItemStack bookItem;

      String bookName = new String();

      for (int i = 1; i < args.length; i++)
      {
         String s = args[i].toUpperCase();

         if (bookName.length() < 1)
         {
            //first word
            bookName = s;
         }
         else
         {
            bookName = bookName + "_" + s;
         }
      }
      if (debug)
         getLogger().info("Getting book " + bookName);

      Books bookType;
      try
      {
         bookType = Books.valueOf(bookName);
      }
      catch (Exception e)
      {
         sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
               + "No book named \"" + bookName + "\".\n");
         usageMessageBooks(sender);
         return null;
      }

      bookItem = books.getBook(bookType);

      return bookItem;
   }

   /**
    * Usage message for Books subcommands.
    *
    * @since 2.2.4
    * @param sender
    */
   private void usageMessageBooks (CommandSender sender)
   {
      sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
            + "Books commands: "
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
    * @param player
    */
   public void spellCannotBeCastMessage (Player player)
   {
      player.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
            + "A powerful protective magic prevents you from casting this spell here.");
   }

   /**
    * Set the players name to their team color if they are sorted.
    *
    * @param player
    */
   public void setPlayerTeamColor (Player player)
   {
      houses.setChatColor(player);
   }

   /**
    * Gives the command sender 1 of every Ollivanders2 potion.
    *
    * @param player
    * @return
    */
   private boolean givePotions (Player player)
   {
      if (debug)
         getLogger().info("Running givePotions...");

      List<ItemStack> kit = new ArrayList<>();

      for (Entry <String, List<ItemStack>> e : potionParser.allPotions.entrySet())
      {
         ItemStack potion = new ItemStack(Material.POTION, 1);
         ItemMeta meta = potion.getItemMeta();

         String potionType = e.getKey();

         ArrayList<String> lore = new ArrayList<>();
         lore.add(potionType);
         meta.setLore(lore);
         meta.setDisplayName(potionType);

         potion.setItemMeta(meta);

         if (debug)
            getLogger().info("Adding " + potionType);

         kit.add(potion);
      }

      givePlayerKit(player, kit);

      return true;
   }
}