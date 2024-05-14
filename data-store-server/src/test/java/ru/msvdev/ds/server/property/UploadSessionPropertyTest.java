package ru.msvdev.ds.server.property;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.msvdev.ds.server.base.ApplicationTest;

@SpringBootTest
public class UploadSessionPropertyTest extends ApplicationTest {

    private final UploadSessionProperty property;

    @Autowired
    public UploadSessionPropertyTest(UploadSessionProperty property) {
        this.property = property;
    }

    @Test
    void propertyTest() {
        System.out.println("============ Upload Session Properties ============");

        System.out.println("            chunk-size: " + property.chunkSize().toBytes() + " (байт)");
        System.out.println("        min-chunk-size: " + property.minChunkSize().toBytes() + " (байт)");
        System.out.println("  upload-chunk-timeout: " + property.uploadChunkTimeout().getSeconds() + " (секунд)");
        System.out.println("upload-session-timeout: " + property.uploadSessionTimeout().getSeconds() + " (секунд)");
    }

}
