package com.klmod.patch.config;

import net.minecraftforge.common.ForgeConfigSpec;


public final class ShipsConfig {
    private ShipsConfig() {
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> MASSLESS_SHIP;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPS_ABOVE_Y_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPS_BELOW_Y_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<String> NO_SHIP_WORLD_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> INVALID_TYPE_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> SUCCESSFUL_DELETE_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> NO_SHIPS_FOUND_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> SHIP_LIST_HEADER;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPS_IN_CURRENT_DIMENSION;
    public static final ForgeConfigSpec.ConfigValue<String> CLICK_TELEPORT_TO_COORDS;
    public static final ForgeConfigSpec.ConfigValue<String> CLICK_TELEPORT_SHIP_TO_PLAYER;

    static {
        BUILDER.push("ships_commands-config");
        MASSLESS_SHIP = BUILDER.comment("Condition description for massless ships.")
                .define("MASSLESS_SHIP", "massless ships");
        SHIPS_ABOVE_Y_LEVEL = BUILDER.comment("Description for ships above a specific Y level.")
                .define("SHIPS_ABOVE_Y_LEVEL", "ships on or above Y level $1");
        SHIPS_BELOW_Y_LEVEL = BUILDER.comment("Description for ships below a specific Y level.")
                .define("SHIPS_BELOW_Y_LEVEL", "ships on or below Y level $1");
        NO_SHIP_WORLD_MESSAGE = BUILDER.comment("Message when no ship world is available.")
                .define("NO_SHIP_WORLD_MESSAGE", "No ship world available in the current dimension.");
        INVALID_TYPE_MESSAGE = BUILDER.comment("Message for invalid type specified in commands.")
                .define("INVALID_TYPE_MESSAGE", "Invalid type specified. Use one of: all, zeromass, above, below.");
        SUCCESSFUL_DELETE_MESSAGE = BUILDER.comment("Message for successful ship deletion.")
                .define("SUCCESSFUL_DELETE_MESSAGE", "✔ Successfully deleted $1 $2.");
        NO_SHIPS_FOUND_MESSAGE = BUILDER.comment("Message when no ships are found to delete.")
                .define("NO_SHIPS_FOUND_MESSAGE", "⚠ No ships found.");
        SHIP_LIST_HEADER = BUILDER.comment("Header for the list of ships.")
                .define("SHIP_LIST_HEADER", "✨ $1:");
        SHIPS_IN_CURRENT_DIMENSION = BUILDER.comment("Description for ships in the current dimension.")
                .define("SHIPS_IN_CURRENT_DIMENSION", "ships in the current dimension");
        CLICK_TELEPORT_TO_COORDS = BUILDER.comment("Hover message for teleporting to ship coordinates.")
                .define("CLICK_TELEPORT_TO_COORDS", "Click to teleport to ship coordinates");
        CLICK_TELEPORT_SHIP_TO_PLAYER = BUILDER.comment("Hover message for teleporting ship to player location.")
                .define("CLICK_TELEPORT_SHIP_TO_PLAYER", "Click to teleport ship to your location");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static ForgeConfigSpec getConfig() {
        return SPEC;
    }
}