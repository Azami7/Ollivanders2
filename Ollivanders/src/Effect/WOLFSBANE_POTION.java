package Effect;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.Ollivanders;

public class WOLFSBANE_POTION extends OEffect implements Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7153440938645339899L;

	public WOLFSBANE_POTION(Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
	}

	@Override
	public void checkEffect(Ollivanders p, Player owner) {
		age(1);
	}

}
