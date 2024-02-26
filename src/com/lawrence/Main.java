package com.lawrence;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        GUI gui = new GUI(); // everything is run by the user from the GUI.
        gui.initialise();
    }
}
