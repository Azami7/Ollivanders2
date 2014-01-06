package Spell;

import java.util.List;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Casts a powerful confusion potion effect on the player that scales 
 * with the caster's level in this spell.
 * @author lownes
 *
 */
public class CONFUNDO extends SpellProjectile implements Spell{

	public CONFUNDO(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		List<LivingEntity> entities = getLivingEntities(1);
		if (entities.size() > 0){
			LivingEntity entity = entities.get(0);
			int modifier = (int)usesModifier;
			PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, modifier * 20, modifier);
			entity.addPotionEffect(confusion);
			kill = true;
		}
	}
}