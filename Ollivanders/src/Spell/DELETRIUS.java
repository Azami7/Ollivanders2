package Spell;

import java.util.List;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Deletes an item entity.
 * @author lownes
 *
 */
public class DELETRIUS extends SpellProjectile implements Spell{
	
	public DELETRIUS(Ollivanders p, Player player, Spells name, Integer rightWand){
		super(p, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Item> items = getItems(1);
		for (Item item : items){
			item.remove();
			kill();
			return;
		}
	}
}