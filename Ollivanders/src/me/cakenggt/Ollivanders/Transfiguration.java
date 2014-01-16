package me.cakenggt.Ollivanders;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Class for transfiguration spells
 * @author lownes
 *
 */
public class Transfiguration extends SpellProjectile{

	private EntityType fromEType;
	private ItemStack fromStack;
	private int toID = -1;
	private boolean hasTransfigured;

	public Transfiguration(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		hasTransfigured = false;
	}	


	/**
	 * Transfigures entity into new EntityType. Do not use on players. Time length is usesModier() number of minutes plus 8 seconds.
	 * @param entity - Entity to transfigure
	 * @param toType - Type to change entity into. If transfiguring into an item, have this be EntityType.DROPPED_ITEM
	 * @param toStack - If transfiguring into an item, the itemstack to transfigure into
	 * @return - The resulting entity.
	 */
	public Entity transfigureEntity(Entity entity, EntityType toType, ItemStack toStack){
		Location loc = location;
		if (entity != null){
			fromEType = entity.getType();
			if (fromEType == EntityType.DROPPED_ITEM){
				fromStack = ((Item)entity).getItemStack();
			}
			if (fromEType == EntityType.FALLING_BLOCK){
				fromStack = new ItemStack(((FallingBlock)entity).getMaterial());
			}
			loc = entity.getLocation();
			for (SpellProjectile spell : p.getProjectiles()){
				if (spell instanceof Transfiguration){
					Transfiguration trans = (Transfiguration)spell;
					if (trans.getToID() == entity.getEntityId()){
						trans.kill();
						return transfigureEntity(trans.endTransfigure(), toType, toStack);
					}
				}
			}
			entity.remove();
		}
		hasTransfigured = true;
		Entity newEntity = null;
		if (toType == EntityType.DROPPED_ITEM){
			newEntity = player.getWorld().dropItemNaturally(loc, toStack);
		}
		else if (toType == null){	
		}
		else{
			newEntity = player.getWorld().spawnEntity(loc, toType);
		}
		if (newEntity == null){
			toID = -1;
		}
		else{
			toID = newEntity.getEntityId();
		}
		this.lifeTicks = (int)(usesModifier*-1200);
		return newEntity;
	}

	/**
	 * Ends the transfiguration. Drops items if there are any in it's inventory.
	 * @return The newly spawned Entity. Null if no entity was spawned from the ending of the transfiguration.
	 */
	public Entity endTransfigure(){
		kill();
		for (World w : p.getServer().getWorlds()){
			for (Entity e : w.getEntities()){
				if (e.getEntityId() == toID){
					e.remove();
					if (e instanceof InventoryHolder){
						for (ItemStack stack : ((InventoryHolder)e).getInventory().getContents()){
							if (stack != null){
								e.getWorld().dropItemNaturally(e.getLocation(), stack);
							}
						}
					}
					if (fromEType == null){
						return null;
					}
					if (fromEType.equals(EntityType.DROPPED_ITEM)){
						return (Entity)e.getWorld().dropItemNaturally(e.getLocation(), fromStack);
					}
					if (fromEType.equals(EntityType.FALLING_BLOCK)){
						return (Entity)e.getWorld().spawnFallingBlock(e.getLocation(), fromStack.getType(), (byte)0);
					}
					else{
						return e.getWorld().spawnEntity(e.getLocation(), fromEType);
					}
				}
			}
		}
		if (toID == -1){
			if (fromEType.equals(EntityType.DROPPED_ITEM)){
				return (Entity)location.getWorld().dropItemNaturally(location, fromStack);
			}
			else{
				return location.getWorld().spawnEntity(location, fromEType);
			}
		}
		else{
			return null;
		}
	}

	/**
	 * Has the transfiguration effect happened yet?
	 * @return If the transfiguration has taken place
	 */
	public boolean hasTransfigured(){
		return hasTransfigured;
	}

	/**
	 * Moves the spell forward until it meets an entity and then performs a simple transfiguration on a non-player entity. This is the basic transfiguration code for most simple transfiguration spells.
	 * @param type - The EntityType to transfigure the non-player entity into
	 * @param stack - The itemstack to transfigure into, if the entitytype is Item
	 */
	public void simpleTransfigure(EntityType type, ItemStack stack){
		if (!hasTransfigured()){
			move();
			for (Entity e : player.getWorld().getEntities()){
				if (e.getType() != EntityType.PLAYER && e.getLocation().distance(location)<1){
					transfigureEntity(e, type, stack);
				}
			}
		}
		else{
			if (lifeTicks > 160){
				//kill = true;
				endTransfigure();
			}
			else{
				lifeTicks ++;
			}
		}
	}

	/**
	 * Gets the id of the transfigured entity
	 * @return the toID
	 */
	public int getToID(){
		return toID;
	}
}