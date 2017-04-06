package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**Shoots a red firework up.
 * @author lownes
 *
 */
public class PERICULUM extends SpellProjectile implements Spell{

	private double lifeTime;
	
	@SuppressWarnings("deprecation")
	public PERICULUM(Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*6;
		moveEffectData = Material.REDSTONE_BLOCK.getId();
	}

	public void checkEffect() {
		move();
		if (lifeTicks > lifeTime){
			Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
			FireworkMeta meta = firework.getFireworkMeta();
			FireworkEffect.Builder builder = FireworkEffect.builder();
			builder.withColor(Color.RED);
			builder.with(Type.BURST);
			meta.addEffect(builder.build());
			firework.setFireworkMeta(meta);
			firework.detonate();
			kill();
		}
	}
	
}