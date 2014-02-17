package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Open the target LivingEntity's inventory
 * @author lownes
 *
 */
public class LEGILIMENS extends SpellProjectile implements Spell {

	public LEGILIMENS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(1)){
			if (live instanceof Player){
				Player target = (Player)live;
				double experience = p.getOPlayer(target).getSpellCount().get(Spells.LEGILIMENS);
				if (usesModifier > experience){
					player.openInventory(target.getInventory());
				}
				kill();
				return;
			}
		}
	}

}
