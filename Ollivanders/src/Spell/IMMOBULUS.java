package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Immobilizes a player for an amount of time depending on the player's
 * spell level.
 * @author lownes
 *
 */
public class IMMOBULUS extends SpellProjectile implements Spell{

	public IMMOBULUS(Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<LivingEntity> entities = getLivingEntities(1);
		for (LivingEntity entity : entities){
			int modifier = (int)usesModifier;
			PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, modifier * 20, 10);
			entity.addPotionEffect(slow);
			kill();
		}
	}
	
}