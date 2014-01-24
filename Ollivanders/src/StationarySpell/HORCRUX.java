package StationarySpell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Player will spawn here when killed, with all of their spell levels intact.
 * Only fiendfyre can destroy it.
 * @author lownes
 *
 */
public class HORCRUX extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5607024325100902862L;

	public HORCRUX(Player player, Location location,
			StationarySpells name, Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		List<LivingEntity> entities = getLivingEntities();
		for(LivingEntity entity : entities){
			if (entity instanceof Player){
				if (((Player)entity).getName() != player){
					PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 200, 2);
					PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 200, 3);
					entity.addPotionEffect(blindness);
					entity.addPotionEffect(wither);
				}
			}
		}
	}

}