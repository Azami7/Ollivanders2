package me.cakenggt.Ollivanders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import StationarySpell.SPONGIFY;

/**
 * Listener for events from the plugin
 * @author lownes
 *
 */
public class OllivandersPlayerListener implements Listener {

	Ollivanders p;

	public OllivandersPlayerListener(Ollivanders plugin) {
		p = plugin;
	}

	/**
	 * Fires on player move
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event){
		protegoTotalum(event);
	}

	/**
	 * Doesn't let players cross a protego totalum
	 * @param event
	 */
	private void protegoTotalum(PlayerMoveEvent event){
		Location toLoc = event.getTo();
		Location fromLoc = event.getFrom();
		for (StationarySpellObj spell : p.getStationary()){
			if (spell instanceof StationarySpell.PROTEGO_TOTALUM) {
				int radius = spell.radius;
				Location spellLoc = spell.location.toLocation();
				if (((fromLoc.distance(spellLoc) < radius-0.5 && toLoc.distance(spellLoc) > radius-0.5)
						|| (toLoc.distance(spellLoc) < radius+0.5 && fromLoc.distance(spellLoc) > radius+0.5)) && spell.active) {
					event.setCancelled(true);
					spell.flair(10);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		//Begin code for chat falloff
		Player sender = event.getPlayer();
		if (!p.inWorld(sender.getWorld())){
			return;
		}
		List<OEffect> effects = p.getOPlayer(sender).getEffects();
		event.setCancelled(true);
		if (effects != null){
			for (OEffect effect : effects){
				if (effect.name == Effects.SILENCIO){
					return;
				}
			}
		}
		Set<Player> recipients = event.getRecipients();
		String message = event.getMessage();
		System.out.println("<" + sender.getPlayerListName() + "> " + message);
		String[] messageWords = message.split(" ");
		String message2 = new String(message);
		if (messageWords[0].equalsIgnoreCase("Apparate")){
			message = messageWords[0];
		}
		int rec = 0;
		double chatDistance = (double)this.p.getChatDistance();
		if (chatDistance == -5)
			return;
		List<StationarySpellObj> stationaries = p.checkForStationary(sender.getLocation());
		List<StationarySpellObj> muffliatos = new ArrayList<StationarySpellObj>();
		for (StationarySpellObj stationary : stationaries){
			if (stationary.name.equals(StationarySpells.MUFFLIATO) && stationary.active){
				muffliatos.add(stationary);
			}
		}
		for (Player recipient : recipients){
			if (recipient.getLocation().getWorld() == sender.getLocation().getWorld()) {
				double distance = recipient.getLocation().distance(
						sender.getLocation());
				if (muffliatos.size() > 0){
					boolean send = false;
					ArrayList<StationarySpellObj> recMuffliatos = new ArrayList<StationarySpellObj>();
					for (StationarySpellObj muffliato : p.checkForStationary(recipient.getLocation())){
						if (muffliato.name.equals(StationarySpells.MUFFLIATO)){
							recMuffliatos.add(muffliato);
						}
					}
					for (StationarySpellObj recMuffliato : recMuffliatos){
						if (muffliatos.contains(recMuffliato)){
							send = true;
						}
					}
					if (send){
						recipient.sendMessage(ChatColor.GREEN
								+ sender.getPlayerListName() + ": " + message);
						rec ++;
					}
				}
				else if (distance <= chatDistance/2) {
					recipient.sendMessage(ChatColor.GREEN
							+ sender.getPlayerListName() + ": " + message);
					rec ++;
				}
				else if (distance > chatDistance/2 && distance <= chatDistance) {
					int messageLength = message.length();
					double percent = (distance - 25) / 25;
					int amountRemoved = (int) (percent * ((double) messageLength));
					char[] charString = message.toCharArray();
					for (int k = 0; k < amountRemoved; k++) {
						int removalPoint = (int) (Math.random() * (charString.length - 1));
						charString[removalPoint] = ' ';
					}
					String charMessage = new String(charString);
					recipient.sendMessage(ChatColor.GREEN
							+ sender.getPlayerListName() + ": " + charMessage);
					rec ++;
				}
			}
		}
		System.out.println("Message received by " + rec + " players.");
		//End code for chat falloff

		//Begin code for spell parsing
		if (holdsWand(sender)){
			String[] words = message2.split(" ");
			//If it was apparate, then this
			if (words[0].equalsIgnoreCase("Apparate")){
				apparate(sender, words);
			}
			//If it wasn't apparate, then this
			else{
				Spells spell;
				System.out.println("Decoding spell");
				spell = Spells.decode(message);
				if (spell!=null){
					//If the spell is valid, run this code
					System.out.println("Spell is " + spell.toString());
					Map<String, OPlayer> opmap = p.getOPlayerMap();
					OPlayer oplayer = opmap.get(sender.getDisplayName());
					oplayer.setSpell(spell);
					opmap.put(sender.getDisplayName(), oplayer);
					p.setOPlayerMap(opmap);
				}
			}
		}
		//End code for spell parsing
	}

	/**
	 * Apparates sender to either specified location or to eye target location. Respects anti-apparition and anti-disapparition spells.
	 * @param sender - Player apparating
	 * @param words - Typed in words
	 */
	private void apparate(Player sender, String[] words){
		boolean canApparateOut = true;
		for (StationarySpellObj stat : p.getStationary()){
			if (stat instanceof StationarySpell.NULLUM_EVANESCUNT && stat.isInside(sender.getLocation()) && stat.active){
				stat.flair(10);
				canApparateOut = false;
			}
		}
		if (canApparateOut){
			int uses = p.incSpellCount(sender, Spells.APPARATE);
			Location from = sender.getLocation().clone();
			Location to;
			if (words.length == 4){
				try {
					to = new Location(sender.getWorld(),
							Double.parseDouble(words[1]),
							Double.parseDouble(words[2]),
							Double.parseDouble(words[3]));
				} catch (NumberFormatException e) {
					to = sender.getLocation().clone();
				}
			}
			else{
				Location eyeLocation = sender.getEyeLocation();
				Material inMat = eyeLocation.getBlock().getType();
				int distance = 0;
				while ((inMat == Material.AIR || inMat == Material.FIRE || inMat == Material.WATER || inMat == Material.STATIONARY_WATER || inMat == Material.LAVA || inMat == Material.STATIONARY_LAVA) && distance < 160){
					eyeLocation = eyeLocation.add(eyeLocation.getDirection());
					distance ++;
					inMat = eyeLocation.getBlock().getType();
				}
				to = eyeLocation.subtract(eyeLocation.getDirection()).clone();
			}
			to.setPitch(from.getPitch());
			to.setYaw(from.getYaw());
			Double distance = from.distance(to);
			Double radius = 1/Math.sqrt(uses)*distance*0.1*wandCheck(sender);
			Double newX = to.getX()-(radius/2)+(radius * Math.random());
			Double newZ = to.getZ()-(radius/2)+(radius * Math.random());
			to.setX(newX);
			to.setZ(newZ);
			boolean canApparateIn = true;
			for (StationarySpellObj stat : p.getStationary()){
				if (stat instanceof StationarySpell.NULLUM_APPAREBIT && stat.isInside(to) && stat.active){
					stat.flair(10);
					canApparateIn = false;
				}
			}
			if (canApparateIn) {
				sender.teleport(to);
				for (Entity e : sender.getWorld().getEntities()) {
					if (from.distance(e.getLocation()) <= 2) {
						e.teleport(to);
					}
				}
			}
		}
	}

	/**
	 * Monitors chat events for the cat-post keywords and enacts the cat-post system
	 * @param event Chat event of type AsyncPlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void catPost(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		Server server = sender.getServer();
		World world = sender.getWorld();
		String message = event.getMessage();
		String[] splited = message.split("\\s+", 3);
		if (splited.length == 3){
			if (splited[0].equalsIgnoreCase("deliver") && splited[1].equalsIgnoreCase("to")){
				for (Entity ocelot : world.getEntities()){
					if (ocelot instanceof Ocelot && ocelot.getLocation().distance(sender.getLocation()) <= 10){
						Ocelot cat = (Ocelot)ocelot;
						if (cat.isTamed()){
							for (Entity item : world.getEntities()){
								if (item instanceof Item && item.getLocation().distance(cat.getLocation()) <= 2){
									OfflinePlayer off = server.getOfflinePlayer(splited[2]);
									if (off.isOnline()){
										Player recipient = off.getPlayer();
										if (recipient.getWorld().getName().equals(world.getName())){
											world.playSound(cat.getLocation(),Sound.CAT_MEOW,1, 0);
											cat.teleport(recipient.getLocation());
											item.teleport(recipient.getLocation());
											world.playSound(cat.getLocation(),Sound.CAT_MEOW,1, 0);
										}
										else{
											world.playSound(cat.getLocation(),Sound.CAT_HISS,1, 0);
										}
									}
									else{
										world.playSound(cat.getLocation(),Sound.CAT_HISS,1, 0);
									}
									return;
								}
							}
						}
					}
				}
			}
		}
	}


	/**
	 * This creates the spell projectile.
	 */
	private void createSpellProjectile(Player player, Spells name, double wandC){
		//spells go here, using any of the three types of m
		String spellClass = "Spell." + name.toString();
		@SuppressWarnings("rawtypes")
		Constructor c = null;
		try {
			//Maybe you have to use Integer.TYPE here instead of Integer.class
			c = Class.forName(spellClass).getConstructor(Ollivanders.class, Player.class, Spells.class, Double.class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			p.addProjectile((SpellProjectile) c.newInstance(p, player, name, wandC));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		//Casting an effect
		if (holdsWand(event.getPlayer()) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)){
			Map<String, OPlayer> opmap = p.getOPlayerMap();
			OPlayer oplayer = p.getOPlayer(event.getPlayer());
			Spells spell = oplayer.getSpell();
			if (spell!=null){
				double wandC = wandCheck(event.getPlayer());
				allyWand(event.getPlayer());
				createSpellProjectile(event.getPlayer(), spell, wandC);
				int spellc = p.getSpellNum(event.getPlayer(), spell);
				System.out.println(spellc);
				if (spellc < 100 || spell == Spells.AVADA_KEDAVRA){
					oplayer.setSpell(null);
					opmap.put(event.getPlayer().getDisplayName(), oplayer);
					p.setOPlayerMap(opmap);
				}
			}
		}
		//See if it is the right wand and play an effect
		if (holdsWand(event.getPlayer()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
			if (wandCheck(event.getPlayer()) == 1){
				Location location = event.getPlayer().getLocation();
				location.setY(location.getY() + 1.6);
				Map<String, OPlayer> opmap = p.getOPlayerMap();
				OPlayer oplayer = opmap.get(event.getPlayer().getDisplayName());
				Spells spell = oplayer.getSpell();
				if (spell == null){
					event.getPlayer().getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
					event.getPlayer().getWorld().playSound(location, Sound.LEVEL_UP, 1, 1);
				}
			}
			//Toggle between known spells
			Spells[] spells = Spells.values();
			List<Spells> knownSpells = new ArrayList<Spells>();
			for (Spells spell : spells){
				if (p.getSpellNum(event.getPlayer(), spell) >= 100){
					//if (spell != Spells.AVADA_KEDAVRA && spell != Spells.CRUCIO && spell != Spells.IMPERIO){
					if (spell != Spells.AVADA_KEDAVRA){
						knownSpells.add(spell);
					}
				}
			}
			if (knownSpells.size() > 0){
				OPlayer oplayer = p.getOPlayer(event.getPlayer());
				Spells spell = oplayer.getSpell();
				int ind = knownSpells.indexOf(spell);
				if (ind == -1){
					oplayer.setSpell(knownSpells.get(0));
				}
				else{
					oplayer.setSpell(knownSpells.get((ind + 1)%knownSpells.size()));
				}
				p.setOPlayer(event.getPlayer(), oplayer);
				event.getPlayer().sendMessage(Spells.recode(oplayer.getSpell()));
			}
		}
		//Reading a book, possibly a spell book
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getItem() != null)){
			if (event.getItem().getType() == Material.WRITTEN_BOOK){
				ItemStack item = event.getItem();
				ItemMeta imeta = item.getItemMeta();
				SpellBookParser.decode(p, event.getPlayer(), imeta);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerLoginEvent event){
		Map<String, OPlayer> map = p.getOPlayerMap();
		if (!map.containsKey(event.getPlayer().getDisplayName())){
			map.put(event.getPlayer().getDisplayName(), new OPlayer());
			System.out.println("Put in new OPlayer.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		Map<String, OPlayer> map = p.getOPlayerMap();
		OPlayer oply = map.get(event.getEntity().getDisplayName());
		oply.resetSpellCount();
		oply.setSpell(null);
		oply.resetSouls();
		oply.resetEffects();
		map.put(event.getEntity().getDisplayName(), oply);
		p.setOPlayerMap(map);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent event){
		if (checkSpongify(event)){
			return;
		}
		//Horcrux code
		List<StationarySpellObj> stationarys = p.getStationary();
		if (event.getEntity() instanceof Player){
			Damageable plyr = (Damageable) event.getEntity();
			String name = ((Player)event.getEntity()).getDisplayName();
			double damage = event.getDamage()*(p.getOPlayer(p.getServer().getPlayer(name)).getSouls());
			event.setDamage(damage);
			if (((double)plyr.getHealth()-damage)<= 0){
				for(StationarySpellObj stationary : stationarys){
					if (stationary.name == StationarySpells.HORCRUX && stationary.player.equals(name)){
						Location tp = stationary.location.toLocation();
						tp.setY(tp.getY()+1);
						plyr.teleport(tp);
						p.getOPlayer((Player) plyr).resetEffects();
						event.setCancelled(true);
						plyr.setHealth(20.0);
						p.remStationary(stationary);
						return;
					}
				}
			}
		}
	}

	/**
	 * Checks to see if the entity was within a spongify stationary spell object. If so, cancells the damage event
	 * @param event - The EntityDamageEvent
	 * @return - True if the entity was within spongify
	 */
	private boolean checkSpongify(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		for (StationarySpellObj spell : p.getStationary()){
			if (spell instanceof SPONGIFY && event.getCause() == DamageCause.FALL){
				if (spell.isInside(entity.getLocation()) && spell.active){
					event.setCancelled(true);
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBook(PlayerEditBookEvent event){
		if (event.isSigning()){
			event.setNewBookMeta(SpellBookParser.encode(p, event.getPlayer(), event.getNewBookMeta()));
		}
	}

	/**Does the player hold a wand item?
	 * @param player - Player to check.
	 * @return True if the player holds a wand. False if not.
	 */
	public boolean holdsWand(Player player){
		if (player.getItemInHand() != null){
			ItemStack held = player.getItemInHand();
			if (held.getType() == Material.STICK || held.getType() == Material.BLAZE_ROD){
				if (held.getItemMeta().hasLore()){
					List<String> lore = held.getItemMeta().getLore();
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

	/**
	 * Checks what kind of wand a player holds. Returns a value based on the
	 * wand and it's relation to the player.
	 * @param player - Player being checked.
	 * @return 2 - The wand is not your type AND/OR is not allied to you.<p>
	 * 1 - The wand is your type and is allied to you OR the wand is the elder wand and is not allied to you.<p>
	 * 0.5 - The wand is the elder wand and it is allied to you.
	 */
	public double wandCheck(Player player){
		String charList = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
		int wood = player.getDisplayName().length()%4;
		String[] woodArray = {"Spruce","Jungle","Birch","Oak"};
		String woodString = woodArray[wood];
		int core = 0;
		for (char a : player.getDisplayName().toCharArray()){
			core += charList.indexOf(a)+1;
		}
		String[] coreArray = {"Spider Eye","Bone","Rotten Flesh","Gunpowder"};
		String coreString = coreArray[core%4];
		List<String> lore = player.getItemInHand().getItemMeta().getLore();
		if (lore.get(0).equals("Blaze and Ender Pearl")){
			if (lore.size() == 2){
				if (lore.get(1).equals(player.getDisplayName())){
					return 0.5;
				}
				else{
					return 1;
				}
			}
			else{
				return 0.5;
			}
		}
		String[] comps = lore.get(0).split(" and ");
		if (woodString.equals(comps[0]) && coreString.equals(comps[1])){
			if (lore.size() == 2){
				if (lore.get(1).equals(player.getDisplayName())){
					return 1;
				}
				else{
					return 2;
				}
			}
			else{
				return 1;
			}
		}
		else{
			return 2;
		}
	}

	/**
	 * If a block is broken that is temporary, prevent it from dropping anything.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		List<Block> tempBlocks = p.getTempBlocks();
		if (tempBlocks.contains(block)){
			event.setCancelled(true);
			tempBlocks.remove(block);
			block.setType(Material.AIR);
		}
	}

	/**If a block is a tempBlock, then don't blow it up.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosion(EntityExplodeEvent event){
		List<Block> blockListCopy = new ArrayList<Block>();
		blockListCopy.addAll(event.blockList());
		List<Block> tempBlocks = p.getTempBlocks();
		for (Block block : blockListCopy) {
			if (tempBlocks.contains(block)){
				event.blockList().remove(block);
			}
		}
	}

	/**If a wand is not already allied with a player, this allies it.
	 * @param player - Player holding a wand.
	 */
	public void allyWand(Player player){
		ItemStack wand = player.getItemInHand();
		ItemMeta wandMeta = wand.getItemMeta();
		List<String> wandLore = wandMeta.getLore();
		if (wandLore.size() == 1){
			wandLore.add(player.getDisplayName());
			wandMeta.setLore(wandLore);
			wand.setItemMeta(wandMeta);
			player.setItemInHand(wand);
		}
		else{
			return;
		}
	}
}