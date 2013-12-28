package Spell;

import java.util.List;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Disarms an entity of it's held item, flinging the item in the direction 
 * of the caster with force determined by the spell level.
 * @author lownes
 *
 */
public class EXPELLIARMUS extends SpellProjectile implements Spell{

	public EXPELLIARMUS(Ollivanders p, Player player, Spells name, Integer rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		List<LivingEntity> entities = getLivingEntities(1);
		if (entities.size() > 0){
			LivingEntity entity = entities.get(0);
			ItemStack itemInHand = entity.getEquipment().getItemInHand().clone();
			if (itemInHand.getType() != Material.AIR){
				entity.getEquipment().setItemInHand(null);
				Item item = entity.getWorld().dropItem(entity.getEyeLocation(), itemInHand);
				item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier/10));
			}
			kill = true;
		}
	}
}