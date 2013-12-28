package Spell;

import java.util.List;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Spell which will grab the targeted item and pull it toward you 
 * with a force determined by your level in the spell.
 * @author lownes
 *
 */
public class ACCIO extends SpellProjectile implements Spell{
	
	public ACCIO(Ollivanders p, Player player, Spells name, Integer rightWand){
		super(p, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Item> items = getItems(1);
		for (Item item : items){
			item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier/10));
			kill();
			return;
		}
	}
}