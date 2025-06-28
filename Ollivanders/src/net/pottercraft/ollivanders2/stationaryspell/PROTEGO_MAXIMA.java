package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hurts any entities within 0.5 meters of the spell wall.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Protego_Maxima">https://harrypotter.fandom.com/wiki/Protego_Maxima</a>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_MAXIMA}
 */
public class PROTEGO_MAXIMA extends ShieldSpell
{
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 30;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;
    /**
     * min damage for this spell - half the damage done by a wooden sword
     */
    public static final int minDamageConfig = 2;
    /**
     * max damage for this spell - damage done by a netherite sword
     */
    public static final int maxDamageConfig = 8;

    /**
     * The amount of damage to do to nearby entities
     */
    private double damage;

    /**
     * The label for the damage data for serializing
     */
    private final String damageLabel = "Damage";

    /**
     * The max cooldown for damaging entities
     */
    private final static int maxCooldown = (Ollivanders2Common.ticksPerSecond / 2);

    /**
     * The cooldown remaining until next damage check
     */
    private static int cooldown = 0;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_MAXIMA;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     * @param damage   the damage done to other entities in this spell area
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration, double damage)
    {
        super(plugin);
        spellType = O2StationarySpellType.PROTEGO_MAXIMA;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;

        setPlayerID(pid);
        setLocation(location);
        setRadius(radius);
        setDuration(duration);
        setDamage(damage);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell and damage any entities nearby
     */
    @Override
    public void checkEffect()
    {
        age();

        Collection<LivingEntity> nearbyEntities = EntityCommon.getLivingEntitiesInRadius(location, radius + 1);

        // only do damage twice per second
        if (cooldown <= 0)
        {
            for (LivingEntity e : nearbyEntities)
            {
                double distance = e.getLocation().distance(location);
                if (distance > radius - 0.5 && distance < radius + 0.5)
                {
                    e.damage(damage);
                    flair(10);
                }
            }

            cooldown = maxCooldown;
        }
        else
            cooldown = cooldown - 1;
    }

    /**
     * Serialize the damage level for this spell
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData()
    {
        Map<String, String> spellData = new HashMap<>();

        spellData.put(damageLabel, Double.toString(damage));

        return spellData;
    }

    /**
     * Deserialize the damage level for this spell
     *
     * @param spellData a map of the saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData)
    {
        for (Map.Entry<String, String> e : spellData.entrySet())
        {
            try
            {
                if (e.getKey().equals(damageLabel))
                {
                    double d = Double.parseDouble(e.getValue());
                    setDamage(d);
                }
            }
            catch (Exception exception)
            {
                common.printDebugMessage("Unable to read Protego Maxima damage", exception, null, false);
            }
        }
    }

    /**
     * Set the damage amount for this shield spell.
     *
     * @param damage the amount of damage the spell should do
     */
    private void setDamage(double damage)
    {
        if (damage < minDamageConfig)
            damage = minDamageConfig;
        else if (damage > maxDamageConfig)
            damage = maxDamageConfig;

        this.damage = damage;
    }
}