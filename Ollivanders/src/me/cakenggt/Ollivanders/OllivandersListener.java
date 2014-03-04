package me.cakenggt.Ollivanders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import Effect.LYCANTHROPY;
import Spell.FORSKNING;
import Spell.PORTUS;
import StationarySpell.COLLOPORTUS;
import StationarySpell.REPELLO_MUGGLETON;
import StationarySpell.SPONGIFY;

/**
 * Listener for events from the plugin
 * @author lownes
 *
 */
public class OllivandersListener implements Listener {

	Ollivanders p;

	public OllivandersListener(Ollivanders plugin) {
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
			if (spell instanceof StationarySpell.PROTEGO_TOTALUM && 
					toLoc.getWorld().getName().equals(spell.location.getWorld()) &&
					fromLoc.getWorld().getName().equals(spell.location.getWorld())) {
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

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		//Begin code for chat falloff
		Player sender = event.getPlayer();
		String message = event.getMessage();
		List<OEffect> effects = p.getOPlayer(sender).getEffects();
		if (effects != null){
			for (OEffect effect : effects){
				if (effect.name == Effects.SILENCIO){
					event.getRecipients().clear();
					return;
				}
			}
		}
		for (SpellProjectile proj : p.getProjectiles()){
			if (proj instanceof FORSKNING && proj.location.getWorld().getName().equals(sender.getWorld().getName())){
				if (proj.location.distance(sender.getLocation()) <= 10){
					((FORSKNING)proj).research(message);
				}
			}
		}
		Set<Player> recipients = event.getRecipients();
		String[] messageWords = message.split(" ");
		String message2 = new String(message);
		Spells spell;
		//getLogger().info("Decoding spell");
		spell = Spells.decode(message);
		if (messageWords[0].equalsIgnoreCase("Apparate")){
			event.setMessage(messageWords[0]);
			spell = Spells.APPARATE;
		}
		else if (messageWords[0].equalsIgnoreCase("Portus")){
			event.setMessage(messageWords[0]);
			spell = Spells.PORTUS;
		}
		if (spell != null){
			if (!p.canCast(sender, spell, true)){
				spell = null;
			}
		}
		double chatDistance = (double)p.getChatDistance();
		List<StationarySpellObj> stationaries = p.checkForStationary(sender.getLocation());
		Set<StationarySpellObj> muffliatos = new HashSet<StationarySpellObj>();
		for (StationarySpellObj stationary : stationaries){
			if (stationary.name.equals(StationarySpells.MUFFLIATO) && stationary.active){
				muffliatos.add(stationary);
			}
		}
		Set<Player> remRecipients = new HashSet<Player>();
		for (Player recipient : recipients){
			double distance;
			try {
				distance = recipient.getLocation().distance(sender.getLocation());
			} catch (IllegalArgumentException e) {
				distance = -1;
			}
			if (spell != null){
				if (distance > chatDistance || distance == -1){
					remRecipients.add(recipient);
				}
			}
			if (muffliatos.size() > 0){
				for (StationarySpellObj muffliato : muffliatos){
					Location recLoc = recipient.getLocation();
					if (!muffliato.isInside(recLoc)){
						remRecipients.add(recipient);
					}
				}
			}
		}
		for (Player remRec : remRecipients){
			try {
				recipients.remove(remRec);
			} catch (UnsupportedOperationException e) {
				p.getLogger().warning("Chat was unable to be removed due "
						+ "to a unmodifiable set.");
			}
		}
		//End code for chat falloff

		//Begin code for spell parsing
		if (spell != null){
			boolean castSuccess;
			if (holdsWand(sender)){
				castSuccess = true;
			}
			else{
				int uses = p.getOPlayer(sender).getSpellCount().get(spell);
				castSuccess = Math.random() < (1.0-(100.0/(uses+101.0)));
			}
			if (castSuccess){
				String[] words = message2.split(" ");
				//If it was apparate, then this
				if (spell == Spells.APPARATE){
					apparate(sender, words);
					spell = null;
				}
				//If it was portus, then this
				else if (spell == Spells.PORTUS){
					p.addProjectile(new PORTUS(p, sender, Spells.PORTUS, 1.0, words));
					spell = null;
				}
				//If it wasn't apparate or portus, then this
				else{
					if (spell!=null){
						//If the spell is valid, run this code
						Map<String, OPlayer> opmap = p.getOPlayerMap();
						OPlayer oplayer = opmap.get(sender.getName());
						oplayer.setSpell(spell);
						opmap.put(sender.getName(), oplayer);
						p.setOPlayerMap(opmap);
					}
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
			Double radius;
			if (holdsWand(sender)){
				radius = 1/Math.sqrt(uses)*distance*0.1*wandCheck(sender);
			}
			else{
				radius = 1/Math.sqrt(uses)*distance*0.01;
			}
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
				sender.getWorld().createExplosion(sender.getLocation(), 0);
				sender.teleport(to);
				sender.getWorld().createExplosion(sender.getLocation(), 0);
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
									Player recipient = server.getPlayer(splited[2]);
									if (recipient != null){
										if (recipient.isOnline()){
											if (recipient.getWorld().getName().equals(world.getName())){
												world.playSound(cat.getLocation(),Sound.CAT_MEOW,1, 0);
												cat.teleport(recipient.getLocation());
												item.teleport(recipient.getLocation());
												world.playSound(cat.getLocation(),Sound.CAT_MEOW,1, 0);
											}
											else{
												world.playSound(cat.getLocation(),Sound.CAT_HISS,1, 0);
												sender.sendMessage(splited[2] + " is not in this world.");
											}
										}
										else{
											world.playSound(cat.getLocation(),Sound.CAT_HISS,1, 0);
											sender.sendMessage(splited[2] + " is not online.");
										}
									}
									else{
										world.playSound(cat.getLocation(),Sound.CAT_HISS,1, 0);
										sender.sendMessage(splited[2] + " is not online.");
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
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
			Map<String, OPlayer> opmap = p.getOPlayerMap();
			OPlayer oplayer = p.getOPlayer(event.getPlayer());
			Spells spell = oplayer.getSpell();
			if (spell!=null){
				double wandC;
				if (holdsWand(event.getPlayer())){
					wandC = wandCheck(event.getPlayer());
					allyWand(event.getPlayer());
				}
				else{
					wandC = 10;
				}
				createSpellProjectile(event.getPlayer(), spell, wandC);
				int spellc = p.getSpellNum(event.getPlayer(), spell);
				if (spellc < 100 || spell == Spells.AVADA_KEDAVRA || !holdsWand(event.getPlayer())){
					oplayer.setSpell(null);
					opmap.put(event.getPlayer().getName(), oplayer);
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
				OPlayer oplayer = opmap.get(event.getPlayer().getName());
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
					int offset = 1;
					if (event.getPlayer().isSneaking()){
						offset = -1;
					}
					oplayer.setSpell(knownSpells.get((ind + offset)%knownSpells.size()));
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
				BookMeta bookM = (BookMeta)imeta;
				if (bookM.getAuthor().equals("cakenggt")){
					for (ItemStack madeBook : SpellBookParser.makeBooks(1)){
						if (((BookMeta)madeBook.getItemMeta()).getTitle().equals(bookM.getTitle())){
							if (item.getAmount() != 1){
								madeBook.setAmount(item.getAmount());
							}
							event.getPlayer().setItemInHand(madeBook);
							imeta = madeBook.getItemMeta();
							break;
						}
					}
				}
				SpellBookParser.decode(p, event.getPlayer(), imeta);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerLoginEvent event){
		Map<String, OPlayer> map = p.getOPlayerMap();
		if (!map.containsKey(event.getPlayer().getName())){
			map.put(event.getPlayer().getName(), new OPlayer());
			p.getLogger().info("Put in new OPlayer.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		Map<String, OPlayer> map = p.getOPlayerMap();
		OPlayer oply = map.get(event.getEntity().getName());
		oply.resetSpellCount();
		oply.setSpell(null);
		oply.resetSouls();
		oply.resetEffects();
		map.put(event.getEntity().getName(), oply);
		p.setOPlayerMap(map);
		event.getEntity().setMaxHealth(20.0);
	}

	/**This checks if a player kills another player, and if so, adds a soul
	 * to the attacking player's oplayer
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event){
		if (event.getEntity() instanceof Player){
			Player damaged = (Player) event.getEntity();
			if (event.getDamager() instanceof Player){
				Player attacker = (Player) event.getDamager();
				if (((Damageable)damaged).getHealth() - event.getDamage() <= 0){
					p.getOPlayer(attacker).addSoul();
				}
			}
			if (event.getDamager() instanceof Wolf){
				Wolf wolf = (Wolf) event.getDamager();
				if (wolf.isAngry()){
					boolean hasLy = false;
					OPlayer oply = p.getOPlayer(damaged);
					for (OEffect effect : oply.getEffects()){
						if (effect.name == Effects.LYCANTHROPY){
							hasLy = true;
						}
					}
					if (!hasLy){
						oply.addEffect(new LYCANTHROPY(damaged, Effects.LYCANTHROPY, 100));
					}
				}
			}
		}
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
			String name = ((Player)event.getEntity()).getName();
			if (((double)plyr.getHealth()-event.getDamage())<= 0){
				for(StationarySpellObj stationary : stationarys){
					if (stationary.name == StationarySpells.HORCRUX && stationary.player.equals(name)){
						Location tp = stationary.location.toLocation();
						tp.setY(tp.getY()+1);
						plyr.teleport(tp);
						p.getOPlayer((Player) plyr).resetEffects();
						event.setCancelled(true);
						plyr.setHealth(plyr.getMaxHealth());
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

	/**If a player is signing a book, try to encode any spells in the book.
	 * @param event - PlayerEditBookEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBook(PlayerEditBookEvent event){
		if (event.isSigning()){
			event.setNewBookMeta(SpellBookParser.encode(p, event.getPlayer(), event.getNewBookMeta()));
		}
	}

	/**Cancels any block place event inside of a colloportus object
	 * @param event - BlockEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloBlockPlaceEvent(BlockPlaceEvent event){
		if (p.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation())){
			event.getBlock().breakNaturally();
			return;
		}
	}	

	/**Cancels any block break event inside of a colloportus object
	 * @param event - BlockEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloBlockBreakEvent(BlockBreakEvent event){
		if (p.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation())){
			event.setCancelled(true);
			return;
		}
	}

	/**Cancels any block physics event inside of a colloportus object
	 * @param event - BlockEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloBlockPhysicsEvent(BlockPhysicsEvent event){
		if (p.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation())){
			event.setCancelled(true);
			return;
		}
	}

	/**Cancels any block interact event inside a colloportus object
	 * @param event - PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloPlayerInteract(PlayerInteractEvent event){
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || 
				event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (p.isInsideOf(StationarySpells.COLLOPORTUS, event.getClickedBlock().getLocation())){
				event.setCancelled(true);
				return;
			}
		}
	}

	/**Cancels any piston extend event inside a colloportus
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloPistonExtend(BlockPistonExtendEvent event){
		ArrayList<COLLOPORTUS> collos = new ArrayList<COLLOPORTUS>();
		for (StationarySpellObj stat : p.getStationary()){
			if (stat instanceof COLLOPORTUS){
				collos.add((COLLOPORTUS) stat);
			}
		}
		List<Block> blocks = event.getBlocks();
		BlockFace direction = event.getDirection();
		for (Block block : blocks){
			Block newBlock = block.getRelative(direction.getModX(), direction.getModY(), direction.getModZ());
			for (COLLOPORTUS collo : collos){
				if (collo.isInside(newBlock.getLocation()) || collo.isInside(block.getLocation())){
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	/**Cancels any piston retract event inside of a colloportus
	 * @param event - BlockPistonRetractEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloPistonRetract(BlockPistonRetractEvent event){
		if (event.isSticky()){
			if (p.isInsideOf(StationarySpells.COLLOPORTUS, event.getRetractLocation())){
				event.setCancelled(true);
				return;
			}
		}
	}

	/**Cancels any block change by an entity inside of a colloportus
	 * @param event - EntityChangeBlockEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onColloEntityChangeBlock(EntityChangeBlockEvent event){
		Location loc = event.getBlock().getLocation();
		Entity entity = event.getEntity();
		if (p.isInsideOf(StationarySpells.COLLOPORTUS, loc)){
			event.setCancelled(true);
			if (event.getEntityType() == EntityType.FALLING_BLOCK){
				loc.getWorld().dropItemNaturally(loc, new ItemStack(((FallingBlock)entity).getMaterial()));
			}
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
		int wood = player.getName().length()%4;
		String[] woodArray = {"Spruce","Jungle","Birch","Oak"};
		String woodString = woodArray[wood];
		int core = 0;
		for (char a : player.getName().toCharArray()){
			core += charList.indexOf(a)+1;
		}
		String[] coreArray = {"Spider Eye","Bone","Rotten Flesh","Gunpowder"};
		String coreString = coreArray[core%4];
		List<String> lore = player.getItemInHand().getItemMeta().getLore();
		if (lore.get(0).equals("Blaze and Ender Pearl")){
			if (lore.size() == 2){
				if (lore.get(1).equals(player.getName())){
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
				if (lore.get(1).equals(player.getName())){
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
	public void onTemporaryBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		List<Block> tempBlocks = p.getTempBlocks();
		if (tempBlocks.contains(block)){
			event.setCancelled(true);
			tempBlocks.remove(block);
			block.setType(Material.AIR);
		}
	}

	/**If a block is a tempBlock or is inside colloportus, then don't blow it up.
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
			for (StationarySpellObj stat : p.getStationary()){
				if (stat instanceof COLLOPORTUS){
					if (stat.isInside(block.getLocation())){
						event.blockList().remove(block);
					}
				}
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
			wandLore.add(player.getName());
			wandMeta.setLore(wandLore);
			wand.setItemMeta(wandMeta);
			player.setItemInHand(wand);
		}
		else{
			return;
		}
	}

	/**Prevents a transfigured entity from changing any blocks by exploding.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void transfiguredEntityExplodeCancel(EntityExplodeEvent event){
		if (event.getEntity() != null){
			for (SpellProjectile proj : p.getProjectiles()){
				if (proj instanceof Transfiguration){
					Transfiguration trans = (Transfiguration) proj;
					if (trans.getToID() == event.getEntity().getEntityId()){
						event.setCancelled(true);
					}
				}
			}
		}
	}

	/**When an item is picked up by a player, if the item is a portkey, the player will be teleported there.
	 * @param event - PlayerPickupItemEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void portkeyPickUp(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		Item item = event.getItem();
		ItemMeta meta = item.getItemStack().getItemMeta();
		List<String> lore;
		if (meta.hasLore()){
			lore = meta.getLore();
		}
		else{
			lore = new ArrayList<String>();
		}
		for (String s : lore){
			if (s.startsWith("Portkey")){
				String[] portArray = s.split(" ");
				Location to;
				to = new Location(Bukkit.getServer().getWorld(portArray[1]),
						Double.parseDouble(portArray[2]),
						Double.parseDouble(portArray[3]),
						Double.parseDouble(portArray[4]));
				to.setDirection(player.getLocation().getDirection());
				for (Entity e : player.getWorld().getEntities()) {
					if (player.getLocation().distance(e.getLocation()) <= 2) {
						e.teleport(to);
					}
				}
				player.teleport(to);
				lore.remove(lore.indexOf(s));
				meta.setLore(lore);
				item.getItemStack().setItemMeta(meta);
				return;
			}
		}
	}

	/**Cancels any targeting of players with the Cloak of Invisibility
	 * or inside of a REPELLO_MUGGLETON while the targeting entity is
	 * outside it.
	 * @param event - EntityTargetEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void cloakPlayer(EntityTargetEvent event){
		Entity target = event.getTarget();
		if (target instanceof Player){
			if (p.getOPlayer((Player)target).isInvisible()){
				event.setCancelled(true);
			}
		}
		if (target != null){
			for (StationarySpellObj stat : p.getStationary()){
				if (stat instanceof REPELLO_MUGGLETON){
					if (stat.isInside(target.getLocation())){
						if (!stat.isInside(event.getEntity().getLocation())){
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	/**This drops a random wand when a witch dies
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void witchWandDrop(EntityDeathEvent event){
		if (event.getEntityType() == EntityType.WITCH && p.getConfig().getBoolean("witchDrop")){
			int wandType = (int) (Math.random()*4);
			int coreType = (int) (Math.random()*4);
			String[] woodArray = {"Spruce","Jungle","Birch","Oak"};
			String[] coreArray = {"Spider Eye","Bone","Rotten Flesh","Gunpowder"};
			ItemStack wand = new ItemStack(Material.STICK);
			List<String> lore = new ArrayList<String>();
			lore.add(woodArray[wandType] + " and " + coreArray[coreType]);
			ItemMeta meta = wand.getItemMeta();
			meta.setLore(lore);
			meta.setDisplayName("Wand");
			wand.setItemMeta(meta);
			event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), wand);
		}
	}
}