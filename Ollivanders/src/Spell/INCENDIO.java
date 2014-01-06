package Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Sets fire to blocks. Also sets fire to living entities and items for
 * an amount of time depending on the player's spell level.
 * @author lownes
 *
 */
public class INCENDIO extends SpellProjectile implements Spell{

	private double lifeTime;
	
	public INCENDIO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*16;
	}

	public void checkEffect() {
		move();
		Block block = getBlock();
		Material type = block.getType();
		if (type == Material.AIR){
			block.setType(Material.FIRE);
		}
		int modifier = (int)(usesModifier*16);
		List<Item> items = getItems(1);
		for (Item item : items){
			item.setFireTicks(modifier);
			kill();
		}
		List<LivingEntity> living = getLivingEntities(1);
		for (LivingEntity live : living){
			live.setFireTicks(modifier);
			kill();
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}
	
}