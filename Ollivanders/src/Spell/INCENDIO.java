package Spell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Sets fire to blocks. Also sets fire to living entities and items for
 * an amount of time depending on the player's spell level.
 * @author lownes
 *
 */
public class INCENDIO extends SpellProjectile implements Spell{

	private double lifeTime;
	boolean move;

	public INCENDIO(Ollivanders2 plugin, Player player, Spells name,
                    Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*16;
		move = true;
	}

	public void checkEffect() {
		if (move){
			move();
			//Check if the blocks set on fire are still on fire
			Set<Block> remChange = new HashSet<Block>();
			for (Block block : changed){
				if (block.getType() != Material.FIRE){
					remChange.add(block);
				}
			}
			changed.removeAll(remChange);
			Block block = getBlock();
			Material type = block.getType();
			if (type == Material.AIR){
				block.setType(Material.FIRE);
				changed.add(block);
			}
			int modifier = (int)(usesModifier*16);
			List<Item> items = getItems(1);
			for (Item item : items){
				item.setFireTicks(modifier);
				kill();
			}
			List<LivingEntity> living = getLivingEntities(1);
			for (LivingEntity live : living){
				live.setFireTicks(modifier);
				kill();
			}
			for (SpellProjectile proj : p.getProjectiles()){
				if ((proj.name == Spells.GLACIUS || proj.name == Spells.GLACIUS_DUO || proj.name == Spells.GLACIUS_TRIA) && proj.location.getWorld() == location.getWorld()){
					if (proj.location.distance(location) < 1){
						proj.revert();
						proj.kill();
					}
				}
			}
			if (lifeTicks > lifeTime){
				kill = false;
				move = false;
				lifeTicks = (int)(-(usesModifier*1200));
			}
		}
		else{
			lifeTicks ++;
		}
		if (lifeTicks >= 159){
			revert();
			kill();
		}
	}

	public void revert(){
		for (Block block : changed){
			Material mat = block.getType();
			if (mat == Material.FIRE){
				block.setType(Material.AIR);
			}
		}
	}

}