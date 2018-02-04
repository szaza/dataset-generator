package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.model.GeneratedData;
import edu.data.generator.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

public class Generator {

    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);
    private final BackgroundGenerator backgroundGenerator;
    private final CardGenerator cardGenerator;
    private AnnotationGenerator annotationGenerator;

    public Generator() {
        backgroundGenerator = new BackgroundGenerator();
        cardGenerator = new CardGenerator();
        annotationGenerator = new AnnotationGenerator();
        createOutputDir();
        init();
    }

    public void generate() {
        LOG.info("Image generation process has started.");
        for (int i=0; i<Config.DATA_SET_SIZE; i++) {
            BufferedImage image = backgroundGenerator.getRandomBackground();
            GeneratedData data = cardGenerator.putCardsToBackground(image);
            ImageUtil.saveImage(data.getImage(), Config.TARGET_DIR + "/Images/" + i);
            annotationGenerator.saveAnnotation(data.getBoxes(), i + "");

        }
        LOG.info("Image generation process has finished.");
    }

    private void init() {
        backgroundGenerator.init();
        cardGenerator.init();
    }

    private void createOutputDir() {
        createDirIfNotExists(new File(Config.TARGET_DIR));
        createDirIfNotExists(new File(Config.TARGET_DIR + "/Images"));
        createDirIfNotExists(new File(Config.TARGET_DIR + "/Annotations"));
    }

    private void createDirIfNotExists(final File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
