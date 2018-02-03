package edu.data.generator.config;

public interface Config {
    String SOURCE_DIR = "../SetCards/Cards";
    String BACKGROUND_DIR = "../SetCards/Backgrounds";
    String TARGET_DIR = "../SetCards/Out";
    Integer IMAGE_WIDTH = 416;
    Integer IMAGE_HEIGHT = 416;
    Integer ROWS = 4;
    Integer COLS = 3;
    Integer DATA_SET_SIZE = 5;
    Integer MARGIN_AROUND = 10; // 10px margin around the cards
    Integer MARGIN_BETWEEN = 10; // 10px space between the cards
}
