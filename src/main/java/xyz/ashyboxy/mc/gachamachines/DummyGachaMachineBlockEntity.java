package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;


// maybe there's a better way to do this, but this seems good enough
public class DummyGachaMachineBlockEntity extends GachaMachineBlockEntity {
    public DummyGachaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DummyGachaMachineBlockEntity(BlockPos pos, BlockState state) {
        this(GachaMachines.DUMMY_GACHA_MACHINE_BLOCK_ENTITY, pos, state);
    }

    private RealGachaMachineBlockEntity getRealBE() {
        // TODO: ...
        BlockEntity be = getWorld().getBlockEntity(getPos().down());
        if (!(be instanceof RealGachaMachineBlockEntity)) {
            throw new IllegalStateException();
        }
        return (RealGachaMachineBlockEntity) be;
    }

    @Override
    public int size() {
        return getRealBE().size();
    }

    @Override
    public boolean isEmpty() {
        return getRealBE().isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return getRealBE().getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return getRealBE().removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return getRealBE().removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        getRealBE().setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return getRealBE().canPlayerUse(player);
    }

    @Override
    public void clear() {
        getRealBE().clear();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        getRealBE().writeScreenOpeningData(player, buf);
    }

    @Override
    public Text getDisplayName() {
        return getRealBE().getDisplayName();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return getRealBE().createMenu(syncId, playerInventory, player);
    }

    @Override
    public int getMaxCountPerStack() {
        return getRealBE().getMaxCountPerStack();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return getRealBE().isValid(slot, stack);
    }

    @Override
    public Ingredient getCurrencyIngredient() {
        return getRealBE().getCurrencyIngredient();
    }

    @Override
    public ItemStack getOutput() {
        return getRealBE().getOutput();
    }

    @Override
    public boolean addInput(ItemStack input) {
        return getRealBE().addInput(input);
    }

    @Override
    public ItemStack createOutput() {
        return getRealBE().createOutput();
    }

    @Override
    public boolean createOutputInSelf() {
        return getRealBE().createOutputInSelf();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return getRealBE().getAvailableSlots(side);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return getRealBE().canInsert(slot, stack, dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return getRealBE().canExtract(slot, stack, dir);
    }
}
