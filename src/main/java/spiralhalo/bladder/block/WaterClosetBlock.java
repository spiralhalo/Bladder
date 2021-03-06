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
import net.minecraft.network.PacketByteBuf;
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
import spiralhalo.bladder.util.NetworkUtil;
import spiralhalo.bladder.util.VoxelShapeUtil;

import java.util.List;

public class WaterClosetBlock extends HorizontalFacingBlock {

    public static final Identifier ENTITY_ID = BladderMod.createId("water_closet_entity");
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
        outlineEast = VoxelShapeUtil.rotateCW(outlineNorth, 1);
        outlineSouth = VoxelShapeUtil.rotateCW(outlineEast, 1);
        outlineWest = VoxelShapeUtil.rotateCW(outlineSouth, 1);
    }

    private final int bpReductionTick;

    public WaterClosetBlock(int bpReductionTick) {
        super(FabricBlockSettings.of(Material.STONE).requiresTool().breakByTool(FabricToolTags.PICKAXES).hardness(1.5f).resistance(2f));
        this.bpReductionTick = bpReductionTick;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if ((state.get(FACING).getHorizontal() + 3) %4 == hit.getSide().getHorizontal()) {
            if (!world.isClient) {
                player.sendMessage(new LiteralText("Flushed the bad stuff out of this world."), false);
            }
            return ActionResult.SUCCESS;
        } else if (hit.getSide().equals(Direction.UP)) {
            ActionResult result = ActionResult.CONSUME;
            if (world.isClient) return result;
            if (state.get(OCCUPIED)) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Box scanArea = new Box(x, y, z, x + 1, y + 1, z + 1);
                List<WaterClosetEntity> wcEntities = world.getEntitiesByClass(WaterClosetEntity.class, scanArea, e -> true);
                for (WaterClosetEntity e : wcEntities) if (e.hasPassengers()) return result;
            }
            Entity rideable = WaterClosetEntity.WATER_CLOSET_ENTITY.create(world);
            if (rideable instanceof WaterClosetEntity) {
                double rideableX = pos.getX() + 0.5 + state.get(FACING).getOffsetX() * 0.1;
                double rideableY = pos.getY() + 0.5;
                double rideableZ = pos.getZ() + 0.5 + state.get(FACING).getOffsetZ() * 0.1;
                float rideableYaw = state.get(FACING).asRotation();
                world.setBlockState(pos, state.with(OCCUPIED, true));
                rideable.updatePositionAndAngles(rideableX, rideableY, rideableZ, rideableYaw, 0);
                world.spawnEntity(rideable);
                player.startRiding(rideable);
            }
            return result;
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
            case NORTH: default: return getDefaultState().with(FACING, Direction.SOUTH).with(OCCUPIED, false);
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
            case EAST: return outlineEast;
            case WEST: return outlineWest;
            case SOUTH: return outlineSouth;
            case NORTH: default: return outlineNorth;
        }
    }

    @SuppressWarnings("EntityConstructor")
    public static class WaterClosetEntity extends Entity {

        public static EntityType WATER_CLOSET_ENTITY;
        static {
            WATER_CLOSET_ENTITY = FabricEntityTypeBuilder.create(SpawnGroup.MISC, WaterClosetEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.05F))
                    .build();
        }

        private boolean passengerAngleAdjusted;
        private int tick;
        // this field is not saved when quitting world, don't use
//        private int bpReductionTick;

        private WaterClosetEntity(EntityType type, World world) {
            super(type, world);
            passengerAngleAdjusted = false;
            noClip = true;
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
            if (world.isClient) return;
            BlockPos pos = getBlockPos();
            BlockState worldBlockState = world.getBlockState(pos);
            Block occupiedBlock = worldBlockState.getBlock();
            if (!hasPassengers() || !(occupiedBlock instanceof WaterClosetBlock)) {
                if (occupiedBlock instanceof WaterClosetBlock) {
                    world.setBlockState(pos, worldBlockState.with(WaterClosetBlock.OCCUPIED, false));
                }
                remove();
            } else {
                Entity primaryPassenger = this.getPrimaryPassenger();
                if (this.scanForOverlap(pos)) return;
                if (!(primaryPassenger instanceof PlayerEntity)) return;
                tick++;
                int bpReductionTick = ((WaterClosetBlock) occupiedBlock).bpReductionTick;
                if (tick > bpReductionTick) {
                    BladderComponents.BLADDER_POINT.get(primaryPassenger).onRelieve(1);
                    BladderComponents.BLADDER_POINT.sync(primaryPassenger);
                    tick = 0;
                }
            }
        }

        private boolean scanForOverlap(BlockPos pos) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            Box scanArea = new Box(x, y, z, x + 1, y + 1, z + 1);
            List<Entity> otherWCs = world.getOtherEntities(this, scanArea, WaterClosetEntity.class::isInstance);
            for (Entity otherWc : otherWCs) {
                if (otherWc.hasPassengers()) {
                    this.remove();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void remove() {
            if (this.hasPassengers()) this.removeAllPassengers();
            super.remove();
        }

        @Override
        public Vec3d updatePassengerForDismount(LivingEntity passenger) {
            Direction d = getHorizontalFacing();
            double dismountX = this.getX() + d.getOffsetX();
            double dismountY = this.getY();
            double dismountZ = this.getZ() + d.getOffsetZ();
            Vec3d dismountPos = new Vec3d(dismountX, dismountY, dismountZ);
            if ( world.getBlockState(new BlockPos(dismountPos)).isAir() ) return dismountPos;
            else return new Vec3d(this.getX(), this.getY() + 0.1, this.getZ());
        }

        public void updatePassengerPosition(Entity passenger) {
            if (!this.hasPassenger(passenger)) return;
            if (!passengerAngleAdjusted) {
                passenger.updatePositionAndAngles(getX(), getY() - 0.65, getZ(), yaw, 0);
                passengerAngleAdjusted = true;
            } else passenger.updatePosition(getX(), getY() - 0.65, getZ());
        }

        @Override
        protected void initDataTracker() { }

        @Override
        protected void readCustomDataFromTag(CompoundTag tag) { }

        @Override
        protected void writeCustomDataToTag(CompoundTag tag) { }

        @Override
        public Packet<?> createSpawnPacket() {
            PacketByteBuf spawnPacket = NetworkUtil.createEntityDataPacket(this);
            return ServerSidePacketRegistry.INSTANCE.toPacket(BladderModClient.SPAWN_ENTITY_PACKET_ID, spawnPacket);
        }

        @Override
        public @Nullable Entity getPrimaryPassenger() {
            return hasPassengers() ? getPassengerList().get(0) : null;
        }
    }
}
