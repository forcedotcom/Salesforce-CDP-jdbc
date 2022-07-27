package com.salesforce.cdp.queryservice.util;

import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlExtractQueryResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class RainbowDataStream extends InputStream {
    Iterator<AnsiSqlExtractQueryResponse> streamIterator;
    byte[] arrowMessage;

    int curIndex;

    public RainbowDataStream(Iterator<AnsiSqlExtractQueryResponse> responseIterator){
        this.streamIterator = responseIterator;
        curIndex =0;
    }

    @Override
    public int read() throws IOException {
        if(arrowMessage == null || arrowMessage.length <= curIndex ) {
            if(streamIterator.hasNext()){
                curIndex = getNextChunk();
            }
            if(curIndex == Constants.END_OF_STREAM){
                return Constants.END_OF_STREAM;
            }
        }
       return  ((int) arrowMessage[curIndex++]) & 0xFF;
    }

    private int getNextChunk() {
        AnsiSqlExtractQueryResponse response = streamIterator.next();
        if(response !=null && response.getData() !=null ){
            arrowMessage=new byte[response.getData().size()];
            response.getData().copyTo(arrowMessage,0);
        }
        if(arrowMessage.length != 0){
            return Constants.START_OF_STREAM;
        }
        else {
            arrowMessage = null;
            return Constants.END_OF_STREAM;
        }
    }

}
