package org.ecando.planebalance;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
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

	private static CommandDispatcher<ServerCommandSource> dispatcher;

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((
				(commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
					dispatcher = commandDispatcher;
					registerHelloCommand();
					registerCreateSimulation();
					registerSetLength();
					registerClearArea();
					registerSetDepth();
					registerSimulateStep();
					dispatcher = null;
				}));
	}

	// TODO, remove at some point
	private static void registerHelloCommand() {
		dispatcher.register(
				CommandManager.literal("hello")
						.executes(commandContext -> {
							Util.chatSendFeedback("Hello, World!", commandContext);
							return 1;
						})
		);
	}

	private static void registerCreateSimulation() {
		dispatcher.register(
				CommandManager.literal("tippingInitialize")
						.then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.executes(CommandOperationManager::runInit))
		);
	}

	private static void registerSetLength() {
		dispatcher.register(
				CommandManager.literal("tippingSetLength")
						.then(CommandManager.argument("length", IntegerArgumentType.integer(3))
								.executes(CommandOperationManager::runSetLength))
		);
	}

	private static void registerClearArea() {
		dispatcher.register(
				CommandManager.literal("tippingClear")
						.executes(CommandOperationManager::runClear)
		);
	}

	private static void registerSetDepth() {
		dispatcher.register(
				CommandManager.literal("tippingSetDepth")
						.then(CommandManager.argument("depth", IntegerArgumentType.integer(1, 100))
								.executes(CommandOperationManager::runSetDepth))
		);
	}

	private static void registerAddMass() {
		dispatcher.register(
				CommandManager.literal("").executes(commandContext -> {

					return 1;
				})
		);
	}

	private static void registerAddPivot() {
		dispatcher.register(
				CommandManager.literal("").executes(commandContext -> {

					return 1;
				})
		);
	}

	private static void registerSimulateStep() {
		dispatcher.register(
				CommandManager.literal("tippingSimulateSteps")
						.executes(CommandOperationManager::runSimulateStep)
						.then(CommandManager.argument("tickDelta", IntegerArgumentType.integer(1))
								.executes(CommandOperationManager::runSimulateSteps))
		);
	}

	private static void registerStartSimulation() {
		dispatcher.register(
				CommandManager.literal("").executes(commandContext -> {

					return 1;
				})
		);
	}


}
