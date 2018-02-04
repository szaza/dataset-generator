package edu.data.generator.config;

public interface Config {
    String SOURCE_DIR = "./dataset/cards";
    String BACKGROUND_DIR = "./dataset/backgrounds";
    String TARGET_DIR = "./dataset/output";
    Integer IMAGE_WIDTH = 416;
    Integer IMAGE_HEIGHT = 416;
    Integer ROWS = 4;
    Integer COLS = 3;
    Integer DATA_SET_SIZE = 5;
    Integer MARGIN_AROUND = 10; // 10px space around the playground
    Integer MARGIN_BETWEEN = 10; // 10px space between the cards
    Integer MAX_ANGLE_TO_ROTATE = 15;
    boolean DEBUG = true;
}
