package Spell;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Creates a larger incendio that doesn't stop after hitting an entity.
 * @author lownes
 *
 */
public class INCENDIO_DUO extends SpellProjectile implements Spell{

	private double lifeTime;

	public INCENDIO_DUO(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
		location.add(vector);
		lifeTime = usesModifier*16;
	}

	public void checkEffect() {
		move();
		for (Block block : getBlocksInRadius(location, 2)){
			block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
			if (block.getType() == Material.AIR){
				block.setType(Material.FIRE);
			}
		}
		int modifier = (int)(usesModifier*16);
		List<Item> items = getItems(2);
		for (Item item : items){
			item.setFireTicks(modifier);
		}
		List<LivingEntity> living = getLivingEntities(2);
		for (LivingEntity live : living){
			//System.out.println(live.getLocation().getPitch());
			live.setFireTicks(modifier);
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}

}