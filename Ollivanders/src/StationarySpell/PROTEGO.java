package StationarySpell;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.cakenggt.Ollivanders.OLocation;
import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.OllivandersListener;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

public class PROTEGO extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8133251737599676134L;

	public PROTEGO(Player player, Location location, StationarySpells name,
			Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		Player ply = Bukkit.getPlayer(getPlayerUUID());
		if (ply == null){
			kill();
			return;
		}
		double rightWand = OllivandersListener.wandCheck(ply);
		if (ply.isSneaking() && rightWand != -1){
			location = new OLocation(ply.getEyeLocation());
			flair(1);
			List<SpellProjectile> projectiles = p.getProjectiles();
			if (projectiles != null){
				for (SpellProjectile proj : projectiles){
					if (isInside(proj.location)){
						if (location.toLocation().distance(proj.location) > radius-1){
							Vector N = proj.location.toVector().subtract(location.toLocation().toVector()).normalize();
							//reflect it
							//b * ( -2*(V dot N)*N + V )
							double b = p.getSpellNum(ply, Spells.PROTEGO)/rightWand/10.0;
							b += 1;
							Vector V = proj.vector.clone();
							proj.vector = N.multiply((V.dot(N))).multiply(-2).add(V).multiply(b);
							flair(10);
						}
					}
				}
			}
		}
		else{
			kill();
		}
	}
	
}