package edu.data.generator;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import edu.data.generator.config.Config;
import edu.data.generator.model.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationGenerator {

    private final static Logger LOG = LoggerFactory.getLogger(AnnotationGenerator.class);
    private final Jinjava jinjava;

    public AnnotationGenerator() {
        jinjava = new Jinjava();
    }

    public void saveAnnotation(final List<BoundingBox> boxes, final String fileName) {
        Map<String, String> context = new HashMap();
        context.put("folder", "../Images");
        context.put("fileName", fileName + ".jpg");
        context.put("width", Config.IMAGE_WIDTH + "");
        context.put("height", Config.IMAGE_HEIGHT + "");
        context.put("objects", generateObjects(boxes));

        try {
            String template = Resources.toString(Resources.getResource("annotation-template.xml"), Charsets.UTF_8);
            writeToFile(jinjava.render(template, context), fileName);
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

    private void writeToFile(final String content, final String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(Config.TARGET_DIR + "/Annotations/" + fileName + ".xml"));
            writer.write(content);
            writer.close();
        } catch (IOException ex) {
            LOG.error("Unable to write file {}!", fileName);
        }
    }
}
