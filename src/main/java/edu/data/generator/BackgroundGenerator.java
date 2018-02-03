package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;

public class BackgroundGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(BackgroundGenerator.class);

    private List<File> backgrounds;
    private Random random;

    public BackgroundGenerator() {
        random = new Random();
        readBackgrounds();
    }

    public BufferedImage getRandomBackground() {
        return ImageUtil.scaleImage(ImageUtil.readImage(backgrounds.get(random.nextInt(backgrounds.size()))),
                Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT);
    }

    private void readBackgrounds() {
        backgrounds = Reader.listFilesFromDir(Config.BACKGROUND_DIR);
        LOG.info("{} backgrounds to process.", backgrounds.size());
    }
}
