package de.maxhenkel.camera;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class EntityUtil {

    public static boolean isLookingAtEntity(Entity entity, PlayerEntity player) {

        Vector3d eyePos = player.getEyePosition(1F);
        Vector3d lookVec = player.getLookAngle();
        Vector3d entityVec = entity.position().subtract(eyePos);
        double distance = entityVec.length();
        entityVec = entityVec.normalize();
        double dot = lookVec.dot(entityVec);
        return dot > 1.0D - 0.025D / distance;

    }

}
