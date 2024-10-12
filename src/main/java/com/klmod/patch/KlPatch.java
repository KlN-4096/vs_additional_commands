package com.klmod.patch;

import com.klmod.patch.command.ShipsCommands;
import com.klmod.patch.config.ShipsConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(KlPatch.MODID)
public class KlPatch {

    public static final String MODID = "vs_additional_commands";

    public KlPatch() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ShipsConfig.getConfig());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(ShipsCommands.register());
    }

}
