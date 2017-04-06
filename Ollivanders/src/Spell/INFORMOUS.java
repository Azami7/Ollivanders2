package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import Effect.LYCANTHROPY;
import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

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

	public INFORMOUS(Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*16;
	}

	public void checkEffect() {
		move();
		for (LivingEntity entity : getLivingEntities(1)){
			if (!iEntity.contains(entity)){
				player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+entity.getType().toString() + " has " + ((Damageable)entity).getHealth() + " health.");
				if (entity instanceof Player){
					Player ePlayer = (Player)entity;
					OPlayer eoplayer = p.getOPlayer(ePlayer);
					for (OEffect effect : eoplayer.getEffects()){
						if (effect instanceof LYCANTHROPY){
							player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+ePlayer.getName() + " has Lycanthropy.");
						}
						else{
							player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+ePlayer.getName() + " has " + Effects.recode(effect.name) + " with " + effect.duration/20 + " seconds left.");
						}
					}
				}
				iEntity.add(entity);
			}
		}
		for (StationarySpellObj spell : p.getStationary()){
			if (spell.isInside(location) && !iSpell.contains(spell)){
				if (spell instanceof StationarySpell.COLLOPORTUS){
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+spell.name.toString() + " of radius " + spell.radius + " has " + spell.duration/1200 + " power left.");
				}
				else if (spell instanceof StationarySpell.HORCRUX){
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+spell.name.toString() + " of player " + Bukkit.getPlayer(spell.getPlayerUUID()).getName() + " of radius " + spell.radius);
				}
				else if (spell instanceof StationarySpell.ALIQUAM_FLOO){
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+"Floo registration of " + ((StationarySpell.ALIQUAM_FLOO)spell).getFlooName());
				}
				else if (spell instanceof StationarySpell.HARMONIA_NECTERE_PASSUS){
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+"Vanishing Cabinet");
				}
				else{
					player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+spell.name.toString() + " of radius " + spell.radius + " has " + spell.duration/20 + " seconds left.");
				}
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
			player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+"There will be " + weather + " for " + weatherTime/20 + " more seconds.");
			if (thunder){
				player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))+"There will be thunder for " + thunderTime/20 + " more seconds.");
			}
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}

}