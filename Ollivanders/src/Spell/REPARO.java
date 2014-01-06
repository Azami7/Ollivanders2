package Spell;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Repairs an itemstack you aim it at.
 * @author lownes
 *
 */
public class REPARO extends SpellProjectile implements Spell{

	public REPARO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Item> items = getItems(1);
		for (Item item : items){
			ItemStack stack = item.getItemStack();
			item.getWorld().dropItem(item.getLocation(), new ItemStack(stack.getType(), stack.getAmount()));
			item.remove();
			kill();
		}
	}
	
}