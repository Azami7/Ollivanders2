package me.cakenggt.Ollivanders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Moving Spell Projectile
 * @author lownes
 *
 */
public class SpellProjectile{

	public Player player;
	public Spells name;
	public Location location;
	public Vector vector;
	public int lifeTicks;
	public boolean kill;
	public Ollivanders p;
	public double rightWand;
	public int spellUses;
	public double usesModifier;
	public Effect moveEffect = Effect.STEP_SOUND;
	@SuppressWarnings("deprecation")
	public int moveEffectData = Material.SPONGE.getId();


	//Constructor
	//In the chat distance dropoff code, there will be code that turns the spoken
	//words into a Spells object.
	public SpellProjectile(Ollivanders plugin, Player player, Spells name, Double rightWand){
		location = player.getEyeLocation();
		vector = location.getDirection().normalize();
		location.add(vector);
		this.name = name;
		this.player = player;
		kill = false;
		lifeTicks = 0;
		p = plugin;
		this.rightWand = rightWand;
		spellUses = p.getSpellNum(player, name);
		usesModifier = getusesModifier();
		if (p.getOPlayer(player).getSpellCount().containsKey(name)){
			p.incSpellCount(player, name);
		}
		else{
			Map<Spells, Integer> spellCount = p.getOPlayer(player).getSpellCount();
			spellCount.put(name, 0);
			p.getOPlayer(player).setSpellCount(spellCount);
		}
	}

