package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Gives a target blindness
 * @author lownes
 *
 */
public class OBSCURO extends SpellProjectile implements Spell {

	public OBSCURO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(1)){
			PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier*1200), 0);
			live.addPotionEffect(blind);
			kill();
			return;
		}
	}

}
