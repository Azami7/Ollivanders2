package Effect;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.Ollivanders;

/**Grants player flight
 * @author lownes
 *
 */
public class VENTO_FOLIO extends OEffect implements Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2762063364036796900L;

	public VENTO_FOLIO(org.bukkit.entity.Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
	}

	@Override
	public void checkEffect(Ollivanders p, org.bukkit.entity.Player owner) {
		age(1);
		if (duration > 1){
			owner.setAllowFlight(true);
			owner.getWorld().playEffect(owner.getLocation(), org.bukkit.Effect.SMOKE, 4);
		}
		else{
			owner.setAllowFlight(false);
		}
	}

}
