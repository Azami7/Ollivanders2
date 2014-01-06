package Spell;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**Apparition code for players who have over 100 uses in apparition.
 * @author lownes
 *
 */
public class APPARATE extends SpellProjectile implements Spell{

	public APPARATE(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		kill();
		location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
		boolean canApparateOut = true;
		for (StationarySpellObj stat : p.getStationary()){
			if (stat instanceof StationarySpell.NULLUM_EVANESCUNT && stat.isInside(player.getLocation()) && stat.active){
				stat.flair(10);
				canApparateOut = false;
			}
		}
		if (canApparateOut){
			Location from = player.getLocation().clone();
			Location to;
			Location eyeLocation = player.getEyeLocation();
			Material inMat = eyeLocation.getBlock().getType();
			int distance = 0;
			while ((inMat == Material.AIR || inMat == Material.FIRE || inMat == Material.WATER || inMat == Material.STATIONARY_WATER || inMat == Material.LAVA || inMat == Material.STATIONARY_LAVA) && distance < 160){
				eyeLocation = eyeLocation.add(eyeLocation.getDirection());
				distance ++;
				inMat = eyeLocation.getBlock().getType();
			}
			to = eyeLocation.subtract(eyeLocation.getDirection()).clone();
			to.setPitch(from.getPitch());
			to.setYaw(from.getYaw());
			Double radius = (1/usesModifier)*from.distance(to)*0.1;
			Double newX = to.getX()-(radius/2)+(radius * Math.random());
			Double newZ = to.getZ()-(radius/2)+(radius * Math.random());
			to.setX(newX);
			to.setZ(newZ);
			boolean canApparateIn = true;
			for (StationarySpellObj stat : p.getStationary()){
				if (stat instanceof StationarySpell.NULLUM_APPAREBIT && stat.isInside(to) && stat.active){
					stat.flair(10);
					canApparateIn = false;
				}
			}
			if (canApparateIn) {
				player.teleport(to);
				for (Entity e : player.getWorld().getEntities()) {
					if (from.distance(e.getLocation()) <= 2) {
						e.teleport(to);
					}
				}
			}
		}
	}

}