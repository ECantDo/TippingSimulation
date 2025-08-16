package org.ecando.planebalance;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Simulation.SimulationController;
import org.ecando.planebalance.Util.Util;


public class CommandOperationManager {
	private static SimulationController simulationController = null;
	private static int depth = 1;
	private static int length = 1;
	private static int height = 1;


	public static int runInit(CommandContext<ServerCommandSource> context) {
		BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
		ServerCommandSource source = context.getSource();

		if (simulationController != null)
			simulationController.clearArea();

		Vec3i loc = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
		Util.chatSendFeedback("Initialized at position: " + pos.toShortString(), context);

		simulationController = new SimulationController(length, depth, height, loc, source.getWorld());
		simulationController.render();
		return 1;
	}

	public static int runSetLength(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot set the length on something that does not exist", context);
			return 0;
		}

		length = IntegerArgumentType.getInteger(context, "length");

		simulationController.setLength(length);
		Util.chatSendFeedback("Set the length to: " + length, context);

		simulationController.clearArea();
		simulationController.render();

		return 1;
	}

	public static int runClear(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		return 1;
	}

	public static int runSetDepth(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();

		int depth = IntegerArgumentType.getInteger(context, "depth");

		simulationController.setPlaneDepth(depth);

		simulationController.render();
		return 1;
	}

	public static int runSimulateStep(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		simulationController.takeSimulationStep();
		simulationController.render();
		return 1;
	}

	public static int runSimulateSteps(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();

		int delta = IntegerArgumentType.getInteger(context, "tickDelta");

		simulationController.takeSimulationStep(delta);

		simulationController.render();
		return 1;
	}

	public static int runRender(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		simulationController.render();
		return 1;
	}
}
