package com.neuromuser.hostileclimbers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void hc$isClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof HostileEntity)) return;
        if (self.isFallFlying()) return;

        try {
            byte flags = self.getDataTracker().get(LivingEntityFlagsAccessor.getLivingFlags());
            if ((flags & 8) != 0) {
                cir.setReturnValue(true);
            }
        } catch (Exception ignored) {}
    }
}