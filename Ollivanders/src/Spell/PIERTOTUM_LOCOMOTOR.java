package Spell;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * Transfigures an iron golem from a block of iron, and snow golem from block of snow.
 * @author lownes
 *
 */
public class PIERTOTUM_LOCOMOTOR extends Transfiguration implements Spell{

	public PIERTOTUM_LOCOMOTOR(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			if (getBlock().getType() == Material.IRON_BLOCK){
				getBlock().setType(Material.AIR);
				FallingBlock ironFall = location.getWorld().spawnFallingBlock(location, Material.IRON_BLOCK, (byte)0);
				transfigureEntity(ironFall, EntityType.IRON_GOLEM, null);
				kill = false;
			}
			if (getBlock().getType() == Material.SNOW_BLOCK){
				getBlock().setType(Material.AIR);
				FallingBlock ironFall = location.getWorld().spawnFallingBlock(location, Material.SNOW_BLOCK, (byte)0);
				transfigureEntity(ironFall, EntityType.SNOWMAN, null);
				kill = false;
			}
		}
		else{
			if (lifeTicks > 160){
				kill = true;
				endTransfigure();
			}
			else{
				lifeTicks ++;
			}
		}
	}
	
}