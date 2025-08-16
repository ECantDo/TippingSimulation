package org.ecando.planebalance.Simulation;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Util.BlockBuilder;
import org.ecando.planebalance.Util.Util;

public class BuildPlane extends BlockBuilder {

	public CommandContext<ServerCommandSource> CC;

	private Plane plane;

	private Vec3i startBounds, endBounds;

	private final static BlockState AIR = Blocks.AIR.getDefaultState();
	private final static BlockState PLATFORM = Blocks.WHITE_CONCRETE.getDefaultState();
	private final static BlockState CENTER_OF_MASS = Blocks.GOLD_BLOCK.getDefaultState();
	private final static BlockState PIVOT_POINT = Blocks.OBSIDIAN.getDefaultState();

	public BuildPlane(ServerWorld world, Vec3i initLocation, Plane plane) {
		super(world, initLocation);
		this.plane = plane;
	}

	public void update(int depth) {
		if (CC != null)
			Util.chatSendFeedback("RUNNING UPDATE", CC);
		int length = plane.getLength();
		int height = plane.getHeight();
		int pivotLocation = plane.getActivePivotLocation();
		int CGi = (int) Math.round(plane.getCenterOfGravity()) - 1;
		double angle = -plane.getAngle();
		int[] pivots = plane.getPivots();

		depth -= 1;

		if (startBounds != null && endBounds != null) {
			this.setBlocks(startBounds, endBounds, AIR);
		}

		// New bounds tracking
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		int minY = 0, maxY = Integer.MIN_VALUE;

		double cosine = Math.cos(angle);
		double sine = Math.sin(angle);

		for (int i = 0; i < length; i++) {
			// Compute rotation relative to pivot
			int dx = i - pivotLocation;
			double rotatedX = dx * cosine;
			double rotatedY = dx * sine;

			int blockX = (int) Math.round(rotatedX) + pivotLocation;
			int blockY = (int) Math.round(rotatedY) + height;
			int blockZ = 0;

			Vec3i start = new Vec3i(blockX, blockY, blockZ);
			Vec3i end = new Vec3i(blockX, blockY, blockZ + depth);

			if (blockX == CGi) {
//				this.setBlock(blockX, blockY, blockZ, CENTER_OF_MASS);
				this.setBlocks(start, end, CENTER_OF_MASS);
			} else {
//				this.setBlock(blockX, blockY, blockZ, PLATFORM);
				this.setBlocks(start, end, PLATFORM);
			}

			// Track bounds for clearing later
			if (blockX < minX) minX = blockX;
			if (blockX > maxX) maxX = blockX;
			if (blockY < minY) minY = blockY;
			if (blockY > maxY) maxY = blockY;
		}

		int halfDepth = (depth >> 2) + 1;
		for (int[] massObject : plane.getMassPoints()) {
			int dx = massObject[1] - pivotLocation;
			double rotatedX = dx * cosine;
			double rotatedY = dx * sine;

			// Compute actual world coordinates
			int blockX = (int) Math.round(rotatedX) + pivotLocation;
			int blockY = (int) Math.round(rotatedY) + height + 1;

			Vec3i start = new Vec3i(blockX, blockY, halfDepth);
			Vec3i end = new Vec3i(blockX, blockY + 3, halfDepth);

			if (blockY + 3 > maxY) maxY = blockY + 3;

			this.setBlocks(start, end, PIVOT_POINT);
		}

		for (int pivotPoint : pivots) {
			Vec3i start = new Vec3i(
					pivotPoint,
					0,
					0);
			Vec3i end = new Vec3i(
					pivotPoint,
					height - 1,
					depth);

			this.setBlocks(start, end, PIVOT_POINT);
		}

		// Update bounds
		this.startBounds = new Vec3i(minX, minY, 0);
		this.endBounds = new Vec3i(maxX, maxY, depth);
	}

	public void clearArea(){
		if (startBounds == null || endBounds == null)
			return;

		this.setBlocks(startBounds, endBounds, AIR);
	}
}
