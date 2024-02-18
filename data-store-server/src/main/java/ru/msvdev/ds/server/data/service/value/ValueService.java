package ru.msvdev.ds.server.data.service.value;

public interface ValueService {

    /**
     * Добавить значение, представленное в виде строки, в БД
     *
     * @param valueString строковое представление добавляемого значения
     * @return идентификатор добавленного или найденного значения
     */
    Long put(String valueString);


    /**
     * Получить строковое представление значения
     *
     * @param id идентификатор значения
     * @return строковое представление значения
     */
    String get(Long id);
}
