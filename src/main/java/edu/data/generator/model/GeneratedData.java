package edu.data.generator.model;

import java.awt.image.BufferedImage;
import java.util.List;

public class GeneratedData {
    private BufferedImage image;
    private List<BoundingBox> boxes;

    public GeneratedData(BufferedImage image, List<BoundingBox> boxes) {
        this.image = image;
        this.boxes = boxes;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public List<BoundingBox> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<BoundingBox> boxes) {
        this.boxes = boxes;
    }
}
