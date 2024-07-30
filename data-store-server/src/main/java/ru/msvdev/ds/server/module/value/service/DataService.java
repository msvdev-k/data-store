package ru.msvdev.ds.server.module.value.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.msvdev.ds.server.module.value.base.DataType;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataService {

    private final Map<String, ValueService> valueServiceMap;


    /**
     * Добавить значение, представленное в виде строки, в БД
     *
     * @param dataType тип данных добавляемого значения
     * @param value    строковое представление добавляемого значения
     * @return идентификатор добавленного значения
     */
    public long put(DataType dataType, String value) {
        ValueService valueService = valueServiceMap.get(dataType.serviceBeanName);
        return valueService.put(value);
    }


    /**
     * Получить строковое представление значения
     *
     * @param dataType тип данных значения
     * @param id       идентификатор значения
     * @return строковое представление значения, либо null если идентификатор
     * для указанного типа данных отсутствует в БД
     */
    public String get(DataType dataType, long id) {
        ValueService valueService = valueServiceMap.get(dataType.serviceBeanName);
        return valueService.get(id);
    }
}
