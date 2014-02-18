package Spell;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.OPlayer;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Reduces the time duration of any levicorpus effects on the target
 * @author lownes
 *
 */
public class LIBERACORPUS extends SpellProjectile implements Spell {

	public LIBERACORPUS(Ollivanders plugin, Player player, Spells name,
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
