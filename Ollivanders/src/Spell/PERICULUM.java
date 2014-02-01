package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Shoots a red firework up.
 * @author lownes
 *
 */
public class  PERICULUM extends SpellProjectile implements Spell{

	public PERICULUM(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		FireworkEffect.Builder builder = FireworkEffect.builder();
		builder.withColor(Color.RED);
		builder.with(Type.BURST);
		meta.addEffect(builder.build());
		meta.setPower((int) usesModifier);
		firework.setFireworkMeta(meta);
		kill();
	}
	
}