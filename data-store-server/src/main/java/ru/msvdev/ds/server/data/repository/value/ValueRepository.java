package ru.msvdev.ds.server.data.repository.value;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import ru.msvdev.ds.server.data.entity.Value;

import java.util.Optional;


@NoRepositoryBean
public interface ValueRepository<V> extends Repository<Value, Long> {

    /**
     * Найти идентификатор по заданному значению
     *
     * @param value заданное значение
     * @return идентификатор найденного значения
     */
    Optional<Long> findIdByValue(V value);


    /**
     * Найти значение по идентификатору
     *
     * @param id идентификатор
     * @return найденное значение
     */
    Optional<V> findValueById(Long id);


    /**
     * Вставить новое значение.
     * Примечание: значение должно быть уникальным, по этому перед
     * вызовом текущего метода необходимо вызвать метод findIdByValue(V value)
     *
     * @param value вставляемое значение
     * @return идентификатор вставленного значения
     */
    Long insert(V value);

}
