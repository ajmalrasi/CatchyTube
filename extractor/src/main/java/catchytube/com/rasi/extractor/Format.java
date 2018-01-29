package catchytube.com.rasi.extractor;

/**
 * Created by kpajm on 21-03-2017.
 */

public class Format {

    public enum VCodec {
        H263, H264, MPEG4, VP8, VP9, NONE
    }

    public enum ACodec {
        MP3, AAC, VORBIS, OPUS, NONE
    }

    private int itag;
    private String ext;
    private int height;
    private int fps;
    private VCodec vCodec;
    private ACodec aCodec;
    private int audioBitrate;
    private boolean isDashContainer;
    private boolean isHlsContent;

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = -1;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, VCodec vCodec, ACodec aCodec, int audioBitrate, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = -1;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, int audioBitrate,
           boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, int audioBitrate,
           boolean isDashContainer, boolean isHlsContent) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = isHlsContent;
    }

    Format(int itag, String ext, int height, VCodec vCodec, int fps, ACodec aCodec, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.audioBitrate = -1;
        this.fps = fps;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    /**
     * Get the frames per second
     */
    public int getFps() {
        return fps;
    }

    /**
     * Audio bitrate in kbit/s or -1 if there is no audio track.
     */
    public int getAudioBitrate() {
        return audioBitrate;
    }

    /**
     * An identifier used by youtube for different formats.
     */
    public int getItag() {
        return itag;
    }

    /**
     * The file extension and conainer format like "mp4"
     */
    public String getExt() {
        return ext;
    }

    public boolean isDashContainer() {
        return isDashContainer;
    }

    public ACodec getAudioCodec() {
        return aCodec;
    }

    public VCodec getVideoCodec() {
        return vCodec;
    }

    public boolean isHlsContent() {
        return isHlsContent;
    }

    /**
     * The pixel height of the video stream or -1 for audio files.
     */
    public int getHeight() {
        return height;
    }

}

