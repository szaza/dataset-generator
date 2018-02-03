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

    public CardGenerator() {
        images = new ArrayList();
        random = new Random();
        init();
    }

    public BufferedImage putCardsToBackground(final BufferedImage image) {
        return copyCardsToBackground(getRandomCards(), image);
    }

    private void init() {
        readImages();
    }

    private BufferedImage copyCardsToBackground(final List<BufferedImage> cards, BufferedImage image) {
        int index= 0;
        int offset = (int) cardSize + MARGIN_BETWEEN;
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        for (int y=0; y<ROWS; y++) {
            for (int x=0; x<COLS; x++) {
                graphics.drawImage(cards.get(index), x * offset, y * offset, null);
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
        for (File file : Reader.listFilesFromDir(Config.SOURCE_DIR)) {
            BufferedImage image = ImageUtil.readImage(file);
            float largestSide = Math.max(image.getWidth(), image.getHeight());
            float proportion = cardSize / largestSide;
            int newWidth = (int) (image.getWidth() * proportion);
            int newHeight = (int) (image.getHeight() * proportion);
            images.add(ImageUtil.scaleImage(image, newWidth, newHeight));
        }
        LOG.info("{} images to process.", images.size());
    }

    private float calcCardSize() {
        float cardWidth = (float) (IMAGE_WIDTH - 2 * MARGIN_AROUND - COLS * MARGIN_BETWEEN) / (float) COLS;
        float cardHeight = (float) (IMAGE_HEIGHT - 2 * MARGIN_AROUND - ROWS * MARGIN_BETWEEN) / (float) ROWS;
        return Math.min(cardWidth, cardHeight);
    }
}
