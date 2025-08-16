package org.ecando.planebalance;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Simulation.BuildPlane;
import org.ecando.planebalance.Simulation.Plane;
import org.ecando.planebalance.Util.BlockBuilder;
import org.ecando.planebalance.Util.Util;

public class ModCommands {

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((
				(commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
						registerHelloCommand(commandDispatcher)));

		CommandRegistrationCallback.EVENT.register((
				((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
						registerTestTip(commandDispatcher))
		));
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

	private static void registerTestTip(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
				CommandManager.literal("testtip").executes(commandContext -> {
					try {
						Util.chatSendFeedback("Running TeST TIP", commandContext);
						int l = 31;
						Plane p = new Plane(l, 10, new int[]{l / 2});
						p.addMassLocation(l, 0);
						p.simulationStep(20);
						BuildPlane bp = new BuildPlane(commandContext.getSource().getWorld(),
								new Vec3i(0, 0, 0), p);
						bp.update(5);
					} catch (Exception e) {
						Util.chatSendFeedback(e.toString(), commandContext);
						e.printStackTrace();
					}
					return 1;
				})
		);
	}


}
