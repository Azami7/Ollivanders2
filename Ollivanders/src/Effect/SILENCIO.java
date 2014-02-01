package Effect;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.Ollivanders;

public class SILENCIO extends OEffect implements Effect{

	/**
	 * 
	 */
	private static final long serialVersionUID = 55811898971448690L;

	public SILENCIO(Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
	}

	public void checkEffect(Ollivanders p, Player owner) {
		age(1);
	}
	
}