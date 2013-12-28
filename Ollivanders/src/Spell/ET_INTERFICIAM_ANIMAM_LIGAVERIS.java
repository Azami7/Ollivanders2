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
			Spells name, Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			double health = ((Damageable)player).getHealth();
			System.out.println("soulpre: " + p.getOPlayer(player).getSouls());
			p.getOPlayer(player).addSoul();
			System.out.println("soulpost: " + p.getOPlayer(player).getSouls());
			int souls = p.getOPlayer(player).getSouls();
			if (health-(4*souls) > 0){
				HORCRUX horcrux = new HORCRUX(player, location, StationarySpells.HORCRUX, 5, 10);
				System.out.println(horcrux);
				System.out.println(horcrux.player);
				horcrux.flair(10);
				p.addStationary(horcrux);
			}
			System.out.println("Souls: " + souls);
			double damage = 3.0*souls;
			if ((((Damageable)player).getHealth()-damage)<= 0){
				List<StationarySpellObj> stationarys = p.getStationary();
				for(StationarySpellObj stationary : stationarys){
					if (stationary.name == StationarySpells.HORCRUX && stationary.player.equals(player.getDisplayName())){
						Location tp = stationary.location.toLocation();
						tp.setY(tp.getY()+1);
						player.teleport(tp);
						player.setHealth(20.0);
						p.remStationary(stationary);
						return;
					}
				}
			}
			player.damage(damage);
			kill();
		}
	}
	
}