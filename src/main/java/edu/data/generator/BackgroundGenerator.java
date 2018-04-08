package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackgroundGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(BackgroundGenerator.class);

    private List<BufferedImage> backgrounds;
    private Random random;
    private boolean initialized;

    public BackgroundGenerator() {
        random = new Random();
        backgrounds = new ArrayList();
    }

    public void init() {
        readBackgrounds();
        initialized = true;
    }

    public BufferedImage getRandomBackground() {
        if (!initialized) {
            init();
        }
        return ImageUtil.cloneImage(backgrounds.get(random.nextInt(backgrounds.size())), false);
    }

    private void readBackgrounds() {
        LOG.info("Loading and scaling backgrounds, please wait. This process can take some time...");
        for (File file : Reader.listFilesFromDir(Config.BACKGROUND_DIR)){
            backgrounds.add(ImageUtil.progressiveScaleImage(ImageUtil.readImage(file),
                    Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT));
            LOG.info("Background {} loaded and rescaled.", file.getName());
        }
        LOG.info("{} backgrounds to process.", backgrounds.size());
    }
}
