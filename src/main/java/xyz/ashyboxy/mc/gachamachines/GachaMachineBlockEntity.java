package xyz.ashyboxy.mc.gachamachines;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class GachaMachineBlockEntity extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory {
    public static final int CURRENCY_SLOT = 0;

    public GachaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // gacha machine
    public abstract Ingredient getCurrencyIngredient();

    public abstract ItemStack getOutput();

    public abstract boolean addInput(ItemStack input);

    public abstract ItemStack createOutput();

    public abstract boolean createOutputInSelf();

    // inventory (overriden to require subclasses to implement them)
    // TODO: some of this should be implemented here using a getInventory method
    @Override
    public abstract int getMaxCountPerStack();

    @Override
    public abstract boolean isValid(int slot, ItemStack stack);

    @Override
    public abstract int size();

    @Override
    public abstract boolean isEmpty();

    @Override
    public abstract ItemStack getStack(int slot);

    @Override
    public abstract ItemStack removeStack(int slot, int amount);

    @Override
    public abstract ItemStack removeStack(int slot);

    @Override
    public abstract void setStack(int slot, ItemStack stack);

    @Override
    public abstract boolean canPlayerUse(PlayerEntity player);

    @Override
    public abstract void clear();

    @Override
    public abstract int[] getAvailableSlots(Direction side);

    @Override
    public abstract boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir);

    @Override
    public abstract boolean canExtract(int slot, ItemStack stack, Direction dir);

    // screen handler
    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public abstract @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player);

    @Override
    public abstract void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf);
}
