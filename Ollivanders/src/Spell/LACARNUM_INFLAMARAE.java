package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.projectiles.ProjectileSource;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Shoots out a SmallFireball projectile
 * @author lownes
 *
 */
public class LACARNUM_INFLAMARAE extends SpellProjectile implements Spell {

	public LACARNUM_INFLAMARAE(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		kill();
		SmallFireball fireball = (SmallFireball) player.getWorld().spawnEntity(location, EntityType.SMALL_FIREBALL);
		fireball.setShooter((ProjectileSource)player);
		fireball.setDirection(vector);
	}

}
