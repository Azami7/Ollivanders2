package Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * If a coreless wand is near enough a core material, makes a wand of
 * the wand wood type and core type. Itemstack amount is 1 regardless of
 * how many items were in either starting stack.
 * @author lownes
 *
 */
public class LIGATIS_COR extends SpellProjectile implements Spell{

	public LIGATIS_COR(Ollivanders p, Player player, Spells name, Integer rightWand){
		super(p, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Item> items = super.getItems(1);
		for (Item item : items){
			ItemMeta corM = item.getItemStack().getItemMeta();
			String[] woodTypes = {"Oak","Spruce","Birch","Jungle"};
			if (corM.hasLore()){
				if (Arrays.asList(woodTypes).contains(corM.getLore().get(0))){
					List<Entity> entities = item.getNearbyEntities(2, 2, 2);
					for (Entity e : entities){
						if (e instanceof Item){
							Item e2 = (Item)e;
							Material mat = e2.getItemStack().getType();
							Material[] cores = {Material.SPIDER_EYE,Material.ROTTEN_FLESH,Material.BONE,Material.SULPHUR};
							if (Arrays.asList(cores).contains(mat)){
								Map<Material, String> matMap = new HashMap<Material, String>();
								matMap.put(Material.SPIDER_EYE, "Spider Eye");
								matMap.put(Material.ROTTEN_FLESH, "Rotten Flesh");
								matMap.put(Material.BONE, "Bone");
								matMap.put(Material.SULPHUR, "Gunpowder");
								String lore = corM.getLore().get(0);
								lore = lore.concat(" and ");
								lore = lore.concat(matMap.get(mat));
								System.out.println(lore);
								corM.setDisplayName("Wand");
								List<String> loreL = new ArrayList<String>();
								loreL.add(lore);
								corM.setLore(loreL);
								ItemStack coreStack = item.getItemStack();
								coreStack.setAmount(1);
								coreStack.setItemMeta(corM);
								item.setItemStack(coreStack);
								e2.remove();
							}
						}
					}
				}
			}
		}
	}
}