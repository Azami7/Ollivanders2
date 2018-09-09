package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.Material;
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
   public HERBIFORS(O2SpellType type)
   {
      super(type);

      flavorText.add("The Flower-Hair Spell");
      text = "Puts a flower on the target player's head.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public HERBIFORS(Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      materialType = Material.YELLOW_FLOWER;
   }
}
