package org.ecando.planebalance;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Simulation.SimulationController;
import org.ecando.planebalance.Util.Util;


@SuppressWarnings("SameReturnValue")
public class CommandOperationManager {
	private static SimulationController simulationController = null;
	private static int depth = 1;
	private static int length = 3;
	private static int height = 1;

	public static int getLength() {
		return CommandOperationManager.length;
	}


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

		CommandOperationManager.length = IntegerArgumentType.getInteger(context, "length");

		simulationController.setLength(length);
		Util.chatSendFeedback("Set the length to: " + length, context);

		simulationController.clearArea();
		simulationController.render();

		return 1;
	}

	public static int runClear(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			return 1;
		}
		simulationController.clearArea();

		Util.chatSendFeedback("Cleared area", context);
		return 1;
	}

	public static int runSetDepth(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot set the depth on something that does not exist", context);
			return 0;
		}
		simulationController.clearArea();

		int depth = IntegerArgumentType.getInteger(context, "depth");

		simulationController.setPlaneDepth(depth);

		simulationController.render();

		Util.chatSendFeedback("Set depth to: " + depth, context);
		return 1;
	}

	public static int runSimulateStep(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot simulate on something that does not exist", context);
			return 0;
		}
		simulationController.clearArea();
		simulationController.takeSimulationStep();
		simulationController.render();
		Util.chatSendFeedback("Simulated 1 tick", context);
		return 1;
	}

	public static int runSimulateSteps(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot simulate on something that does not exist", context);
			return 0;
		}
		simulationController.clearArea();

		int delta = IntegerArgumentType.getInteger(context, "tickDelta");

		simulationController.takeSimulationStep(delta);

		simulationController.render();

		Util.chatSendFeedback("Simulated " + delta + " ticks", context);
		return 1;
	}

	public static int runAddMass(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot add mass on something that does not exist", context);
			return 0;
		}
		simulationController.clearArea();
		int mass = IntegerArgumentType.getInteger(context, "mass");
		int pos = IntegerArgumentType.getInteger(context, "pos");

		simulationController.addMass(mass, pos);

		simulationController.render();

		Util.chatSendFeedback("Created a new mass, " + mass + "kg | at: " + pos, context);
		return 1;
	}

	public static int runAddPivotPoint(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot add a pivot point on something that does not exist", context);
			return 0;
		}
		int pos = IntegerArgumentType.getInteger(context, "pos");

		simulationController.addPivot(pos);

		simulationController.render();

		Util.chatSendFeedback("Added a pivot point at: " + pos, context);
		return 1;
	}

	public static int runSetHeight(CommandContext<ServerCommandSource> context) {
		if (simulationController == null) {
			Util.chatSendFeedback("Cannot set the height on something that does not exist", context);
			return 0;
		}
		int height = IntegerArgumentType.getInteger(context, "height");

		simulationController.clearArea();
		simulationController.setHeight(height);

		simulationController.render();

		Util.chatSendFeedback("Set height to: " + height, context);

		return 1;
	}

	public static int runRender(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		simulationController.render();
		return 1;
	}

	public static int runList(CommandContext<ServerCommandSource> context) {
		String type = StringArgumentType.getString(context, "type");

		if (type.equals("pivots"))
			return runListPivots(context);
		else if (type.equals("masses"))
			return runListMasses(context);

		return 0;
	}

	public static int runListPivots(CommandContext<ServerCommandSource> context) {
		int[] pivots = simulationController.getPivots();
		StringBuilder sb = new StringBuilder();
		Util.chatSendFeedback("Pivots:", context);
		for (int i = 0; i < pivots.length; i++) {
			sb.append(i).append(": x=").append(pivots[i]).append("\n");
		}
		Util.chatSendFeedback(sb.substring(0, sb.length() - 1), context);
		return 1;
	}

	public static int runListMasses(CommandContext<ServerCommandSource> context) {
		int[][] masses = simulationController.getMasses();
		StringBuilder sb = new StringBuilder();
		Util.chatSendFeedback("Masses:", context);
		for (int i = 0; i < masses.length; i++) {
			sb.append(i).append(": ").append(masses[i][0]).append("kg, x=").append(masses[i][1]).append("\n");
		}
		Util.chatSendFeedback(sb.substring(0, sb.length() - 1), context);
		return 1;
	}

	public static int runRemoveMass(CommandContext<ServerCommandSource> context) {
		int idx = IntegerArgumentType.getInteger(context, "idx");
		simulationController.clearArea();
		boolean removed = simulationController.removeMass(idx);
		simulationController.render();
		if (removed)
			Util.chatSendFeedback("Removed mass " + idx, context);
		else
			Util.chatSendFeedback("Unable to removed mass: " + idx, context);
		return 1;
	}

	public static int runClearMass(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		simulationController.clearMasses();
		simulationController.render();
		Util.chatSendFeedback("Removed ALL masses", context);
		return 1;
	}

	public static int runRemovePivot(CommandContext<ServerCommandSource> context) {
		int idx = IntegerArgumentType.getInteger(context, "idx");
		simulationController.clearArea();
		boolean removed = simulationController.removePivot(idx);
		simulationController.render();
		if (removed)
			Util.chatSendFeedback("Removed mass " + idx, context);
		else
			Util.chatSendFeedback("Unable to removed mass: " + idx, context);
		return 1;
	}

	public static int runClearPivot(CommandContext<ServerCommandSource> context) {
		simulationController.clearArea();
		simulationController.clearPivots();
		simulationController.render();
		Util.chatSendFeedback("Removed ALL pivots", context);
		return 1;
	}

}
