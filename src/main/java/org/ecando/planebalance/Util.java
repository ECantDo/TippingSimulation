package org.ecando.planebalance;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Util {

	public static void chatSendFeedback(String content, CommandContext<ServerCommandSource> commandContext) {
		ServerCommandSource source = commandContext.getSource(); // now we have the correct type
		source.sendFeedback(() -> Text.literal(content), false);
	}


}
