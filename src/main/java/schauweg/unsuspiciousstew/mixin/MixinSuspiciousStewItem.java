package schauweg.unsuspiciousstew.mixin;

import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.tooltip.TooltipType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(SuspiciousStewItem.class)
public class MixinSuspiciousStewItem {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/tooltip/TooltipType;isCreative()Z"), method = "appendTooltip")
    private boolean forceShowTooltip(TooltipType type, Operation<Boolean> original) {
        return true;
    }
}
