package Effect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import me.cakenggt.Ollivanders.Effects;
import me.cakenggt.Ollivanders.OEffect;
import me.cakenggt.Ollivanders.Ollivanders;

/**Turns player into a werewolf during the full moon. Doesn't go away until death.
 * @author lownes
 *
 */
public class LYCANTHROPY extends OEffect implements Effect {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7925837173707212516L;
	int wereId = -1;

	public LYCANTHROPY(Player sender, Effects effect, int duration) {
		super(sender, effect, duration);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void checkEffect(Ollivanders p, Player owner) {
		long time = owner.getWorld().getFullTime();
		long dayOrNight = (time/12000)%2;
		long days=time/24000;
		long phase=days%8;
		if (phase == 0 && dayOrNight == 1){
			//Full moon at night
			if (wereId == -1){
				//spawn werewolf and set wereId and set wolf name to werewolf and set
				//it to be angry
				Wolf werewolf = (Wolf) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.WOLF);
				werewolf.setAngry(true);
				werewolf.setCustomName("Werewolf");
				werewolf.setCustomNameVisible(true);
				wereId = werewolf.getEntityId();
			}
			else{
				//see if wereId points to a wolf with name werewolf.
				//If so, teleport to it. if not, kill player
				for (Entity entity : owner.getWorld().getEntities()){
					if (entity.getEntityId() == wereId && entity.getType() == EntityType.WOLF){
						Wolf wolf = (Wolf)entity;
						if (wolf.getCustomName().equals("Werewolf")){
							owner.teleport(entity);
							if (!wolf.isAngry()){
								wolf.setAngry(true);
							}
							if(wolf.getTarget() == owner){
								wolf.setTarget(null);
							}
							if (time%20 == 0) {
								for (Player other : owner.getWorld().getPlayers()) {
									other.hidePlayer(owner);
								}
							}
							return;
						}
					}
				}
				owner.damage(1000.0);
			}
		}
		else{
			//if wereId points to a wolf with the name werewolf, kill it and
			//set wereId to -1
			if (wereId != -1){
				for (Player other : owner.getWorld().getPlayers()){
					other.showPlayer(owner);
				}
				for (Entity entity : owner.getWorld().getEntities()){
					if (entity.getEntityId() == wereId && entity.getType() == EntityType.WOLF){
						if (((Wolf)entity).getCustomName().equals("Werewolf")){
							entity.remove();
							wereId = -1;
							return;
						}
					}
				}
				wereId = -1;
			}
		}
	}

}
