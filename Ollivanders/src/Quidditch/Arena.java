package Quidditch;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getLogger;

/**
 * @author autumnwoz
 */
public class Arena {
    private final String name;
    private final Location center;
    private ArenaState state;
    private Size size;

    /**
     * The sizes of the arenas
     */
    private final int MEDIUM_X_SIZE = 41;
    private final int MEDIUM_Y_SIZE = 60;
    private final int LARGE_X_SIZE = 0;
    private final int LARGE_Y_SIZE = 0;


    /**
     * @param name
     *            The name of the arena
     * @param center
     *            The center of the arena
     */
    public Arena(String name, Location center, Size size) {
        this.name = name;
        this.center = center;
        this.state = ArenaState.FREE;
        this.size = size;
        buildArena();
    }

    /**
     * @return The name of the arena
     */
    public String getName() {
        return name;
    }

    /**
     * @return The center of the arena
     */
    public Location getCenter() {
        return center;
    }

    /**
     * @return The {@link Size}
     */
    public Size getSize() {
        return size;
    }

    /**
     * @return The {@link ArenaState}
     */
    public ArenaState getState() {
        return state;
    }

    /**
     * Start the game
     */
    public void start() {
        state = ArenaState.RUNNING;
    }

    /**
     * Ends the game
     */
    public void endGame() {
        state = ArenaState.FREE;
    }

