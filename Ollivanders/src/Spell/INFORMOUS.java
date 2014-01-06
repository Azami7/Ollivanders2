package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.OPlayer;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**
 * Gives information on LivingEntity (health) and StationarySpellObj (duration)
 * and weather (duration) and Player (spell effects). Range of spell depends on level.
 * @author lownes
 *
 */
public class INFORMOUS extends SpellProjectile implements Spell{

	List <LivingEntity> iEntity = new ArrayList<LivingEntity>();
	List <StationarySpellObj> iSpell = new ArrayList<StationarySpellObj>();
	boolean toldWeather = false;
	private double lifeTime;

	public INFORMOUS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*16;
	}

	public void checkEffect() {
		move();
		for (LivingEntity entity : getLivingEntities(1)){
			if (!iEntity.contains(entity)){
				player.sendMessage(entity.getType().toString() + " has " + ((Damageable)entity).getHealth() + " health.");
				if (entity instanceof Player){
					Player ePlayer = (Player)entity;
					OPlayer eoplayer = p.getOPlayer(ePlayer);
					for (OEffect effect : eoplayer.getEffects()){
						player.sendMessage(ePlayer.getDisplayName() + " has " + Effects.recode(effect.name) + ".");
					}
				}
				iEntity.add(entity);
			}
		}
		for (StationarySpellObj spell : p.getStationary()){
			if (spell.isInside(location) && !iSpell.contains(spell)){
				player.sendMessage(spell.name.toString() + " of radius " + spell.radius + " has " + spell.duration/20 + " seconds left.");
				iSpell.add(spell);
			}
		}
		if (location.getY() > 256 && !toldWeather){
			toldWeather = true;
			String weather;
			World world = location.getWorld();
			boolean thunder = world.isThundering();
			if (world.hasStorm()){
				weather = "rain";
			}
			else{
				weather = "clear skies";
			}
			int weatherTime = world.getWeatherDuration();
			int thunderTime = world.getThunderDuration();
			player.sendMessage("There will be " + weather + " for " + weatherTime/20 + " more seconds.");
			if (thunder){
				player.sendMessage("There will be thunder for " + thunderTime/20 + " more seconds.");
			}
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}

}