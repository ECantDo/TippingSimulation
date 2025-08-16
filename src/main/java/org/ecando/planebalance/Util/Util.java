package org.ecando.planebalance.Util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Util {

	public static void chatSendFeedback(String text, CommandContext<ServerCommandSource> commandContext) {
		ServerCommandSource source = commandContext.getSource(); // now we have the correct type
		source.sendFeedback(() -> Text.literal(text), false);
	}


}
