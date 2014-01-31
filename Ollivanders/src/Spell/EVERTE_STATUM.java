package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Throws another player away from the caster. Twice as powerful as depulso.
 * @author lownes
 *
 */
public class EVERTE_STATUM extends SpellProjectile implements Spell{

	public EVERTE_STATUM(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<LivingEntity> living = this.getLivingEntities(1);
		for (LivingEntity live : living){
			if (live instanceof Player){
				live.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier/10));
				kill();
				return;
			}
		}
	}

}