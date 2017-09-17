package net.pottercraft.Ollivanders2;

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
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import Quidditch.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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

   private Map<UUID, OPlayer> OPlayerMap = new HashMap<UUID, OPlayer>();
   private List<SpellProjectile> projectiles = new ArrayList<SpellProjectile>();
   private List<StationarySpellObj> stationary = new ArrayList<StationarySpellObj>();

   private Set<Prophecy> prophecy = new HashSet<Prophecy>();
   private Listener playerListener;
   private OllivandersSchedule schedule;
   private List<Block> tempBlocks = new ArrayList<Block>();
   private FileConfiguration fileConfig;
   private O2Houses houses;

   private static String mcVersion;

   public static Random random = new Random();

   public static boolean debug = false;

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
         SLAPI.save(OPlayerMap, "plugins/Ollivanders2/OPlayerMap.bin");
         getLogger().finest("Saved OPlayerMap.bin");
      } catch (Exception e)
      {
         getLogger().warning("Could not save OPlayerMap.bin");
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

      getLogger().info(this + " is now disabled!");
   }

   @SuppressWarnings("unchecked")
   public void onEnable ()
   {
      playerListener = new OllivandersListener(this);
      getServer().getPluginManager().registerEvents(playerListener, this);
      //loads data
	   if (new File("plugins/Ollivanders2/").mkdirs())
	   {
		   getLogger().info("File created for Ollivanders2");
	   }
      OPlayerMap = new HashMap<UUID, OPlayer>();
      projectiles = new ArrayList<SpellProjectile>();
      stationary = new ArrayList<StationarySpellObj>();
      prophecy = new HashSet<Prophecy>();
      fileConfig = getConfig();
      //verify version of server
      mcVersion = Bukkit.getBukkitVersion();
      if (!mcVersionCheck())
      {
         getLogger().warning("MC version " + mcVersion + ". Some features of Ollivanders2 require MC 1.12 and higher.");
         //this.setEnabled(false);
         //return;
      }

      //finished loading data

      try
      {
         OPlayerMap = (HashMap<UUID, OPlayer>) SLAPI
               .load("plugins/Ollivanders2/OPlayerMap.bin");
         getLogger().finest("Loaded OPlayerMap.bin");
      } catch (Exception e)
      {
         getLogger().warning("Did not find OPlayerMap.bin");
      }
      try
      {
         stationary = (List<StationarySpellObj>) SLAPI
               .load("plugins/Ollivanders2/stationary.bin");
         getLogger().finest("Loaded stationary.bin");
      } catch (Exception e)
      {
         getLogger().warning("Did not find stationary.bin");
      }
      try
      {
         prophecy = (HashSet<Prophecy>) SLAPI
               .load("plugins/Ollivanders2/prophecy.bin");
         getLogger().finest("Loaded prophecy.bin");
      }
      catch (Exception e)
      {
         getLogger().warning("Did not find prophecy.bin");
      }

      if (!new File(this.getDataFolder(), "config.yml").exists())
      {
         this.saveDefaultConfig();
      }
      //debug
      if (getConfig().getBoolean("debug"))
         debug = true;

      fillAllSpellCount();
      this.schedule = new OllivandersSchedule(this);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.schedule, 20L, 1L);
      //floo powder recipe
      ItemStack flooPowder = new ItemStack(Material.getMaterial(fileConfig.getString("flooPowder")), 8);
      ItemMeta fmeta = flooPowder.getItemMeta();
      fmeta.setDisplayName("Floo Powder");
      List<String> flore = new ArrayList<String>();
      flore.add("Glittery, silver powder");
      fmeta.setLore(flore);
      flooPowder.setItemMeta(fmeta);
      //broomstick recipe
      ItemStack broomstick = new ItemStack(Material.getMaterial(fileConfig.getString("broomstick")));
      ItemMeta bmeta = broomstick.getItemMeta();
      bmeta.setDisplayName("Broomstick");
      List<String> blore = new ArrayList<String>();
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

      // set up houses
      houses = new O2Houses(this);
      houses.loadHouses();

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
      if (!isOp(sender))
      {
         return true;
      }

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
            sender.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor"))
                  + "Only server ops can use the /ollivanders2 commands.");
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
            return okitWands((Player) sender);
         else if (subCommand.equalsIgnoreCase("reload"))
            return runReloadConfigs(sender);
         else if (subCommand.equalsIgnoreCase("books"))
            return okitBooks((Player) sender);
         else if (subCommand.equalsIgnoreCase("items"))
            return okitItems((Player) sender);
         else if (subCommand.equalsIgnoreCase("house"))
            return runHouse(sender, cmd, commandLabel, args);
         else if (subCommand.equalsIgnoreCase("debug"))
            return toggleDebug(sender);
      }

      usageMessageOllivanders(sender);

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
               + "Houses are not currently enabled for your server."
               + "\nTo enable houses, update the Ollivanders2 config.yml setting to true and restart your server."
               + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki");

         return true;
      }

      // parse args
      if (args.length >= 2)
      {
         String subCommand = args[1];

         if (subCommand.equalsIgnoreCase("sort"))
         {
            // sort player in to a house
            if (args.length < 4)
            {
               usageMessageHouseSort(sender);

               return true;
            }

            return runSort(sender, args[2], args[3]);
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
            + "Ollivanders2 Houses are:\n" + houseNames + "\n"
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
    * @return true unless an error occurs
    */
   private boolean runSort (CommandSender sender, String targetPlayer, String targetHouse)
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

      if (houses.sort(player, house))
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
      List<ItemStack> kit = new ArrayList<ItemStack>();

      //Elder Wand
      ItemStack wand = new ItemStack(Material.BLAZE_ROD);
      List<String> lore = new ArrayList<String>();
      lore.add("Blaze and Ender Pearl");
      ItemMeta meta = wand.getItemMeta();
      meta.setLore(lore);
      meta.setDisplayName("Elder Wand");
      wand.setItemMeta(meta);
      kit.add(wand);

      //Cloak of Invisibility
      ItemStack cloak = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
      List<String> cloakLore = new ArrayList<String>();
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
    * Give a player all the books.
    *
    * @param player
    * @return true if the command was a success, false otherwise.
    */
   private boolean okitBooks (Player player)
   {
      List<ItemStack> kit = new ArrayList<ItemStack>();
      if (!kit.addAll(SpellBookParser.makeBooks(1)))
         return false;

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

      List<ItemStack> kit = new ArrayList<ItemStack>();

      for (String wood : woodArray)
      {
         for (String core : coreArray)
         {
            ItemStack wand = new ItemStack(Material.STICK);
            List<String> lore = new ArrayList<String>();
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

   public Map<UUID, OPlayer> getOPlayerMap ()
   {
      return OPlayerMap;
   }

   public void setOPlayerMap (Map<UUID, OPlayer> m)
   {
      OPlayerMap = m;
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
    * Get's the spell count associated with a player's spell.
    * This code relies on the fact that fillAllSpellCount() has placed every
    * spell from Spells into the OPlayer's SpellCount.
    *
    * @param player
    * @param spell
    * @return the spell count
    */
   public int getSpellNum (Player player, Spells spell)
   {
      OPlayer op = getOPlayer(player);
      Map<Spells, Integer> spellCount = op.getSpellCount();
      return spellCount.get(spell);
      //		if (spellCount.containsKey(spell)){
      //			return op.getSpellCount().get(spell);
      //		}
      //		spellCount.put(spell, 0);
      //		op.setSpellCount(spellCount);
      //		setOPlayer(player, op);
      //		return 0;
   }

   public void setSpellNum (Player player, Spells spell, int i)
   {
      OPlayer op = getOPlayer(player);
      Map<Spells, Integer> spellCount = op.getSpellCount();
      spellCount.put(spell, i);
      op.setSpellCount(spellCount);
      setOPlayer(player, op);
   }

   public int incSpellCount (Player player, Spells s)
   {
      //returns the incremented spell count
      OPlayer oply = OPlayerMap.get(player.getUniqueId());
      Map<Spells, Integer> spellMap = oply.getSpellCount();
      int next = spellMap.get(s) + 1;
      spellMap.put(s, next);
      oply.setSpellCount(spellMap);
      OPlayerMap.put(player.getUniqueId(), oply);
      return next;
   }

   /**
    * Gets the OPlayer associated with the Player
    *
    * @param p Player
    * @return Oplayer of playername s
    */
   public OPlayer getOPlayer (Player p)
   {
      if (OPlayerMap.containsKey(p.getUniqueId()))
      {
         return OPlayerMap.get(p.getUniqueId());
      }
      else
      {
         OPlayerMap.put(p.getUniqueId(), new OPlayer());
         getLogger().info("Put in new OPlayer.");
         return OPlayerMap.get(p.getUniqueId());
      }
   }

   /**
    * Sets the player's OPlayer by their playername
    *
    * @param p      the player
    * @param player the OPlayer associated with the player
    */
   public void setOPlayer (Player p, OPlayer player)
   {
      OPlayerMap.put(p.getUniqueId(), player);
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
      List<StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
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
   public ArrayList getOPlayerKeys ()
   {
      ArrayList<UUID> playerKeys = new ArrayList<UUID>();

      for (UUID key : OPlayerMap.keySet())
      {
         playerKeys.add(key);
      }

      return playerKeys;
   }

   /**
    * Fills every OPlayer's spell list with all possible spells that aren't there set to 0
    */
   private void fillAllSpellCount ()
   {
      for (UUID name : OPlayerMap.keySet())
      {
         OPlayer op = OPlayerMap.get(name);
         Map<Spells, Integer> spellCount = op.getSpellCount();
         for (Spells spell : Spells.values())
         {
            if (!spellCount.containsKey(spell))
            {
               spellCount.put(spell, 0);
            }
         }
         op.setSpellCount(spellCount);
         OPlayerMap.put(name, op);
      }
   }

   /**
    * Gets the worldguard plugin, if it exists.
    *
    * @return WorldGuardPlugin or null
    */
   private WorldGuardPlugin getWorldGuard ()
   {
      Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

      // WorldGuard may not be loaded
      if (plugin == null || !(plugin instanceof WorldGuardPlugin))
      {
         return null; // Maybe you want throw an exception instead
      }

      return (WorldGuardPlugin) plugin;
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
         player.sendMessage(ChatColor.getByChar(fileConfig.getString("chatColor")) + "Casting of "
               + spell.toString() + " is not allowed in this area");
      }
      return cast;
   }

   public boolean canLive (Location loc, Spells spell)
   {
		/*
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
            List<Spells> allowedSpells = new ArrayList<Spells>();
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
            List<Spells> disallowedSpells = new ArrayList<Spells>();
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
               List<Integer> area = new ArrayList<Integer>();
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
      if (Ollivanders2.debug)
         getLogger().info("Ollivander2:holdsWand: enter");

      if (player != null && player.getInventory().getItemInMainHand() != null)
      {
         if (Ollivanders2.debug)
            getLogger().info("Ollivander2:holdsWand: item in hand is not null, we can check it.");

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
      if (Ollivanders2.debug)
         getLogger().info("Ollivander2:isWand: enter");

      if (stack != null)
      {
         if (stack.getType() == Material.STICK || stack.getType() == Material.BLAZE_ROD)
         {
            if (Ollivanders2.debug)
               getLogger().info("Ollivander2:isWand: item is wand material");

            if (stack.getItemMeta().hasLore())
            {
               String itemName = stack.getItemMeta().getDisplayName();
               List<String> lore = stack.getItemMeta().getLore();
               //TODO refactor this - item name should contain "wand" and lore should not just contain "and" but have the correct components
               if (lore.get(0).split(" and ").length == 2)
               {
                  if (Ollivanders2.debug)
                     getLogger().info("Ollivander2:isWand: item is a wand");

                  return true;
               }
               else
               {
                  if (Ollivanders2.debug)
                     getLogger().info("Ollivander2:isWand: item is not a wand");

                  return false;
               }
            }
            else
            {
               if (Ollivanders2.debug)
                  getLogger().info("Ollivander2:isWand: item does not have wand lore");

               return false;
            }
         }
         else
         {
            if (Ollivanders2.debug)
               getLogger().info("Ollivander2:isWand: item is not a stick or blaze rod");

            return false;
         }
      }
      else
      {
         if (Ollivanders2.debug)
            getLogger().info("Ollivander2:isWand: item to check is null");

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
         int seed = Math.abs(player.getUniqueId().hashCode());
         Ollivanders2.random.setSeed(seed);
         int wood = Math.abs(Ollivanders2.random.nextInt() % 4);
         int core = Math.abs(Ollivanders2.random.nextInt() % 4);
         String[] woodArray = {"Spruce", "Jungle", "Birch", "Oak"};
         String woodString = woodArray[wood];
         String[] coreArray = {"Spider Eye", "Bone", "Rotten Flesh", "Gunpowder"};
         String coreString = coreArray[core];
         List<String> lore = stack.getItemMeta().getLore();
         String[] comps = lore.get(0).split(" and ");
         if (woodString.equals(comps[0]) && coreString.equals(comps[1]))
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
    * @return true of verion string is 1.12 - 1.19, false otherwise.
    */
   public static boolean mcVersionCheck ()
   {
      if (mcVersion.contains("1.12"))
         return true;

      return false;
   }
}