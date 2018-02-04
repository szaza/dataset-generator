package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static edu.data.generator.config.Config.COLS;
import static edu.data.generator.config.Config.IMAGE_HEIGHT;
import static edu.data.generator.config.Config.IMAGE_WIDTH;
import static edu.data.generator.config.Config.MARGIN_AROUND;
import static edu.data.generator.config.Config.MARGIN_BETWEEN;
import static edu.data.generator.config.Config.ROWS;

public class CardGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(CardGenerator.class);
    private final List<BufferedImage> images;
    private Random random;
    private float cardSize;
    private boolean initialized = false;

    public CardGenerator() {
        images = new ArrayList();
        random = new Random();
    }

    public void init() {
        readImages();
        initialized = true;
    }

    public BufferedImage putCardsToBackground(final BufferedImage image) {
        if (!initialized) {
            init();
        }
        return copyCardsToBackground(getRandomCards(), image);
    }

    private BufferedImage copyCardsToBackground(final List<BufferedImage> cards, BufferedImage image) {
        int index= 0;
        int offset = (int) cardSize + MARGIN_BETWEEN;
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        int baseOffsetX = (int) (IMAGE_WIDTH - (COLS * cardSize + (COLS-1) * MARGIN_BETWEEN) + cardSize - cards.get(0).getWidth()) / 2;
        int baseOffsetY = (int) (IMAGE_HEIGHT - (ROWS * cardSize + (ROWS-1) * MARGIN_BETWEEN) + cardSize - cards.get(0).getHeight()) / 2;

        for (int y=0; y<ROWS; y++) {
            for (int x=0; x<COLS; x++) {
                graphics.drawImage(cards.get(index), baseOffsetX + x * offset, baseOffsetY + y * offset, null);
                index++;
            }
        }

        graphics.dispose();
        return image;
    }

    private List<BufferedImage> getRandomCards() {
        List<BufferedImage> cards = new ArrayList();
        for (int i = 0; i< COLS * ROWS; i++) {
            cards.add(images.get(random.nextInt(images.size())));
        }
        return cards;
    }

    private void readImages() {
        cardSize = calcCardSize();
        LOG.info("Loading and scaling images, please wait. This process can take some time...");
        for (File file : Reader.listFilesFromDir(Config.SOURCE_DIR)) {
            BufferedImage image = ImageUtil.readImage(file);
            float largestSide = Math.max(image.getWidth(), image.getHeight());
            float proportion = cardSize / largestSide;
            int newWidth = (int) (image.getWidth() * proportion);
            int newHeight = (int) (image.getHeight() * proportion);
            images.add(ImageUtil.scaleImage(image, newWidth, newHeight));
            LOG.info("{} loaded and rescaled.", file.getName());
        }
        LOG.info("{} images to process.", images.size());
    }

    private float calcCardSize() {
        float cardWidth = (float) (IMAGE_WIDTH - (COLS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) COLS;
        float cardHeight = (float) (IMAGE_HEIGHT - (ROWS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) ROWS;
        return Math.min(cardWidth, cardHeight);
    }
}
