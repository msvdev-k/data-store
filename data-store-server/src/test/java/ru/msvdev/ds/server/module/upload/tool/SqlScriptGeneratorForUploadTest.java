package ru.msvdev.ds.server.module.upload.tool;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.msvdev.ds.server.module.upload.base.ChunkingSchema;
import ru.msvdev.ds.server.module.upload.base.UploadSessionState;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;


@Disabled
public class SqlScriptGeneratorForUploadTest {


    @Test
    void containerSqlScriptGenerator() throws NoSuchAlgorithmException {
        // region Given
        int containerID = 364;
        int startChunkId = 260;

        long size = 134;
        int chunkSize = 32;
        int minChunkSize = 16;
        // endregion


        ChunkingSchema chunkingSchema = ChunkingSchema.of(size, chunkSize, minChunkSize);

        byte[] content = new byte[(int) size];
        new Random().nextBytes(content);

        Base64.Encoder base64Encoder = Base64.getEncoder();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        HexFormat hexFormat = HexFormat.of().withLowerCase();

        String sha256 = hexFormat.formatHex(messageDigest.digest(content));


        // region CONTAINERS
        {
            System.out.print("INSERT INTO \"containers\" ");
            System.out.print("(\"id\", \"sha256\", \"size\", \"chunk_count\", \"chunk_size\", \"last_chunk_size\") ");
            System.out.print("OVERRIDING SYSTEM VALUE\nVALUES ");
            System.out.printf("(%d, '%s', %d, %d, %d, %d);\n",
                    containerID, sha256, size, chunkingSchema.count(), chunkingSchema.chunkSize(), chunkingSchema.lastChunkSize());
            System.out.println();
        }
        // endregion


        // region CHUNKS
        {
            System.out.println("INSERT INTO \"chunks\" (\"id\", \"content\") OVERRIDING SYSTEM VALUE\nVALUES");

            for (int chunkNumber = 1; chunkNumber < chunkingSchema.count(); chunkNumber++) {
                int from = (int) chunkingSchema.getOffsetChunk(chunkNumber);
                int to = from + chunkingSchema.getChunkSize(chunkNumber);

                String chunkString = base64Encoder.encodeToString(Arrays.copyOfRange(content, from, to));

                System.out.printf("(%d, '%s'),\n", startChunkId + chunkNumber - 1, chunkString);
            }

            int chunkNumber = chunkingSchema.count();
            int from = (int) chunkingSchema.getOffsetChunk(chunkNumber);
            int to = from + chunkingSchema.getChunkSize(chunkNumber);
            String chunkString = base64Encoder.encodeToString(Arrays.copyOfRange(content, from, to));
            System.out.printf("(%d, '%s');\n", startChunkId + chunkNumber - 1, chunkString);
            System.out.println();
        }
        // endregion


        // region CONTAINER_CHUNKS
        {
            System.out.print("INSERT INTO \"container_chunks\" ");
            System.out.print("(\"container_id\", \"chunk_id\", \"number\") ");
            System.out.print("\nVALUES\n");

            for (int chunkNumber = 1; chunkNumber < chunkingSchema.count(); chunkNumber++) {
                System.out.printf("(%d, %d, %d),\n", containerID, startChunkId + chunkNumber - 1, chunkNumber);
            }

            int chunkNumber = chunkingSchema.count();
            System.out.printf("(%d, %d, %d);\n", containerID, startChunkId + chunkNumber - 1, chunkNumber);
            System.out.println();
        }
        // endregion

    }


    @Test
    void uploadSessionSqlScriptGenerator() throws NoSuchAlgorithmException {
        // region Given
        String userUUID = "cf13dd5d-0092-47c7-93dd-5d0092f7c7d0";
        int uploadSessionID = 147;
        int startChunkId = 154;

        long size = 132;
        int chunkSize = 32;
        int minChunkSize = 16;
        // endregion


        ChunkingSchema chunkingSchema = ChunkingSchema.of(size, chunkSize, minChunkSize);

        byte[] content = new byte[(int) size];
        new Random().nextBytes(content);

        Base64.Encoder base64Encoder = Base64.getEncoder();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        HexFormat hexFormat = HexFormat.of().withLowerCase();

        String sha256 = hexFormat.formatHex(messageDigest.digest(content));


        // region UPLOAD_SESSIONS
        {
            System.out.print("INSERT INTO \"upload_sessions\" ");
            System.out.print("(\"id\", \"state\", \"sha256\", \"size\", \"chunk_count\", \"chunk_size\", \"last_chunk_size\") ");
            System.out.print("OVERRIDING SYSTEM VALUE\nVALUES ");
            System.out.printf("(%d, '%S', '%s', %d, %d, %d, %d);\n",
                    uploadSessionID, UploadSessionState.PROCESSING, sha256, size, chunkingSchema.count(), chunkingSchema.chunkSize(), chunkingSchema.lastChunkSize());
            System.out.println();
        }
        // endregion


        // region CHUNKS
        {
            System.out.println("INSERT INTO \"chunks\" (\"id\", \"content\") OVERRIDING SYSTEM VALUE\nVALUES");

            for (int chunkNumber = 1; chunkNumber < chunkingSchema.count(); chunkNumber++) {
                int from = (int) chunkingSchema.getOffsetChunk(chunkNumber);
                int to = from + chunkingSchema.getChunkSize(chunkNumber);

                String chunkString = base64Encoder.encodeToString(Arrays.copyOfRange(content, from, to));

                System.out.printf("(%d, '%s'),\n", startChunkId + chunkNumber - 1, chunkString);
            }

            int chunkNumber = chunkingSchema.count();
            int from = (int) chunkingSchema.getOffsetChunk(chunkNumber);
            int to = from + chunkingSchema.getChunkSize(chunkNumber);
            String chunkString = base64Encoder.encodeToString(Arrays.copyOfRange(content, from, to));
            System.out.printf("(%d, '%s');\n", startChunkId + chunkNumber - 1, chunkString);
            System.out.println();
        }
        // endregion


        // region UPLOAD_CHUNKS
        {
            System.out.print("INSERT INTO \"upload_chunks\" ");
            System.out.print("(\"upload_session_id\", \"chunk_id\", \"number\", \"user_uuid\", \"state\", \"last_modified\") ");
            System.out.print("\nVALUES\n");

            for (int chunkNumber = 1; chunkNumber < chunkingSchema.count(); chunkNumber++) {
                System.out.printf("(%d, %d, %d, '%s', '%S', '%s'),\n",
                        uploadSessionID, startChunkId + chunkNumber - 1, chunkNumber, userUUID, UploadSessionState.PROCESSING, LocalDateTime.now());
            }

            int chunkNumber = chunkingSchema.count();
            System.out.printf("(%d, %d, %d, '%s', '%S', '%s');\n",
                    uploadSessionID, startChunkId + chunkNumber - 1, chunkNumber, userUUID, UploadSessionState.PROCESSING, LocalDateTime.now());
            System.out.println();
        }
        // endregion

    }

}
