package de.maxhenkel.camera;

import de.maxhenkel.camera.net.MessageRequestImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@OnlyIn(Dist.CLIENT)
public class TextureCache {

    private Map<UUID, CameraTextureObject> clientImageCache;
    private Map<UUID, ResourceLocation> clientResourceCache;
    private Map<UUID, Long> awaitingImages;

    public static TextureCache instance;

    public TextureCache() {
        clientImageCache = new HashMap<>();
        clientResourceCache = new HashMap<>();
        awaitingImages = new HashMap<>();
    }

    public void addImage(UUID uuid, BufferedImage image) {

        if (awaitingImages.containsKey(uuid)) {
            awaitingImages.remove(uuid);
        }

        ClientCache.saveImage(uuid, image);

        ResourceLocation resourceLocation = new ResourceLocation(Main.MODID, "texures/camera/" + uuid.toString());
        CameraTextureObject cameraTextureObject = new CameraTextureObject(ImageTools.toNativeImage(image));
        clientImageCache.put(uuid, cameraTextureObject);
        clientResourceCache.put(uuid, resourceLocation);
        Minecraft.getInstance().getEntityRenderDispatcher().textureManager.register(resourceLocation, cameraTextureObject);
    }

    public ResourceLocation getImage(UUID uuid) {
        CameraTextureObject cameraTextureObject = clientImageCache.get(uuid);

        if (checkImage(uuid, cameraTextureObject)) {
            return null;
        }
        return clientResourceCache.get(uuid);
    }

    private boolean checkImage(UUID uuid, CameraTextureObject cameraTextureObject) {
        if (cameraTextureObject == null) {
            if (awaitingImages.containsKey(uuid)) {
                if (awaitingImages.get(uuid).longValue() + 10_000 > System.currentTimeMillis()) {
                    return true;
                }
            }
            awaitingImages.put(uuid, System.currentTimeMillis());

            if(ClientCache.imageExists(uuid)) {
                Executors.newCachedThreadPool().submit(() -> {
                    BufferedImage img = ClientCache.loadImage(uuid);
                    if (img != null) {
                        addImage(uuid, img);
                    }
                });
                return true;
            }

            Main.SIMPLE_CHANNEL.sendToServer(new MessageRequestImage(uuid));

            return true;
        }
        return false;
    }

    public NativeImage getNativeImage(UUID uuid) {
        CameraTextureObject cameraTextureObject = clientImageCache.get(uuid);

        if (checkImage(uuid, cameraTextureObject)) {
            return null;
        }
        return cameraTextureObject.getPixels();
    }

    public class CameraTextureObject extends DynamicTexture {

        public CameraTextureObject(NativeImage image) {
            super(image);
        }
    }

    public static TextureCache instance() {
        if (instance == null) {
            instance = new TextureCache();
        }
        return instance;
    }

}
