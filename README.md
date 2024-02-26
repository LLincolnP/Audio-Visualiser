# Audio-Visualiser
Java program designed to create visual representation of audio

## Example video created from sample audio (Beach by MBB)
[Video Example.webm](https://github.com/Lazzza2805/Audio-Visualiser/assets/104611224/d4a6df3a-dab7-44df-903d-f70b60c5997d)


## How it works
- First, the raw audio data bytes and combined into single channel samples.
- These samples are then passed through badlogic's Fast Fourier Transform (FFT)
- The `getSpectrum()` method is then run after the FFT, thus providing the strength of different frequency bands.
- Most of the bands are discarded, as only the first 20 or so have a useful, visualisable strength
- Thes bands are then turned into a series of images, like so:
- ![Sample image](https://github.com/Lazzza2805/Audio-Visualiser/assets/104611224/7227ff44-79bf-487a-804a-00aa7789bbcf)

- The images are then combined at 60fps to form the video.

## Known Issues:
The library used to combine images into video automatically chooses which codex to use. This results in some videos being audio only for most video players, as the codex is not supported. VLC media player is a good get around, as it will download the codex for you.
