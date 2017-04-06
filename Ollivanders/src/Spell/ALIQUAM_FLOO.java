package Spell;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpells;

/**Registers a new floo network entry
 * @author lownes
 *
 */
public class ALIQUAM_FLOO extends SpellProjectile implements Spell {

	public ALIQUAM_FLOO(Ollivanders2 plugin, Player player, Spells name,
                        Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@Override
	public void checkEffect() {
		move();
		if (super.getBlock().getType() == Material.FIRE){
			Location statLocation = new Location(location.getWorld(), super.getBlock().getX()+0.5, super.getBlock().getY()+0.125, super.getBlock().getZ()+0.5);
			if (super.getBlock().getRelative(BlockFace.UP).getType() == Material.WALL_SIGN){
				Sign sign = (Sign) super.getBlock().getRelative(BlockFace.UP).getState();
				String flooName = sign.getLine(0).trim() + " " + sign.getLine(1).trim() + " " + sign.getLine(2).trim() + " " + sign.getLine(3).trim();
				flooName = flooName.trim();
				flooName = flooName.toLowerCase();
				for (StationarySpellObj stat : p.getStationary()){
					if (stat instanceof StationarySpell.ALIQUAM_FLOO){
						StationarySpell.ALIQUAM_FLOO ali = (StationarySpell.ALIQUAM_FLOO) stat;
						if (ali.getFlooName().equals(flooName) || ali.getBlock().equals(statLocation.getBlock())){
							return;
						}
					}
				}
				StationarySpell.ALIQUAM_FLOO aliquam = new StationarySpell.ALIQUAM_FLOO(player, statLocation, StationarySpells.ALIQUAM_FLOO, 2, 10, flooName);
				aliquam.flair(20);
				p.addStationary(aliquam);
			}
			kill();
		}
	}

}
