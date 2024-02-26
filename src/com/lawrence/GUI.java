package com.lawrence;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

// This class allows the run and control all aspects of the program, so they can make the video they want.
public class GUI extends JFrame implements ChangeListener, ActionListener {
    //frame (The window) and panel
    static JFrame frame;
    static JPanel panel;
    // all the buttons
    static JButton audioFileButton;
    static JButton savePathButton;

    static JButton barColourButton;
    static JButton backgroundColourButton;
    // bools that keep track of which variables have been set. The file paths and colours all need to be selected by the user
    static boolean audioFileSelected = false;
    static boolean savePathSelected = false;
    static boolean barColourSelected = false;
    static boolean backgroundColourSelected = false;

    static JButton runButton;


    // sliders
    static JSlider bandWidthSlider;
    static JSlider bandSpaceSlider;
    static JSlider bandHeightSlider;
    static JSlider numberOfBandsSlider;

    // labels for the sliders
    static JLabel widthLabel;
    static JLabel spaceLabel;
    static JLabel heightLabel;
    static JLabel numOfBandsLabel;

    // label that informs the user the total width of the video
    static JLabel videoWidthLabel;

    static JLabel sampleImage; // is meant to show the user what their video is going to look like. Currently not working. TODO: fix.

    // set up the other classes
    static ImageCreator imageCreator;
    static AudioData audioData;
    static ImageToVideo imageToVideo;

    public void initialise() {
        // setup classes
        imageCreator = new ImageCreator();
        audioData = new AudioData();
        imageToVideo = new ImageToVideo();

        // initiate frame and panel
        frame = new JFrame("Audio Visualiser");
        panel = new JPanel();
        sampleImage = new JLabel();
        //sampleImage.setIcon(new ImageIcon(""));
        sampleImage.setVisible(true);

        // set up all the buttons

        audioFileButton = new JButton("Upload audio");
        this.setupComponent(audioFileButton);

        savePathButton = new JButton("Save as");
        this.setupComponent(savePathButton);

        barColourButton = new JButton("Bar colour...");
        this.setupComponent(barColourButton);

        backgroundColourButton = new JButton("Background Colour");
        this.setupComponent(backgroundColourButton);


        // setup sliders
        bandWidthSlider = new JSlider();
        setupComponent(bandWidthSlider, "Band Width", 0, 50, 5, 1, 500, 50);
        widthLabel = new JLabel();
        this.setupComponent(widthLabel, "Band Width");

        bandSpaceSlider = new JSlider();
        setupComponent(bandSpaceSlider, "Band Spacing", 0, 50, 5, 1, 500, 50);
        spaceLabel = new JLabel();
        this.setupComponent(spaceLabel, "Band Spacing");

        videoWidthLabel = new JLabel();
        this.setupComponent(videoWidthLabel, "Width: ");

        bandHeightSlider = new JSlider();
        setupComponent(bandHeightSlider, "Band Height", 0, 1440, 90, 12, 600, 50);
        heightLabel = new JLabel();
        this.setupComponent(heightLabel, "Max band height");

        numberOfBandsSlider = new JSlider();
        setupComponent(numberOfBandsSlider, "Number of bands", 1, 20, 1, 0, 500, 50);
        numOfBandsLabel = new JLabel();
        this.setupComponent(numOfBandsLabel, "Number of bands");

        // setup final button
        runButton = new JButton("Run Program");
        this.setupComponent(runButton);


        // add everything to the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // make frame close when "x" tab is hit.
        panel.add(sampleImage);
        frame.add(panel);


        frame.pack();
        frame.setSize(900, 600);

        frame.setVisible(true);
    }

    // overloaded function setupComponent sets up the given component depending on what it is.
    private void setupComponent(JButton button) { // initialises them and adds them to the list of buttons
        // = new JButton(title);
        button.addActionListener(this);
        panel.add(button);
    }

    // slider version of the function
    private void setupComponent(JSlider slider, String name, int min, int max, int majorSpacing, int minorSpacing, int width, int height) {


        // set up ticks and labels
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setName(name);
        slider.setToolTipText(name);

        //spacing
        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing(minorSpacing);

        slider.setPreferredSize(new Dimension(width, height));

        // add listener so that we know when the value is changed.
        slider.addChangeListener(this);

        panel.add(slider); // add it to the panel
    }

