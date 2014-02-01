package Effect;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.Ollivanders;

public class MUCUS_AD_NAUSEAM extends OEffect implements Effect{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8685445385963186772L;

	public MUCUS_AD_NAUSEAM(Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
	}

	public void checkEffect(Ollivanders p, Player owner) {
		age(1);
		if (duration%20 == 0){
			World world = owner.getWorld();
			Slime slime = (Slime) world.spawnEntity(owner.getEyeLocation(), EntityType.SLIME);
			slime.setSize(1);
		}
	}
	
}