package com.neuromuser.hostileclimbers.mixin;

import com.neuromuser.hostileclimbers.HostileClimbers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFallDamageMixin {

    @Inject(method = "computeFallDamage", at = @At("RETURN"), cancellable = true)
    private void hc$reduceFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof HostileEntity) {
            if (HostileClimbers.CONFIG == null) return;
            float multiplier = HostileClimbers.CONFIG.fallDamageMultiplier;
            if (multiplier >= 1.0f) return;
            cir.setReturnValue((int) Math.ceil(cir.getReturnValue() * multiplier));
        }
    }
}