package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Shoots the hit entity away from you.
 * @author lownes
 *
 */
public class DEPULSO extends SpellProjectile implements Spell{

	public DEPULSO(Ollivanders2 plugin, Player player, Spells name,
                   Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Entity> entities = getCloseEntities(1);
		for (Entity entity : entities){
			entity.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier/20));
			kill();
			return;
		}
	}
	
}