package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Shrinks a giant to a normal zombie. It's radius depends on experience
 * @author lownes
 *
 */
public class REDUCIO extends SpellProjectile implements Spell{

	public REDUCIO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(usesModifier)){
			if (live instanceof Giant){
				live.getWorld().spawnEntity(live.getLocation(), EntityType.ZOMBIE);
				live.remove();
				kill();
			}
		}
	}
	
}