package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Throws another player away from the caster. Twice as powerful as depulso.
 * @author lownes
 *
 */
public class EVERTE_STATUM extends SpellProjectile implements Spell{

	public EVERTE_STATUM(Ollivanders2 plugin, Player player, Spells name,
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