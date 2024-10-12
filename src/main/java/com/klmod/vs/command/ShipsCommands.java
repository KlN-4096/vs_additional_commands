package com.klmod.vs.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.Commands;
import com.klmod.vs.config.ShipsConfig;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ShipsCommands {

    private static ServerShipWorldCore getShipWorld(CommandContext<CommandSourceStack> context) {
        IShipObjectWorldServerProvider level = (IShipObjectWorldServerProvider) context.getSource().getLevel();
        return level.getShipObjectWorld();
    }

    public static int execute(CommandContext<CommandSourceStack> context, String type, double yValue) {
        Predicate<ServerShip> condition;
        String description;

        switch (type) {
            case "zeromass":
                condition = ship -> ship.getInertiaData().getMass() == 0.0;
                description = ShipsConfig.MASSLESS_SHIP.get();
                break;
            case "above":
                condition = ship -> ship.getTransform().getPositionInWorld().y() >= yValue;
                description = ShipsConfig.SHIPS_ABOVE_Y_LEVEL.get().replace("$1", String.valueOf(yValue));
                break;
            case "below":
                condition = ship -> ship.getTransform().getPositionInWorld().y() <= yValue;
                description = ShipsConfig.SHIPS_BELOW_Y_LEVEL.get().replace("$1", String.valueOf(yValue));
                break;
            default:
                context.getSource().sendFailure(Component.literal(ShipsConfig.INVALID_TYPE_MESSAGE.get()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))));
                return 1;
        }

        return executeWithCondition(context, condition, description);
    }

    private static int executeWithCondition(CommandContext<CommandSourceStack> context, Predicate<ServerShip> condition, String description) {
        ServerShipWorldCore shipObjectWorld = getShipWorld(context);

        if (shipObjectWorld == null) {
            context.getSource().sendFailure(Component.literal(ShipsConfig.NO_SHIP_WORLD_MESSAGE.get()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))));
            return 1;
        }

        List<ServerShip> shipsToDelete = shipObjectWorld.getAllShips().stream()
                .filter(condition)
                .toList();

        shipsToDelete.forEach(shipObjectWorld::deleteShip);

        long deletedShipsCount = shipsToDelete.size();

        if (deletedShipsCount > 0) {
            context.getSource().sendSuccess(() -> Component.literal(ShipsConfig.SUCCESSFUL_DELETE_MESSAGE.get().replace("$1", String.valueOf(deletedShipsCount)).replace("$2", description)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal(ShipsConfig.NO_SHIPS_FOUND_MESSAGE.get().replace("$1", description)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))), false);
        }

        return 0;
    }

    public static int listShips(CommandContext<CommandSourceStack> context, String type, double yValue) {
        Predicate<ServerShip> condition;
        String description;

        switch (type) {
            case "all":
                condition = ship -> true;
                description = ShipsConfig.SHIP_LIST_HEADER.get().replace("$1", ShipsConfig.SHIPS_IN_CURRENT_DIMENSION.get());
                break;
            case "zeromass":
                condition = ship -> ship.getInertiaData().getMass() == 0.0;
                description = ShipsConfig.SHIP_LIST_HEADER.get().replace("$1", ShipsConfig.MASSLESS_SHIP.get());
                break;
            case "above":
                condition = ship -> ship.getTransform().getPositionInWorld().y() >= yValue;
                description = ShipsConfig.SHIP_LIST_HEADER.get().replace("$1", ShipsConfig.SHIPS_ABOVE_Y_LEVEL.get().replace("$1", String.valueOf(yValue)));
                break;
            case "below":
                condition = ship -> ship.getTransform().getPositionInWorld().y() <= yValue;
                description = ShipsConfig.SHIP_LIST_HEADER.get().replace("$1", ShipsConfig.SHIPS_BELOW_Y_LEVEL.get().replace("$1", String.valueOf(yValue)));
                break;
            default:
                context.getSource().sendFailure(Component.literal(ShipsConfig.INVALID_TYPE_MESSAGE.get()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))));
                return 1;
        }

        return listWithCondition(context, condition, description);
    }

    private static int listWithCondition(CommandContext<CommandSourceStack> context, Predicate<ServerShip> condition, String description) {
        ServerShipWorldCore shipObjectWorld = getShipWorld(context);

        if (shipObjectWorld == null) {
            context.getSource().sendFailure(Component.literal(ShipsConfig.NO_SHIP_WORLD_MESSAGE.get()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))));
            return 1;
        }

        List<ServerShip> ships = shipObjectWorld.getAllShips().stream()
                .filter(condition)
                .sorted(Comparator.comparingDouble(ship -> ship.getTransform().getPositionInWorld().length()))
                .toList();

        if (ships.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal(ShipsConfig.NO_SHIPS_FOUND_MESSAGE.get()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal(description + "\n").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))), false);
            for (int i = 0; i < ships.size(); i++) {
                ServerShip ship = ships.get(i);
                String shipId = (ship.getSlug() != null ? ship.getSlug() : String.valueOf(ship.getId()));
                String position = Math.round(ship.getTransform().getPositionInWorld().x()) + " " + Math.round(ship.getTransform().getPositionInWorld().y()) + " " + Math.round(ship.getTransform().getPositionInWorld().z());

                Component positionComponent = Component.literal(String.format("[%-25s]", position))
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + Math.round(ship.getTransform().getPositionInWorld().x()) + " ~ " + Math.round(ship.getTransform().getPositionInWorld().z()))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(ShipsConfig.CLICK_TELEPORT_TO_COORDS.get()))));

                Component shipIdComponent = Component.literal("[" + shipId + "]")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vs teleport " + shipId + " ~ ~ ~")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(ShipsConfig.CLICK_TELEPORT_SHIP_TO_PLAYER.get()))));

                int finalI = i;
                context.getSource().sendSuccess(() -> Component.literal((finalI + 1) + ". [id:" + ship.getId() + "] ")
                        .append(positionComponent)
                        .append(" ")
                        .append(shipIdComponent)
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00))), false);
            }
        }

        return 0;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("ships")
                .requires(source -> source.hasPermission(ShipsConfig.PERMISSION_LEVEL.get()))
                .then(Commands.literal("remove")
                        .then(Commands.literal("above")
                                .then(Commands.argument("yValue", DoubleArgumentType.doubleArg())
                                        .executes(context -> execute(context, "above", DoubleArgumentType.getDouble(context, "yValue")))))
                        .then(Commands.literal("below")
                                .then(Commands.argument("yValue", DoubleArgumentType.doubleArg())
                                        .executes(context -> execute(context, "below", DoubleArgumentType.getDouble(context, "yValue")))))
                        .then(Commands.literal("zeromass")
                                .executes(context -> execute(context, "zeromass", 0))))
                .then(Commands.literal("list")
                        .then(Commands.literal("all")
                                .executes(context -> listShips(context, "all", 0)))
                        .then(Commands.literal("zeromass")
                                .executes(context -> listShips(context, "zeromass", 0)))
                        .then(Commands.literal("above")
                                .then(Commands.argument("yValue", DoubleArgumentType.doubleArg())
                                        .executes(context -> listShips(context, "above", DoubleArgumentType.getDouble(context, "yValue")))))
                        .then(Commands.literal("below")
                                .then(Commands.argument("yValue", DoubleArgumentType.doubleArg())
                                        .executes(context -> listShips(context, "below", DoubleArgumentType.getDouble(context, "yValue"))))));
    }
}