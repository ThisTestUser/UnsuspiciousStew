package schauweg.unsuspiciousstew.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.SuspiciousStewEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SuspiciousStewEffects.class)
public class MixinSuspiciousStewEffects {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isCreative()Z"), method = "addToTooltip")
    private boolean forceShowTooltip(TooltipFlag flag, Operation<Boolean> original) {
        return true;
    }
}
