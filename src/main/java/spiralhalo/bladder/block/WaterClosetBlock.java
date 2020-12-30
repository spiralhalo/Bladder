package spiralhalo.bladder.block;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spiralhalo.bladder.BladderMod;
import spiralhalo.bladder.client.BladderModClient;
import spiralhalo.bladder.mechanics.BladderComponents;
import spiralhalo.bladder.util.NetworkUtils;
import spiralhalo.bladder.util.VoxelShapeUtils;

import java.util.List;

public class WaterClosetBlock extends HorizontalFacingBlock {

    public static final Identifier ID = BladderMod.createId("water_closet");
    public static final Identifier ENTITY_ID = BladderMod.createId("water_closet_entity");
    public static final WaterClosetBlock BLOCK;
    public static final WaterClosetBlockItem BLOCK_ITEM;
    public static final BooleanProperty OCCUPIED;

    private static final double
            xMin1 = 0.1875, yMin1 = 0.0, zMin1 = 0.0,    xMax1 = 0.8125, yMax1 = 0.4375, zMax1 = 0.6875,
            xMin2 = 0.125,  yMin2 = 0.0, zMin2 = 0.6875, xMax2 = 0.875,  yMax2 = 1.0,    zMax2 = 1.0;
    private static final VoxelShape outlineNorth;
    private static final VoxelShape outlineEast;
    private static final VoxelShape outlineSouth;
    private static final VoxelShape outlineWest;

    static {
        OCCUPIED = Properties.OCCUPIED;

        final VoxelShape cuboid1 = VoxelShapes.cuboid(xMin1, yMin1, zMin1, xMax1, yMax1, zMax1);
        final VoxelShape cuboid2 = VoxelShapes.cuboid(xMin2, yMin2, zMin2, xMax2, yMax2, zMax2);

        outlineNorth = VoxelShapes.union(cuboid1, cuboid2);
        outlineEast = VoxelShapeUtils.rotateCW(outlineNorth, 1);
        outlineSouth = VoxelShapeUtils.rotateCW(outlineEast, 1);
        outlineWest = VoxelShapeUtils.rotateCW(outlineSouth, 1);

        BLOCK = new WaterClosetBlock();

        BLOCK_ITEM = new WaterClosetBlockItem(BLOCK);
    }

