package Spell;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Blasts spiders away
 * @author lownes
 *
 */
public class ARANIA_EXUMAI extends SpellProjectile implements Spell{

	public ARANIA_EXUMAI(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Entity> entities = getCloseEntities(1);
		for (Entity entity : entities){
			EntityType type = entity.getType();
			if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER){
				entity.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier/10));
				kill();
				return;
			}
		}
	}

}