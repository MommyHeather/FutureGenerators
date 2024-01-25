package co.uk.mommyheather.futuregenerators.util;

import java.util.Optional;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class TransferUtils {
    

    public static boolean handleFluidInteraction(Player player, InteractionHand hand, ItemStack itemStack, IFluidTank tank) {

        ItemStack stack = itemStack.copyWithCount(1);

        FluidStack fluidStack;
        int drained;

        Optional<IFluidHandlerItem> handlerOptional = FluidUtil.getFluidHandler(stack).resolve();

        if (!stack.isEmpty() && handlerOptional.isPresent()) {
            IFluidHandlerItem handler = handlerOptional.get();
            if (handler.getFluidInTank(0).isEmpty() || tank.getFluidAmount() >= tank.getCapacity()) {
                //Empty item tanks should always try to drain our tanks.
                //We'll also drain our tank if it's full.
                fluidStack = tank.drain(handler.getTankCapacity(0), FluidAction.SIMULATE);
                drained = handler.fill(fluidStack, FluidAction.EXECUTE);
                tank.drain(drained, FluidAction.EXECUTE);

            }
            else {
                //How do we decide which tank should drain here..?
                //For now, we'll just stick to draining the item in hand.

                fluidStack = handler.drain(tank.getCapacity(), FluidAction.SIMULATE);
                drained = tank.fill(fluidStack, FluidAction.EXECUTE);
                handler.drain(drained, FluidAction.EXECUTE);
            }

            ItemStack container = handler.getContainer().copy();
            //now to adjust the in-hand item
            itemStack.shrink(1);
            //If we've just emptied that slot, we'll place the filled container back in the same slot.
            if (itemStack.isEmpty()) {
                player.setItemInHand(hand, container);
            }
            //Try adding directly to the player inventory. Drop the item if there's no room.
            else if (!player.getInventory().add(container)) {
                player.drop(container, false, true);
            }
            
            return true;
        }
        return false;


    }


}
