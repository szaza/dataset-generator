package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

public class Generator {

    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);
    private final BackgroundGenerator backgroundGenerator;
    private final CardGenerator cardGenerator;

    public Generator() {
        backgroundGenerator = new BackgroundGenerator();
        cardGenerator = new CardGenerator();
        createOutputDir();
    }

    public void generate() {
        for (int i=0; i<Config.DATA_SET_SIZE; i++) {
            BufferedImage image = backgroundGenerator.getRandomBackground();
            image = cardGenerator.putCardsToBackground(image);
            ImageUtil.saveImage(image, Config.TARGET_DIR + "/generated-" + i);
        }
    }

    private void createOutputDir() {
        File directory = new File(Config.TARGET_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
