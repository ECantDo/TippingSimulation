package org.ecando.planebalance;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((
				(commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
						registerHelloCommand(commandDispatcher)));
	}

	// TODO, remove at some point
	private static void registerHelloCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				CommandManager.literal("hello")
						.executes(commandContext -> {
							Util.chatSendFeedback("Hello, World!", commandContext);
							return 1;
						})
		);
	}


}
