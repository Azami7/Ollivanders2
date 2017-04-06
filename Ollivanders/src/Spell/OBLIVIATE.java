package Spell;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Decreases all of target player's spell levels by the caster's level
 * in obliviate.
 * @author lownes
 *
 */
public class OBLIVIATE extends SpellProjectile implements Spell{

	public OBLIVIATE(Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		int i = spellUses;
		List<LivingEntity> entities = getLivingEntities(1);
		for (Entity entity : entities){
			if (entity instanceof Player){
				Player ply = (Player)entity;
				for (Spells spell : Spells.values()){
					int know = p.getSpellNum(ply, spell);
					int to = know - i;
					if (to < 0){
						to = 0;
					}
					p.setSpellNum(ply, spell, to);
				}
				kill();
			}
		}
	}
	
}