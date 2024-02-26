package com.lawrence;

import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;

public class ImageToVideo {
    // audio grabber and video recorder
    FFmpegFrameGrabber grabber;
    FFmpegFrameRecorder recorder;

    // converts types
    Java2DFrameConverter frameConverter = new Java2DFrameConverter();

    String savePath;
    String audioFilePath;

    public  void compileVideo() throws FFmpegFrameRecorder.Exception{
        recorder.stop(); // video is automatically compiled once the recorder stops running
    }

    public void recordImageFrame(BufferedImage bufferedImage) throws FFmpegFrameRecorder.Exception { // records given image into the video

        recorder.record(frameConverter.getFrame(bufferedImage, 1, true)); // channels are flipped to give correct image colour
    }

    public void recordAudio() throws FFmpegFrameRecorder.Exception, FFmpegFrameGrabber.Exception {

        this.grabber.start(); // start the grabber so it can grab audio frames
        this.recorder.setAudioChannels(grabber.getAudioChannels());
        this.recorder.start();

        Frame frame;
        // record audio
        while ((frame = grabber.grabFrame(true, true, true, false)) != null) {
            this.recorder.record(frame); // record the frame
        }
        this.grabber.stop(); // no longer need this
        this.recorder.setTimestamp(0); // go back to the start of the video so that recording of images can commence
    }

    public void initiate(BufferedImage sampleImage) { // this 'initiates' the class. It is done like this (rather than naming the method 'ImageToVideo' which would run
        this.grabber = new FFmpegFrameGrabber(audioFilePath); // need this for grabbing audio
        this.recorder = new FFmpegFrameRecorder(savePath, sampleImage.getWidth(), sampleImage.getHeight(), 2);
        this.recorder.setFrameRate(60);
        this.recorder.setFormat("mp4");
        this.recorder.setVideoQuality(0); // highest quality
    }
}
