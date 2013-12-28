package Spell;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * All animals that you have created through transfiguration will target the targeted LivingEntity.
 * @author lownes
 *
 */
public class OPPUGNO extends SpellProjectile implements Spell{

	public OPPUGNO(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		for (LivingEntity e : getLivingEntities(1)){
			for (SpellProjectile spell : p.getProjectiles()){
				if (spell instanceof Transfiguration){
					if (spell.player.equals(player)){
						for (Entity entity : player.getWorld().getEntities()){
							if (entity.getEntityId() == ((Transfiguration)spell).getToID()){
								if (entity instanceof LivingEntity){
									//System.out.println(entity.getType() + " is now targeting " + e.getType());
									//((Creature)entity).setTarget(e);
									((Creature)entity).damage(0.0, e);
									kill();
								}
							}
						}
					}
				}
			}
		}
	}

}