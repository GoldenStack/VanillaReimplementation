package net.minestom.vanilla.blocks.behaviours;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.vanilla.blocks.VanillaBlocks;
import net.minestom.vanilla.system.EnderChestSystem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderChestBlockBehaviour extends ChestLikeBlockBehaviour {
    public EnderChestBlockBehaviour(@NotNull VanillaBlocks.BlockContext context) {
        super(context, 3 * 9);
    }

    @Override
    public boolean dropContentsOnDestroy() {
        return false;
    }

    @Override
    protected List<ItemStack> getAllItems(Instance instance, Point pos, Player player) {
        return EnderChestSystem.getInstance().getItems(player);
    }
}
