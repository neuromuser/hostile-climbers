package com.neuromuser.hostileclimbers.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityFlagsAccessor {
    @Accessor("LIVING_FLAGS")
    static TrackedData<Byte> getLivingFlags() {
        throw new AssertionError();
    }
}