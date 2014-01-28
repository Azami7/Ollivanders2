package Spell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Creates a pair of vanishing cabinets and teleports between them.
 * @author lownes
 *
 */
public class HARMONIA_NECTERE_PASSUS extends SpellProjectile implements Spell{

	public HARMONIA_NECTERE_PASSUS(Ollivanders plugin, Player player,
			Spells name, Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		kill();
		Location playerLoc = player.getLocation();
		Block feet = playerLoc.getBlock();
		if (!cabinetCheck(feet)){
			return;
		}
		if (feet.getState() instanceof Sign){
			Sign sign1 = (Sign)feet.getState();
			String[] lines1 = sign1.getLines();
			if (lines1.length == 4){
				for (int i = 0; i < 4; i++){
					if (lines1[i].contains("§k")){
						lines1[i] = lines1[i].substring(2);
					}
				}
				World toWorld = Bukkit.getWorld(lines1[0]);
				if (toWorld == null){
					return;
				}
				int toX;
				int toY;
				int toZ;
				try {
					toX = Integer.parseInt(lines1[1]);
					toY = Integer.parseInt(lines1[2]);
					toZ = Integer.parseInt(lines1[3]);
				} catch (NumberFormatException e) {
					return;
				}
				Location toLoc = new Location(toWorld, toX, toY, toZ, playerLoc.getYaw(), playerLoc.getPitch());
				if (!cabinetCheck(toLoc.getBlock())){
					return;
				}
				if (toLoc.getBlock().getState() instanceof Sign){
					Sign sign2 = (Sign)toLoc.getBlock().getState();
					String[] lines2 = sign2.getLines();
					if (lines2.length == 4){
						for (int i = 0; i < 4; i++){
							if (lines2[i].contains("§k")){
								lines2[i] = lines2[i].substring(2);
							}
						}
						World fromWorld = Bukkit.getWorld(lines2[0]);
						int fromX;
						int fromY;
						int fromZ;
						try {
							fromX = Integer.parseInt(lines2[1]);
							fromY = Integer.parseInt(lines2[2]);
							fromZ = Integer.parseInt(lines2[3]);
						} catch (NumberFormatException e) {
							return;
						}
						if (fromWorld != feet.getWorld() || fromX != feet.getX() || fromY != feet.getY() || fromZ != feet.getZ()){
							return;
						}
						toLoc.add(0.5, 0, 0.5);
						player.teleport(toLoc);
						for (int i = 0; i < 4; i++){
							sign2.setLine(i, "§k" + lines2[i]);
							sign2.update(true);
						}
					}
				}
				for (int i = 0; i < 4; i++){
					sign1.setLine(i, "§k" + lines1[i]);
					sign1.update(true);
				}
			}
		}
	}
	
	
	public boolean cabinetCheck(Block feet){
		if (feet.getType() == Material.WALL_SIGN || feet.getType() == Material.SIGN_POST){
			if (feet.getRelative(1, 0, 0).getType() == Material.AIR || 
					feet.getRelative(-1, 0, 0).getType() == Material.AIR || 
					feet.getRelative(0, 0, 1).getType() == Material.AIR || 
					feet.getRelative(0, 0, -1).getType() == Material.AIR || 
					feet.getRelative(1, 1, 0).getType() == Material.AIR || 
					feet.getRelative(-1, 1, 0).getType() == Material.AIR || 
					feet.getRelative(0, 1, 1).getType() == Material.AIR || 
					feet.getRelative(0, 1, -1).getType() == Material.AIR || 
					feet.getRelative(0, 2, 0).getType() == Material.AIR){
				return false;
				}
		}
		else{
			return false;
		}
		return true;
	}
}