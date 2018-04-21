package edu.data.generator;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import edu.data.generator.config.Config;
import edu.data.generator.model.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.data.generator.config.Config.ANNOTATIONS_DIR;
import static edu.data.generator.config.Config.DARKNET_CONFIG_DIR;
import static edu.data.generator.config.Config.DARKNET_LABELS;
import static edu.data.generator.config.Config.DATASET_DIR;
import static edu.data.generator.config.Config.IMAGES_DIR;
import static edu.data.generator.config.Config.VOC_LABELS;

public class AnnotationGenerator {

    private final static Logger LOG = LoggerFactory.getLogger(AnnotationGenerator.class);
    private final Jinjava jinjava;

    public AnnotationGenerator() {
        jinjava = new Jinjava();
    }

    public void saveAnnotation(final String[] classNames, final List<BoundingBox> boxes, final String fileName) {
        saveInVocFormat(boxes, fileName);
        if (Config.DARKNET) {
            saveInDarknetFormat(classNames, boxes, fileName);
        }
    }

    public void generateLabels(final String[] classNames) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String className : classNames) {
            stringBuilder.append(className);
            stringBuilder.append(System.getProperty("line.separator"));
        }

        writeToFile(stringBuilder.toString(), Config.TARGET_DIR + VOC_LABELS + "/labels.txt");

        if (Config.DARKNET) {
            writeToFile(stringBuilder.toString(), Config.TARGET_DIR + DARKNET_CONFIG_DIR + "/voc.names");
            createDataFile(classNames);
            createDataSet("train.txt", Config.DATA_SET_SIZE);
            createDataSet("val.txt", Config.VAL_SET_SIZE);
        }
    }

    private void createDataFile(final String[] classNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("classes = ");
        sb.append(classNames.length);
        sb.append(System.getProperty("line.separator"));
        sb.append("train = ");
        sb.append(new File(formatPath(Config.TARGET_DIR + DARKNET_CONFIG_DIR) + "/train.txt").getAbsolutePath());
        sb.append(System.getProperty("line.separator"));
        sb.append("valid = ");
        sb.append(new File(formatPath(Config.TARGET_DIR + DARKNET_CONFIG_DIR) + "/val.txt").getAbsolutePath());
        sb.append(System.getProperty("line.separator"));
        sb.append("names = data/voc.names");
        sb.append(System.getProperty("line.separator"));
        sb.append("backup = backup");
        writeToFile(sb.toString(), Config.TARGET_DIR + DARKNET_CONFIG_DIR + "/voc.data");
    }

    private void createDataSet(final String name, final Integer size) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<size; i++) {
            sb.append(new File(formatPath(Config.TARGET_DIR) + DATASET_DIR + IMAGES_DIR + i + ".jpg").getAbsolutePath());
            sb.append(System.getProperty("line.separator"));
        }
        writeToFile(sb.toString(), Config.TARGET_DIR + DARKNET_CONFIG_DIR + "/" + name);
    }

    private String formatPath(final String path) {
        return path.substring(2, path.length());
    }

    private void saveInDarknetFormat(final String[] classNames, final List<BoundingBox> boxes, final String fileName) {
        Map<String, Integer> map = getClassNamesMap(classNames);

        StringBuilder sb = new StringBuilder();
        for (BoundingBox bBox:boxes) {
            sb.append(map.get(bBox.getClassName()));
            sb.append(" ");
            double [] bBoxCoords = convertToDarknetFormat(bBox);
            for (double param : bBoxCoords) {
                sb.append(param);
                sb.append(" ");
            }
            sb.append(System.getProperty("line.separator"));
        }
        writeToFile(sb.toString(), Config.TARGET_DIR + DATASET_DIR + DARKNET_LABELS + fileName + ".txt");
    }

    private void saveInVocFormat(final List<BoundingBox> boxes, final String fileName) {
        Map<String, String> context = new HashMap();
        context.put("folder", ".." + IMAGES_DIR);
        context.put("fileName", fileName + ".jpg");
        context.put("width", Config.IMAGE_WIDTH + "");
        context.put("height", Config.IMAGE_HEIGHT + "");
        context.put("objects", generateObjects(boxes));

        try {
            String template = Resources.toString(Resources.getResource("annotation-template.xml"), Charsets.UTF_8);
            writeToFile(jinjava.render(template, context), Config.TARGET_DIR + DATASET_DIR + ANNOTATIONS_DIR + fileName + ".xml");
        } catch (IOException ex) {
            LOG.error("Unable to save annotation xml!");
        }
    }

    private String generateObjects(final List<BoundingBox> boxes) {
        String objects = "";

        try {
            String objectTemplate = Resources.toString(Resources.getResource("object-template.xml"), Charsets.UTF_8);
            for (BoundingBox box : boxes) {
                Map<String, String> context = new HashMap();
                context.put("xMin", box.getxMin() + "");
                context.put("xMax", box.getxMax() + "");
                context.put("yMin", box.getyMin() + "");
                context.put("yMax", box.getyMax() + "");
                context.put("className", box.getClassName());
                objects += jinjava.render(objectTemplate, context);
            }
        } catch (IOException ex) {
            LOG.error("Unable to read object-template.xml!");
        }

        return objects;
    }

    private Map<String, Integer> getClassNamesMap(final String[] classNames) {
        Map<String, Integer> map = new HashMap();
        for (int i=0; i < classNames.length; i++) {
            map.put(classNames[i], i);
        }
        return map;
    }

    private double[] convertToDarknetFormat(final BoundingBox boundingBox) {
        double dw = 1d / (double) Config.IMAGE_WIDTH;
        double dh = 1d / (double) Config.IMAGE_HEIGHT;
        double x = (double) (boundingBox.getxMin() + boundingBox.getxMax()) / 2d - 1d;
        double y = (double) (boundingBox.getyMin() + boundingBox.getyMax()) / 2d - 1d;
        double w = (double) (boundingBox.getxMax() - boundingBox.getxMin());
        double h = (double) (boundingBox.getyMax() - boundingBox.getyMin());
        x = x * dw;
        w = w * dw;
        y = y * dh;
        h = h * dh;
        return new double[]{x,y,w,h};
    }

    private void writeToFile(final String content, final String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(fileName));
            writer.write(content);
            writer.close();
        } catch (IOException ex) {
            LOG.error("Unable to write file {}!", fileName);
        }
    }
}
