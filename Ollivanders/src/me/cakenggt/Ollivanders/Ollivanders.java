package me.cakenggt.Ollivanders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
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
 * Ollivanders plugin object
 * @author lownes
 *
 */
public class Ollivanders extends JavaPlugin{

	private Map<String, OPlayer> OPlayerMap = new HashMap<String, OPlayer>();
	private List<SpellProjectile> projectiles = new ArrayList<SpellProjectile>();
	private List<StationarySpellObj> stationary = new ArrayList<StationarySpellObj>();;
	private Set<Prophecy> prophecy = new HashSet<Prophecy>();
	private Listener playerListener;
	private OllivandersSchedule schedule;
	private List<Block> tempBlocks = new ArrayList<Block>();
	private FileConfiguration fileConfig;

	public void onDisable() {
		for (Block block : tempBlocks){
			block.setType(Material.AIR);
		}
		for (SpellProjectile proj : projectiles){
			if (proj instanceof Transfiguration){
				getLogger().finest("Ended transfiguration");
				((Transfiguration)proj).endTransfigure();
			}
			proj.revert();
		}
		for (StationarySpellObj stat : stationary){
			stat.active = true;
		}
		try {
			SLAPI.save(OPlayerMap, "plugins/Ollivanders/OPlayerMap.bin");
			getLogger().finest("Saved OPlayerMap.bin");
		} catch (Exception e) {
			getLogger().warning("Could not save OPlayerMap.bin");
		}
		try {
			SLAPI.save(stationary, "plugins/Ollivanders/stationary.bin");
			getLogger().finest("Saved stationary.bin");
		} catch (Exception e) {
			getLogger().warning("Could not save stationary.bin");
		}
		try {
			SLAPI.save(prophecy, "plugins/Ollivanders/prophecy.bin");
			getLogger().finest("Saved prophecy.bin");
		} catch (Exception e) {
			getLogger().warning("Could not save prophecy.bin");
		}
		getLogger().info(this + " is now disabled!");
	}

