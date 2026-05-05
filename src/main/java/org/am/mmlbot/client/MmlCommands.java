package org.am.mmlbot.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.client.bot.MmlBot;
import org.am.mmlbot.client.muscleMemory.Situation;

public class MmlCommands {
    public static Situation prevSituation = null;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("mml")
                .then(literal("start")
                        .executes(context -> {
                            MinecraftClient client = context.getSource().getClient();
                            MmlbotClient.getBot().enable();

                            ClientPlayerEntity player = client.player;
                            if (player == null)
                                return 0;

                            player.sendMessage(Text.literal("Bot enabled."), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(literal("stop")
                        .executes(context -> {
                            MinecraftClient client = context.getSource().getClient();
                            MmlbotClient.getBot().disable();

                            ClientPlayerEntity player = client.player;
                            if (player == null)
                                return 0;

                            player.sendMessage(Text.literal("Bot disabled."), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(literal("test")  // Test command for alatasek
                        .executes(context -> {
                            // Get client for player & world references
                            MinecraftClient client = context.getSource().getClient();
                            ClientPlayerEntity player = client.player;

                            // player might be null, fuck it
                            if (player == null)
                                return 0;

                            // sweet meow here
                            if (MmlbotClient.getBot().currentTarget == null) {
                                DebugUtils.chat("ударь кого то перед началом!");
                                return 0;
                            }

//                            Situation situation = new Situation(player, MmlbotClient.getBot().currentTarget, Situation.);
//                            DebugUtils.chat("Снапшот ситуации: " + situation);
//
//                            if (prevSituation != null){
//                                DebugUtils.chat("Посравнению с предыдущей ситуацией..." + "(текущий порог похожести равен" + MmlBot.minMemoryDistance + ")");
//                                float distance = situation.calculateDistance(prevSituation);
//                                DebugUtils.chat("Дистанция: " + distance);
//                            }
//
//                            prevSituation = situation;

                            // SINGLE_SUCCESS if constant for 1. miro loves non-hardcoded values!
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}
