package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.model.GeneratedData;
import edu.data.generator.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

import static edu.data.generator.config.Config.ANNOTATIONS_DIR;
import static edu.data.generator.config.Config.DARKNET_CONFIG_DIR;
import static edu.data.generator.config.Config.DARKNET_LABELS;
import static edu.data.generator.config.Config.DATASET_DIR;
import static edu.data.generator.config.Config.IMAGES_DIR;
import static edu.data.generator.config.Config.VOC_LABELS;

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
            ImageUtil.saveImage(data.getImage(), Config.TARGET_DIR + DATASET_DIR + IMAGES_DIR + i + ".jpg");
            annotationGenerator.saveAnnotation(Config.CLASS_LABELS, data.getBoxes(), i + "");
        }

        annotationGenerator.generateLabels(Config.CLASS_LABELS);

        LOG.info("Image generation process has finished.");
    }

    private void init() {
        backgroundGenerator.init();
        cardGenerator.init();
    }

    private void createOutputDir() {
        createDirIfNotExists(new File(Config.TARGET_DIR));
        createDirIfNotExists(new File(Config.TARGET_DIR + DATASET_DIR));
        createDirIfNotExists(new File(Config.TARGET_DIR + DATASET_DIR + IMAGES_DIR));
        createDirIfNotExists(new File(Config.TARGET_DIR + DATASET_DIR + ANNOTATIONS_DIR));
        createDirIfNotExists(new File(Config.TARGET_DIR + VOC_LABELS));

        if (Config.DARKNET) {
            createDirIfNotExists(new File(Config.TARGET_DIR + DATASET_DIR + DARKNET_LABELS));
            createDirIfNotExists(new File(Config.TARGET_DIR + DARKNET_CONFIG_DIR));
        }
    }

    private void createDirIfNotExists(final File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
