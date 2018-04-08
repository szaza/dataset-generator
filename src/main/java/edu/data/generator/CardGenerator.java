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

        int baseOffsetX = (int) (IMAGE_WIDTH - (COLS * cardSize + (COLS - 1) * MARGIN_BETWEEN)) / 2;
        int baseOffsetY = (int) (IMAGE_HEIGHT - (ROWS * cardSize + (ROWS - 1) * MARGIN_BETWEEN)) / 2;

        for (int y=0; y<ROWS; y++) {
            for (int x=0; x<COLS; x++) {

                int angle = random.nextInt(2 * MAX_ANGLE_TO_ROTATE) - MAX_ANGLE_TO_ROTATE;
                int cardIndex = cardIndexes.get(index);
                BufferedImage rotatedCard = rotateCard(ImageUtil.cloneImage(images.get(cardIndex), true), Math.toRadians(angle));

                int posX = baseOffsetX + x * offset;
                int posY = baseOffsetY + y * offset;

                if (Config.BLUR) {
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                }
                graphics.drawImage(rotatedCard, posX, posY, null);
                BoundingBox bBox = createBoundingBox(posX, posY, rotatedCard.getWidth(), rotatedCard.getHeight(), cardIndex);

                if (DEBUG) {
                    showBoundingBoxes(graphics, bBox, cardIndex);
                }

                boxes.add(bBox);
                index++;
            }
        }

        graphics.dispose();
        return new GeneratedData(image, boxes);
    }

    private BoundingBox createBoundingBox(final int posX, final int posY, final int width, final int height, final int index) {
        BoundingBox bBox = new BoundingBox();
        bBox.setxMin(posX);
        bBox.setyMin(posY);
        bBox.setxMax(posX + width);
        bBox.setyMax(posY + height);
        bBox.setClassName(classNames.get(index));
        return bBox;
    }

    private void showBoundingBoxes(final Graphics2D graphics, final BoundingBox bBox, int index) {
        graphics.setColor(Color.GREEN);
        graphics.drawRect(bBox.getxMin(), bBox.getyMin(), bBox.getxMax() - bBox.getxMin(), bBox.getyMax() - bBox.getyMin());
        graphics.drawString(bBox.getClassName(), bBox.getxMin(), bBox.getyMin() - 5);
    }

    private BufferedImage rotateCard(final BufferedImage card, final Double angle) {
        // Rotate the card
        AffineTransform transform = new AffineTransform();

        Double offsetX = Math.sin(angle) * card.getHeight();
        Double offsetY = Math.sin(angle) * card.getWidth();

        transform.translate((offsetX > 0) ? offsetX : 0, (offsetY < 0) ? - offsetY : 0);
        transform.rotate(angle);

        int newWidth = (int) (card.getWidth() + Math.abs(offsetX));
        int newHeight = (int) (card.getHeight() + Math.abs(offsetY));

        // Rescale the card
        float largestSide = Math.max(newWidth, newHeight);
        double proportion = (cardSize + Math.abs(Math.sin(angle) * cardSize)) / largestSide;
        int rescaledWidth = (int) (newWidth * proportion);
        int rescaledHeight = (int) (newHeight * proportion);

        BufferedImage rotatedImage = new BufferedImage(rescaledWidth, rescaledHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D rotatedCardGraphics = (Graphics2D) rotatedImage.getGraphics();

        if (Config.BLUR) {
            rotatedCardGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        rotatedCardGraphics.drawImage(card, transform, null);
        rotatedCardGraphics.dispose();

        return rotatedImage;
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
        LOG.info("Loading, please wait. This process can take some time...");
        for (File file : Reader.listFilesFromDir(Config.SOURCE_DIR)) {
            BufferedImage image = ImageUtil.readImage(file);
            float largestSide = Math.max(image.getWidth(), image.getHeight());
            float proportion = cardSize / largestSide;
            int newWidth = (int) (image.getWidth() * proportion);
            int newHeight = (int) (image.getHeight() * proportion);
            images.add(ImageUtil.progressiveScaleImage(image, newWidth, newHeight));
            classNames.add(getClassName(file.getName()));
            LOG.info("{} loaded.", file.getName());
        }
        LOG.info("{} images to process.", images.size());
    }

    private String getClassName(final String fileName) {
        String withoutExtension = fileName.substring(0, fileName.indexOf('.'));
        for (String classLabel : Config.CLASS_LABELS) {
            if (withoutExtension.startsWith(classLabel)) {
                return classLabel;
            }
        }

        return withoutExtension;
    }

    private float calcCardSize() {
        float cardWidth = (float) (IMAGE_WIDTH - (COLS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) COLS;
        float cardHeight = (float) (IMAGE_HEIGHT - (ROWS-1) * MARGIN_BETWEEN - 2 * MARGIN_AROUND) / (float) ROWS;
        return Math.min(cardWidth, cardHeight);
    }
}