    public WaterClosetBlock() {
        super(FabricBlockSettings.of(Material.STONE).requiresTool().breakByTool(FabricToolTags.PICKAXES).hardness(1.5f).resistance(2f));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if ((state.get(FACING).getHorizontal() + 3) %4 == hit.getSide().getHorizontal()) {

            if (!world.isClient) {
                player.sendMessage(new LiteralText("Flushed the bad stuff out of this world."), false);
            }

            return ActionResult.SUCCESS;

        } else if (hit.getSide().equals(Direction.UP)) {

            if (!world.isClient) {
                if (state.get(OCCUPIED)) {
                    List<WaterClosetEntity> wcEntities = world.getEntitiesByType(WaterClosetEntity.WATER_CLOSET_ENTITY,
                            new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                            entity -> true);
                    for (WaterClosetEntity e : wcEntities) {
                        if (e.hasPassengers()) {
                            return ActionResult.CONSUME;
                        }
                    }
                }

                Entity rideable = WaterClosetEntity.WATER_CLOSET_ENTITY.create(world);

                if (rideable != null) {
                    world.setBlockState(pos, state.with(OCCUPIED, true));

                    rideable.updatePositionAndAngles(
                            pos.getX() + 0.5 + state.get(FACING).getOffsetX() * 0.1,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5 + state.get(FACING).getOffsetZ() * 0.1,
                            state.get(FACING).asRotation(), 0);
                    world.spawnEntity(rideable);
                    player.startRiding(rideable);
                }
            }

            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        switch (ctx.getPlayerFacing()){
            case SOUTH: return getDefaultState().with(FACING, Direction.NORTH).with(OCCUPIED, false);
            case WEST: return getDefaultState().with(FACING, Direction.EAST).with(OCCUPIED, false);
            case EAST: return getDefaultState().with(FACING, Direction.WEST).with(OCCUPIED, false);
            case NORTH:
            default: return getDefaultState().with(FACING, Direction.SOUTH).with(OCCUPIED, false);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(Properties.OCCUPIED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        switch (dir) {
            case EAST:
                return outlineEast;
            case WEST:
                return outlineWest;
            case SOUTH:
                return outlineSouth;
            case NORTH:
            default: return outlineNorth;
        }
    }

    private static class WaterClosetBlockItem extends BlockItem {
        private WaterClosetBlockItem(Block block) {
            super(block, new Item.Settings().group(ItemGroup.MISC));
        }
    }

    @SuppressWarnings("EntityConstructor")
    public static class WaterClosetEntity extends Entity {

        public static EntityType WATER_CLOSET_ENTITY;

        static {
            WATER_CLOSET_ENTITY = FabricEntityTypeBuilder.create(SpawnGroup.MISC, WaterClosetEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.05F)).build();
        }

        private boolean adjusted;
        private int tick;

        private WaterClosetEntity(EntityType type, World world) {
            super(type, world);
            noClip = true;
            adjusted = false;
            tick = 0;
        }

        @Override
        public boolean canBeRiddenInWater() {
            return true;
        }

        @Override
        public double getMountedHeightOffset() {
            return 0;
        }

        @Override
        public void tick() {
            super.tick();

            if (!world.isClient) {
                BlockPos pos = getBlockPos();
                BlockState occupiedBlock = world.getBlockState(pos);
                if (!hasPassengers() || !(occupiedBlock.getBlock() instanceof WaterClosetBlock)) {

                    if (occupiedBlock.getBlock() instanceof WaterClosetBlock) {
                        world.setBlockState(pos, occupiedBlock.getBlock().getDefaultState()
                                .with(WaterClosetBlock.FACING, getHorizontalFacing())
                                .with(WaterClosetBlock.OCCUPIED, false));
                    }
                    if (hasPassengers()) {
                        removeAllPassengers();
                    }
                    remove();

                } else {

                    Entity primaryPassenger = getPrimaryPassenger();
                    if (primaryPassenger instanceof PlayerEntity) {
                        tick++;
                        if (tick > 4) {
                            BladderComponents.BLADDER_POINT.get(primaryPassenger).onRelieve(1);
                            BladderComponents.BLADDER_POINT.sync(primaryPassenger);
                            tick = 0;
                        }
                    }
                    List<Entity> otherWcEntities = world.getOtherEntities(this,
                            new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                            entity -> entity instanceof WaterClosetEntity);

                    for (Entity e : otherWcEntities) {
                        if (e.hasPassengers()) {
                            if (hasPassengers()) {
                                removeAllPassengers();
                            }
                            remove();
                        }
                    }
                }
            }
        }

        @Override
        public Vec3d updatePassengerForDismount(LivingEntity passenger) {
            Direction d = getHorizontalFacing();
            Vec3d dismountPos = new Vec3d(this.getX() + d.getOffsetX(), this.getY(), this.getZ() + d.getOffsetZ());
            if ( world.getBlockState(new BlockPos(dismountPos)).isAir() ) {
                return dismountPos;
            } else {
                return new Vec3d(this.getX(), this.getY() + 0.1, this.getZ());
            }
        }

        public void updatePassengerPosition(Entity passenger) {
            if (this.hasPassenger(passenger)) {
                if (!adjusted) {
                    passenger.updatePositionAndAngles(getX(), getY() - 0.65, getZ(), yaw, 0);
                    adjusted = true;
                } else {
                    passenger.updatePosition(getX(), getY() - 0.65, getZ());
                }
            }
        }

        @Override
        protected void initDataTracker() {

        }

        @Override
        protected void readCustomDataFromTag(CompoundTag tag) {

        }

        @Override
        protected void writeCustomDataToTag(CompoundTag tag) {

        }

        @Override
        public Packet<?> createSpawnPacket() {
            return ServerSidePacketRegistry.INSTANCE.toPacket(BladderModClient.SPAWN_ENTITY_PACKET_ID,
                    NetworkUtils.createEntityDataPacket(this));
        }

        @Override
        public @Nullable Entity getPrimaryPassenger() {
            if (hasPassengers()) {
                return getPassengerList().get(0);
            }
            return null;
        }
    }
}
