package central.net.http.executor.okhttp.body;

import central.util.Stringx;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 空 Body
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class EmptyBody extends RequestBody {

    private final MediaType contentType;

    public EmptyBody() {
        this.contentType = MediaType.parse("application/octet-stream");
    }

    public EmptyBody(String contentType) {
        if (Stringx.isNullOrBlank(contentType)) {
            this.contentType = MediaType.parse("application/octet-stream");
            ;
        } else {
            this.contentType = MediaType.parse(contentType);
        }
    }

    @Override
    public long contentLength() throws IOException {
        return 0;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.contentType;
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

    }
}
