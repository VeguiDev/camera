package de.maxhenkel.camera;

import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class EntityUtil {

    public static boolean isLookingAtEntity(Entity entity, PlayerEntity player) {

        AxisAlignedBB boundingBox = entity.getBoundingBox();

        // Obtener el vector de dirección del jugador
        Vector3d lookVec = player.getLookAngle();

        // Calcular el vector que apunta desde el jugador hacia el centro de la BoundingBox
        Vector3d direccionALaCaja = boundingBox.getCenter().subtract(player.position()).normalize();

        // Calcular el ángulo entre los dos vectores
        double dotProduct = lookVec.dot(direccionALaCaja);
        double angulo = Math.toDegrees(Math.acos(dotProduct));

        // Verificar si el ángulo está dentro del margen especificado
        return angulo <= 180 / 2;

    }

}
