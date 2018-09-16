package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Transfigures a rotten flesh into inferi
 *
 * @author lownes
 * @author Azami7
 */
public final class MORTUOS_SUSCITATE extends Transfiguration
{
   protected O2MagicBranch branch = O2MagicBranch.DARK_ARTS;
   public O2SpellType spellType = O2SpellType.MORTUOS_SUSCITATE;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("They are corpses, dead bodies that have been bewitched to do a Dark wizard's bidding. Inferi have not been seen for a long time, however, not since Voldemort was last powerful... He killed enough people to make an army of them, of course.");
   }};

   protected String text = "Mortuos Suscitate will transfigure a piece of rotten flesh into an Inferius. The Inferius will not attack it's owner.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MORTUOS_SUSCITATE () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MORTUOS_SUSCITATE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      if (!hasTransfigured())
      {
         move();
         for (Item item : getItems(1))
         {
            if (item.getItemStack().getType() == Material.ROTTEN_FLESH)
            {
               Zombie inferi = (Zombie) transfigureEntity(item, EntityType.ZOMBIE, null);
               inferi.setCustomName("Inferius");
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            endTransfigure();
         }
         else
         {
            lifeTicks++;
         }
      }
   }
}
