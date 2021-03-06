package vazkii.quark.oddities.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.module.TotemOfHoldingModule;

/**
 * @author WireSegal
 * Created at 1:25 PM on 3/30/20.
 */
public class SoulCompassItem extends QuarkItem {

    private static final String TAG_POS_X = "posX";
    private static final String TAG_POS_Y = "posY";
    private static final String TAG_POS_Z = "posZ";

    @OnlyIn(Dist.CLIENT)
    private static double rotation, rota;

    @OnlyIn(Dist.CLIENT)
    private static long lastUpdateTick;

    public SoulCompassItem(Module module) {
        super("soul_compass", module, new Properties().group(ItemGroup.TOOLS).maxStackSize(1));

        addPropertyOverride(new ResourceLocation("angle"), (stack, world, entityIn) -> {
            if(entityIn == null && !stack.isOnItemFrame())
                return 0;

            else {
                boolean hasEntity = entityIn != null;
                Entity entity = (hasEntity ? entityIn : stack.getItemFrame());

                if (entity == null)
                    return 0;

                if(world == null)
                    world = entity.world;

                double angle;
                BlockPos pos = getPos(stack);

                if(pos.getY() == world.getDimension().getType().getId()) {
                    double yaw = hasEntity ? entity.rotationYaw : getFrameRotation((ItemFrameEntity) entity);
                    yaw = MathHelper.positiveModulo(yaw / 360.0, 1.0);
                    double relAngle = getDeathToAngle(entity, pos) / (Math.PI * 2);
                    angle = 0.5 - (yaw - 0.25 - relAngle);
                }
                else angle = Math.random();

                if (hasEntity)
                    angle = wobble(world, angle);

                return MathHelper.positiveModulo((float) angle, 1.0F);
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote) {
            BlockPos pos = TotemOfHoldingModule.getPlayerDeathPosition(entityIn);
            ItemNBTHelper.setInt(stack, TAG_POS_X, pos.getX());
            ItemNBTHelper.setInt(stack, TAG_POS_Y, pos.getY());
            ItemNBTHelper.setInt(stack, TAG_POS_Z, pos.getZ());
        }
    }

    private BlockPos getPos(ItemStack stack) {
        if(stack.hasTag()) {
            int x = ItemNBTHelper.getInt(stack, TAG_POS_X, 0);
            int y = ItemNBTHelper.getInt(stack, TAG_POS_Y, -1);
            int z = ItemNBTHelper.getInt(stack, TAG_POS_Z, 0);

            return new BlockPos(x, y, z);
        }

        return new BlockPos(0, -1, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private double wobble(World worldIn, double angle) {
        if(worldIn.getGameTime() != lastUpdateTick) {
            lastUpdateTick = worldIn.getGameTime();
            double relAngle = angle - rotation;
            relAngle = MathHelper.positiveModulo(relAngle + 0.5, 1.0) - 0.5;
            rota += relAngle * 0.1;
            rota *= 0.8;
            rotation = MathHelper.positiveModulo(rotation + rota, 1.0);
        }

        return rotation;
    }

    @OnlyIn(Dist.CLIENT)
    private double getFrameRotation(ItemFrameEntity frame) {
        Direction facing = frame.getHorizontalFacing();
        return MathHelper.wrapDegrees(180 + facing.getHorizontalAngle());
    }

    @OnlyIn(Dist.CLIENT)
    private double getDeathToAngle(Entity entity, BlockPos blockpos) {
        return Math.atan2(blockpos.getZ() - entity.getPosZ(), blockpos.getX() - entity.getPosX());
    }


}
