package ru.msvdev.ds.server.dao.entity.file;

/**
 * Объект представляющий схему разбивки контейнера с данными на части.
 * В файловой системе пользователя контейнер с бинарными данными
 * соответствует содержимому файла.
 *
 * @param size          размер контейнера (байт)
 * @param count         количество фрагментов
 * @param chunkSize     размер фрагментов с 1 до count-1 (байт)
 * @param lastChunkSize размер последнего фрагмента (байт)
 */
public record ChunkingSchema(
        long size,
        int count,
        int chunkSize,
        int lastChunkSize
) {

    /**
     * Получить схему разбиения контейнера данных.
     * Правила разбиения:
     * <p>
     * 1. Если размер контейнера <= chunkSize + minChunkSize, то контейнер представляется единым фрагментом.
     * <p>
     * 2. Если размер контейнера > chunkSize + minChunkSize, то контейнер делится на n фрагментов.
     * В этом случае размер фрагментов от 1 до n-1 выбирается равным chunkSize.
     * А размер оставшегося n-ого фрагмента равным lastChunkSize (minChunkSize < lastChunkSize <= chunkSize + minChunkSize)
     *
     * @param size         полный размер развиваемого контейнера (байт)
     * @param chunkSize    предпочтительный размер фрагмента (байт)
     * @param minChunkSize минимальный размер фрагмента при разбивке (байт)
     * @return объект представляющий схему разбивки контейнера на части
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

    /**
     * Получить смещение начала фрагмента контейнера (байт).
     * Начало контейнера соответствует нулевому смещению
     *
     * @param chunkNumber номер фрагмента
     * @return смещение от начала контейнера (байт)
     */
    public long getOffsetChunk(int chunkNumber) {
        if (chunkNumber < 1 && chunkNumber > count)
            throw new IndexOutOfBoundsException("Порядковый номер фрагмента выходит за диапазон допустимых значений");

        return (long) (chunkNumber - 1) * chunkSize;
    }

    /**
     * Получить размер фрагмента
     *
     * @param chunkNumber порядковый номер фрагмента
     * @return размер фрагмента (байт)
     */
    public int getChunkSize(int chunkNumber) {
        if (chunkNumber < 1 && chunkNumber > count)
            throw new IndexOutOfBoundsException("Порядковый номер фрагмента выходит за диапазон допустимых значений");

        return chunkNumber == count ? lastChunkSize : chunkSize;
    }

    /**
     * Флаг согласованности параметров схемы разбиения контейнера
     *
     * @return True - параметры разбиения согласованы, False - не согласованы
     */
    public boolean isValid() {
        return (count == 1 && size == lastChunkSize) || size == (long) (count - 1) * chunkSize + lastChunkSize;
    }
}