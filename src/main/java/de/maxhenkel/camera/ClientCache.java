package de.maxhenkel.camera;

import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class ClientCache {

    private static final String cache_folder = Minecraft.getInstance().gameDirectory + "/camera_cache/";

    private static Map<UUID, BufferedImage> clientImageCache = new HashMap<>();

    public static String getImageRoute(UUID id) {

        List<String> idParts = Arrays.asList(id.toString().split("-"));

        String imageName = String.join("",idParts.subList(1, idParts.size()));

        return cache_folder + idParts.get(0) + "/" + imageName + ".png";

    }

    public static void clearCache() {
        File cache = new File(cache_folder);
        if (cache.exists()) {
            for (File file : cache.listFiles()) {
                file.delete();
            }
        }
    }

    public static void clearImage(UUID id) {
        File image = new File(getImageRoute(id));
        if (image.exists()) {
            image.delete();
        }
    }

    public static void saveImage(UUID uuid, BufferedImage img) {

        try {
            File image = new File(getImageRoute(uuid));
            File imageFolder = image.getParentFile();

            if (!imageFolder.exists()) {
                imageFolder.mkdirs();
            }

            if(image.exists()) {
                return;
            }

            clientImageCache.put(uuid, img);

            ImageIO.write(img, "png", image);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage loadImage(UUID uuid) {
        try {

            if(clientImageCache.containsKey(uuid)) {
                return clientImageCache.get(uuid);
            }

            File image = new File(getImageRoute(uuid));

            if (!image.exists()) {
                return null;
            }

            return ImageIO.read(Files.newInputStream(image.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
