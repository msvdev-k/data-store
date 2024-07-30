package ru.msvdev.ds.server.module.value.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.module.value.entity.Value;


@NoRepositoryBean
public interface ValueRepository<V> extends Repository<Value, Long> {

    /**
     * Найти идентификатор по заданному значению
     *
     * @param value заданное значение
     * @return идентификатор найденного значения
     */
    Long findIdByValue(V value);


    /**
     * Найти значение по идентификатору
     *
     * @param id идентификатор
     * @return найденное значение
     */
    V findValueById(long id);


    /**
     * Вставить новое значение.
     * Примечание: значение должно быть уникальным, по этому перед
     * вызовом текущего метода необходимо вызвать метод {@link #findIdByValue(V)}
     *
     * @param value вставляемое значение
     * @return идентификатор вставленного значения
     */
    Long insert(V value);

}
