package ru.msvdev.ds.server.data.entity.file;

/**
 * Объект представляющий схему разбивки файла на части
 *
 * @param size          размер файла (байт)
 * @param count         количество фрагментов
 * @param chunkSize     размер фрагментов с 1 до count-1 (байт)
 * @param lastChunkSize размер последнего (байт)
 */
public record ChunkingSchema(
        long size,
        int count,
        int chunkSize,
        int lastChunkSize
) {

    /**
     * Получить схему разбиения файла.
     * Правила разбиения:
     * <p>
     * 1. Если размер файла <= chunkSize + minChunkSize, то файл представляется единым фрагментом.
     * <p>
     * 2. Если размер файла > chunkSize + minChunkSize, то файл делится на n фрагментов.
     * В этом случае размер фрагментов от 1 до n-1 выбирается равным chunkSize.
     * А размер оставшегося n-ого фрагмента равным lastChunkSize (minChunkSize < lastChunkSize <= chunkSize + minChunkSize)
     *
     * @param size         размер развиваемого файла (байт)
     * @param chunkSize    размер предпочтительного фрагмента файла (байт)
     * @param minChunkSize минимальный размер фрагмента при разбивке файла (байт)
     * @return объект представляющий схему разбивки файла на части
     **/
    public static ChunkingSchema of(long size, int chunkSize, int minChunkSize) {

        int maxSize = chunkSize + minChunkSize;

        if (size <= maxSize) {
            return new ChunkingSchema(size, 1, (int) size, (int) size);
        }

        int count = (int) (size / chunkSize);
        int lastChankSize = (int) (size % chunkSize);

        if (lastChankSize < minChunkSize) {
            lastChankSize += chunkSize;

        } else {
            count++;
        }

        return new ChunkingSchema(size, count, chunkSize, lastChankSize);
    }


    public boolean isValid() {
        return (count == 1 && size == chunkSize) || size == (long) (count - 1) * chunkSize + lastChunkSize;
    }
}