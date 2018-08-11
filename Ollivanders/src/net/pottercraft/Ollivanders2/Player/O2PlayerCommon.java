package net.pottercraft.Ollivanders2.Player;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;

enum Year {
   YEAR_1,
   YEAR_2,
   YEAR_3,
   YEAR_4,
   YEAR_5,
   YEAR_6,
   YEAR_7
}

public final class O2PlayerCommon
{
   public O2PlayerCommon () { }

   /**
    * Wand wood types
    */
   static final ArrayList<String> woodArray = new ArrayList<String>() {{
      add("Spruce");
      add("Jungle");
      add("Birch");
      add("Oak");
   }};

   /**
    * Wand core types
    */
   static final ArrayList<String> coreArray = new ArrayList<String>() {{
      add("Spider Eye");
      add("Bone");
      add("Rotten Flesh");
      add("Gunpowder");
   }};

   static String wandLoreConjunction = " and ";

   /**
    * Get all possible animagus shapes
    *
    * @return a list of all possible EntityTypes for animagus form
    */
   static ArrayList<EntityType> getAnimagusShapes ()
   {
      ArrayList<EntityType> animagusShapes = new ArrayList<>();

      animagusShapes.add(EntityType.OCELOT);
      animagusShapes.add(EntityType.WOLF);
      animagusShapes.add(EntityType.COW);
      animagusShapes.add(EntityType.PIG);
      animagusShapes.add(EntityType.HORSE);
      animagusShapes.add(EntityType.SHEEP);
      animagusShapes.add(EntityType.RABBIT);
      animagusShapes.add(EntityType.MULE);
      animagusShapes.add(EntityType.DONKEY);

      if (Ollivanders2.hostileMobAnimagi)
      {
         animagusShapes.add(EntityType.SPIDER);
         animagusShapes.add(EntityType.SLIME);
         animagusShapes.add(EntityType.CAVE_SPIDER);
         animagusShapes.add(EntityType.CREEPER);
         animagusShapes.add(EntityType.EVOKER);
         animagusShapes.add(EntityType.HUSK);
         animagusShapes.add(EntityType.SILVERFISH);
         animagusShapes.add(EntityType.WITCH);
         animagusShapes.add(EntityType.VINDICATOR);
         animagusShapes.add(EntityType.SHULKER);
      }

      if (Ollivanders2.mcVersionCheck())
      {
         animagusShapes.add(EntityType.POLAR_BEAR);
         animagusShapes.add(EntityType.LLAMA);
      }

      return animagusShapes;
   }


   /**
    * Take an integer and get the corresponding year
    * @param year The year; must be between 1 and 7
    * @return The corresponding year or null if invalid input
    */
   public static Year intToYear(int year) {
      switch (year) {
         case 1:
            return Year.YEAR_1;
         case 2:
            return Year.YEAR_2;
         case 3:
            return Year.YEAR_3;
         case 4:
            return Year.YEAR_4;
         case 5:
            return Year.YEAR_5;
         case 6:
            return Year.YEAR_6;
         case 7:
            return Year.YEAR_7;
         default:
            return null;
      }
   }

   /**
    * Take a year and get the corresponding integer
    * @param year The year
    * @return The corresponding number year
    */
   public static int yearToInt(Year year) {
      switch (year) {
         case YEAR_1:
            return 1;
         case YEAR_2:
            return 2;
         case YEAR_3:
            return 3;
         case YEAR_4:
            return 4;
         case YEAR_5:
            return 5;
         case YEAR_6:
            return 6;
         case YEAR_7:
            return 7;
         default:
            return -1;
      }
   }
}
