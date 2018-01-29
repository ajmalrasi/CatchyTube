package com.golshadi.majid.report;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/10/2014.
 */
public class ReportStructure {

    public int id;
    public String videoId;
    public String name;
    public int state;
    public String url;
    public long fileSize;
    public boolean resumable;
    public String type;
    public int chunks;
    public double percent;
    public long downloadLength;
    public String saveAddress;
    public boolean priority;
    public String quality;
    public boolean isDash;
    private long audioLength;

    public long setAudioLength(long audioDLength) {
        return audioLength += audioDLength;
    }

    public long setDownloadLength(long downloadedLength){
        return downloadLength += downloadedLength;
    }

    public long getTotalSize(){
        return fileSize;
    }

    public boolean isResumable(){
        return resumable;
    }

    public ReportStructure setObjectValues(Task task, List<Chunk> taskChunks){
        this.id = task.id;
        this.videoId = task.videoId;
        this.name = task.name;
        this.state = task.state;
        this.quality = task.quality;
        this.resumable = task.resumable;
        this.url = task.url;
        this.fileSize = task.size;
        this.type = task.extension;
        this.isDash = task.isDash;
        this.chunks = task.chunks;
        this.priority = task.priority;
        this.saveAddress = task.save_address+"/"+task.name+"."+task.extension;
        if (task.percent == 100.0){
            this.percent = task.percent;
            this.downloadLength = task.size;
        }else{
            this.percent = calculatePercent(task, taskChunks);
        }
        //this.chunkData = caluclateChunkPercent(task,taskChunks);
        return this;
    }


    public ArrayList<Long> caluclateChunkPercent(Task task, List<Chunk> chunks){
        ArrayList<Long> chunkReports = new ArrayList<>();
        for (int i =0;i<chunks.size();i++) {
            long chunkSize = chunks.get(i).end - chunks.get(i).begin;
            long chunkCompletedSize = FileUtils.size(task.save_address, String.valueOf(chunks.get(i).id));
            long chunkReport = (chunkCompletedSize / chunkSize) * 100;
            chunkReports.add(chunkReport);
        }
        return chunkReports;
    }

    /** calculate download percent from compare chunks size with real file size **/
    private double calculatePercent(Task task, List<Chunk> chunks){
    	// initialize report
    	double report = 0;
    	
    	// if download not completed we have chunks 
    	if (task.state != TaskStates.END) {
	        int sum = 0;

	        for (Chunk chunk : chunks){

                this.downloadLength += FileUtils.size(task.save_address, String.valueOf(chunk.id));
	        }
	
	        if (task.size > 0) {
                //download progress in float value
	            report = ((float)downloadLength / task.size * 100);
	        }   
    	} else {
    		this.downloadLength = task.size;
    		report = 100;
    	}
    	
    	return report;
    }


    public JSONObject toJsonObject(){
        JSONObject json = new JSONObject();
        try {
            return json.put("token", String.valueOf(id))
                    .put("name", name)
                    .put("state", state)
                    .put("resumable", resumable)
                    .put("fileSize", fileSize)
                    .put("url", url)
                    .put("type", type)
                    .put("chunks", chunks)
                    .put("percent", percent)
                    .put("downloadLength", downloadLength)
                    .put("saveAddress", saveAddress)
                    .put("priority", priority);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
