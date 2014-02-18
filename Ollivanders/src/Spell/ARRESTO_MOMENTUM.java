package Spell;

import java.util.List;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Slows down any item or living entity according to your 
 * level in the spell.
 * @author lownes
 *
 */
public class ARRESTO_MOMENTUM extends SpellProjectile implements Spell{

	public ARRESTO_MOMENTUM(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		double modifier = usesModifier;
		List<LivingEntity> entities = getLivingEntities(1);
		for (LivingEntity entity : entities){
			double speed = entity.getVelocity().length()/(Math.pow(modifier, 2));
			entity.setVelocity(entity.getVelocity().normalize().multiply(speed));
			entity.setFallDistance((float) (entity.getFallDistance()/modifier));
			kill = true;
			return;
		}
		List<Item> items = getItems(1);
		for (Item item : items){
			double speed = item.getVelocity().length()/(Math.pow(modifier, 2));
			item.setVelocity(item.getVelocity().normalize().multiply(speed));
			kill = true;
			return;
		}
	}
}