	/**
	 * Moves the projectile forward, creating a particle effect
	 */
	public void move(){
		location.add(vector);
		location.getWorld().playEffect(location, moveEffect, moveEffectData);
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER && getBlock().getType() != Material.STATIONARY_LAVA && getBlock().getType() != Material.LAVA){
			kill = true;
		}
		lifeTicks ++;
		if (lifeTicks > 160){
			kill = true;
		}
	}

	/**
	 * This kills the projectile.
	 */
	public void kill(){
		kill = true;
	}

	/**
	 * Gets the block the projectile is inside
	 * @return Block the projectile is inside
	 */
	public Block getBlock(){
		return location.getBlock();
	}

	/**
	 * Gets entities within one block of projectile
	 * @param radius - radius within which to get entities
	 * @return List of entities within one block of projectile
	 */
	public List<Entity> getCloseEntities(double radius){
		List<Entity> entities = location.getWorld().getEntities();
		List<Entity> close = new ArrayList<Entity>();
		for (Entity e : entities){
			if (e instanceof LivingEntity){
				if (((LivingEntity) e).getEyeLocation().distance(location) < 1){
					if (!e.equals(player)){
						close.add(e);
					}
					else{
						if (lifeTicks > 1){
							//System.out.println(((LivingEntity) e).getEyeLocation().distance(location));
							close.add(e);
						}
					}
				}
			}
			else{
				if (e.getLocation().distance(location) < 1){
					close.add(e);
				}
			}
		}
		return close;
	}

	/**
	 * Gets item entities within one block of the projectile
	 * @param radius - radius within which to get entities
	 * @return List of item entities within one block of projectile
	 */
	public List<Item> getItems(double radius){
		List<Entity> entities = getCloseEntities(radius);
		List<Item> items = new ArrayList<Item>();
		for (Entity e : entities){
			if (e instanceof Item){
				items.add((Item) e);
			}
		}
		return items;
	}

	/**
	 * Gets all LivingEntity within one block of projectile
	 * @param radius - radius within which to get entities
	 * @return List of LivingEntity within one block of projectile
	 */
	public List<LivingEntity> getLivingEntities(double radius){
		List<Entity> entities = getCloseEntities(radius);
		List<LivingEntity> living = new ArrayList<LivingEntity>();
		for (Entity e : entities){
			if (e instanceof LivingEntity){
				living.add((LivingEntity) e);
			}
		}
		if (lifeTicks == 1 && player.getEyeLocation().getPitch() > 80){
			living.add(player);
		}
		return living;
	}

	/**
	 * This clones a SpellProjectile
	 * @return Cloned SpellProjectile
	 */
	public SpellProjectile copy(){
		SpellProjectile sp = new SpellProjectile(p, player, name, rightWand);
		sp.location = this.location.clone();
		sp.vector = this.vector.clone();
		sp.lifeTicks = this.lifeTicks;
		sp.kill = this.kill;
		return sp;
	}

	/**
	 * Provides the uses modifier that takes into account spell uses and wand type. Returns 10.0 if the uses are 100 and the right wand is held.
	 * @return Uses modifier
	 */
	private double getusesModifier(){
		double modifier = Math.sqrt(p.getSpellNum(player, name))/rightWand;
		return modifier;
	}

	/**
	 * Gets the blocks in a radius of a location.
	 * @param loc - The Location that is the center of the block list
	 * @param radius - The radius of the block list
	 * @return List of blocks that are within radius of the location.
	 */
	public List<Block> getBlocksInRadius(Location loc, double radius){
		Block center = loc.getBlock();
		int blockRadius = (int)(radius+1);
		List<Block> blockList = new ArrayList<Block>();
		for (int x = -blockRadius; x <= blockRadius; x++){
			for (int y = -blockRadius; y <= blockRadius; y++){
				for (int z = -blockRadius; z <= blockRadius; z++){
					blockList.add(center.getRelative(x, y, z));
				}
			}
		}
		ArrayList<Block> returnList = new ArrayList<Block>();
		for (Block block : blockList){
			if (block.getLocation().distance(center.getLocation()) < radius){
				returnList.add(block);
			}
		}
		return returnList;
	}
}

		//	//for each metadatable, it will check what the effect
		//	//will be given the spell's name
		//	public void spellEffect(Metadatable m){
		//		int spellUses = p.getOPlayer(player).getSpellCount().get(name);
		//		//spells go here, using any of the three types of m
		//		String spellClass = "Spell." + name.toString();
		//		@SuppressWarnings("rawtypes")
		//		Constructor c = null;
		//		try {
		//			//Maybe you have to use Integer.TYPE here instead of Integer.class
		//			c = Class.forName(spellClass).getConstructor(Player.class, Integer.class, Integer.class);
		//		} catch (SecurityException e) {
		//			e.printStackTrace();
		//		} catch (NoSuchMethodException e) {
		//			e.printStackTrace();
		//		} catch (ClassNotFoundException e) {
		//			e.printStackTrace();
		//		}
		//		try {
		//			c.newInstance(player, m, spellUses, rightWand);
		//		} catch (IllegalArgumentException e) {
		//			e.printStackTrace();
		//		} catch (InstantiationException e) {
		//			e.printStackTrace();
		//		} catch (IllegalAccessException e) {
		//			e.printStackTrace();
		//		} catch (InvocationTargetException e) {
		//			e.printStackTrace();
		//		}
		//		if (name.equals(Spells.BOMBARDA)){
		//			if (block != null){
		//				block.getLocation().getWorld().createExplosion(block.getLocation(), (float)(Math.sqrt(spellUses)*0.4/rightWand));
		//			}
		//		}
		//		if (name.equals(Spells.FRANGE_LIGNEA)){
		//			if (block != null){
		//				if (block.getType() == Material.LOG){
		//					block.getLocation().getWorld().createExplosion(block.getLocation(), 0);
		//					int data = block.getData()%4;
		//					String[] woodTypes = {"Oak","Spruce","Birch","Jungle"};
		//					int number = (int)(Math.sqrt(spellUses)*0.4/rightWand);
		//					if (number > 0){
		//						ItemStack shellStack = new ItemStack(Material.STICK, number);
		//						ItemMeta shellM = shellStack.getItemMeta();
		//						shellM.setDisplayName("Coreless Wand");
		//						List<String> lore = new ArrayList<String>();
		//						lore.add(woodTypes[data]);
		//						shellM.setLore(lore);
		//						shellStack.setItemMeta(shellM);
		//						player.getWorld().dropItemNaturally(block.getLocation(), shellStack);
		//					}
		//					block.setType(Material.AIR);
		//				}
		//			}
		//		}
		//		if (name.equals(Spells.LIGATIS_COR)){
		//			if (item != null){
		//				ItemMeta corM = item.getItemStack().getItemMeta();
		//				String[] woodTypes = {"Oak","Spruce","Birch","Jungle"};
		//				if (corM.hasLore()){
		//					if (Arrays.asList(woodTypes).contains(corM.getLore().get(0))){
		//						List<Entity> entities = item.getNearbyEntities(2, 2, 2);
		//						for (Entity e : entities){
		//							if (e instanceof Item){
		//								Item e2 = (Item)e;
		//								Material mat = e2.getItemStack().getType();
		//								Material[] cores = {Material.SPIDER_EYE,Material.ROTTEN_FLESH,Material.BONE,Material.SULPHUR};
		//								if (Arrays.asList(cores).contains(mat)){
		//									Map<Material, String> matMap = new HashMap<Material, String>();
		//									matMap.put(Material.SPIDER_EYE, "Spider Eye");
		//									matMap.put(Material.ROTTEN_FLESH, "Rotten Flesh");
		//									matMap.put(Material.BONE, "Bone");
		//									matMap.put(Material.SULPHUR, "Gunpowder");
		//									String lore = corM.getLore().get(0);
		//									lore = lore.concat(" and ");
		//									lore = lore.concat(matMap.get(mat));
		//									System.out.println(lore);
		//									corM.setDisplayName("Wand");
		//									List<String> loreL = new ArrayList<String>();
		//									loreL.add(lore);
		//									corM.setLore(loreL);
		//									ItemStack coreStack = item.getItemStack();
		//									coreStack.setAmount(1);
		//									coreStack.setItemMeta(corM);
		//									item.setItemStack(coreStack);
		//									e2.remove();
		//								}
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}
		//	}