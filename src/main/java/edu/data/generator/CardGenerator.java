package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.model.BoundingBox;
import edu.data.generator.model.GeneratedData;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static edu.data.generator.config.Config.COLS;
import static edu.data.generator.config.Config.DEBUG;
import static edu.data.generator.config.Config.IMAGE_HEIGHT;
import static edu.data.generator.config.Config.IMAGE_WIDTH;
import static edu.data.generator.config.Config.MARGIN_AROUND;
import static edu.data.generator.config.Config.MARGIN_BETWEEN;
import static edu.data.generator.config.Config.MAX_ANGLE_TO_ROTATE;
import static edu.data.generator.config.Config.ROWS;

public class CardGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(CardGenerator.class);
    private final List<BufferedImage> images;
    private final List<String> classNames;
    private Random random;
    private float cardSize;
    private boolean initialized = false;

    public CardGenerator() {
        images = new ArrayList();
        classNames = new ArrayList();
        random = new Random();
    }

    public void init() {
        readImages();
        initialized = true;
    }

    public GeneratedData putCardsToBackground(final BufferedImage image) {
        if (!initialized) {
            init();
        }
        return copyCardsToBackground(getRandomCards(), image);
    }

    public List<String> getClassNames() {
        return classNames;
    }

    private GeneratedData copyCardsToBackground(final List<Integer> cardIndexes, BufferedImage image) {
        int index= 0;
        int offset = (int) cardSize + MARGIN_BETWEEN;
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        List<BoundingBox> boxes = new ArrayList();

        int baseOffsetX = (int) (IMAGE_WIDTH - (COLS * cardSize + (COLS - 1) * MARGIN_BETWEEN) - images.get(0).getWidth()) / 2;
        int baseOffsetY = (int) (IMAGE_HEIGHT - (ROWS * cardSize + (ROWS - 1) * MARGIN_BETWEEN)- images.get(0).getHeight()) / 2;

        for (int y=0; y<ROWS; y++) {
            for (int x=0; x<COLS; x++) {
                int angle = random.nextInt(2 * MAX_ANGLE_TO_ROTATE) - MAX_ANGLE_TO_ROTATE;
                int posX = baseOffsetX + x * offset;
                int posY = baseOffsetY + y * offset;
                int cardIndex = cardIndexes.get(index);

                BufferedImage rotatedCard = rotateCard(ImageUtil.cloneImage(images.get(cardIndex)), angle);
                graphics.drawImage(rotatedCard, posX, posY, null);
                BoundingBox bBox = createBoundingBox(posX, posY, angle, cardIndex);

                if (DEBUG) {
                    showBoundingBoxes(graphics, bBox.getxMin(), bBox.getyMin(), cardIndex);
                }

                boxes.add(bBox);
                index++;
            }
        }

        graphics.dispose();
        return new GeneratedData(image, boxes);
    }

    private BoundingBox createBoundingBox(final int posX, final int posY, final int angle, final int index) {
        BoundingBox bBox = new BoundingBox();
        int bBoxX = (int) (posX + cardSize / 2 - Math.sin(Math.toRadians(angle)) * cardSize);
        int bBoxY = (int) (posY + cardSize/2 + Math.sin(Math.toRadians(angle)) * cardSize);
        bBox.setxMin(bBoxX);
        bBox.setyMin(bBoxY);
        bBox.setxMax((int) (bBoxX + cardSize));
        bBox.setyMax((int) (bBoxY + cardSize));
        bBox.setClassName(classNames.get(index));
        return bBox;
    }

    private void showBoundingBoxes(final Graphics2D graphics, int x, int y, int index) {
        graphics.setColor(Color.GREEN);
        graphics.drawRect(x, y, (int) cardSize, (int) cardSize);
        graphics.drawString(classNames.get(index), x, y - 5);
    }

    private BufferedImage rotateCard(final BufferedImage card, final Integer angle) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle));
        transform.translate(cardSize - card.getWidth() / 2, cardSize - card.getHeight() / 2);
        BufferedImage rotatedCard = new BufferedImage((int) (2 *cardSize), (int) (2 * cardSize), BufferedImage.TYPE_INT_ARGB);
        Graphics2D rotatedCardGraphics = (Graphics2D) rotatedCard.getGraphics();
        rotatedCardGraphics.drawImage(card, transform, null);
        rotatedCardGraphics.dispose();
        return rotatedCard;
    }

    private List<Integer> getRandomCards() {
        List<Integer> cardIndexes = new ArrayList();
        for (int i = 0; i< COLS * ROWS; i++) {
            cardIndexes.add(random.nextInt(images.size()));
        }
        return cardIndexes;
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
            classNames.add(getClassName(file.getName()));
            LOG.info("{} loaded and rescaled.", file.getName());
        }
        LOG.info("{} images to process.", images.size());
    }

    private String getClassName(final String fileName) {
        return fileName.substring(0, fileName.indexOf('.'));
    }

    private float calcCardSize() {
        float cardWidth = (float) (IMAGE_WIDTH - (COLS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) COLS;
        float cardHeight = (float) (IMAGE_HEIGHT - (ROWS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) ROWS;
        return Math.min(cardWidth, cardHeight);
    }
}