    // setups label
    private void setupComponent(JLabel label, String text) {
        label.setText(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVisible(true);
        panel.add(label);

    }


    private String audioPathFrame(String requiredExtension) {
        JFileChooser fileChooser = new JFileChooser();

        int returnValue;
        if (requiredExtension.equals(".wav")) {
            // filter out any files other than .wav
            fileChooser.setFileFilter(new FileNameExtensionFilter("wav files", "wav"));
            fileChooser.setDialogTitle("Open...");
            returnValue = fileChooser.showOpenDialog(null); // open file browser in file open mode
        } else {
            fileChooser.setDialogTitle("Save as...");
            // filter out files
            fileChooser.setFileFilter(new FileNameExtensionFilter("mp4 files", "mp4"));
            returnValue = fileChooser.showSaveDialog(null); // open file browser in save mode
        }


        if (returnValue == JFileChooser.APPROVE_OPTION) {  // check that the user clicked "ok" and not "cancel"
            // get the file selected and convert to string
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = fileChooser.getSelectedFile().toPath().toString();
            return compareExtension(selectedFile, requiredExtension) ? fileName : fileName + requiredExtension;  // add the extension name if needed
        } else
            return null;

    }

    private boolean compareExtension(File file, String extension) { // returns whether file has given extension
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf("."); // get where the final dot is in the string
        if (lastDotIndex > -1) {
            return fileName.substring(lastDotIndex).equals(extension);
        } else {
            return false; // last dot index is less than 0 which means there is no extension.
        }
    }

    private Color runColourSelector() { // shows the colour selecting dialog.
        return JColorChooser.showDialog(null, "Select colour", Color.black);
    }

    private void updateWidthLabelText() { // updates the text that displays the video's total width
        videoWidthLabel.setText("       Video Width: " + ((bandWidthSlider.getValue() * numberOfBandsSlider.getValue()) + (bandSpaceSlider.getValue() * (numberOfBandsSlider.getValue() - 1))));
    }

    private void showErrorMessage(String[] errorMessages) { // gives a popup error message
        // set up frame and panel
        JFrame errorFrame = new JFrame("Error!");
        JPanel errorPanel = new JPanel();
        errorFrame.setPreferredSize(new Dimension(260, 200));


        // add instructional line
        errorPanel.add(new JLabel("Please resolve the following:"));

        // iterate over the provided errors and add them to the panel
        for (String error : errorMessages) {
            System.out.println(error);
            errorPanel.add(new JLabel(error));
        }

        // add panel to frame and show frame.
        errorFrame.add(errorPanel);
        errorFrame.pack();
        errorFrame.setVisible(true);
    }

    @Override // looks at slider states changed
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        // all the sliders. Change the value of the image creator.
        if (source == bandWidthSlider) {
            imageCreator.BAND_WIDTH = bandWidthSlider.getValue();
            updateWidthLabelText();

        } else if (source == bandSpaceSlider) {
            imageCreator.BAND_GAP = bandSpaceSlider.getValue();
            updateWidthLabelText();

        } else if (source == bandHeightSlider) {
            imageCreator.HEIGHT = bandHeightSlider.getValue();

        } else if (source == numberOfBandsSlider) {
            imageCreator.NUM_BANDS = numberOfBandsSlider.getValue();
            audioData.numBands = numberOfBandsSlider.getValue();
            updateWidthLabelText();
        }

    }

    @Override // looks at button action
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource(); // find out which component was modified

        // file controls
        if (source == audioFileButton) {
            String audioPath = audioPathFrame(".wav");
            if (audioPath != null) { // ensure that a file was actually provided
                imageToVideo.audioFilePath = audioPath;
                audioData.audioFile = new File(audioPath);

                audioFileSelected = true;
            }

        } else if (source == savePathButton) {
            String savePath = audioPathFrame(".mp4");
            if (savePath != null) {
                imageToVideo.savePath = savePath;
                savePathSelected = true;
            }


            // colour controls
        } else if (source == barColourButton) {
            Color selectedColour = this.runColourSelector();
            if (selectedColour != null) { // make sure the user actually selected a colour
                imageCreator.barColour = selectedColour;
                barColourSelected = true; // set the bool to true
            }

        } else if (source == backgroundColourButton) {
            Color selectedColour = this.runColourSelector();
            if (selectedColour != null) {// make sure the user actually selected a colour
                imageCreator.backgroundColour = selectedColour;
                backgroundColourSelected = true; // set the bool to true
            }


            // run the program
        } else if (source == runButton) {
            System.out.println("RUN");
            // ensure that everything that needs to be selected has been
            if (barColourSelected && backgroundColourSelected && audioFileSelected && savePathSelected) {
                try {
                    RunProgram.run(imageCreator, audioData, imageToVideo);
                    //this.runProgram();
                } catch (UnsupportedAudioFileException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{ // there are some errors
                String[] errors = new String[4];
                // append possible errors by giving a string only if the associated bool has not been set
                errors[0] = !audioFileSelected ? "Please select a valid audio file" : null;
                errors[1] = !savePathSelected ? "Please select a valid save path" : null;
                errors[2] = !barColourSelected ? "Please select a valid bar colour" : null;
                errors[3] = !backgroundColourSelected ? "Please select a valid background" : null;

                this.showErrorMessage(errors); // run the popup method.
            }
        }
    }
}