package Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;

public class MEMORY_POTION extends OEffect implements Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4202743924958098780L;

	public MEMORY_POTION(Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
	}

	@Override
	public void checkEffect(Ollivanders2 p, Player owner) {
		age(1);
	}

}
