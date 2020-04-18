package com.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FunctionTest {
    @Test
    public void testCreateFile() throws Exception {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<FileRequest>> req = mock(HttpRequestMessage.class);

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(final InvocationOnMock invocation) {
                final HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        @SuppressWarnings("unchecked")
        final OutputBinding<byte[]> outputItem = (OutputBinding<byte[]>) mock(OutputBinding.class);

        final HttpResponseMessage ret = new Function().createFile(req, "name", "content", outputItem, context);

        assertEquals(ret.getStatus(), HttpStatus.OK);
    }

    @Test
    public void testAddFileToQueue_ReturnBadRequest_WhenRequestBodyEmpty() throws Exception {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<FileRequest>> req = mock(HttpRequestMessage.class);

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(final InvocationOnMock invocation) {
                final HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        @SuppressWarnings("unchecked")
        final OutputBinding<FileRequest> outputItem = (OutputBinding<FileRequest>) mock(OutputBinding.class);

        final HttpResponseMessage ret = new Function().addFileToQueue(req, outputItem, context);

        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testAddFileToQueue() throws Exception {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<FileRequest>> req = mock(HttpRequestMessage.class);

        final Optional<String> queryBody = Optional.of("json");
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(final InvocationOnMock invocation) {
                final HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        @SuppressWarnings("unchecked")
        final OutputBinding<FileRequest> outputItem = (OutputBinding<FileRequest>) mock(OutputBinding.class);

        final HttpResponseMessage ret = new Function().addFileToQueue(req, outputItem, context);

        assertEquals(ret.getStatus(), HttpStatus.OK);
    }
}
