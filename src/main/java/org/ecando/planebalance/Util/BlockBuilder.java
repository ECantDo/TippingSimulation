package org.ecando.planebalance.Util;


import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockBuilder {
	private ServerWorld world;
	private Vec3i initLocation;

	/**
	 * Set the block position in the world relative to the init position
	 *
	 * @param x     The x pos
	 * @param y     The y pos
	 * @param z     The z pos
	 * @param block The block state for the block
	 */
	public void setBlock(int x, int y, int z, BlockState block) {
		setBlock(new BlockPos(x, y, z), block);
	}

	/**
	 * Set the block position in the world relative to the init position
	 *
	 * @param blockPos The position of the block
	 * @param block    The block state for the block
	 */
	public void setBlock(BlockPos blockPos, BlockState block) {
		blockPos = blockPos.add(initLocation);
		world.setBlockState(blockPos, block);
	}

	/**
	 * Set the block position in the world as an absolute value; exact coordinates
	 *
	 * @param x     The x pos
	 * @param y     The y pos
	 * @param z     The z pos
	 * @param block The block state for the block
	 */
	public void setBlockAbs(int x, int y, int z, BlockState block) {
		setBlockAbs(new BlockPos(x, y, z), block);
	}

	/**
	 * Set the block position in the world as an absolute value; exact coordinates
	 *
	 * @param blockPos The position of the block
	 * @param block    The block state for the block
	 */
	public void setBlockAbs(BlockPos blockPos, BlockState block) {
		world.setBlockState(blockPos, block);
	}


	public void setBlocks(Vec3i startPos, Vec3i endPos, BlockState block) {
		startPos = startPos.add(initLocation);
		endPos = endPos.add(initLocation);

		for (int x = Math.min(startPos.getX(), endPos.getX()); x < Math.max(startPos.getX(), endPos.getX()); x++) {
			for (int y = Math.min(startPos.getY(), endPos.getY()); y < Math.max(startPos.getY(), endPos.getY()); y++) {
				for (int z = Math.min(startPos.getZ(), endPos.getZ()); z < Math.max(startPos.getZ(), endPos.getZ()); z++) {
					setBlockAbs(x, y, z, block);
				}
			}
		}
	}
}
