package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Pulls a living entity towards the caster.
 * @author lownes
 *
 */
public class CARPE_RETRACTUM extends SpellProjectile implements Spell{

	public CARPE_RETRACTUM(Ollivanders2 plugin, Player player, Spells name,
                           Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<LivingEntity> living = getLivingEntities(1);
		for (LivingEntity live : living){
			live.setVelocity(player.getEyeLocation().toVector().subtract(live.getLocation().toVector()).normalize().multiply(usesModifier/10));
			kill();
			return;
		}
	}
	
}