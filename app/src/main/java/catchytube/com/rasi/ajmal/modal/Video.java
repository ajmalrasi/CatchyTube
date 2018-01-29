package catchytube.com.rasi.ajmal.modal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by kpajm on 05-04-2017.
 */

public class Video implements Parcelable {
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            try {
                return new Video(in);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return new Video();
            }
        }

        public Video[] newArray(int size) {
            System.out.println("Video array");
            return new Video[size];
        }
    };
    private static HashMap<String, String> regexMap = new HashMap<>();
    private static String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
    private static String two = "0$1";
    private String id;
    private String title;
    private String thumbnails;
    private String views;
    private String publishedDate;
    private String likes;
    private String disLikes;
    private String category;
    private String channel;
    private String duration;
    private String description;

    public Video() {

    }

    public Video(Parcel input) {
        id = input.readString();
        title = input.readString();
        thumbnails = input.readString();
        views = input.readString();
        publishedDate = input.readString();
        likes = input.readString();
        disLikes = input.readString();
        category = input.readString();
        channel = input.readString();
        duration = input.readString();
        description = input.readString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String getRegex(String date) {
        for (String r : regexMap.keySet())
            if (Pattern.matches(r, date))
                return r;
        return null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getViews() {
        if (views != null) {
            Locale locale = new Locale("EN", "US");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            return numberFormat.format(Double.valueOf(views)) + " views";
        } else {
            return "";
        }

    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getPublishedDate() {
        DateTime dateTime = new DateTime(this.publishedDate);
        long time = dateTime.getMillis() / 1000;
        long unixTimestamp = Instant.now().getMillis() / 1000;
        long diff = unixTimestamp - time;
        Map<Integer, String> token = new LinkedHashMap<>();
        token.put(31536000, "year");
        token.put(2592000, "month");
        token.put(604800, "day");
        token.put(86400, "week");
        token.put(3600, "hour");
        token.put(60, "minute");
        token.put(1, "second");
        for (Map.Entry<Integer, String> entry : token.entrySet()) {
            if (diff > entry.getKey()) {
                long unit = diff / entry.getKey();
                if (unit > 1) {
                    return "Published " + unit + " " + entry.getValue() + "s ago";
                }
                return "Published " + unit + " " + entry.getValue() + " ago";
            }
        }
        return "";
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getLikes() {
        if (this.likes != null) {
            double like = Integer.parseInt(this.likes);
            double views = Integer.parseInt(this.likes) + Integer.parseInt(this.disLikes);
            return (int) ((like / views) * 100);
        }
        return 100;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setDisLikes(String disLikes) {
        this.disLikes = disLikes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    @Override
    public int describeContents() {
        Log.i("Video", "describe contents");
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        Log.i("Video","write to parcel");
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(thumbnails);
        dest.writeString(views);
        dest.writeString(publishedDate);
        dest.writeString(likes);
        dest.writeString(disLikes);
        dest.writeString(category);
        dest.writeString(channel);
        dest.writeString(duration);
        dest.writeString(description);
    }

    public String getDuration() {
        if (duration != null) {
            regexMap.put("PT(\\d\\d)S", "0:$1");
            regexMap.put("PT(\\d\\d)M", "$1:00");
            regexMap.put("PT(\\d\\d)H", "$1:00:00");
            regexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
            regexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
            regexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
            regexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");
            String time = this.duration;
            String d = time.replaceAll(regex2two, two);
            String regex = getRegex(d);
            if (regex == null) {
                System.out.println(d + ": invalid");
            }
            return d.replaceAll(regex, regexMap.get(regex));
        }
        return "";
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}
