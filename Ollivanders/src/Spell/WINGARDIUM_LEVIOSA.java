package Spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import StationarySpell.COLLOPORTUS;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**Moves a group of blocks.
 * @author lownes
 *
 */
public class WINGARDIUM_LEVIOSA extends SpellProjectile implements Spell{

	Map<Location, Material> materialMap = new HashMap<Location, Material>();
	List<Block> blockList = new ArrayList<Block>();
	List<Location> locList = new ArrayList<Location>();
	boolean moving = true;
	double length = 0;
	boolean dropBlocks = true;	//If the blocks should be converted to fallingBlocks after the end of the spell.

	public WINGARDIUM_LEVIOSA(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (moving){
			move();
			Material type = getBlock().getType();
			if (type != Material.AIR && type != Material.WATER && type != Material.STATIONARY_WATER && type != Material.LAVA && type != Material.STATIONARY_LAVA){
				moving = false;
				double radius = usesModifier/4;
				ArrayList<COLLOPORTUS> collos = new ArrayList<COLLOPORTUS>();
				for (StationarySpellObj stat : p.getStationary()){
					if (stat instanceof COLLOPORTUS){
						collos.add((COLLOPORTUS) stat);
					}
				}
				for (Block block : getBlocksInRadius(location, radius)){
					boolean insideCollo = false;
					for (COLLOPORTUS collo : collos){
						if (collo.isInside(block.getLocation())){
							insideCollo = true;
						}
					}
					if (!insideCollo){
						type = block.getType();
						if (type != Material.WATER && type != Material.STATIONARY_WATER && type != Material.LAVA && type != Material.STATIONARY_LAVA && type != Material.SAND && type != Material.GRAVEL && type != Material.AIR && type != Material.BEDROCK && type.isSolid() && !p.getTempBlocks().contains(block)){
							Location loc = centerOfBlock(block).subtract(location);
							Material mat = block.getType();
							materialMap.put(loc, mat);
							locList.add(loc);
							blockList.add(block);
						}
					}
				}
				length = player.getEyeLocation().distance(location);
				kill = false;
			}
		}
		else{
			if (player.isSneaking()){
				List<Location> locList2 = new ArrayList<Location>(locList);
				for (Block block : blockList){
					if (block.getType() == Material.AIR){
						locList2.remove(locList.get(blockList.indexOf(block)));
					}
				}
				locList = locList2;
				for (Block block : blockList){
					block.setType(Material.AIR);
				}
				blockList.clear();
				for (Location loc : locList){
					Vector direction = player.getEyeLocation().getDirection().multiply(length);
					Location center = player.getEyeLocation().add(direction);
					location = center;
					Location toLoc = center.clone().add(loc);
					if (toLoc.getBlock().getType() == Material.AIR){
						toLoc.getBlock().setType(materialMap.get(loc));
						blockList.add(toLoc.getBlock());
					}
				}
			}
			else if(dropBlocks){
				for (Block block : blockList){
					block.setType(Material.AIR);
				}
				Vector direction = player.getEyeLocation().getDirection().multiply(length);
				Location center = player.getEyeLocation().add(direction);
				Vector  moveVec = center.toVector().subtract(location.toVector());
				for (Location loc : locList){
					Location toLoc = center.clone().add(loc);
					FallingBlock fall = loc.getWorld().spawnFallingBlock(toLoc, materialMap.get(loc), (byte)0);
					fall.setVelocity(moveVec);
				}
				//				blockList.clear();
				//				Vector direction = player.getEyeLocation().getDirection().multiply(length);
				//				Location center = player.getEyeLocation().add(direction);
				//				for (Location loc : materialMap.keySet()){
				//					loc.getWorld().spawnFallingBlock(center.clone().add(loc).getBlock().getLocation(), materialMap.get(loc), (byte)0);
				//				}
				kill();
			}
			else{
				kill();
			}
		}
	}

	/**Returns the location at the center of the block, instead of the corner.
	 * @param block - Block to get the center location of.
	 * @return Location at the center of the block.
	 */
	private Location centerOfBlock(Block block){
		Location newLoc = block.getLocation().clone();
		newLoc.setX(newLoc.getX()+0.5);
		newLoc.setY(newLoc.getY()+0.5);
		newLoc.setZ(newLoc.getZ()+0.5);
		return newLoc;
	}

}