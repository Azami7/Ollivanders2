package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Spells that create extra dimensions of space.
 */
public abstract class ExtraDimensional extends O2StationarySpell
{
   int dimenRadius;
   private Location edLoc;

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public ExtraDimensional(@NotNull Ollivanders2 plugin)
   {
      super(plugin);
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public ExtraDimensional(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration, int dimenRadius)
   {
      super(plugin, pid, location, type, radius, duration);
      this.dimenRadius = dimenRadius;
      createSpace();
   }

   /**
    * Creates the space in the extra dimension for this spell area.
    */
   private void createSpace ()
   {
      boolean spaceFree = false;
      Location loc = null;
      createLoop:
      while (!spaceFree)
      {
         double x = (int) (29000000 - Math.random() * 58000000) + 0.5;
         double z = (int) (29000000 - Math.random() * 58000000) + 0.5;
         double y = 230.0;
         loc = new Location(location.getWorld(), x, y, z);
         for (Block block : getBlocksInCube(loc))
         {
            if (block.getType() != Material.AIR)
            {
               continue createLoop;
            }
         }
         spaceFree = true;
      }
      edLoc = loc;
      for (Block block : getBlocksInCube(loc))
      {
         block.setType(Material.BEDROCK);
      }
      for (Block block : Ollivanders2API.common.getBlocksInRadius(loc, dimenRadius))
      {
         block.setType(Material.AIR);
      }
      loc.getBlock().setType(Material.BEDROCK);
   }

   /**
    * Get the blocks in the radius of the location
    *
    * @param loc the location
    * @return the set of blocks
    */
   @NotNull
   private Set<Block> getBlocksInCube(@NotNull Location loc)
   {
      Block center = loc.getBlock();
      int blockRadius = dimenRadius + 1;
      Set<Block> blockList = new HashSet<>();
      for (int x = -blockRadius; x <= blockRadius; x++)
      {
         for (int y = -blockRadius; y <= blockRadius; y++)
         {
            for (int z = -blockRadius; z <= blockRadius; z++)
            {
               blockList.add(center.getRelative(x, y, z));
            }
         }
      }
      return blockList;
   }

   @Override
   public void kill ()
   {
      flair(20);
      kill = true;
      for (Block block : getBlocksInCube(edLoc))
      {
         if (block.getType() != Material.BEDROCK)
         {
            World world = location.getWorld();
            if (world == null)
            {
               p.getLogger().warning("ExtraDimensional.kill: world is null");
               return;
            }

            for (ItemStack stack : block.getDrops())
            {
               world.dropItem(location, stack);
            }
            BlockState state = block.getState();
            if (state instanceof InventoryHolder)
            {
               for (ItemStack stack : ((InventoryHolder) state).getInventory())
               {
                  if (stack != null)
                  {
                     world.dropItem(location, stack);
                  }
               }
            }
            block.setType(Material.AIR);
         }
      }

      World edWorld = edLoc.getWorld();
      if (edWorld == null)
      {
         p.getLogger().warning("ExtraDimensional.kill: world is null");
         return;
      }

      for (Entity entity : edWorld.getEntities())
      {
         if (entity.getLocation().distance(edLoc) < dimenRadius)
         {
            entity.teleport(location);
         }
      }
   }

   /**
    * Returns the location of the spell in the extra dimension.
    */
   protected Location getEDLoc ()
   {
      return edLoc;
   }
}
