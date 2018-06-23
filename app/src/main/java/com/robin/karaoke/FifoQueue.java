package com.robin.karaoke;

/**
 * Created by lixiangping on 2018/6/23.
 */

public class FifoQueue {
    private int queueLen = 1024 * 16;

    private short mData[];

    private int start = 0;

    private int end = 0;

    private int count = 0;

    public FifoQueue() {
        mData = new short[queueLen];
    }

    public FifoQueue(int len) {
        mData = new short[len < queueLen ? queueLen : len];
    }

    public int inData(short[] data, int size) {
        if(queueLen < size){
            size = queueLen;
        }
        for (int i = 0; i < size; i++) {
            mData[end % queueLen] = data[i];
            end++;
            count++;
        }
        if (queueLen <= end) {
            end = end % queueLen;
            start = end;
        }
        return size;
    }

    public int outData(short outdata[], int size) {
        int avail = 0;
        if (0 < count) {
            if (start < end) {
                avail = (end - start) > size ? size : (end - start);
            } else {
                avail = (end + queueLen - start > size) ? size : (end + queueLen - start);
            }
            for (int i = 0; i < avail; i++) {
                outdata[i] = mData[start % queueLen];
                start++;
                count--;
            }
        }
        return avail;
    }

}
