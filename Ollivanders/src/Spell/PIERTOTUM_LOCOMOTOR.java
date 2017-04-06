package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Transfigures an iron golem from a block of iron, and snow golem from block of snow.
 * @author lownes
 *
 */
public class PIERTOTUM_LOCOMOTOR extends Transfiguration implements Spell{

	public PIERTOTUM_LOCOMOTOR(Ollivanders2 plugin, Player player, Spells name,
                               Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			if (getBlock().getType() == Material.IRON_BLOCK){
				getBlock().setType(Material.AIR);
				@SuppressWarnings("deprecation")
				FallingBlock ironFall = location.getWorld().spawnFallingBlock(location, Material.IRON_BLOCK, (byte)0);
				transfigureEntity(ironFall, EntityType.IRON_GOLEM, null);
				kill = false;
			}
			if (getBlock().getType() == Material.SNOW_BLOCK){
				getBlock().setType(Material.AIR);
				@SuppressWarnings("deprecation")
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