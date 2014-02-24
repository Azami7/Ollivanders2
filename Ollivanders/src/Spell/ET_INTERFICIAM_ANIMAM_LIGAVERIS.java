package Spell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import StationarySpell.HORCRUX;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Creates a horcrux stationary spell object where it collides with a block. 
 * Also damages the player and increases their souls count.
 * @author lownes
 *
 */
public class ET_INTERFICIAM_ANIMAM_LIGAVERIS extends SpellProjectile implements Spell{

	public ET_INTERFICIAM_ANIMAM_LIGAVERIS(Ollivanders plugin, Player player,
			Spells name, Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			double futureHealth = ((Damageable)player).getHealth();
			if (futureHealth > ((Damageable)player).getMaxHealth()/2.0){
				futureHealth = ((Damageable)player).getMaxHealth()/2.0;
			}
			int souls = p.getOPlayer(player).getSouls();
			//If the player's soul is split enough and they can survive
			//making another horcrux, then make a new one and damage them
			if (futureHealth-1 > 0 && souls > 0){
				HORCRUX horcrux = new HORCRUX(player, location, StationarySpells.HORCRUX, 5, 10);
				horcrux.flair(10);
				p.addStationary(horcrux);
				player.setMaxHealth(((Damageable)player).getMaxHealth()/2.0);
				p.getOPlayer(player).subSoul();
				player.damage(1.0);
				kill();
			}
			else{
				if (souls == 0){
					player.sendMessage("Your soul is not yet so damaged to allow this.");
					return;
				}
				//If they player couldn't survive making another horcrux
				//then they are sent back to a previous horcrux
				else if ((futureHealth-1)<= 0){
					List<StationarySpellObj> stationarys = p.getStationary();
					for(StationarySpellObj stationary : stationarys){
						if (stationary.name == StationarySpells.HORCRUX && stationary.player.equals(player.getName())){
							Location tp = stationary.location.toLocation();
							tp.setY(tp.getY()+1);
							player.teleport(tp);
							player.setHealth(((Damageable)player).getMaxHealth());
							p.getOPlayer(player).resetEffects();
							p.remStationary(stationary);
							return;
						}
					}
					//If the player doesn't have any horcruxes left
					//then they are killed.
					player.damage(1000.0);
				}
			}
		}
	}

}