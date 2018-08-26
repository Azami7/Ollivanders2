package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.ANIMAGUS_INCANTATION;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Amato Animo Animato Animagus is an incantation used in the process of becoming an Animagus. The spell alone is
 * not enough to change a player in to an Animagus. The spell has to be recited either at sunrise or sunset
 * and the player must drink the Animagus potion after saying the incantation. The potion must be consumed during
 * a lightning storm. Once the player has successfully become an Animagus it will require considerable practice
 * before they can consistently take the form.
 *
 * @since 2.2.6
 * @author Azami7
 * @link https://github.com/Azami7/Ollivanders2/issues/87
 */
public class AMATO_ANIMO_ANIMATO_ANIMAGUS extends Transfiguration
{
   /**
    * The percent chance this spell will succeed each casting.
    */
   protected int successRate = 0;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AMATO_ANIMO_ANIMATO_ANIMAGUS ()
   {
      super();

      flavorText.add("An Animagus is a wizard who elects to turn into an animal.");
      flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");

      text = "Becoming an Animagus takes practice, skill, and patience. The animagus incantation is the one of the "
            + "most difficult Transfiguration spells. The spell alone is not sufficient to transform the caster the "
            + "first time. You must drink the Animagus potion immediately after saying the incantation. Both the "
            + "incantation and the potion also have specific environmental requirements. The incantation must be said "
            + "at either sunrise or sunset. The potion must be consumed during a thunderstorm. One you have successfully "
            + "transformed, you no longer need the potion and can use the spell at any time, however it will take "
            + "considerable practice before you will be able to consistently change form.";
   }

   /**
    * Constructor.
    *
    * @param plugin
    * @param player
    * @param name
    */
   public AMATO_ANIMO_ANIMATO_ANIMAGUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      setSuccessRate();
   }

   /**
    * Set the successRate for this spell. If the player is not already an Animagus the spell needs to be
    * recited at the correct time of day. If they are already an Animagus, their success will depend
    * on their experience.
    */
   private void setSuccessRate ()
   {
      O2Player o2p = p.getO2Player(player);

      if (o2p.isAnimagus())
      {
         if (o2p.isTransformed())
         {
            // always allow a player to transform back
            successRate = 100;
         }
         else
         {
            int uses = (int)(usesModifier * 10);
            // set success rate based on their experience
            if (uses < 25)
               successRate = 5;
            else if (uses < 100)
               successRate = 10;
            else if (uses < 200)
               successRate = uses / 2;
            else
               successRate = 100;
         }
      }
      else
      {
         // player is not an animagus, set success based on if this was cast at sunrise/sunset
         long curTime = player.getWorld().getTime();
         if ((curTime >= 23000 && curTime <= 24000) || (curTime >=12000 && curTime <= 13000))
         {
            // it is sunrise or sunset
            successRate = 100;
         }
         else
         {
            player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
                  + "Nothing seems to happen.");
         }
      }
   }

   /**
    * Perform the Animagus spell
    */
   @Override
   public void checkEffect ()
   {
      kill();

      O2Player o2p = p.getO2Player(player);

      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);

      if (rand < successRate)
      {
         if (o2p.isAnimagus())
         {
            o2p.animagusForm();
         }
         else
         {
            o2p.addEffect(new ANIMAGUS_INCANTATION(p, O2EffectType.ANIMAGUS_INCANTATION, 300));
            player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
                  + "You feel slightly different.");

            if (Ollivanders2.debug)
               p.getLogger().info("Adding effect ANIMAGUS_INCANTATION");
         }
      }
   }
}
