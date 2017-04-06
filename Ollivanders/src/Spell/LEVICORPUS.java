package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Puts a levicorpus effect on the player
 * @author lownes
 *
 */
public class LEVICORPUS extends SpellProjectile implements Spell {

	public LEVICORPUS(Ollivanders2 plugin, Player player, Spells name,
                      Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(1)){
			if (live instanceof Player){
				OPlayer oply = p.getOPlayer((Player)live);
				Effect.LEVICORPUS levi = new Effect.LEVICORPUS((Player)live, Effects.LEVICORPUS, (int)(usesModifier*1200.0), live.getEyeLocation());
				oply.addEffect(levi);
				kill();
				return;
			}
		}
	}

}
