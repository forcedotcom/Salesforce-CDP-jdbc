package com.salesforce.cdp.queryservice.util;

import com.google.protobuf.ByteString;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class AnsiSqlQueryStreamResponseDataStream extends InputStream {
  Iterator<AnsiSqlQueryStreamResponse> streamIterator;
  byte[] arrowMessage;
  int curIndex;

  public AnsiSqlQueryStreamResponseDataStream(Iterator<AnsiSqlQueryStreamResponse> streamIterator) {
    this.streamIterator = streamIterator;
    this.curIndex = 0;
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
    AnsiSqlQueryStreamResponse response = streamIterator.next();

    if (response.hasArrowResponseChunk()) {
      ByteString arrowResponseChunk = response.getArrowResponseChunk().getData();
      arrowMessage= new byte[arrowResponseChunk.toByteArray().length];
      arrowResponseChunk.copyTo(arrowMessage,0);
    }
    if(arrowMessage.length != 0){
      return Constants.START_OF_STREAM;
    }
    else {
      arrowMessage = null;
      return Constants.END_OF_STREAM;
    }
  }

  public boolean hasNext() {
    if (curIndex < arrowMessage.length) {
      return true;
    } else {
      return streamIterator.hasNext();
    }
  }
}
