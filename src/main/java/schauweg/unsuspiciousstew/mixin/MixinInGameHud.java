package schauweg.unsuspiciousstew.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(at = @At("HEAD"), method = "renderHeldItemTooltip")
    public void onInjectTooltip(DrawContext context, CallbackInfo info) {

        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().getFormatting());
            if (this.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
                mutableText.formatted(Formatting.ITALIC);
            }

            int mainItemNameWidth = this.getTextRenderer().getWidth(mutableText);
            int textWidth = (context.getScaledWindowWidth() - mainItemNameWidth) / 2;
            int hotbarOffset = context.getScaledWindowHeight() - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                hotbarOffset += 14;
            }

            int opacity = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                context.getMatrices().push();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int var10001 = textWidth - 2;
                int var10002 = hotbarOffset - 2;
                int var10003 = textWidth + mainItemNameWidth + 2;
                context.fill(var10001, var10002, var10003, hotbarOffset + 9 + 2, this.client.options.getTextBackgroundColor(0));
                if (currentStack.getItem() == Items.SUSPICIOUS_STEW){
                    SuspiciousStewEffectsComponent suspiciousStewEffectsComponent = currentStack.getOrDefault(
                        DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT);
                        for (int i = 0; i < suspiciousStewEffectsComponent.effects().size(); i++) {
                            SuspiciousStewEffectsComponent.StewEffect stewEffect = suspiciousStewEffectsComponent.effects().get(i);
                            StatusEffectInstance effectInstance = stewEffect.createStatusEffectInstance();
                            Text time = StatusEffectUtil.getDurationText(effectInstance, 1, 20);
                            Text completeText = Text.translatable(effectInstance.getTranslationKey()).append(" ").append(time);
                            textWidth = (context.getScaledWindowWidth() - getTextRenderer().getWidth(completeText)) / 2;
                            context.drawTextWithShadow(getTextRenderer(), completeText, textWidth, hotbarOffset-(i*14)-14, 13421772 + (opacity << 24));
                        }
                }
                RenderSystem.disableBlend();
                context.getMatrices().pop();
            }
        }

    }


}
