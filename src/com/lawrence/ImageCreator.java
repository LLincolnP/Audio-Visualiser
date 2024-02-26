package com.lawrence;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageCreator {
    // initiate with placeholder values
    int HEIGHT = 100;
    int WIDTH = 200;
    int BAND_WIDTH = 5;
    int BAND_GAP = 2;
    int NUM_BANDS = 4;

    Color barColour;
    Color backgroundColour;



    public BufferedImage generateImage(float[] spectrum){
        this.WIDTH = (BAND_WIDTH * NUM_BANDS) + (BAND_GAP) * (NUM_BANDS - 1);
        // create a buffered image of type (a, r, g, b)
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bufferedImage.createGraphics(); // graphics

        // fill with background colour
        // flip the r and b parts as the video generator messes with the positions, TODO: figure out why
        graphics2D.setColor(new Color(backgroundColour.getBlue(), backgroundColour.getGreen(), backgroundColour.getRed()));
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);

        int bandNum = 0; // used to calculate where to put the band rectangle
        // we have to switch around red and blue because of how buffered images are converted at a later stage in imageToVideo. TODO: Figure out why flipping image twice works when never flipping it doesn't.
        graphics2D.setColor(new Color(barColour.getBlue(), barColour.getGreen(), barColour.getRed(), 255));
        for (float band: spectrum){ // for band in spectrum
            // get x and y values for the rectangle
            int bandHeight = normaliseBand(band); // bring the band down to a good size

            int rectangleBottomLeftX = ((BAND_WIDTH * bandNum) + (BAND_GAP * bandNum)); // where the rectangle starts
            // draw the rectangle
            graphics2D.fillRect(rectangleBottomLeftX, HEIGHT - bandHeight, BAND_WIDTH, bandHeight);

            bandNum ++;
        }
        // free up memory
        graphics2D.dispose();


        return bufferedImage;
    }



    public BufferedImage generateSample(){ // generates an example image for the user to see
        Random random = new Random();
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        // fill background
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(backgroundColour);
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);
        graphics2D.setColor(barColour);

        for (int bandNum = 0; bandNum < NUM_BANDS; bandNum++) { // generate as many random bars as are required
            int bandValue = random.nextInt(0, HEIGHT); // generate a random value
            int rectangleTopLeftX = ((BAND_WIDTH * bandNum) + (BAND_GAP * bandNum)); // where it starts

            graphics2D.fillRect(rectangleTopLeftX, HEIGHT - bandValue, BAND_WIDTH, bandValue); // draw the band

        }

        return new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

    }


    private int normaliseBand(float band){ // get it down to a more reasonable number
        return (int) (band / (2_500_000 / HEIGHT));
    }

}
