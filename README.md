# Dataset generator for Machine Learning projects
## It supports the VOC and Darknet style datasets
It is a very simple program to generate Pascal VOC style learning dataset from images. It generates images and XML style annotations with bounding box coordinates.

### Motivation

I've created this tool because I was needed to generate a large set of data for my machine learning project. I wanted to train a YOLO model with SET game cards. In my case I had to create some learning data like this one:

<img src="https://github.com/szaza/dataset-generator/blob/master/dataset/original/0005.jpg" alt="Set card grid" title="Generated playground" width="300">

I also needed to generate the annotation file for each item.
The current solution makes possible to generate similar learning data with annotations what contains the coordinates of the bounding boxes.

You can find below some generated data:

<img src="https://github.com/szaza/dataset-generator/blob/master/dataset/output/VOCDataSet/JPEGImages/card-0.jpg" alt="Generated playground" title="Generated playground" width="300"> <img src="https://github.com/szaza/dataset-generator/blob/master/dataset/output/VOCDataSet/JPEGImages/card-1.jpg" alt="Generated playground2" title="Generated playground2" width="300">

### Configuration
There are some configuration possibilities. Unfortunately, I didn't create any property file for these, so you have to set the parameters in the `Configuration.java` interface.

    String SOURCE_DIR = "./dataset/cards";
    String BACKGROUND_DIR = "./dataset/backgrounds";
    String TARGET_DIR = "./dataset/output";
    String DATASET_DIR = "/VOCDataSet/";
    String IMAGES_DIR = "/JPEGImages/";
    String ANNOTATIONS_DIR = "/Annotations/";
    String DARKNET_LABELS = "/labels/";
    String DARKNET_CONFIG_DIR = "/DarknetConf/";
    String VOC_LABELS = "/VocLabels/";    
    Integer IMAGE_WIDTH = 416;
    Integer IMAGE_HEIGHT = 416;
    Integer ROWS = 4; // How many rows you need to generate
    Integer COLS = 3; // How many columns you need
    Integer DATA_SET_SIZE = 5; // How many data should be generated
    Integer VAL_SET_SIZE = 5; // How many validation data should be generated (it generates only the val.txt)
    Integer MARGIN_AROUND = 10; // 10px space around the playground
    Integer MARGIN_BETWEEN = 10; // 10px space between the cards
    Integer MAX_ANGLE_TO_ROTATE = 15; // The maximum rotation angle of the cards
    boolean BLUR = true; // If it is true, it uses BICUBIC interpolation for each resize operation
    boolean DEBUG = true; // You are able to draw the bounding boxes to the cards
    boolean DARKNET = true; // Generates labels and train-val.txt for darknet

### Installation
Verys simple to install and run the project, just run the following commands from command propmt:

`./gradlew clean build -xtest` - it builds the project

`./gradlew run` - to run the project

### Demo dataset
In order to try it out you can download some demo data from the following links:
- [Backgrounds](https://drive.google.com/open?id=1k2tQTgyuAuCpzkEq3tqSDhjpsKTfDn_z)
- [Cards](https://drive.google.com/open?id=1adU-sSAeNNnd7fKqPAhasQdN56FxYI-d)

Save and extract them respectively under the `dataset/backgrounds`, `dataset/cards`.

### Output
* **output/**
  * **DarknetConf/** - contains the darknet configuration files
    * **voc.names** - similar to the labels.txt. 
    * **train.txt** - it contains the paths for the images
    * **val.txt** - it contains the paths for the images
    * **voc.data** - data file for darknet framework  
  * **VOCDataSet/** - contains the Pascal VOC dataset
    * **Annotations/** - contains the xml style annotations
    * **JPEGImages/** - it contains the generated images
    * **labels/** - it generates the *.txt files for darknet
  * **VOCLabels/**  
    * **labels.txt** - label file needed to darkflow
  


