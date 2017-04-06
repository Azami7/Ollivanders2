package Spell;

import java.util.HashSet;
import java.util.Set;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Burns sun-sensitive entities with a radius
 * @author lownes
 *
 */
public class LUMOS_SOLEM extends SpellProjectile implements Spell {

	boolean move = true;
	Set<Block> blocks = new HashSet<Block>();

	public LUMOS_SOLEM(Ollivanders2 plugin, Player player, Spells name,
                       Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		if (move){
			move();
			if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
				for (LivingEntity live : getLivingEntities(usesModifier)){
					boolean burn = false;
					if (live.getType() == EntityType.ZOMBIE){
						Zombie zombie = (Zombie)live;
						if (!zombie.isBaby()){
							burn = true;
						}
					}
					if (live.getType() == EntityType.SKELETON){
						burn = true;
					}
					if (burn){
						live.setFireTicks(160);
					}
				}
				kill = false;
				move = false;
				for (Block block : getBlocksInRadius(location, usesModifier)){
					if (block.getType() == Material.AIR){
						blocks.add(block);
						block.setType(Material.GLOWSTONE);
					}
				}
			}
		}
		else{
			kill();
			for (Block block : blocks){
				block.setType(Material.AIR);
			}
		}
	}

}
