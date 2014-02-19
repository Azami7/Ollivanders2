package Spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Shoots out a SmallFireball projectile
 * @author lownes
 *
 */
public class LACARNUM_INFLAMARI extends SpellProjectile implements Spell {

	public LACARNUM_INFLAMARI(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		kill();
		player.launchProjectile(SmallFireball.class, vector);
	}

}