	@SuppressWarnings("unchecked")
	public void onEnable() {
		playerListener = new OllivandersListener(this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		//loads data
		if (new File("plugins/Ollivanders/").mkdirs())
			getLogger().info("File created for Ollivanders");
		OPlayerMap = new HashMap<String, OPlayer>();
		projectiles = new ArrayList<SpellProjectile>();
		stationary = new ArrayList<StationarySpellObj>();
		prophecy = new HashSet<Prophecy>();
		fileConfig = getConfig();
		//finished loading data
		if (fileConfig.getBoolean("update")){
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, 72117, this.getFile(), Updater.UpdateType.DEFAULT, true);
		}
		try {
			OPlayerMap = (HashMap<String, OPlayer>) SLAPI
					.load("plugins/Ollivanders/OPlayerMap.bin");
			getLogger().finest("Loaded OPlayerMap.bin");
		} catch (Exception e) {
			getLogger().warning("Did not find OPlayerMap.bin");
		}
		try {
			stationary = (List<StationarySpellObj>) SLAPI
					.load("plugins/Ollivanders/stationary.bin");
			getLogger().finest("Loaded stationary.bin");
		} catch (Exception e) {
			getLogger().warning("Did not find stationary.bin");
		}
		try {
			prophecy = (HashSet<Prophecy>) SLAPI
					.load("plugins/Ollivanders/prophecy.bin");
			getLogger().finest("Loaded prophecy.bin");
		} catch (Exception e) {
			getLogger().warning("Did not find prophecy.bin");
		}
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		if (!new File(this.getDataFolder(), "config.yml").exists()){
			this.saveDefaultConfig();
		}
		fillAllSpellCount();
		this.schedule = new OllivandersSchedule(this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.schedule, 20L, 1L);
		ItemStack flooPowder = new ItemStack(Material.REDSTONE, 8);
		ItemMeta meta = flooPowder.getItemMeta();
		meta.setDisplayName("Floo Powder");
		List<String> lore = new ArrayList<String>();
		lore.add("Glittery, silver powder");
		meta.setLore(lore);
		flooPowder.setItemMeta(meta);
		getServer().addRecipe(new FurnaceRecipe(flooPowder, Material.ENDER_PEARL));
		getLogger().info(this + " is now enabled!");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("Okit")){
			String[] woodArray = {"Spruce","Jungle","Birch","Oak"};
			String[] coreArray = {"Spider Eye","Bone","Rotten Flesh","Gunpowder"};
			Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			if (args.length == 1){
				if (args[0].equalsIgnoreCase("reload")){
					if (player != null){
						if (!player.isOp()){
							sender.sendMessage("Only server ops can use the /Okit command.");
							return true;
						}
					}
					reloadConfig();
					fileConfig = getConfig();
					sender.sendMessage("Config reloaded");
					return true;
				}
			}
			if (args.length >= 4){
				if (player != null){
					if (!player.isOp()){
						sender.sendMessage("Only server ops can use the /Okit command.");
					}
				}
				if (args[0].equalsIgnoreCase("wand")){
					Player receiver = Bukkit.getPlayer(args[1]);
					if (receiver != null){
						String wood = "";
						String core = "";
						if (args[2].startsWith("*")){
							wood = woodArray[(int) (Math.random()*4)];
						}
						else{
							for (String w : woodArray){
								if (w.toLowerCase().startsWith(args[2].toLowerCase())){
									wood = w;
									break;
								}
							}
						}
						if (args[3].startsWith("*")){
							core = coreArray[(int) (Math.random()*4)];
						}
						else{
							for (String c : coreArray){
								if (c.toLowerCase().startsWith(args[3].toLowerCase())){
									core = c;
									break;
								}
							}
						}
						ItemStack wand = new ItemStack(Material.STICK);
						List<String> lore = new ArrayList<String>();
						lore.add(wood + " and " + core);
						ItemMeta meta = wand.getItemMeta();
						meta.setLore(lore);
						meta.setDisplayName("Wand");
						wand.setItemMeta(meta);
						boolean add = true;
						if (args.length == 5){
							int wandIndex = 0;
							boolean changeWand = false;
							for (ItemStack item : receiver.getInventory()){
								if (isWand(item)){
									add = false;
									if (args[4].equalsIgnoreCase("t")){
										wandIndex = receiver.getInventory().first(item);
										changeWand = true;
									}
									else if (args[4].equalsIgnoreCase("f")){
										if (!destinedWand(receiver, item)){
											wandIndex = receiver.getInventory().first(item);
											changeWand = true;
										}
									}
									break;
								}
							}
							if (changeWand){
								receiver.getInventory().setItem(wandIndex, wand);
							}
						}
						if (add){
							if (receiver.getInventory().addItem(wand).size() != 0){
								receiver.getWorld().dropItem(receiver.getLocation(), wand);
							}
						}
					}
				}
				return true;
			}
			else if (player != null){
				sender.sendMessage("Ollivanders " + this.getDescription().getVersion());
				//Arguments of command
				boolean wands = true;
				boolean books = true;
				int amount = 1;
				if (args.length > 0){
					if (args[0].equalsIgnoreCase("wands")){
						books = false;
					}
					else if (args[0].equalsIgnoreCase("books")){
						wands = false;
					}
					if (args.length == 2){
						try {  
							amount = Integer.parseInt(args[1]);  
						}  
						catch(NumberFormatException nfe) { 
						}  
					}
				}
				if (player.isOp()){
					//give them the kit
					Location loc = player.getEyeLocation();
					List<ItemStack> kit = new ArrayList<ItemStack>();
					//Give amount of each type of wand
					if (wands){
						for (String i : woodArray){
							for (String j : coreArray){
								ItemStack wand = new ItemStack(Material.STICK);
								List<String> lore = new ArrayList<String>();
								lore.add(i + " and " + j);
								ItemMeta meta = wand.getItemMeta();
								meta.setLore(lore);
								meta.setDisplayName("Wand");
								wand.setItemMeta(meta);
								wand.setAmount(amount);
								kit.add(wand);
							}
						}
					}
					if (wands && books){
						//Give Elder Wand
						ItemStack wand = new ItemStack(Material.BLAZE_ROD);
						List<String> lore = new ArrayList<String>();
						lore.add("Blaze and Ender Pearl");
						ItemMeta meta = wand.getItemMeta();
						meta.setLore(lore);
						meta.setDisplayName("Elder Wand");
						wand.setItemMeta(meta);
						kit.add(wand);
						//Give Invisibility Cloak
						ItemStack cloak = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
						List<String> cloakLore = new ArrayList<String>();
						cloakLore.add("Silvery Transparent Cloak");
						ItemMeta cloakMeta = cloak.getItemMeta();
						cloakMeta.setLore(cloakLore);
						cloakMeta.setDisplayName("Cloak of Invisibility");
						cloak.setItemMeta(cloakMeta);
						kit.add(cloak);
					}
					//give them books
					if (books){
						List<ItemStack> booksList = SpellBookParser.makeBooks(amount);
						kit.addAll(booksList);
					}
					ItemStack[] kitArray = kit.toArray(new ItemStack[kit.size()]);
					HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kitArray);
					for (ItemStack item : leftover.values()){
						player.getWorld().dropItem(loc, item);
					}
				}
				else{
					sender.sendMessage("Only server ops can use the /Okit command.");
				}
				return true;
			}
		}
		return false;
	}

	public Map<String, OPlayer> getOPlayerMap(){
		return OPlayerMap;
	}

	public void setOPlayerMap(Map<String, OPlayer> m){
		OPlayerMap = m;
	}

	public int getChatDistance(){
		return fileConfig.getInt("chatDropoff");
	}

	public List<SpellProjectile> getProjectiles(){
		return projectiles;
	}

	public void addProjectile(SpellProjectile s){
		projectiles.add(s);
	}

	public void remProjectile(SpellProjectile s){
		projectiles.remove(s);
	}

	/**
	 * Get's the spell count associated with a player's spell.
	 * This code relies on the fact that fillAllSpellCount() has placed every
	 * spell from Spells into the OPlayer's SpellCount.
	 * @param player
	 * @param spell
	 * @return the spell count
	 */
	public int getSpellNum(Player player, Spells spell){
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

	public void setSpellNum(Player player, Spells spell, int i){
		OPlayer op = getOPlayer(player);
		Map<Spells, Integer> spellCount = op.getSpellCount();
		spellCount.put(spell, i);
		op.setSpellCount(spellCount);
		setOPlayer(player, op);
	}

	public int incSpellCount(Player player, Spells s){
		//returns the incremented spell count
		OPlayer oply = OPlayerMap.get(player.getName());
		Map<Spells, Integer> spellMap = oply.getSpellCount();
		int next = spellMap.get(s)+1;
		spellMap.put(s, next);
		oply.setSpellCount(spellMap);
		OPlayerMap.put(player.getName(), oply);
		return next;
	}

	/**
	 * Gets the OPlayer associated with the Player
	 * @param p Player
	 * @return Oplayer of playername s
	 */
	public OPlayer getOPlayer(Player p){
		if (OPlayerMap.containsKey(p.getName())){
			return OPlayerMap.get(p.getName());
		}
		else{
			OPlayerMap.put(p.getName(), new OPlayer());
			getLogger().info("Put in new OPlayer.");
			return OPlayerMap.get(p.getName());
		}
	}

	/**
	 * Sets the player's OPlayer by their playername
	 * @param p the player
	 * @param player the OPlayer associated with the player
	 */
	public void setOPlayer(Player p, OPlayer player){
		OPlayerMap.put(p.getName(), player);
	}

	/**
	 * Gets the list of stationary spell objects
	 * @return List of stationary spell objects in server
	 */
	public List<StationarySpellObj> getStationary(){
		return stationary;
	}

	/**Gets the set of prophecy objects
	 * @return Set of prophecy objects in server
	 */
	public Set<Prophecy> getProphecy(){
		return prophecy;
	}

	/**
	 * Adds a stationary spell object to plugin stationary spell ojbect list
	 * @param s - StationarySpellObj to be added to list. Cannot be null.
	 */
	public void addStationary(StationarySpellObj s){
		stationary.add(s);
	}

	/**
	 * Removes a stationary spell object from plugin's stationary list
	 * @param s - StationarySpellObj to be removed. Cannot be null.
	 */
	public void remStationary(StationarySpellObj s){
		stationary.remove(s);
	}

	/**
	 * Checks if the location is within one or more stationary spell objects, regardless of whether or not they are active.
	 * @param location - location to check
	 * @return List of StationarySpellObj that the location is inside
	 */
	public List<StationarySpellObj> checkForStationary(Location location){
		List<StationarySpellObj> stationaries = getStationary();
		List<StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
		for (StationarySpellObj stationary: stationaries){
			if (stationary.location.getWorld().equals(location.getWorld().getName())){
				if (stationary.location.distance(location) < stationary.radius){
					inside.add(stationary);
				}
			}
		}
		return inside;
	}

	/**
	 * Gets the tempBlocks list.
	 * @return tempBlocks Block list.
	 */
	public List<Block> getTempBlocks(){
		return tempBlocks;
	}

	public boolean isInsideOf(StationarySpells statName, Location loc){
		for (StationarySpellObj stat : getStationary()){
			if (stat.name == statName){
				if (stat.isInside(loc) && stat.active){
					return true;
				}
			}
		}
		return false;
	}

	/**Fills every OPlayer's spell list with all possible spells that aren't there set to 0
	 * 
	 */
	private void fillAllSpellCount(){
		for (String name : OPlayerMap.keySet()){
			OPlayer op = OPlayerMap.get(name);
			Map<Spells, Integer> spellCount = op.getSpellCount();
			for (Spells spell : Spells.values()){
				if (!spellCount.containsKey(spell)){
					spellCount.put(spell, 0);
				}
			}
			op.setSpellCount(spellCount);
			OPlayerMap.put(name, op);
		}
	}

	/**Gets the worldguard plugin, if it exists.
	 * @return WorldGuardPlugin or null
	 */
	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	/**Can this player cast this spell?
	 * @param player - Player to check
	 * @param spell - Spell to check
	 * @param verbose - Whether or not to inform the player of why they cannot cast a spell
	 * @return True if yes, false if not
	 */
	public boolean canCast(Player player, Spells spell, boolean verbose){
		if (player.isPermissionSet("Ollivanders."+spell.toString())){
			if (!player.hasPermission("Ollivanders." + spell.toString())){
				if (verbose){
					player.sendMessage("You do not have permission to use " + spell.toString());
				}
				return false;
			}
		}
		boolean cast = canLive(player.getLocation(), spell);
		if (!cast && verbose){
			player.sendMessage("Casting of " + spell.toString() + " is not allowed in this area");
		}
		return cast;
	}

	public boolean canLive(Location loc, Spells spell){
		/*
		 * cast is whether or not the spell can be cast.
		 * set to false whenever it can't be
		 * return true whenever it is in allowed-spells
		 */
		boolean cast = true;
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		if (fileConfig.contains("zones")){
			ConfigurationSection config = fileConfig.getConfigurationSection("zones");
			for (String zone : config.getKeys(false)){
				String prefix = zone + ".";
				String type = config.getString(prefix + "type");
				String world = config.getString(prefix + "world");
				String region = config.getString(prefix + "region");
				String areaString = config.getString(prefix + "area");
				boolean allAllowed = false;
				boolean allDisallowed = false;
				List<Spells> allowedSpells = new ArrayList<Spells>();
				for (String spellString : config.getStringList(prefix + "allowed-spells")){
					if (spellString.equalsIgnoreCase("ALL")){
						allAllowed = true;
					}
					else{
						allowedSpells.add(Spells.decode(spellString));
					}
				}
				List<Spells> disallowedSpells = new ArrayList<Spells>();
				for (String spellString : config.getStringList(prefix + "disallowed-spells")){
					if (spellString.equalsIgnoreCase("ALL")){
						allDisallowed = true;
					}
					else{
						disallowedSpells.add(Spells.decode(spellString));
					}
				}
				if (type.equalsIgnoreCase("World")){
					if (loc.getWorld().getName().equals(world)){
						if (allowedSpells.contains(spell) || allAllowed){
							return true;
						}
						if (disallowedSpells.contains(spell) || allDisallowed){
							cast = false;
						}
					}
				}
				if (type.equalsIgnoreCase("Cuboid")){
					List<String> areaStringList = Arrays.asList(areaString.split(" "));
					List<Integer> area = new ArrayList<Integer>();
					for (int i = 0; i < areaStringList.size(); i++){
						area.add(Integer.parseInt(areaStringList.get(i)));
					}
					if (area.size() < 6){
						for (int i = 0; i < 6; i++){
							area.set(i, 0);
						}
					}
					if (loc.getWorld().getName().equals(world)){
						if ((area.get(0) < x) && (x < area.get(3))){
							if ((area.get(1) < y) && (y < area.get(4))){
								if ((area.get(2) < z) && (z < area.get(5))){
									if (allowedSpells.contains(spell) || allAllowed){
										return true;
									}
									if (disallowedSpells.contains(spell) || allDisallowed){
										cast = false;
									}
								}
							}
						}
					}
				}
				if (type.equalsIgnoreCase("WorldGuard")){
					WorldGuardPlugin worldGuard = getWorldGuard();
					if (worldGuard != null){
						RegionManager regionManager = worldGuard.getRegionManager(Bukkit.getWorld(world));
						if (regionManager != null){
							ProtectedRegion protRegion = regionManager.getRegion(region);
							if (protRegion != null){
								if (protRegion.contains(BukkitUtil.toVector(loc))){
									if (allowedSpells.contains(spell) || allAllowed){
										return true;
									}
									if (disallowedSpells.contains(spell) || allDisallowed){
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

	/**Get the file configuration
	 * @return FileConfiguration
	 */
	public FileConfiguration getFileConfig(){
		return fileConfig;
	}

	/**Is this item stack a wand?
	 * @param stack - stack to be checked
	 * @return true if yes, false if no
	 */
	public static boolean isWand(ItemStack stack){
		if (stack != null){
			if (stack.getType() == Material.STICK || stack.getType() == Material.BLAZE_ROD){
				if (stack.getItemMeta().hasLore()){
					List<String> lore = stack.getItemMeta().getLore();
					if (lore.get(0).split(" and ").length == 2){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					return false;
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**Is this itemstack the player's destined wand?
	 * @param player - Player to check the stack against.
	 * @param stack - Itemstack to be checked
	 * @return true if yes, false if no
	 */
	public static boolean destinedWand(Player player, ItemStack stack){
		if (isWand(stack)){
			String charList = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
			int wood = player.getName().length()%4;
			String[] woodArray = {"Spruce","Jungle","Birch","Oak"};
			String woodString = woodArray[wood];
			int core = 0;
			for (char a : player.getName().toCharArray()){
				core += charList.indexOf(a)+1;
			}
			String[] coreArray = {"Spider Eye","Bone","Rotten Flesh","Gunpowder"};
			String coreString = coreArray[core%4];
			List<String> lore = stack.getItemMeta().getLore();
			String[] comps = lore.get(0).split(" and ");
			if (woodString.equals(comps[0]) && coreString.equals(comps[1])){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/** SLAPI = Saving/Loading API
	 * API for Saving and Loading Objects.
	 * @author Tomsik68
	 */
	public static class SLAPI
	{
		public static void save(Object obj,String path) throws Exception
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		public static Object load(String path) throws Exception
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object result = ois.readObject();
			ois.close();
			return result;
		}
	}
}