    /**
     * @param e
     *            The {@link BlockBreakEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (isInside(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    /**
     * @param e
     *            The {@link BlockPlaceEvent}
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isInside(e.getBlock().getLocation())) {
            e.setBuild(false);
        }
    }

    /**
     * @param e
     *            The {@link BlockPistonExtendEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonPush(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) {
            if (isInside(block.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }

    }

    /**
     * @param e
     *            The {@link BlockPistonRetractEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonPull(BlockPistonRetractEvent e) {
        Block block = e.getBlock();
        block = block.getRelative(e.getDirection().getOppositeFace(), 2);
        if (isInside(block.getLocation())) {
            e.setCancelled(true);
        }
    }

    /**
     * @param e
     *            The {@link EntityExplodeEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            if (isInside(block.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "[" + "name=" + name + ", center=" + center + ", size=" + size + "]";
    }

    private Boolean isInside(Location location) {
        if (size == Size.MEDIUM) {
            if (Math.abs(location.getBlockX() - center.getBlockX()) <= MEDIUM_X_SIZE) {
                if (Math.abs(location.getBlockY() - center.getBlockY()) <= MEDIUM_Y_SIZE) {
                    return true;
                }
            }
        } else if (size == Size.LARGE) {
            if (Math.abs(location.getBlockX() - center.getBlockX()) <= LARGE_X_SIZE) {
                if (Math.abs(location.getBlockY() - center.getBlockY()) <= LARGE_Y_SIZE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Build the arena using WorldEdit
     */
    private void buildArena(){
        double x = center.getX();
        double y = center.getY() - 1;
        double z = center.getZ();
        BukkitWorld world = new BukkitWorld(center.getWorld());

        if (size == Size.MEDIUM) {
            ArrayList<CuboidRegion> areas = new ArrayList<>();

            areas.add(createRegion(x - 41, y, z + 16, x - 41, y, z - 16, world));
            areas.add(createRegion(x - 40, y, z + 22, x - 37, y, z - 22, world));
            areas.add(createRegion(x - 36, y, z + 36, x - 33, y, z - 36, world));
            areas.add(createRegion(x - 32, y, z + 42, x - 29, y, z - 42, world));
            areas.add(createRegion(x - 28, y, z + 48, x - 25, y, z - 48, world));
            areas.add(createRegion(x - 24, y, z + 52, x - 21, y, z - 52, world));
            areas.add(createRegion(x - 20, y, z + 48, x - 17, y, z - 48, world));
            areas.add(createRegion(x - 16, y, z + 46, x - 13, y, z - 46, world));
            areas.add(createRegion(x - 12, y, z + 45, x - 9, y, z - 45, world));
            areas.add(createRegion(x - 8, y, z + 44, x - 5, y, z - 44, world));
            areas.add(createRegion(x - 4, y, z + 44, x - 1, y, z - 44, world));
            areas.add(createRegion(x, y, z + 44, x + 3, y, z - 44, world));
            areas.add(createRegion(x + 4, y, z + 44, x + 7, y, z - 44, world));
            areas.add(createRegion(x + 8, y, z + 45, x + 11, y, z - 45, world));
            areas.add(createRegion(x + 12, y, z + 47, x + 15, y, z - 47, world));
            areas.add(createRegion(x + 16, y, z + 49, x + 19, y, z - 49, world));
            areas.add(createRegion(x + 20, y, z + 51, x + 23, y, z - 51, world));
            areas.add(createRegion(x + 24, y, z + 47, x + 27, y, z - 47, world));
            areas.add(createRegion(x + 28, y, z + 41, x + 31, y, z - 41, world));
            areas.add(createRegion(x + 32, y, z + 34, x + 35, y, z - 34, world));
            areas.add(createRegion(x + 36, y, z + 16, x + 39, y, z - 16, world));

            EditSessionFactory sessionFactory = WorldEdit.getInstance().getEditSessionFactory();
            EditSession session = sessionFactory.getEditSession(areas.get(0).getWorld(), -1);
            try {
                int i = 0;
                for (CuboidRegion reg : areas) {
                    if (i % 2 == 0) {
                        session.setBlocks(reg, new BaseBlock(35, 13));
                    } else {
                        session.setBlocks(reg, new BaseBlock(2));
                    }
                    i++;
                }
                session.setBlocks(createRegion(x + 7, y, z - 45, x + 7, y, z - 45, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 10, y, z - 46, x + 11, y, z - 46, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 14, y, z - 48, x + 15, y, z - 48, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 17, y, z - 50, x + 19, y, z -50, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 18, y, z - 51, x + 19, y, z - 51, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 19, y, z - 52, x + 19, y, z - 52, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 20, y, z - 53, x + 20, y, z - 53, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 20, y, z - 52, x + 22, y, z - 52, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 24, y, z - 50, x + 24, y, z - 50, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 24, y, z - 49, x + 25, y, z - 49, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 24, y, z - 48, x + 26, y, z - 48, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 28, y, z - 42, x + 28, y, z - 46, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 29, y, z - 44, x + 29, y, z - 42, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 30, y, z - 42, x + 30, y, z - 42, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 32, y, z - 39, x + 32, y, z - 35, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 33, y, z - 35, x + 33, y, z - 38, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 34, y, z - 36, x + 34, y, z - 35, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 36, y, z - 30, x + 36, y, z - 17, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 37, y, z - 17, x + 37, y, z - 26, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 38, y, z - 22, x + 38, y, z - 17, world), new BaseBlock(35, 13));

                session.setBlocks(createRegion(x + 7, y, z + 45, x + 7, y, z + 45, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 10, y, z + 46, x + 11, y, z + 46, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 14, y, z + 48, x + 15, y, z + 48, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 17, y, z + 50, x + 19, y, z + 50, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 18, y, z + 51, x + 19, y, z + 51, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 19, y, z + 52, x + 19, y, z + 52, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 20, y, z + 53, x + 20, y, z + 53, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 20, y, z + 52, x + 22, y, z + 52, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 24, y, z + 50, x + 24, y, z + 50, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 24, y, z + 49, x + 25, y, z + 49, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 24, y, z + 48, x + 26, y, z + 48, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 28, y, z + 42, x + 28, y, z + 46, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 29, y, z + 44, x + 29, y, z + 42, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 30, y, z + 42, x + 30, y, z + 42, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 32, y, z + 39, x + 32, y, z + 35, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 33, y, z + 35, x + 33, y, z + 38, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 34, y, z + 36, x + 34, y, z + 35, world), new BaseBlock(2));
                session.setBlocks(createRegion(x + 36, y, z + 30, x + 36, y, z + 17, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 37, y, z + 17, x + 37, y, z + 26, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x + 38, y, z + 22, x + 38, y, z + 17, world), new BaseBlock(35, 13));

                session.setBlocks(createRegion(x - 12, y, z - 46, x - 12, y, z - 46, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 14, y, z - 47, x - 16, y, z - 47, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 16, y, z - 48, x - 16, y, z - 48, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 18, y, z - 49, x - 20, y, z - 49, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 20, y, z - 50, x - 19, y, z - 50, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 20, y, z - 51, x - 20, y, z - 51, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 22, y, z - 53, x - 22, y, z - 53, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 25, y, z - 51, x - 25, y, z - 51, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 25, y, z - 50, x - 26, y, z - 50, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 27, y, z - 49, x - 25, y, z - 49, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 29, y, z - 47, x - 29, y, z - 43, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 30, y, z - 43, x - 30, y, z - 45, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 31, y, z - 44, x - 31, y, z - 43, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 33, y, z - 41, x - 33, y, z - 37, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 34, y, z - 37, x - 34, y, z - 39, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 35, y, z - 38, x - 35, y, z - 37, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 37, y, z - 34, x - 37, y, z - 23, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 38, y, z - 23, x - 38, y, z - 30, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 39, y, z - 26, x - 39, y, z - 23, world), new BaseBlock(2));

                session.setBlocks(createRegion(x - 12, y, z + 46, x - 12, y, z + 46, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 14, y, z + 47, x - 16, y, z + 47, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 16, y, z + 48, x - 16, y, z + 48, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 18, y, z + 49, x - 20, y, z + 49, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 20, y, z + 50, x - 19, y, z + 50, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 20, y, z + 51, x - 20, y, z + 51, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 22, y, z + 53, x - 22, y, z + 53, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 25, y, z + 51, x - 25, y, z + 51, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 25, y, z + 50, x - 26, y, z + 50, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 27, y, z + 49, x - 25, y, z + 49, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 29, y, z + 47, x - 29, y, z + 43, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 30, y, z + 43, x - 30, y, z + 45, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 31, y, z + 44, x - 31, y, z + 43, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 33, y, z + 41, x - 33, y, z + 37, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 34, y, z + 37, x - 34, y, z + 39, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 35, y, z + 38, x - 35, y, z + 37, world), new BaseBlock(35, 13));
                session.setBlocks(createRegion(x - 37, y, z + 34, x - 37, y, z + 23, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 38, y, z + 23, x - 38, y, z + 30, world), new BaseBlock(2));
                session.setBlocks(createRegion(x - 39, y, z + 26, x - 39, y, z + 23, world), new BaseBlock(2));
            } catch(MaxChangedBlocksException e) {
                return;
            }
        } else  if (size == Size.LARGE) {
            return;
        }
    }

    private CuboidRegion createRegion(double x1, double y1, double z1, double x2, double y2, double z2, BukkitWorld world) {
        Vector v1 = new Vector(x1, y1, z1);
        Vector v2 = new Vector(x2, y2, z2);
        CuboidRegion region = new CuboidRegion(v1, v2);
        region.setWorld(world);
        return region;
    }

    /**
     * The state of the arena
     */
    public static enum ArenaState {
        /**
         * The arena is ready to play a new game.
         */
        FREE,
        /**
         * A game is currently being played in the arena.
         */
        RUNNING;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    /**
     * The state of the arena
     */
    public static enum Size {
        /**
         * A medium sized arena.
         */
        MEDIUM,
        /**
         * A large sized arena.
         */
        LARGE;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
