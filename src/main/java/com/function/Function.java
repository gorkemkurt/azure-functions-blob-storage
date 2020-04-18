package com.function;

import java.util.*;
import java.util.logging.Level;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class Function {

    @FunctionName("file")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage createFile(@HttpTrigger(name = "req", methods = {
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<FileRequest>> request,
            @BindingName("name") String name, @BindingName("content") String content,
            @BlobOutput(name = "target", path = "file-container/{name}", dataType = "binary") OutputBinding<byte[]> outputItem,
            final ExecutionContext context) {
        try {
            byte[] decodedURLBytes = Base64.getUrlDecoder().decode(content);
            outputItem.setValue(decodedURLBytes);
        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(ex.getMessage()).build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("The File \"" + name + " is created").build();
    }

    @FunctionName("fileQueue")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage addFileToQueue(@HttpTrigger(name = "req", methods = {
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<FileRequest>> request,
            @QueueOutput(name = "target", queueName = "file-queue") OutputBinding<FileRequest> outputItem,
            final ExecutionContext context) {
        if (!request.getBody().isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a file request in the request body").build();
        } else {
            outputItem.setValue(request.getBody().get());
            return request.createResponseBuilder(HttpStatus.OK).body("File added to queue.").build();
        }
    }

    @FunctionName("queueTrigger")
    @StorageAccount("AzureWebJobsStorage")
    public void processQueueItem(@QueueTrigger(name = "myQueueItem", queueName = "file-queue") FileRequest myQueueItem,
            @BindingName("name") String name, @BindingName("content") String content,
            @BlobOutput(name = "target", dataType = "binary", path = "file-container/{name}") OutputBinding<byte[]> outputItem,
            final ExecutionContext executionContext) {
        try {
            byte[] decodedURLBytes = Base64.getUrlDecoder().decode(content);
            outputItem.setValue(decodedURLBytes);
        } catch (Exception ex) {
            executionContext.getLogger().log(Level.SEVERE, ex.getMessage());
        }
        executionContext.getLogger().info("Queue trigger input: " + myQueueItem);
    }
}
