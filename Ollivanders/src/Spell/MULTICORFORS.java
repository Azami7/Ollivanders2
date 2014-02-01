package Spell;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Colorable;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Changes color of sheep and colorable blocks
 * @author lownes
 *
 */
public class MULTICORFORS extends SpellProjectile implements Spell{

	public MULTICORFORS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		DyeColor[] values = DyeColor.values();
		DyeColor newColor = values[(int) (Math.random()*values.length)];
		for (LivingEntity live : getLivingEntities(1)){
			if (live instanceof Sheep){
				Sheep sheep = (Sheep)live;
				sheep.setColor(newColor);
				kill();
			}
		}
		if (getBlock().getType() != Material.AIR){
			for (Block block : getBlocksInRadius(location, usesModifier)){
				if (block instanceof Colorable){
					Colorable colorable = (Colorable)block;
					colorable.setColor(newColor);
				}
				kill();
			}
		}
	}
	
}