package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * This spell places a flower on the target player's head.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class HERBIFORS extends GaleatiSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HERBIFORS()
   {
      super();

      flavorText.add("The Flower-Hair Spell");
      text = "Puts a flower on the target player's head.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public HERBIFORS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      materialType = Material.YELLOW_FLOWER;
   }
}
