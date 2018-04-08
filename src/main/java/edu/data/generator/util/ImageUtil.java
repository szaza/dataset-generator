package edu.data.generator.util;

import edu.data.generator.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    private final static Double proportion = 0.75d;
    private final static Logger LOG = LoggerFactory.getLogger(ImageUtil.class);

    public static BufferedImage progressiveScaleImage(final BufferedImage originalImage, final Integer width, final Integer height) {
        BufferedImage resizedImage = cloneImage(originalImage, true);
        int newWidth = (int) ((double) originalImage.getWidth() * proportion);
        int newHeight = (int) ((double) originalImage.getHeight() * proportion);

        while (newWidth > width || newHeight > height) {
            resizedImage = scaleImage(resizedImage, newWidth, newHeight);
            newWidth = (int) ((double) newWidth * proportion);
            newHeight = (int) ((double) newHeight * proportion);
        }

        resizedImage = scaleImage(resizedImage, width, height);
        return resizedImage;
    }

    public static BufferedImage readImage(final File image) {
        try {
            return ImageIO.read(image);
        } catch (IOException ex) {
            LOG.error("Unable to read image {}!", image.getName());
            return new BufferedImage(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public static void saveImage(final BufferedImage image, final String target) {
        try {
            ImageIO.write(cloneImage(image, false),"jpg", new File(target));
        } catch (IOException e) {
            LOG.error("Unagle to save image {}!", target);
        }
    }

    public static BufferedImage cloneImage(final BufferedImage imageToClone, final boolean alpha) {
        BufferedImage clone = (alpha) ? new BufferedImage(imageToClone.getWidth(), imageToClone.getHeight(), BufferedImage.TYPE_INT_ARGB):
                new BufferedImage(imageToClone.getWidth(), imageToClone.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cloneGraphics = (Graphics2D) clone.getGraphics();

        if (Config.BLUR) {
            cloneGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        cloneGraphics.drawImage(imageToClone, 0, 0, null);
        cloneGraphics.dispose();
        return clone;
    }

    private static BufferedImage scaleImage(final BufferedImage originalImage, final Integer width, final Integer height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());

        Graphics2D graphics = resizedImage.createGraphics();
        if (Config.BLUR) {
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }

}
