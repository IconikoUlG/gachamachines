package xyz.ashyboxy.mc.gachamachines;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import static xyz.ashyboxy.mc.gachamachines.GachaMachines.LOGGER;

import java.util.ArrayList;
import java.util.List;

// TODO: this should probably implement SidedInventory instead of mixining into HopperBlockEntity
public class RealGachaMachineBlockEntity extends GachaMachineBlockEntity {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

    // TODO: dataify
    private ItemStack currencyItem = new ItemStack(Items.EMERALD,3);
    private String configuration = "default";
    private CustomLoot customLoot = null;

    private Ingredient currencyIngredient() {
        return Ingredient.ofItems(currencyItem.getItem());
    }

    private int currencyCost() {
        return currencyItem.getCount();
    }

    public RealGachaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RealGachaMachineBlockEntity(BlockPos pos, BlockState state) {
        this(GachaMachines.GACHA_MACHINE_BLOCK_ENTITY, pos, state);
    }

    public int getCurrencyNeeded() {
        return currencyCost();
    }

    public Ingredient getCurrencyIngredient() {
        return currencyIngredient();
    }

    public ItemStack getOutput() {
        if (customLoot!=null)
            return customLoot.getRandomItem();

        if (!(getWorld() instanceof ServerWorld serverWorld)) return ItemStack.EMPTY;
        LootContextParameterSet parameters = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.ORIGIN, this.getPos().toCenterPos())
                .add(LootContextParameters.BLOCK_ENTITY, this)
                .build(GachaMachines.GACHA_MACHINE_LOOT_CONTEXT);
        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(getLootTableId());
        return lootTable.generateLoot(parameters).pop();
    }

    public Identifier getLootTableId() {
        // TODO: dataify
        return GachaMachines.id("gacha_machine").withPrefixedPath(configuration+"/");
    }

    public boolean addInput(ItemStack input) {
        //LOGGER.warn("addInput(ItemStack input) < "+input.getItem().getTranslationKey());
        if (input.isEmpty()) {
            //LOGGER.warn("return false1");
            return false;
        }
        if (!currencyItem.getItem().equals(input.getItem())) {
            //LOGGER.warn("return false2");
            return false;
        }

        //GachaMachines.LOGGER.warn("addInput -> "+input.getItem().getTranslationKey());

        ItemStack storedCurrency = inventory.get(CURRENCY_SLOT);
        if (storedCurrency.getCount() >= getMaxCountPerStack()) {
            //LOGGER.warn("return false3");
            return false;
        }

        if (storedCurrency.isEmpty()) {
            inventory.set(CURRENCY_SLOT, input);
            markDirty();
            //LOGGER.warn("return true");
            return true;
        }

        if (ItemStack.canCombine(storedCurrency, input)) {
            int count = Math.min(input.getCount(), Math.min(getMaxCountPerStack(), storedCurrency.getMaxCount()) - input.getCount());
            if (count > 0) {
                storedCurrency.increment(count);
                {
                    //LOGGER.warn("decrementing "+count+" storedCurrency = "+storedCurrency.getCount());
                }
                input.decrement(count);
                markDirty();
                return true;
            }
        }

        return false;
    }

    public ItemStack createOutput() {
        //LOGGER.warn("createOutput()");
        ItemStack storedCurrency = inventory.get(CURRENCY_SLOT);
        if (storedCurrency.getCount() < currencyCost() || !currencyIngredient().test(storedCurrency)) return ItemStack.EMPTY;
        //LOGGER.warn("storedCurrency.decrement(currencyCost()); < "+currencyCost());
        storedCurrency.decrement(currencyCost());
        markDirty();
        var t = getOutput();
        //LOGGER.warn("return < "+t.getItem().getTranslationKey()+" c"+t.getCount());
        return t;
    }

    public boolean createOutputInSelf() {
        for (int i = 1 ; i < inventory.size(); i++)
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, createOutput());
                return true;
            }

        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return currencyCost();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) return currencyIngredient().test(stack) && stack.getCount() < getMaxCountPerStack();
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
        //LOGGER.warn("removeStack < "+slot+","+amount);
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        //LOGGER.warn("removeStack < "+slot);
        ItemStack result = Inventories.removeStack(inventory, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        //LOGGER.warn("setStack < "+slot+","+stack.getItem().getTranslationKey());
        /*
        if (slot == CURRENCY_SLOT && !currencyIngredient().test(stack)) {
            return;
        }*/
        inventory.set(slot, stack);
        /*
        if (stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());*/
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
    public int[] getAvailableSlots(Direction side) {
        int[] a = new int[size()];
        for (int i = 0; i < a.length; i++) a[i] = i;
        return a;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        boolean bl;
        if (slot != CURRENCY_SLOT) bl = false;
        else bl = currencyIngredient().test(stack);
        //GachaMachines.LOGGER.warn("Slot is "+slot+" and stack is "+stack.getItem().getTranslationKey()+" for ingredients "+currencyIngredient().toJson().getAsString()+" and bl = "+bl);
        return bl;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != CURRENCY_SLOT;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GachaMachineScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);

        if (nbt.contains("cost", NbtElement.COMPOUND_TYPE))
            currencyItem = ItemStack.fromNbt(nbt.getCompound("cost"));

        if (nbt.contains("config_id", NbtElement.STRING_TYPE))
            configuration = nbt.getString("config_id");

        if (nbt.contains("config", NbtElement.COMPOUND_TYPE))
            customLoot = CustomLoot.fromNbt(nbt.getCompound("config"));
        else customLoot = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);

        var stackNbt = new NbtCompound();
        currencyItem.writeNbt(stackNbt);
        nbt.put("cost", stackNbt);

        nbt.putString("config_id",configuration);

        if (customLoot!=null) {
            nbt.put("config",customLoot.toNbt());
        }

        super.writeNbt(nbt);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(currencyCost());
    }
}
