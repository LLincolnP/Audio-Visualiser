package com.lawrence;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RunProgram { // called by com.lawrence.GUI when user presses "Run"
    public static void run(ImageCreator imageCreator, AudioData audioData, ImageToVideo imageToVideo) throws UnsupportedAudioFileException, IOException {
        // clear the frame of everything

        float[][] data = audioData.main();


        // generate a sample and pass it to the video creator. The sample lets the video creator properly set up size.
        BufferedImage sample = imageCreator.generateSample();
        imageToVideo.initiate(sample);
        imageToVideo.recordAudio();
        // generate an image for each data point, and record it in the video
        for (float[] singleSpectrum :data) {

            BufferedImage bufferedImage = imageCreator.generateImage(singleSpectrum);
            imageToVideo.recordImageFrame(bufferedImage);

        }

        imageToVideo.compileVideo(); // finished recording, now compile. NOTE: can be processor intensive.
        System.out.println("DONE DONE DONE");
    }
}
