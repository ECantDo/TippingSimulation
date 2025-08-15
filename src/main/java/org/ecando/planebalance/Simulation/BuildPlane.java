package org.ecando.planebalance.Simulation;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import org.ecando.planebalance.Util.BlockBuilder;

public class BuildPlane extends BlockBuilder {

	private Plane plane;

	private Vec3i startBounds, endBounds;

	private final static BlockState AIR = Blocks.AIR.getDefaultState();
	private final static BlockState PLATFORM = Blocks.WHITE_CONCRETE.getDefaultState();
	private final static BlockState CENTER_OF_MASS = Blocks.GOLD_BLOCK.getDefaultState();

	public BuildPlane(ServerWorld world, Vec3i initLocation, Plane plane) {
		super(world, initLocation);
		this.plane = plane;
	}

	public void update(int depth) {
		int length = plane.getLength();
		int height = plane.getHeight();
		int pivotLocation = plane.getActivePivotLocation();
		int CGi = (int) Math.round(plane.getCenterOfGravity()) - 1;
		double angle = -plane.getAngle();
		int[] pivots = plane.getPivots();

		if (startBounds != null && endBounds != null) {
			this.setBlocks(startBounds, endBounds, AIR);
		}

		// New bounds tracking
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		double cosine = Math.cos(angle);
		double sine = Math.sin(angle);

		for (int i = 0; i < length; i++) {
			// Compute rotation relative to pivot
			int dx = i - pivotLocation;
			double rotatedX = dx * cosine;
			double rotatedY = dx * sine;

			int blockX = this.initLocation.getX() + (int) Math.round(rotatedX) + pivotLocation;
			int blockY = this.initLocation.getY() + (int) Math.round(rotatedY) + height;
			int blockZ = this.initLocation.getZ();

			if (blockX == CGi)
				this.setBlock(blockX, blockY, blockZ, CENTER_OF_MASS);
			else
				this.setBlock(blockX, blockY, blockZ, PLATFORM);

			// Track bounds for clearing later
			if (blockX < minX) minX = blockX;
			if (blockX > maxX) maxX = blockX;
			if (blockY < minY) minY = blockY;
			if (blockY > maxY) maxY = blockY;
		}

		// Update bounds
		this.startBounds = new Vec3i(minX, minY, 0);
		this.endBounds = new Vec3i(maxX, maxY, depth);
	}
}
