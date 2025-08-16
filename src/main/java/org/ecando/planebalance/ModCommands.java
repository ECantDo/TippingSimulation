package org.ecando.planebalance;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.List;

public class ModCommands {

	private static CommandDispatcher<ServerCommandSource> dispatcher;

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((
				(commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
					dispatcher = commandDispatcher;
					registerCreateSimulation();
					registerSetLength();
					registerClearArea();
					registerSetDepth();
					registerSimulateStep();
					registerAddMass();
					registerAddPivot();
					registerSetHeight();
					registerList();
					registerRemoveMass();
					registerRemovePivot();
					dispatcher = null;
				}));
	}

	private static void registerRemoveMass() {
		dispatcher.register(
				CommandManager.literal("tippingRemoveMass")
						.then(CommandManager.literal("all")
								.executes(CommandOperationManager::runClearMass))
						.then(CommandManager.argument("idx", IntegerArgumentType.integer(0))
								.executes(CommandOperationManager::runRemoveMass))
		);
	}

	private static void registerRemovePivot() {
		dispatcher.register(
				CommandManager.literal("tippingRemovePivot")
						.then(CommandManager.literal("all")
								.executes(CommandOperationManager::runClearPivot))
						.then(CommandManager.argument("idx", IntegerArgumentType.integer(0))
								.executes(CommandOperationManager::runRemovePivot))
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
				CommandManager.literal("tippingAddMass")
						.then(CommandManager.argument("mass", IntegerArgumentType.integer(1))
								.then(CommandManager.argument("pos",
												IntegerArgumentType.integer(0))
										.executes(CommandOperationManager::runAddMass)))
		);
	}

	private static void registerAddPivot() {
		dispatcher.register(
				CommandManager.literal("tippingAddPivot")
						.then(CommandManager.argument("pos",
										IntegerArgumentType.integer(0))
								.executes(CommandOperationManager::runAddPivotPoint))
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

	private static void registerSetHeight() {
		dispatcher.register(
				CommandManager.literal("tippingSetHeight")
						.then(CommandManager.argument("height", IntegerArgumentType.integer(1))
								.executes(CommandOperationManager::runSetHeight))
		);
	}

	private static void registerList() {
		dispatcher.register(
				CommandManager.literal("tippingList")
						.then(CommandManager.argument("type", StringArgumentType.word())
								.suggests(((commandContext, suggestionsBuilder) ->
										CommandSource.suggestMatching(List.of("pivots", "masses"), suggestionsBuilder)))
								.executes(CommandOperationManager::runList)
						)
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
