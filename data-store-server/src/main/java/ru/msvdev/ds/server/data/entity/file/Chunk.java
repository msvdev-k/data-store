package ru.msvdev.ds.server.data.entity.file;

/**
 * Фрагмент бинарных данных содержащихся в контейнере
 *
 * @param size    размер фрагмента данных контейнера (байт)
 * @param content содержимое фрагмента данных контейнера в виде строки Base64
 * @param number  порядковый номер фрагмента (нумерация начинается с единицы, т.е. 1,2,3,4,...)
 */
public record Chunk(
        int size,
        String content,
        int number
) {
}
