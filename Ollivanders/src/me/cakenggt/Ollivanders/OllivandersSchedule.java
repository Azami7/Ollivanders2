package me.cakenggt.Ollivanders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import Effect.Effect;
import Effect.VENTO_FOLIO;
import Spell.Spell;
import StationarySpell.REPELLO_MUGGLETON;
import StationarySpell.StationarySpell;

/**
 * Scheduler for Ollivanders
 * @author lownes
 *
 */
class OllivandersSchedule implements Runnable{
	Ollivanders p;
	int counter = 0;
	static Set<UUID> flying = new HashSet<UUID>();
	public OllivandersSchedule(Ollivanders plugin) {
		p = plugin;
	}

	public void run() {
		try
		{
			projectileSched();	
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			oeffectSched();	
		}
		catch(Exception e){}
		
		try
		{
			stationarySched();	
		}
		catch(Exception e){}
		
		try
		{
			broomSched();	
		}
		catch(Exception e){}
				
		if (counter %20 == 0){
			itemCurseSched();
		}
		if (counter %20 == 1){
			invisPlayer();
		}
		if (counter %20 == 2 && p.getConfig().getBoolean("divination")){
			scry();
			effectProphecy();
		}
		counter = (counter+1)%20;
	}

	/**Scheduling method that calls checkEffect() on all SpellProjectile objects
	 * and removes those that have kill set to true.
	 * 
	 */
	private void projectileSched(){
		List<SpellProjectile> projectiles = p.getProjectiles();
		List<SpellProjectile> projectiles2 = new ArrayList<SpellProjectile>(projectiles);
		if (projectiles2.size() > 0){
			for (SpellProjectile proj : projectiles2){
				((Spell) proj).checkEffect();
				if (proj.kill){
					p.remProjectile(proj);
				}
			}
		}
	}

	/**
	 * Scheduling method that calls checkEffect on all OEffect objects associated with every player
	 * and removes those that have kill set to true.
	 */
	private void oeffectSched(){
		List<Player> onlinePlayers = new ArrayList<Player>();
		for (World world : p.getServer().getWorlds()){
			onlinePlayers.addAll(world.getPlayers());
		}
			
		for (UUID name : p.getOPlayerMap().keySet()){
			OPlayer oply = p.getOPlayerMap().get(name);
			if (oply.getEffects() != null && onlinePlayers.contains(p.getServer().getPlayer(name))){
				List<OEffect> effects2 = new ArrayList<OEffect>(oply.getEffects());
				for (OEffect effect : effects2){
					((Effect) effect).checkEffect(p, Bukkit.getPlayer(name));
					if (effect.kill){
						oply.remEffect(effect);
					}
				}
			}
		}
	}

	/**
	 * Scheduling method that calls checkEffect on all StationarySpellObj objects associated with every player
	 * and removes those that have kill set to true.
	 */
	private void stationarySched(){
		List<StationarySpellObj> stationary = p.getStationary();
		List<StationarySpellObj> stationary2 = new ArrayList<StationarySpellObj>(stationary);
		if (stationary2.size() > 0){
			for (StationarySpellObj stat : stationary2){
				if (stat.active){
					((StationarySpell) stat).checkEffect(p);
				}
				if (stat.kill){
					p.remStationary(stat);
				}
			}
		}
	}

	/**
	 * Scheduling method that checks for any geminio or
	 * flagrante curses on items in player inventories and
	 * performs their effect.
	 */
	private void itemCurseSched(){
		for (World world : p.getServer().getWorlds()){
			for (Player player : world.getPlayers()){
				List<ItemStack> geminioIS = new ArrayList<ItemStack>();
				ListIterator<ItemStack> invIt = player.getInventory().iterator();
				while (invIt.hasNext()){
					ItemStack item = invIt.next();
					if (item != null){
						ItemMeta meta = item.getItemMeta();
						if (meta.hasLore()){
							List<String> lored = meta.getLore();
							for (String lore : lored){
								if (lore.contains("Geminio ")){
									geminioIS.add(geminio(item.clone()));
									invIt.set(null);
								}
								if (lore.contains("Flagrante ")){
									flagrante(player, item);
								}
							}
						}
					}
				}
				HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(geminioIS.toArray(new ItemStack[geminioIS.size()]));
				for (ItemStack item : leftover.values()){
					player.getWorld().dropItem(player.getLocation(), item);
				}
			}
		}
	}

