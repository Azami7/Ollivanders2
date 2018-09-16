package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
   public O2SpellType spellType = O2SpellType.HERBIFORS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Flower-Hair Spell");
   }};

   protected String text = "Puts a flower on the target player's head.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HERBIFORS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HERBIFORS(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      materialType = Material.YELLOW_FLOWER;
   }
}
