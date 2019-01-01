package net.pottercraft.Ollivanders2;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.UUID;

import Quidditch.Arena;

import net.pottercraft.Ollivanders2.Book.O2Books;
import net.pottercraft.Ollivanders2.Divination.O2Prophecies;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.House.O2HouseType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Player.O2PlayerCommon;
import net.pottercraft.Ollivanders2.Potion.IngredientType;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Potion.O2Potion;

import net.pottercraft.Ollivanders2.Spell.Transfiguration;

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
   private List<O2Spell> projectiles = new ArrayList<>();

   private List<Block> tempBlocks = new ArrayList<>();
   private FileConfiguration fileConfig;
   public O2Houses houses;
   public O2Players players;
   public O2Books books;
   public O2Spells spells;
   public O2Potions potions;
   public O2Prophecies prophecies;
   public O2StationarySpells stationarySpells;
   public Ollivanders2Common common;
   public O2PlayerCommon playerCommon;

   private static String mcVersion;
   public static boolean debug = false;
   public static boolean useNonVerbalCasting = false;
   public static boolean useHostileMobAnimagi = false;
   public static boolean useBookLearning = false;
   public static boolean useHouses = false;
   public static boolean useYears = false;
   public static Ollivanders2WorldGuard worldGuardO2;
   public static boolean worldGuardEnabled = false;
   public static boolean libsDisguisesEnabled = false;
   public static ChatColor chatColor = ChatColor.AQUA;
   public static Material wandMaterial = Material.STICK;
   public static int chatDropoff = 15;

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
      for (O2Spell proj : projectiles)
      {
         if (proj instanceof Transfiguration)
         {
            getLogger().finest("Ended transfiguration");
            ((Transfiguration) proj).endTransfigure();
         }
         proj.revert();
      }

      Ollivanders2API.saveStationarySpells();
      prophecies.saveProphecies();
      if (useHouses)
      {
         Ollivanders2API.saveHouses();
      }
      Ollivanders2API.savePlayers();

      getLogger().info(this + " is now disabled!");
   }

   /**
    * onEnable runs when the Minecraft server is starting up.
    *
    * Primary function is to set up static plugin data and load saved configs and data from disk.
    */
   public void onEnable ()
   {
      Ollivanders2API.init(this);

      Listener playerListener = new OllivandersListener(this);
      getServer().getPluginManager().registerEvents(playerListener, this);

      //loads data
      if (new File("plugins/Ollivanders2/").mkdirs())
      {
         getLogger().info("File directory for Ollivanders2");
      }
      projectiles = new ArrayList<>();
      fileConfig = getConfig();

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

      //
      // read configuration
      //

      debug = getConfig().getBoolean("debug");
      if (debug)
         getLogger().info("Enabling debug mode.");

      if (getConfig().isSet("chatColor"))
      {
         chatColor = ChatColor.getByChar(getConfig().getString("chatColor"));
         getLogger().info("Setting plugin message color to " + chatColor.toString());
      }

      if (getConfig().isSet("chatDropoff"))
      {
         int drop = getConfig().getInt("chatDropoff");
         if (drop > 0)
            chatDropoff = drop;
      }

      useNonVerbalCasting = getConfig().getBoolean("nonVerbalSpellCasting");
      if (useNonVerbalCasting)
         getLogger().info("Enabling non-verbal spell casting.");

      useHostileMobAnimagi = getConfig().getBoolean("hostileMobAnimagi");
      if (useHostileMobAnimagi)
         getLogger().info("Enabling hostile mob types for animagi.");

      useBookLearning = getConfig().getBoolean("bookLearning");
      if (useBookLearning)
         getLogger().info("Enabling book learning.");

      useHouses = getConfig().getBoolean("houses");
      if (useHouses)
         getLogger().info("Enabling school houses.");

      useYears = getConfig().getBoolean("years");
      if (useYears)
         getLogger().info("Enabling school years.");

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
         prophecies = new O2Prophecies(this);
      }
      catch (Exception e)
      {
         getLogger().warning("Failure setting up prophecies.");
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
         return playerSummary(sender, (Player)sender);
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
            if (args.length == 1)
            {
               return playerSummary(sender, (Player)sender);
            }
            else
            {
               Player target = getServer().getPlayer(args[1]);
               return playerSummary(sender, target);
            }
         }
         else if (subCommand.equalsIgnoreCase("potions"))
         {
            return runPotions(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("year"))
         {
            return runYear(sender, args);
         }
         else if (subCommand.equalsIgnoreCase("effect"))
         {
            return runEffect(sender, args);
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
   private boolean playerSummary (CommandSender sender, Player player)
   {
      if (player == null)
         return true;

      if (debug)
         getLogger().info("Running playerSummary");

      O2Player o2p = getO2Player(player);
      if (o2p == null)
      {
         sender.sendMessage(chatColor + "Unable to find player.");
         return true;
      }

      StringBuilder summary = new StringBuilder();

      summary.append("Ollivanders2 player summary:\n\n");

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
         if (Ollivanders2API.getHouses().isSorted(player))
         {
            String house = Ollivanders2API.getHouses().getHouse(player).getName();
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
         summary.append("\nAnimagus Form: ").append(Ollivanders2API.common.enumRecode(o2p.getAnimagusForm().toString()));
      }

      // effects
      if (isOp(sender))
      {
         List<O2EffectType> effects = Ollivanders2API.getPlayers().playerEffects.getEffects(o2p.getID());
         summary.append("\n\nAffected by:\n");

         if (effects == null || effects.isEmpty())
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
            if (knownSpells.containsKey(spellType))
            {
               String name = spellType.getSpellName();
               if (name != null) // happens if a spell is not currently loaded, such as if a server removes LibsDisguises
               {
                  summary.append("\n* ").append(name).append(" ").append(knownSpells.get(spellType).toString());
               }
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
            if (knownPotions.containsKey(potionType))
            {
               String name = potionType.getPotionName();
               if (name != null) // happens if a spell is not currently loaded, such as if a server removes LibsDisguises
               {
                  summary.append("\n* ").append(name).append(" ").append(knownPotions.get(potionType).toString());
               }
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
         {
            return Ollivanders2API.getHouses().reset();
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

         O2HouseType house = Ollivanders2API.getHouses().getHouseType(targetHouse);
         if (house != null)
         {
            ArrayList<String> members = Ollivanders2API.getHouses().getHouseMembers(house);
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
      ArrayList<String> h = Ollivanders2API.getHouses().getAllHouseNames();

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

      O2HouseType house = Ollivanders2API.getHouses().getHouseType(targetHouse);

      if (house == null)
      {
         sender.sendMessage(chatColor + targetHouse + " is not a valid house name.");

         return true;
      }

      boolean success;
      if (forcesort)
      {
         Ollivanders2API.getHouses().forceSetHouse(player, house);
         success = true;
      }
      else
      {
         success = Ollivanders2API.getHouses().sort(player, house);
      }

      if (success)
      {
         sender.sendMessage(chatColor + targetPlayer + " has been successfully sorted in to " + targetHouse);
      }
      else
      {
         String curHouse = Ollivanders2API.getHouses().getHouse(player).getName();
         if (curHouse == null)
         {
            sender.sendMessage(chatColor + "Oops, something went wrong with the sort.  If this persists, check your server logs.");
         }
         else
         {
            sender.sendMessage(chatColor + targetPlayer + " is already a member of " + Ollivanders2API.getHouses().getHouse(player).getName());
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
            return Ollivanders2API.getHouses().resetHousePoints();
         }

         if (args.length > 4)
         {
            String h = args[3];

            if (debug)
               getLogger().info("runHousePoints: house = " + h);

            O2HouseType houseType = null;
            try
            {
               houseType = Ollivanders2API.getHouses().getHouseType(h);
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
               return Ollivanders2API.getHouses().addHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("subtract"))
            {
               return Ollivanders2API.getHouses().subtractHousePoints(houseType, value);
            }
            else if (option.equalsIgnoreCase("set"))
            {
               return Ollivanders2API.getHouses().setHousePoints(houseType, value);
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
      if (o2p == null)
      {
         sender.sendMessage(chatColor + "Unable to find player.");
         return true;
      }

      int year = o2p.getYear().getIntValue() + yearChange;
      if (year > 0 && year < 8)
      {
         o2p.setYear(O2PlayerCommon.intToYear(year));
      }
      return true;
   }

   /**
    * The effects command, this is for testing purposes only and is not listed in the usage message.
    *
    * @param sender the player that issued the command
    * @param args the arguments for the command, if any
    * @return true unless an error occurred
    */
   private boolean runEffect (CommandSender sender, String[] args)
   {
      // olli effect <effect>
      if (args.length >= 2)
      {
         String [] subArgs = Arrays.copyOfRange(args, 1, args.length);
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

         if (Ollivanders2API.getPlayers().playerEffects.hasEffect(((Player) sender).getUniqueId(), effectType))
         {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(((Player) sender).getUniqueId(), effectType);
            sender.sendMessage(chatColor + "Removed " + effectName + " from " + sender + ".\n");
         }
         else
         {
            Class effectClass = effectType.getClassName();
            getLogger().info("Trying to add effect " + effectClass);

            O2Effect effect = null;
            try
            {
               effect = (O2Effect)effectClass.getConstructor(Ollivanders2.class, Integer.class, UUID.class).newInstance(this, 1200, ((Player) sender).getUniqueId());
            }
            catch (Exception e)
            {
               sender.sendMessage(chatColor + "Failed to add effect " + effectName + " to " + sender + ".\n");
               e.printStackTrace();
               return true;
            }

            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
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

      String wood = woodArray[Math.abs(Ollivanders2Common.random.nextInt() % woodArray.length)];
      String core = coreArray[Math.abs(Ollivanders2Common.random.nextInt() % coreArray.length)];

      List<ItemStack> kit = new ArrayList<>();
      ItemStack wand = new ItemStack(wandMaterial);
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
      List<ItemStack> kit = Ollivanders2API.common.getAllWands();

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
    * Get all the active spell projectiles
    *
    * @return a list of all active spell projectiles
    */
   public List<O2Spell> getProjectiles ()
   {
      return projectiles;
   }

   /**
    * Add a new spell projectile
    *
    * @param spell the spell projectile
    */
   public void addProjectile (O2Spell spell)
   {
      projectiles.add(spell);
   }

   /**
    * Remove a spell projectile. This will make it stop if it has not already been killed.
    *
    * @param spell the spell projectile
    */
   public void remProjectile (O2Spell spell)
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
   public int getSpellNum (Player player, O2SpellType spell)
   {
      O2Player o2p = getO2Player(player);

      return o2p.getSpellCount(spell);
   }

   /**
    * Set the spell use count for a player.
    *
    * @param player the player to set the spell count for
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellNum (Player player, O2SpellType spell, int count)
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
    * @param spell the spell to increment
    * @return the incremented use count for this player for this spell
    */
   public int incSpellCount (Player player, O2SpellType spell)
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
    * @param player the player to increment the count for
    * @param potionType the potion to increment
    */
   public void incPotionCount (Player player, O2PotionType potionType)
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
   public O2Player getO2Player (Player player)
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
    * Sets the player's OPlayer by their playername
    *
    * @param player      the player
    * @param o2p the OPlayer associated with the player
    */
   public void setO2Player (Player player, O2Player o2p)
   {
      if (!(player instanceof NPC))
      {
         Ollivanders2API.getPlayers().updatePlayer(player.getUniqueId(), o2p);
      }
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
      return Ollivanders2API.getPlayers().getPlayerIDs();
   }

   /**
    * Can this player cast this spell?
    *
    * @param player Player to check
    * @param spell Spell to check
    * @param verbose Whether or not to inform the player of why they cannot cast a spell
    * @return True if the player can cast this spell, false if not
    */
   public boolean canCast (Player player, O2SpellType spell, boolean verbose)
   {
      if (spell == null)
      {
         getLogger().info("canCast called for null spell");
         return false;
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
   public boolean canLive (Location loc, O2SpellType spell)
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
            List<O2SpellType> allowedSpells = new ArrayList<>();
            for (String spellString : config.getStringList(prefix + "allowed-spells"))
            {
               if (spellString.equalsIgnoreCase("ALL"))
               {
                  allAllowed = true;
               }
               else
               {
                  allowedSpells.add(Ollivanders2API.getSpells().getSpellTypeByName(spellString));
               }
            }
            List<O2SpellType> disallowedSpells = new ArrayList<>();
            for (String spellString : config.getStringList(prefix + "disallowed-spells"))
            {
               if (spellString.equalsIgnoreCase("ALL"))
               {
                  allDisallowed = true;
               }
               else
               {
                  disallowedSpells.add(Ollivanders2API.getSpells().getSpellTypeByName(spellString));
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
         bookStack = Ollivanders2API.getBooks().getAllBooks();

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
      String title = Ollivanders2API.common.stringArrayToString(args);

      ItemStack bookItem = Ollivanders2API.getBooks().getBookByTitle(title);

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
                  return givePotionIngredient((Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
               }
            }
         }
         else if (subCommand.equalsIgnoreCase("list"))
         {
            return listAllPotions((Player)sender);
         }
         else if (subCommand.equalsIgnoreCase("all"))
         {
            return giveAllPotions((Player)sender);
         }
         else if (subCommand.equalsIgnoreCase("give"))
         {
            if (args.length > 3)
            {
               // potions give fred memory potion
               Player targetPlayer = getServer().getPlayer(args[2]);
               String [] subArgs = Arrays.copyOfRange(args, 3, args.length);
               return givePotion(sender, targetPlayer, Ollivanders2API.common.stringArrayToString(subArgs));
            }
         }
         else
         {
            // potions memory potion
            String [] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return givePotion(sender, (Player) sender, Ollivanders2API.common.stringArrayToString(subArgs));
         }
      }

      usageMessagePotions(sender);
      return true;
   }

   /**
    * Give a specific potion to a player.
    *
    * @param player the player to give the potion to
    * @param potionName the potion to give the player
    */
   public boolean givePotion (CommandSender sender, Player player, String potionName)
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

      O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);

      if (potion == null)
         return true;

      ItemStack brewedPotion = potion.brew((Player)sender, false);
      List<ItemStack> kit = new ArrayList<>();
      kit.add(brewedPotion);

      givePlayerKit(player, kit);

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
   private boolean listAllIngredients (Player player)
   {
      List<String> ingredientList = IngredientType.getAllIngredientNames();
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
   private boolean listAllPotions (Player player)
   {
      StringBuilder displayString = new StringBuilder();
      displayString.append("Potions:");

      List<String> potionNames = Ollivanders2API.getPotions().getAllPotionNames();
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
   private boolean givePotionIngredient(Player player, String name)
   {
      List<ItemStack> kit = new ArrayList<>();
      ItemStack ingredient = Ollivanders2API.getPotions().getIngredientByName(name);

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

      for (O2Potion potion : Ollivanders2API.getPotions().getAllPotions())
      {
         ItemStack brewedPotion = potion.brew(player, false);

         if (debug)
            getLogger().info("Adding " + potion.getName());

         kit.add(brewedPotion);
      }

      givePlayerKit(player, kit);

      return true;
   }

   /**
    * Show a list of all Ollivanders2 books
    *
    * @param player the player to display the list to
    */
   public void listAllBooks (Player player)
   {
      StringBuilder titleList = new StringBuilder();
      titleList.append("Book Titles:");

      for (String bookTitle : Ollivanders2API.getBooks().getAllBookTitles())
      {
         titleList.append("\n").append(bookTitle);
      }

      player.sendMessage(chatColor + titleList.toString());
   }
}