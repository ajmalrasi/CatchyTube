package com.golshadi.majid.report;

/**
 * Created by kpajm on 31-03-2017.
 */

public class ChunkData {
    private double percentage;
    private int taskId;
    private int size;



    public void setChunkDetails(int Id, long end,long start){

        size = (int) (end - start);


    }




}
