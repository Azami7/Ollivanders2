package me.cakenggt.Ollivanders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Stationary spell object in Ollivanders
 * @author lownes
 *
 */
public class StationarySpellObj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9013964903309999847L;
	public String player;
	public StationarySpells name;
	public OLocation location;
	public int duration;
	public boolean kill;
	public int radius;
	public boolean active;


	//Constructor
	//In the chat distance dropoff code, there will be code that turns the spoken
	//words into a Spells object.
	public StationarySpellObj(Player player, Location location, StationarySpells name, Integer radius, Integer duration){
		this.location = new OLocation(location);
		this.name = name;
		this.player = player.getName();
		kill = false;
		this.duration = duration;
		this.radius = radius;
		active = true;
	}

	/**
	 * Ages the StationarySpellObj
	 */
	public void age(){
		duration--;
		if (duration < 0){
			kill();
		}
	}
	
	/**
	 * Ages the StationarySpellObj
	 * @param i - amount to age
	 */
	public void age(int i){
		duration -= i;
		if (duration < 0){
			kill();
		}
	}

	/**
	 * This kills the stationarySpellObj.
	 */
	public void kill(){
		flair(20);
		kill = true;
	}

	/**
	 * Is the location specified inside the object's radius?
	 * @param loc - The location specified.
	 * @return true if yes, false if no.
	 */
	public boolean isInside(Location loc){
		double distance;
		try {
			distance = loc.distance(location.toLocation());
		} catch (IllegalArgumentException e) {
			return false;
		}
		if (distance < radius){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Gets the block the projectile is inside
	 * @return Block the projectile is inside
	 */
	public Block getBlock(){
		return location.toLocation().getBlock();
	}

	/**
	 * Gets entities within radius of stationary spell
	 * @return List of entities within one block of projectile
	 */
	public List<Entity> getCloseEntities(){
		List<Entity> entities = new ArrayList<Entity>();
		entities = Bukkit.getServer().getWorld(location.getWorld()).getEntities();
		List<Entity> close = new ArrayList<Entity>();
		for (Entity e : entities){
			if (e instanceof LivingEntity){
				if (location.distance(((LivingEntity) e).getEyeLocation()) < radius){
					close.add(e);
				}
			}
			else{
				if (e.getLocation().distance(location.toLocation()) < radius){
					close.add(e);
				}
			}
		}
		return close;
	}

	/**
	 * Gets item entities within radius of the projectile
	 * @return List of item entities within radius of projectile
	 */
	public List<Item> getItems(){
		List<Entity> entities = getCloseEntities();
		List<Item> items = new ArrayList<Item>();
		for (Entity e : entities){
			if (e instanceof Item){
				items.add((Item) e);
			}
		}
		return items;
	}

	/**
	 * Gets all LivingEntity within radius of projectile
	 * @return List of LivingEntity within radius of projectile
	 */
	public List<LivingEntity> getLivingEntities(){
		List<Entity> entities = getCloseEntities();
		List<LivingEntity> living = new ArrayList<LivingEntity>();
		for (Entity e : entities){
			if (e instanceof LivingEntity){
				living.add((LivingEntity) e);
			}
		}
		return living;
	}

	/**
	 * Makes a particle effect at all points along the radius of
	 * spell and at spell loc
	 * @param d - Intensity of the flair. If greater than 10, is reduced to 10.
	 */
	public void flair(double d){
		if (d > 10){
			d = 10;
		}
		for (double inc = (Math.random()*Math.PI)/d; inc < Math.PI; inc += Math.PI/d){
			for (double azi = (Math.random()*Math.PI)/d; azi < 2*Math.PI; azi += Math.PI/d){
				double[] spher = new double[2];
				spher[0] = inc;
				spher[1] = azi;
				Location e = location.toLocation().clone().add(spherToVec(spher));
				e.getWorld().playEffect(e, Effect.SMOKE, 4);
			}
		}
	}

	/**
	 * Translates vector to spherical coords
	 * @param vec - Vector to be translated
	 * @return Spherical coords in double array with
	 * indexes 0=inclination 1=azimuth
	 */
	@SuppressWarnings("unused")
	private double[] vecToSpher(Vector vec){
		double inc = Math.acos(vec.getZ());
		double azi = Math.atan2(vec.getY(),vec.getX());
		double[] ret = new double[2];
		ret[0] = inc;
		ret[1] = azi;
		return ret;
	}

	/**
	 * Translates spherical coords to vector
	 * @param double array with indexes 0=inclination 1=azimuth
	 * @return Vector
	 */
	private Vector spherToVec(double[] spher){
		double inc = spher[0];
		double azi = spher[1];
		double x = radius*Math.sin(inc)*Math.cos(azi);
		double z = radius*Math.sin(inc)*Math.sin(azi);
		double y = radius*Math.cos(inc);
		Vector ret = new Vector(x, y, z);
		return ret;
	}

	private boolean holdsWand(Player player){
		ItemStack held;
		if (player != null){
			if (player.isOnline()){
				if (player.getItemInHand() != null){
					held = player.getItemInHand();
					if (held.getType() == Material.STICK){
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
		else{
			return false;
		}
	}

	private int wandCheck(Player player){
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
		String[] comps = lore.get(0).split(" and ");
		if (woodString.equals(comps[0]) && coreString.equals(comps[1])){
			return 1;
		}
		else{
			return 2;
		}
	}

	/**
	 * Returns the rightWand int. Returns -1 if the player isn't holding a wand.
	 * @param player - Player to check
	 * @return - 1 if the correct wand, 2 if the wrong wand, -1 if no wand.
	 */
	public int rightWand(Player player){
		if (holdsWand(player)){
			return wandCheck(player);
		}
		else{
			return -1;
		}
	}

}