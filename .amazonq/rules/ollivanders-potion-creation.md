# Ollivanders2 Potion Creation Rules

When creating new Ollivanders2 potions that cause Minecraft potion effects:

## Implementation Pattern
1. **Extend O2Potion class** - All potions must extend the base O2Potion class
2. **Add to O2PotionType enum** - Register the new potion type with appropriate magic level
3. **Add to spellbook** - Include in the appropriate book class (e.g., MAGICAL_DRAFTS_AND_POTIONS)
4. **Update book Javadoc** - Add the new potion to the book's documentation

## Required Components
- **potionType** - Set to the corresponding O2PotionType enum value
- **potionColor** - Use Color.fromRGB() with thematically appropriate colors
- **duration** - Use Ollivanders2Common.ticksPerMinute multiplied by desired minutes
- **ingredients** - Always include STANDARD_POTION_INGREDIENT plus thematic ingredients
- **text** - Spellbook description of the potion's effects
- **minecraftPotionEffect** - Create PotionEffect with appropriate type, duration, and amplifier
- **drink() method** - Override to send appropriate message to player using Ollivanders2.chatColor

## Ingredient Guidelines
- Choose ingredients that thematically match the potion's effect
- Use existing O2ItemType ingredients when possible
- Create new ingredients only when necessary and ensure they don't duplicate existing ones
- Always include STANDARD_POTION_INGREDIENT as it's in every potion
- Consider ingredient amounts based on rarity and effect strength

## Naming Conventions
- Potion class names should be ALL_CAPS matching the effect (e.g., HUNGER_POTION)
- Ingredient names should be descriptive and follow existing patterns
- Use appropriate Material types for new ingredients that match their appearance