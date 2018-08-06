package net.pottercraft.Ollivanders2.Effect;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Causes a player to chat nonsense.
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BABBLING_EFFECT extends OEffect implements Effect
{
   public final ArrayList<String> dictionary = new ArrayList<String>() {{
      add("mimble");
   }};

   public BABBLING_EFFECT (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }
}
