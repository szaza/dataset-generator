package edu.data.generator.config;

public interface Config {
    String SOURCE_DIR = "../SetCards/TestInput";
    String BACKGROUND_DIR = "../SetCards/Backgrounds";
    String TARGET_DIR = "../SetCards/Out";
    Integer IMAGE_WIDTH = 416;
    Integer IMAGE_HEIGHT = 416;
    Integer ROWS = 4;
    Integer COLS = 3;
    Integer DATA_SET_SIZE = 5;
    Integer MARGIN_AROUND = 10; // 10px space around the playground
    Integer MARGIN_BETWEEN = 10; // 10px space between the cards
    Integer MAX_ANGLE_TO_ROTATE = 15;
}
