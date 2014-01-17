package Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Effect;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Spawns magma cubes, blazes, and ghasts
 * @author lownes
 *
 */
public class FIENDFYRE extends SpellProjectile implements Spell{

	private double lifeTime;
	
	public FIENDFYRE(Ollivanders plugin, Player player, Spells name, Double rightWand) {
		super(plugin, player, name, rightWand);
		moveEffect = Effect.MOBSPAWNER_FLAMES;
		moveEffectData = 0;
		lifeTime = usesModifier*4;
	}

	public void checkEffect() {
		move();
		List<StationarySpellObj> stationaries = p.checkForStationary(location);
		for (StationarySpellObj stationary : stationaries){
			if (stationary.name.equals(StationarySpells.HORCRUX)){
				stationary.kill();
			}
		}
		if (lifeTicks > lifeTime){
			spawnCreatures();
			kill();
		}
		if (location.getBlock().getType() != Material.AIR){
			location.subtract(vector);
			spawnCreatures();
			kill();
		}
	}
	
	/**
	 * This spawns the magmacubes, blazes, and ghasts according to usesModifier
	 */
	private void spawnCreatures(){
		World world = location.getWorld();
		for (int x = 1; x < usesModifier; x++){
			world.spawnEntity(location, EntityType.MAGMA_CUBE);
		}
		for (int x = 1; x < usesModifier/5; x++){
			world.spawnEntity(location, EntityType.BLAZE);
		}
		for (int x = 1; x < usesModifier/10; x++){
			world.spawnEntity(location, EntityType.GHAST);
		}
	}
	
}