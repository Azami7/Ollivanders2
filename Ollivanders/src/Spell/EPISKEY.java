package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Gives an entity a healing effect for usesModifier seconds
 * @author lownes
 *
 */
public class EPISKEY extends SpellProjectile implements Spell{

	public EPISKEY(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(1)){
			live.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (usesModifier*20), 0), true);
			kill();
			return;
		}
	}
	
}