package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

// TODO: this should probably implement SidedInventory instead of mixining into HopperBlockEntity
public class GachaMachineBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {
    public static final int CURRENCY_SLOT = 0;

    //    ItemStack storedCurrency = ItemStack.EMPTY;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    // TODO: dataify
    private int currencyNeeded = 4;
    private Ingredient currencyIngredient = Ingredient.ofItems(Items.EMERALD);
    private Item output = Items.DIRT;

    public GachaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        this(GachaMachines.GACHA_MACHINE_BLOCK_ENTITY, pos, state);
    }

    public int getCurrencyNeeded() {
        return currencyNeeded;
    }

    public Ingredient getCurrencyIngredient() {
        return currencyIngredient;
    }

    public Item getOutput() {
        return output;
    }

    public boolean addInput(ItemStack input) {
        if (input.isEmpty()) return false;
        if (!currencyIngredient.test(input)) return false;
        ItemStack storedCurrency = inventory.get(CURRENCY_SLOT);
        if (storedCurrency.getCount() >= getMaxCountPerStack()) return false;

        if (storedCurrency.isEmpty()) {
            inventory.set(CURRENCY_SLOT, input);
            markDirty();
            return true;
        }

        if (ItemStack.canCombine(storedCurrency, input)) {
            int count = Math.min(input.getCount(), Math.min(getMaxCountPerStack(), storedCurrency.getMaxCount()) - input.getCount());
            if (count > 0) {
                storedCurrency.increment(count);
                input.decrement(count);
                markDirty();
                return true;
            }
        }

        return false;
    }

    public ItemStack createOutput() {
        ItemStack storedCurrency = inventory.get(CURRENCY_SLOT);
        if (storedCurrency.getCount() < currencyNeeded) return ItemStack.EMPTY;
        storedCurrency.decrement(currencyNeeded);
        markDirty();
        return new ItemStack(output);
    }

    @Override
    public int getMaxCountPerStack() {
        return currencyNeeded;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) return currencyIngredient.test(stack) && stack.getCount() < getMaxCountPerStack();
        // TODO: capsule ingredient
        return true;
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(inventory, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == CURRENCY_SLOT && !currencyIngredient.test(stack)) return;
        inventory.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GachaMachineScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        super.writeNbt(nbt);
    }
}
