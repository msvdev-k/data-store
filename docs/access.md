# Полномочия пользователей при работе с картотекой

## Таблица полномочий

| №  | Полномочие                  | Разрешённые действия                                                              |
|----|-----------------------------|-----------------------------------------------------------------------------------|
| 0  | Авторизованный пользователь | Получить список картотек. Создать новую картотеку                                 |
| 1  | `MASTER`                    | Создатель, владелец и полноправный организатор всей картотеки                     |
| 2  | `GRANT_AUTHORITY`           | Предоставление полномочий другим пользователям картотеки                          |
| 3  | `READING`                   | Чтение и поиск карточек в картотеке                                               |
| 4  | `WRITING`                   | Создание и изменение карточек                                                     |
| 5  | `DELETING`                  | Удаление карточек из картотеки                                                    |
| 6  | `FIELD_TEMPLATE_WRITING`    | Добавление и изменение шаблонов полей карточек                                    |
| 7  | `FIELD_TEMPLATE_DELETING`   | Удаление шаблонов полей карточек                                                  |
| 8  | `FILE_UPLOAD`               | Выгрузка содержимого файла на сервер                                              |
| 9  | `FILE_DOWNLOAD`             | Загрузка содержимого файла с сервера                                              |
| 10 | `FILE_SYSTEM_READ`          | Чтение файловой системы картотеки                                                 |
| 11 | `FILE_SYSTEM_WRITE`         | Изменения в файловой системе картотеки за исключением удаления файлов и каталогов |
| 12 | `FILE_SYSTEM_DELETE`        | Удаление файлов и каталогов из файловой системы картотеки                         |

## Таблица разрешений REST запросов

| 0        | 1        | 2        | 3        | 4        | 5        | 6        | 7        | 8        | 9        | 10       | 11       | 12       | Запрос | Путь                                            |
|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|--------|-------------------------------------------------|
| &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | GET    | catalog                                         |
| &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | POST   | catalog                                         |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | PUT    | catalog/{catalogId}                             |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}                             |
| &#10008; | &#10004; | &#10008; | &#10004; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | GET    | catalog/{catalogId}/field                       |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | POST   | catalog/{catalogId}/field                       |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | PUT    | catalog/{catalogId}/field/{fieldId}             |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}/field/{fieldId}             |
| &#10008; | &#10004; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | GET    | catalog/{catalogId}/card                        |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | POST   | catalog/{catalogId}/card                        |
| &#10008; | &#10004; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | GET    | catalog/{catalogId}/card/{cardId}               |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}/card/{cardId}               |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | PUT    | catalog/{catalogId}/card/{cardId}/tag           |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}/card/{cardId}/tag           |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}/card/{cardId}/tag/{fieldId} |
| &#10008; | &#10004; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | GET    | catalog/{catalogId}/user                        |
| &#10008; | &#10004; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | PUT    | catalog/{catalogId}/user                        |
| &#10008; | &#10004; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | DELETE | catalog/{catalogId}/user                        |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | POST   | catalog/{catalogId}/upload                      |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | PUT    | catalog/{catalogId}/upload                      |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | GET    | catalog/{catalogId}/fs/{nodeId}                 |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | POST   | catalog/{catalogId}/fs/{nodeId}                 |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | PUT    | catalog/{catalogId}/fs/{nodeId}                 |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | DELETE | catalog/{catalogId}/fs/{nodeId}                 |
| &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10008; | &#10004; | &#10008; | &#10008; | &#10008; | GET    | catalog/{catalogId}/fs/{nodeId}/download        | 