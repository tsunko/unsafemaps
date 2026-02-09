package academy.hekiyou.unsafemaps;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;

/// Represents a PBO from OpenGL; this class forces us to use at least OpenGL 4.5 because of ARB_buffer_storage...
/// But boy does it do a lot better than just an old-fashioned PBO.
public class PixelBufferObject {

    private static final int BUFFER_FLAGS = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT;
    private static final int PBO_BUFFER_COUNT = 3;

    private final AbstractTexture texture;
    private final int[] pboIds;
    private final ByteBuffer[] pboBuffers;
    private final int size;
    private int currentPboIdx = 0;

    public PixelBufferObject(AbstractTexture texture, int size) {
        this.texture = texture;
        this.size = size;
        // initialize all the pbo buffers
        this.pboIds = new int[PBO_BUFFER_COUNT];
        this.pboBuffers = new ByteBuffer[PBO_BUFFER_COUNT];
        for (int i = 0; i < PBO_BUFFER_COUNT; i++){
            int id = glCreateBuffers();
            glNamedBufferStorage(id, size, BUFFER_FLAGS);
            this.pboIds[i] = id;
            this.pboBuffers[i] = glMapNamedBufferRange(id, 0, size, BUFFER_FLAGS | GL_MAP_FLUSH_EXPLICIT_BIT);
        }
    }

    public BoundPixelBufferObject bindAndMap() {
        ByteBuffer buffer = this.pboBuffers[currentPboIdx].clear();
        return new BoundPixelBufferObject(buffer);
    }

    public class BoundPixelBufferObject implements AutoCloseable {

        private final ByteBuffer buffer;

        private BoundPixelBufferObject(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }

        @Override
        public void close() {
            glFlushMappedNamedBufferRange(PixelBufferObject.this.pboIds[currentPboIdx], 0, size);

            GlTexture glTexture = (GlTexture)PixelBufferObject.this.texture.getGlTexture();
            glBindTexture(GL_TEXTURE_2D, glTexture.getGlId());
            glBindBuffer(GL_PIXEL_UNPACK_BUFFER, PixelBufferObject.this.pboIds[currentPboIdx]);

            // have to set SKIP_ROWS and SKIP_PIXELS to 0; it's set by minecraft somewhere else and doesn't get unset
            glPixelStorei(GL_UNPACK_ROW_LENGTH, glTexture.getWidth(0));
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, glTexture.getWidth(0), glTexture.getHeight(0), GL_RED, GL_UNSIGNED_BYTE, 0);

            // unset working buffer; minecraft barrels along with the buffer and corrupts it if we don't
            glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
            currentPboIdx = (currentPboIdx + 1) % PBO_BUFFER_COUNT;
        }

    }

}
