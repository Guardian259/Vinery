package net.satisfy.vinery.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.EnumProperty

object GeneralUtil {
    val LINE_CONNECTING_TYPE: EnumProperty<LineConnectingType> = EnumProperty.create(
        "type",
        LineConnectingType::class.java
    )


    fun popResourceFromFace(level: Level, blockPos: BlockPos, side: Direction, itemStack: ItemStack?) {
        val blockState = level.getBlockState(blockPos)
        val itemWidth = EntityType.ITEM.width.toDouble()
        val itemHeight = EntityType.ITEM.height.toDouble()
        val shape = blockState.getCollisionShape(level, blockPos)
        var posX = blockPos.x.toDouble() + 0.5
        var posY = blockPos.y.toDouble() + 0.5
        var posZ = blockPos.z.toDouble() + 0.5
        var offsetX = 0.0
        var offsetY = 0.0
        var offsetZ = 0.0
        when (side) {
            Direction.DOWN -> {
                posY = blockPos.y.toDouble() - shape.min(Direction.Axis.Y)
                offsetY = -itemHeight * 2.0
            }

            Direction.UP -> posY = blockPos.y.toDouble() + shape.max(Direction.Axis.Y)
            Direction.NORTH -> {
                posZ = blockPos.z.toDouble() + shape.min(Direction.Axis.Z)
                offsetZ = -itemWidth
            }

            Direction.SOUTH -> {
                posZ = blockPos.z.toDouble() + shape.max(Direction.Axis.Z)
                offsetZ = itemWidth
            }

            Direction.WEST -> {
                posX = blockPos.x.toDouble() + shape.min(Direction.Axis.X)
                offsetX = -itemWidth
            }

            Direction.EAST -> {
                posX = blockPos.x.toDouble() + shape.max(Direction.Axis.X)
                offsetX = itemWidth
            }
        }
        val i = side.stepX
        val j = side.stepY
        val k = side.stepZ
        val deltaX = if (i == 0) Mth.nextDouble(level.random, -0.1, 0.1) else i.toDouble() * 0.1
        val deltaY = if (j == 0) Mth.nextDouble(level.random, 0.0, 0.1) else j.toDouble() * 0.1 + 0.1
        val deltaZ = if (k == 0) Mth.nextDouble(level.random, -0.1, 0.1) else k.toDouble() * 0.1
        popResource(
            level,
            ItemEntity(level, posX + offsetX, posY + offsetY, posZ + offsetZ, itemStack, deltaX, deltaY, deltaZ),
            itemStack!!
        )
    }
    private fun popResource(level: Level, itemEntity: ItemEntity, itemStack: ItemStack) {
        if (!level.isClientSide && !itemStack.isEmpty && level.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            itemEntity.setDefaultPickUpDelay()
            level.addFreshEntity(itemEntity)
        }
    }

    enum class LineConnectingType(private val serializedName: String) : StringRepresentable {
        NONE("none"),
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        override fun getSerializedName(): String {
            return serializedName
        }
    }
}