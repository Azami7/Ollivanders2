package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Reduces the time duration of any levicorpus effects on the target
 * @author lownes
 *
 */
public class LIBERACORPUS extends SpellProjectile implements Spell {

	public LIBERACORPUS(Ollivanders2 plugin, Player player, Spells name,
                        Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		for (LivingEntity live : getLivingEntities(1)){
			if (live instanceof Player){
				Player ply = (Player)live;
				OPlayer op = p.getOPlayer(ply);
				for (OEffect effect : op.getEffects()){
					if (effect.name == Effects.LEVICORPUS){
						effect.age((int) (usesModifier*2400));
					}
				}
				kill();
				return;
			}
		}
	}

}