	/** Enacts the geminio duplicating effect on an itemstack
	 * @param item - item with geminio curse on it
	 * @return Duplicated itemstacks
	 */
	private ItemStack geminio(ItemStack item){
		int stackSize = item.getAmount();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		ArrayList<String> newLore = new ArrayList<String>();
		for (int i = 0; i < lore.size(); i++){
			if (lore.get(i).contains("Geminio ")){
				String[] loreParts = lore.get(i).split(" ");
				int magnitude = Integer.parseInt(loreParts[1]);
				if (magnitude > 1){
					magnitude --;
					newLore.add("Geminio " + magnitude);
				}
				stackSize = stackSize * 2;
			}
			else{
				newLore.add(lore.get(i));
			}
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		item.setAmount(stackSize);
		return item;
	}

	/**Enacts the flagrante burning effect on the player
	 * @param item
	 */
	private void flagrante(Player player, ItemStack item){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int magnitude = 0;
		for (int i = 0; i < lore.size(); i++){
			if (lore.get(i).contains("Flagrante ")){
				String[] loreParts = lore.get(i).split(" ");
				magnitude = Integer.parseInt(loreParts[1]);
			}
		}
		player.damage(magnitude * 0.05 * item.getAmount());
		player.setFireTicks(160);
	}

	/**Hides a player with the Cloak of Invisibility from other players.
	 * Also hides players in Repello Muggleton from players not in that same spell.
	 * Also sets any Creature targeting this player to have null target.
	 * 
	 */
	private void invisPlayer(){
		for (World world : p.getServer().getWorlds()){
			for (Player player : world.getPlayers()){
				OPlayer oplayer = p.getOPlayer(player);
				Set<REPELLO_MUGGLETON> muggletons = new HashSet<REPELLO_MUGGLETON>();
				for (StationarySpellObj stat : p.getStationary()){
					if (stat instanceof REPELLO_MUGGLETON){
						if (stat.isInside(player.getLocation()) && stat.active){
							muggletons.add((REPELLO_MUGGLETON) stat);
						}
					}
				}
				boolean hasCloak = hasCloak(player);
				if (hasCloak || muggletons.size() > 0){
					oplayer.setMuggleton(true);
					
					for (Player viewer : world.getPlayers()){
						if (viewer.isPermissionSet("Ollivanders.BYPASS")){
							if (viewer.hasPermission("Ollivanders.BYPASS")){
								continue;
							}
						}
						if (muggletons.size() == 0){
							viewer.hidePlayer(player);
						}
						else{
							for (REPELLO_MUGGLETON muggleton : muggletons){
								if (hasCloak || (!muggleton.isInside(viewer.getLocation()) && muggleton.active)){
									viewer.hidePlayer(player);
									break;
								}
								else{
									viewer.showPlayer(player);
								}
							}
						}
					}
					
					if (!oplayer.isInvisible()){
						for (Entity entity : player.getWorld().getEntities()){
							if (entity instanceof Creature){
								Creature creature = (Creature)entity;
								if (creature.getTarget() == player){
									if (muggletons.size() == 0){
										creature.setTarget(null);
									}
									else{
										for (REPELLO_MUGGLETON muggleton : muggletons){
											if (hasCloak || (!muggleton.isInside(creature.getLocation()) && muggleton.active)){
												creature.setTarget(null);
											}
										}
									}
								}
							}
						}
					}
					oplayer.setInvisible(hasCloak);
				}
				else if (oplayer.isInvisible() || oplayer.isMuggleton()){
					for (Player viewer : world.getPlayers()){
						viewer.showPlayer(player);
					}
					oplayer.setInvisible(false);
					oplayer.setMuggleton(false);
				}
			}
		}
	}

	/**Does the player have the Cloak of Invisibility
	 * @param player - Player to be checked
	 * @return - True if yes, false if no
	 */
	private boolean hasCloak(Player player){
		ItemStack chestPlate = player.getInventory().getChestplate();
		if (chestPlate != null){
			if (chestPlate.getType() == Material.CHAINMAIL_CHESTPLATE){
				if (chestPlate.getItemMeta().hasLore()){
					List<String> lore = chestPlate.getItemMeta().getLore();
					if (lore.get(0).equals("Silvery Transparent Cloak")){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**Checks all players to see if they will receive a prophecy
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void scry(){
		Material ball = Material.getMaterial("divinationBlock");		

		for (World world : p.getServer().getWorlds()){
			for (Player player : world.getPlayers()){
				if (player.getTargetBlock(null, 100).getType() != ball || !player.isSneaking()){
					return;
				}
				double experience = p.getOPlayer(player).getSpellCount().get(Spells.INFORMOUS);
				if (Math.random() < experience/1000.0){
					//The scrying is successful
					Prophecy prophecy = new Prophecy(player);
					//Prophecy prophecy = new Prophecy(player, 2000, 30000);
					p.getProphecy().add(prophecy);
					String message = "";
					List<String> lore = prophecy.toLore();
					for (String str : lore){
						message = message.concat(str + " ");
					}
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + message);
					ItemStack hand = player.getItemInHand();
					if (hand.getType() == ball){
						ItemStack record = new ItemStack(ball, 1);
						ItemMeta recordM = record.getItemMeta();
						recordM.setDisplayName("Prophecy Record");
						recordM.setLore(lore);
						record.setItemMeta(recordM);
						if (hand.getAmount() == 1){
							player.setItemInHand(null);
						}
						else{
							hand.setAmount(hand.getAmount()-1);
							player.setItemInHand(hand);
						}
						for (ItemStack drop : player.getInventory().addItem(record).values()){
							player.getWorld().dropItem(player.getLocation(), drop);
						}
					}
				}
			}
		}
	}

	/**Goes through all prophecies and enacts them if they are due
	 * and deletes them if they are past
	 */
	private void effectProphecy(){
		Iterator<Prophecy> iter = p.getProphecy().iterator();
		while (iter.hasNext()){
			Prophecy prop = iter.next();
			if (prop.isActive()){
				Player player = p.getServer().getPlayer(prop.getPlayerUUID());
				if (player != null){
					player.addPotionEffect(prop.toPotionEffect(), true);
				}
			}
			else if (prop.isFinished()){
				iter.remove();
			}
		}
	}

	/**Goes through all players and sets any holding a broom to flying
	 * 
	 */
	private void broomSched(){
		playerIter:
			for (World world : p.getServer().getWorlds()) {			
				for (Player player : world.getPlayers()){
					if (p.isBroom(player.getItemInHand()) && p.canLive(player.getLocation(), Spells.VOLATUS)){
						player.setAllowFlight(true);
						player.setFlying(true);
						if (flying.contains(player.getUniqueId())){
							Vector broomVec = player.getLocation().getDirection().clone();
							broomVec.multiply(Math.sqrt(player.getItemInHand().getEnchantmentLevel(Enchantment.PROTECTION_FALL))/40.0);
							player.setVelocity(player.getVelocity().add(broomVec));
						}
					}
					else{
						if (player.getGameMode() == GameMode.SURVIVAL){
							for (OEffect effect : p.getOPlayer(player).getEffects()){
								if (effect instanceof VENTO_FOLIO){
									continue playerIter;
								}
							}
							player.setAllowFlight(false);
							player.setFlying(false);
						}
					}
				}
			}
	}

	/**Get the set of flying players
	 * @return set of flying players
	 */
	public static Set<UUID> getFlying(){
		return flying;
	}
}