package Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OPlayer;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Silences a player for a duration depending on the spell's level. The
 * target player can only use nonverbal spells.
 * @author lownes
 *
 */
public class SILENCIO extends SpellProjectile implements Spell{

	public SILENCIO(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
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
				oply.addEffect(new Effect.SILENCIO(player, Effects.SILENCIO, dur));
				p.setOPlayer(ply, oply);
			}
		}
	}
	
}