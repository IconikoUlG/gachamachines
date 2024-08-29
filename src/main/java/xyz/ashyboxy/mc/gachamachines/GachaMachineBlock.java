package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// TODO: add comparator output
public class GachaMachineBlock extends BlockWithEntity implements BlockEntityProvider {
    public GachaMachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        // this sneaking check is temporary
        if (screenHandlerFactory != null && !player.isSneaking()) {
            player.openHandledScreen(screenHandlerFactory);
            return ActionResult.SUCCESS;
        }


        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof GachaMachineBlockEntity be)) return ActionResult.PASS;

        ItemStack payment = ItemStack.EMPTY;
        for (ItemStack i : player.getHandItems()) {
            if (be.getCurrencyIngredient().test(i)) {
                payment = i;
                break;
            }
        }
        if (!payment.isEmpty() && be.addInput(payment.copyWithCount(1))) payment.decrement(1);

        ItemStack output = be.createOutput();
        if (!output.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), output);
            itemEntity.addVelocity(new Vec3d(player.getX() - pos.getX(), 0, player.getZ() - pos.getZ()));
            world.spawnEntity(itemEntity);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GachaMachineBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GachaMachineBlockEntity be) {
                ItemScatterer.spawn(world, pos, be);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
