package Spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Shoots out a SmallFireball projectile
 * @author lownes
 *
 */
public class LACARNUM_INFLAMARI extends SpellProjectile implements Spell {

	public LACARNUM_INFLAMARI(Ollivanders2 plugin, Player player, Spells name,
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
