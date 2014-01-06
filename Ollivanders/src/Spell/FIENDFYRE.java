package Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Effect;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Spawns other fiendfyre projectiles with a decreasing chance as the spell
 * ages. Projectiles set entities on fire for one minute. They also can
 * destroy horcrux stationary spell objects. If a player is under level 100
 * in the spell when they cast it, they will be set on fire.
 * @author lownes
 *
 */
public class FIENDFYRE extends SpellProjectile implements Spell{

	public FIENDFYRE(Ollivanders plugin, Player player, Spells name, Double rightWand) {
		super(plugin, player, name, rightWand);
		moveEffect = Effect.MOBSPAWNER_FLAMES;
		moveEffectData = 0;
	}
	
	public FIENDFYRE(SpellProjectile s){
		super(s.p, s.player, s.name, s.rightWand);
		location = s.location.clone();
		vector = s.vector.clone();
		lifeTicks = s.lifeTicks;
		kill = s.kill;
		moveEffect = Effect.MOBSPAWNER_FLAMES;
		moveEffectData = 0;
	}

	public void checkEffect() {
		move();
		spawner();
		if (spellUses < 100){
			player.setFireTicks(12000);
		}
		List<Item> items = getItems(1);
		for (Item item : items){
			item.setFireTicks(12000);
		}
		List<LivingEntity> living = getLivingEntities(1);
		for (LivingEntity live : living){
			live.setFireTicks(12000);
		}
		if (getBlock().getType() == Material.AIR){
			getBlock().setType(Material.FIRE);
		}
		List<StationarySpellObj> stationaries = p.checkForStationary(location);
		for (StationarySpellObj stationary : stationaries){
			if (stationary.name.equals(StationarySpells.HORCRUX)){
				stationary.kill();
			}
		}
	}
	
	/**
	 * This spawns a new FIENDFYRE if the chance is high enough
	 */
	private void spawner() {
		if (lifeTicks*Math.random() < 1){
			int spellUses = p.getSpellNum(player, name);
			FIENDFYRE newFire = new FIENDFYRE(copy());
			p.setSpellNum(player, name, spellUses);
			double inc = Math.acos(vector.getZ());
			double azi = Math.atan2(vector.getY(),vector.getX());
			double ranInc = 1/Math.sqrt(spellUses)*2.4*rightWand;
			double ranAzi = 1/Math.sqrt(spellUses)*2.4*rightWand;
			inc = (inc - (ranInc/2)) + (Math.random()*ranInc);
			azi = (azi - (ranAzi/2)) + (Math.random()*ranAzi);
			double x = Math.sin(inc)*Math.cos(azi);
			double y = Math.sin(inc)*Math.sin(azi);
			double z = Math.cos(inc);
			newFire.vector = newFire.vector.setX(x);
			newFire.vector = newFire.vector.setY(y);
			newFire.vector = newFire.vector.setZ(z);
			p.addProjectile(newFire);
		}
	}
}