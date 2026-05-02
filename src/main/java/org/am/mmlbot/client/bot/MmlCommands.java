package org.am.mmlbot.client.bot;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.am.mmlbot.client.MmlbotClient;

public class MmlCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("mml")
                .then(literal("start")
                        .executes(context -> {
                            MinecraftClient client = context.getSource().getClient();
                            MmlbotClient.getBot().enable(client);

                            ClientPlayerEntity player = client.player;
                            if (player == null)
                                return 0;

                            player.sendMessage(Text.literal("Bot enabled."), false);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(literal("stop")
                        .executes(context -> {
                            MinecraftClient client = context.getSource().getClient();
                            MmlbotClient.getBot().disable(client);

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
                            player.sendMessage(Text.literal("meow"), false);

                            // SINGLE_SUCCESS if constant for 1. miro loves non-hardcoded values!
                            return Command.SINGLE_SUCCESS;
                        }))
        );
    }
}
