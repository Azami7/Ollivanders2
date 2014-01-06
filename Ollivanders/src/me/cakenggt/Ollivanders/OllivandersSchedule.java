package me.cakenggt.Ollivanders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import Effect.Effect;
import Spell.Spell;
import StationarySpell.StationarySpell;

/**
 * Scheduler for Ollivanders
 * @author lownes
 *
 */
class OllivandersSchedule implements Runnable{
	Ollivanders p;
	public OllivandersSchedule(Ollivanders plugin) {
		p = plugin;
	}

	public void run() {
		List<SpellProjectile> projectiles = p.getProjectiles();
		if (projectiles != null){
			List<SpellProjectile> projectiles2 = new ArrayList<SpellProjectile>(projectiles);
			if (projectiles2.size() > 0){
				for (SpellProjectile proj : projectiles2){
					((Spell) proj).checkEffect();
					if (proj.kill){
						p.remProjectile(proj);
					}
				}
			}
		}
		List<Player> onlinePlayers = Arrays.asList(p.getServer().getOnlinePlayers());
		for (String name : p.getOPlayerMap().keySet()){
			OPlayer oply = p.getOPlayerMap().get(name);
			if (oply.getEffects() != null && onlinePlayers.contains(p.getServer().getPlayer(name))){
				List<OEffect> effects2 = new ArrayList<OEffect>(oply.getEffects());
				for (OEffect effect : effects2){
					((Effect) effect).checkEffect(p);
					if (effect.kill){
						oply.remEffect(effect);
					}
				}
			}
		}
		List<StationarySpellObj> stationary = p.getStationary();
		if (stationary != null){
			List<StationarySpellObj> stationary2 = new ArrayList<StationarySpellObj>(stationary);
			if (stationary2.size() > 0){
				for (StationarySpellObj stat : stationary2){
					if (stat.active){
						((StationarySpell) stat).checkEffect(p);
					}
					if (stat.kill){
						p.remStationary(stat);
					}
				}
			}
		}
	}
}