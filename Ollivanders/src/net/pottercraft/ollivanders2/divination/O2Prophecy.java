package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Class representing a prophecy. Every prophecy involves predicting an effect on a player in the future and
 * has a specific accuracy which changes the likelihood the prophecy will come to pass.
 */
public class O2Prophecy
{
    /**
     * A reference to the plugin
     */
    final private Ollivanders2 p;

    /**
     * The effect that will happen to the player
     */
    private final O2EffectType effectType;

    /**
     * The player that will be affected
     */
    private final UUID targetID;

    /**
     * The player that made the prophecy
     */
    private final UUID prophetID;

    /**
     * The time until the prophecy will come to pass, in game ticks - one game tick is 1/20 of a second
     */
    private long time;

    /**
     * The duration, in game ticks, for this prophecy
     */
    private final int duration;

    /**
     * The percent accuracy of this prophecy
     */
    private final int accuracy;

    /**
     * The message of this prophecy
     */
    private final String prophecyMessage;

    /**
     * Whether this prophecy has expired
     */
    private boolean kill = false;

    /**
     * Constructor
     *
     * @param effectType     the effect that will happen to the player
     * @param targetID       the id of the player that will be affected
     * @param prophetID      the id of the player that made this prophecy
     * @param delayTime      the time, in game ticks, until the prophecy will come to pass, less than 1200 (1 minute) will be rounded up to 1200
     * @param effectDuration the duration of the effect, 0 for permanent
     * @param accuracy       the accuracy of this prophecy as a percent from 0 to 99, greater than 99 will be rounded down to 99
     */
    O2Prophecy(@NotNull Ollivanders2 plugin, @NotNull O2EffectType effectType, @NotNull String message, @NotNull UUID targetID, @NotNull UUID prophetID, long delayTime, int effectDuration, int accuracy)
    {
        p = plugin;
        this.effectType = effectType;
        this.targetID = targetID;
        this.prophetID = prophetID;
        this.prophecyMessage = message;
        this.time = delayTime;
        this.duration = effectDuration;

        if (accuracy > 99)
            this.accuracy = 99;
        else if (accuracy < 0)
            this.accuracy = 0;
        else
            this.accuracy = accuracy;
    }

    /**
     * Get the effect this prophecy causes.
     *
     * @return the effect type
     */
    @NotNull
    public O2EffectType getEffect()
    {
        return effectType;
    }

    /**
     * Get the ID of target player for this prophecy
     *
     * @return the target player's unique ID
     */
    @NotNull
    public UUID getTargetID()
    {
        return targetID;
    }

    /**
     * Get the ID of player who made this prophecy
     *
     * @return the target player's unique ID
     */
    @NotNull
    UUID getProphetID()
    {
        return prophetID;
    }

    /**
     * Get the time until this prophecy happens
     *
     * @return the time in game ticks
     */
    public long getTime()
    {
        return time;
    }

    /**
     * Get the duration of the effect from this prophecy
     *
     * @return the duration in game ticks
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * Get the prophecy message, ie. "After the sun sets on the 3rd day, Fred will fall in to a deep sleep."
     *
     * @return the prophecy message
     */
    String getProphecyMessage()
    {
        return prophecyMessage;
    }

    /**
     * Get the percent accuracy of this prophecy
     *
     * @return the accuracy percent 0-99
     */
    int getAccuracy()
    {
        return accuracy;
    }

    /**
     * Is this prophecy expired/killed
     *
     * @return true if killed, false otherwise
     */
    boolean isKilled()
    {
        return kill;
    }

    /**
     * Age this prophecy 1 game tick
     */
    public void age()
    {
        time = time - 1;
    }

    /**
     * Kill this prophecy
     */
    public void kill()
    {
        kill = true;
    }

    /**
     * Execute this prophecy.
     */
    void fulfill()
    {
        if (Ollivanders2.debug)
        {
            p.getLogger().info("Fulfilling prophecy");
        }

        // this should only be called when the prophecy time has expired
        if (kill)
        {
            return;
        }

        Player target = p.getServer().getPlayer(targetID);

        if (target == null)
        {
            // player is offline, stash this prophecy for when the player returns
            Ollivanders2API.getProphecies().addOfflineProphecy(this);
            return;
        }

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        if (accuracy > rand)
        {
            O2Effect effect;
            Class<?> effectClass = effectType.getClassName();

            try
            {
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, duration, targetID);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                kill();
                return;
            }

            effect.setPermanent(false);
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            O2Player player = Ollivanders2API.getPlayers().getPlayer(prophetID);
            if (player != null)
            {
                String playerName = player.getPlayerName();
                p.getServer().broadcastMessage(Ollivanders2.chatColor + "And so came to pass the prophecy of " + playerName + ", \"" + prophecyMessage + "\"");
            }
        }
        else
        {
            Player prophet = p.getServer().getPlayer(prophetID);
            if (prophet != null)
                prophet.sendMessage(Ollivanders2.chatColor + "Your prophecy, \"" + prophecyMessage + "\" did not come to pass.");
        }

        kill();
    }
}
