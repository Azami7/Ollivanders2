package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OPlayer;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Adds a MUCUS_AD_NAUSEAM oeffect to the player
 * @author lownes
 *
 */
public class MUCUS_AD_NAUSEAM extends SpellProjectile implements Spell{

	public MUCUS_AD_NAUSEAM(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<LivingEntity> living = getLivingEntities(1);
		for (LivingEntity live : living){
			if (live instanceof Player){
				Player ply = (Player)live;
				OPlayer oply = p.getOPlayer(ply);
				int dur = (int)(usesModifier*1200);
				oply.addEffect(new Effect.MUCUS_AD_NAUSEAM(player, Effects.MUCUS_AD_NAUSEAM, dur));
				p.setOPlayer(ply, oply);
				kill();
			}
		}
	}
	
}