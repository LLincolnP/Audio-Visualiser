package com.lawrence;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import com.badlogic.audio.analysis.FFT;
// this class extracts the audio data from the specified file path and converts it to a usable form.
public class AudioData {
    int numBands;
    File audioFile;
    public float[][] main() throws UnsupportedAudioFileException, IOException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile); // create audio stream with the file

        byte[] rawData = getAudioData(audioInputStream); // extract raw data
        float[][] spectrumAudio = this.rawDataToSpectrum(rawData); // convert it to a spectrum using a furrier transform
        float[][] correctBandAudio = this.getBands(numBands, spectrumAudio); // delete excess bands

        return averageSpectrum(correctBandAudio); // average the data so that the fps of the video will be 60.
    }

    private static byte[] getAudioData(AudioInputStream audioInputStream) throws IOException { // converts audio stream to bytes.
        // now we need to actually read the data, but to do that we need to know how many bytes the data is
        int frameLength = (int) audioInputStream.getFrameLength();
        int frameNum = audioInputStream.getFormat().getFrameSize(); // how many frames there are

        byte[] bytes = new byte[frameLength * frameNum];

        int bytesRead = audioInputStream.read(bytes); // bytes has now been updated with the read data

        // return the raw data
        return bytes;

    }

    /* This is not my code. I got it from
        https://web.archive.org/web/20090221151851/http://codeidol.com/java/swing/Audio/Build-an-Audio-Waveform-Display/#swinghacks-CHP-10-FIG-4
     */
    private int getSixteenBitSample(int high, int low) { // gets sixteen bits
        return (high << 8) + (low & 0x00ff);
    }

    private float[] performFFT(float[] samples) { // runs the Fast furrier transform on the samples
        FFT fft = new FFT(512, 44100);
        fft.forward(samples);
        return fft.getSpectrum();
    }

    private float[][] rawDataToSpectrum(byte[] bytes) { // uses an FFT to create a spectrum.
        // NOTE: 512 is a power of 2 which is required to run the FFT. 1024 is used as it is twice this.

        int totalNumberSampleLists = (bytes.length / 1024);
        // number that keeps track of where we are in all the sample data

        float[][] fTTSpectrumList = new float[totalNumberSampleLists][512];
        // iterate over the number of sample lists we are creating and add a list for each one.
        int byteIndex = 0;
        for (int sampleListIndex = 0; sampleListIndex < totalNumberSampleLists; sampleListIndex++) {

            float[] samplesToProcess = new float[512];

            for (int i = 0; i < 512; i++) { // 512 at a time (for reason see note), combine channels and save it to the list to be processed.
                if (byteIndex == bytes.length) {
                    break;
                } // make sure we don't try to access an index that doesn't exist


                // combine channels
                int lowChannel = bytes[byteIndex];
                byteIndex++;
                int highChannel = bytes[byteIndex];
                byteIndex++;
                int sample = this.getSixteenBitSample(highChannel, lowChannel);

                // save to list
                samplesToProcess[i] = (float) sample;
            }
            fTTSpectrumList[sampleListIndex] = this.performFFT(samplesToProcess);
        }
        return fTTSpectrumList;
    }

    private float[][] getBands(int numBands, float[][] spectrum) { // saves only the specified number of bands from the spectrum
        float[][] bands = new float[spectrum.length][numBands];

        for (int spectrumNum = 0; spectrumNum < spectrum.length; spectrumNum++) {

            for (int bandNum = 0; bandNum < numBands; bandNum++) { // only save the num of bands specified
                bands[spectrumNum][bandNum] = spectrum[spectrumNum][bandNum];
            }
        }

        return bands;
    }

    private float[] averageList(float[] list, int numberToAverage){ // averages each value in a list by the number specified
        for (int i = 0; i < list.length; i++) {
            list[i] /= numberToAverage;
        }
        return list;
    }
    private float[][] averageSpectrum(float[][] fullSpectrum) { //Averages the full spectrum
        int numBands = fullSpectrum[0].length;

        float spectrumsPerAverage = (float)6.25 / 2;
        float[][] averagedSpectrums = new float[(int) (fullSpectrum.length / spectrumsPerAverage)][numBands];
        int averageSpectrumIndex = 0;   // keeps track of what index we are up to in the above list
        float[] currentSpectrumAverage = new float[numBands];
        int endIndex = 5; // the last one of the first group
        int spectrumsToBeAveraged = 0;


        for (int spectrumNum = 0; spectrumNum < fullSpectrum.length; spectrumNum++) { // run this for the length of the spectrum

            if (endIndex < spectrumNum){ // we have collected the right amount to average.
                // average and add to average list
                averagedSpectrums[averageSpectrumIndex] = averageList(currentSpectrumAverage, spectrumsToBeAveraged);
                averageSpectrumIndex += 1;
                endIndex = (int) (spectrumsPerAverage * (averageSpectrumIndex + 1));

                // reset vars
                spectrumsToBeAveraged = 0;
                currentSpectrumAverage = new float[numBands];
            }

            for (int bandNum = 0; bandNum < fullSpectrum[spectrumNum].length; bandNum++) { // append to the spectrumList
                currentSpectrumAverage[bandNum] += fullSpectrum[spectrumNum][bandNum];
            }
            spectrumsToBeAveraged += 1;
        }
        return averagedSpectrums;
    }


}
