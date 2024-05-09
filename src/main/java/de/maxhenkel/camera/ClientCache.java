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
import java.util.concurrent.Executors;

public class ClientCache {

    private static final String cache_folder = Minecraft.getInstance().gameDirectory + "/camera_cache/";

    public static String getImageRoute(UUID id, int level) {

        List<String> idParts = Arrays.asList(id.toString().split("-"));

        String imageName = String.join("",idParts.subList(1, idParts.size()));

        if(level == 0) {
            return cache_folder + idParts.get(0) + "/" + imageName + ".png";
        } else {
            return cache_folder + idParts.get(0) + "/" + imageName + "_" + level + ".png";
        }

    }

    public static void clearCache() {
        File cache = new File(cache_folder);
        if (cache.exists()) {
            for (File file : cache.listFiles()) {
                file.delete();
            }
        }
    }

    public static void clearImage(UUID uuid) {
        File image = new File(getImageRoute(uuid, 0));
        if (image.exists()) {
            image.delete();
        }
    }

    public static void saveImageLods(UUID uuid, BufferedImage img) {

        List<BufferedImage> images = levelOfDetail(img);

        for(int i = 0; i < images.size(); i++) {

            if(i == 0) {
                saveImage(uuid, images.get(i));
                continue;
            }

            saveImage(uuid, images.get(i), i+1, false);
        }

    }

    public static void saveImage(UUID uuid, BufferedImage img) {
        saveImage(uuid, img, 0, true);
    }

    public static void saveImage(UUID uuid, BufferedImage img, int level, boolean saveLod) {

        try {
            File image = new File(getImageRoute(uuid, level));
            File imageFolder = image.getParentFile();

            if (!imageFolder.exists()) {
                imageFolder.mkdirs();
            }

            if(image.exists()) {
                return;
            }

            ImageIO.write(img, "png", image);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<BufferedImage> levelOfDetail(BufferedImage img) {

        int[] sizes = {350, 200, 100};

        List<BufferedImage> imagenesRedimensionadas = new ArrayList<>();

        for (int i = 0; i < sizes.length; i++) {
            int width = sizes[i];
            int height = (int) ((double) img.getHeight() / img.getWidth() * width);

            BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaled.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(img, 0, 0, width, height, null);
            g2d.dispose();

            imagenesRedimensionadas.add(scaled);
        }

        return imagenesRedimensionadas;

    }

    public static boolean imageExists(UUID uuid) {
        File image = new File(getImageRoute(uuid, 0));
        return image.exists();
    }

    public static BufferedImage loadImage(UUID uuid) {
        try {

            File image = new File(getImageRoute(uuid, 0));

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
