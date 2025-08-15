package org.ecando.planebalance.Simulation
//
//import com.sloimay.smath.vectors.IVec3
//import com.sloimay.smath.vectors.Vec3
//import net.minecraft.block.BlockState
//import net.minecraft.block.Blocks
//import net.minecraft.server.world.ServerWorld
//import net.minecraft.util.math.BlockPos
//import net.minecraft.world.World
//import kotlin.math.*
//
//class WorldInteraction {
//
//    private lateinit var world: ServerWorld
//    private lateinit var initPosition: IVec3;
//
//    private val platformBlock: BlockState = Blocks.WHITE_CONCRETE.defaultState
//
//    fun setBlock(x: Int, y: Int, z: Int, block: BlockState) {
//        setBlock(IVec3(x, y, z), block)
//    }
//
//    fun setBlock(pos: IVec3, block: BlockState) {
//        val posR = pos + initPosition
//        world.setBlockState(BlockPos(posR.x, posR.y, posR.z), block)
//    }
//
//    fun setBlocks(startPos: IVec3, endPos: IVec3, block: BlockState) {
//        val start = startPos + initPosition
//        val end = endPos + initPosition
//
//        for (x in min(start.x, end.x)..max(start.x, end.x)) {
//            for (y in min(start.y, end.y)..max(start.y, end.y)) {
//                for (z in min(start.z, end.z)..max(start.z, end.z)) {
//                    setBlock(x, y, z, block)
//                }
//            }
//        }
//    }
//}