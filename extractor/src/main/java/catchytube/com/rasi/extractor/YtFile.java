package catchytube.com.rasi.extractor;

/**
 * Created by kpajm on 21-03-2017.
 */

public class YtFile {

    private Format format;
    private String url = "";

    YtFile(Format format, String url) {
        this.format = format;
        this.url = url;
    }

    /**
     * The url to download the file.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Format data for the specific file.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Format data for the specific file.
     */
    @Deprecated
    public Format getMeta(){
        return format;
    }
}
