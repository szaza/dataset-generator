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

    private final static Logger LOG = LoggerFactory.getLogger(ImageUtil.class);

    public static BufferedImage scaleImage(final BufferedImage originalImage, final Integer width, final Integer height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        if (Config.BLUR) {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
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
            ImageIO.write(image,"jpg", new File(target));
        } catch (IOException e) {
            LOG.error("Unagle to save image {}!", target);
        }
    }

    public static BufferedImage cloneImage(final BufferedImage background) {
        BufferedImage clone = new BufferedImage(background.getWidth(), background.getHeight(), background.getType());
        Graphics2D cloneGraphics = (Graphics2D) clone.getGraphics();

        if (Config.BLUR) {
            cloneGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        cloneGraphics.drawImage(background, 0, 0, null);
        cloneGraphics.dispose();
        return clone;
    }

}
