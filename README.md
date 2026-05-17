#My Project
openapi: 3.0.4

info:
  title: Book store
  description: Книжный магазин
  version: 1.0.0

servers:
  - url: http://localhost:8080/api

paths:

  /books:
    get:
      tags:
        - books

      summary: Получить книги
      operationId: getBooks
      parameters:
        - in: query
          name: id
          description: Параметр принимает id книги для поиска
          schema:
            type: integer
        - in: query
          name: authorId
          description: Параметр примнимает id автора для поиска
          schema:
            type: integer
        - in: query
          name: seriesId
          description: Параметр принимает id серии книги для поиска
          schema:
            type: integer
        - in: query
          name: publisherId
          description: Параметр принимает id издателя для поиска
          schema:
            type: integer
        - in: query
          name: genre
          description: Параметр принимает жанр для поиска
          schema:
            type: string
        - in: query
          name: publisher
          description: Параметр указывает издателя по поиска
          schema:
            type: string
        - in: query
          name: isPopular
          description: Параметр указывает значение популярности (популярно/непопулярно) для поиска
          schema:
            type: boolean
        - in: query
          name: isNew
          description: Параметр указывает значение новизны (новое/неновое)
          schema:
            type: boolean    
        - in: query
          name: limit
          description: Параметр указывает лимит вывода
          schema:
            type: integer
            default: 10
        - in: query
          name: offset
          description: Смещение относительно начала списка
          schema:
            type: integer
            default: 0

      responses:
        '200':
          description: Список книг
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Book'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'

  /authors/popular:
    get:
      tags:
        - authors

      summary: Поиск популярных авторов
      description: Поиск популярных авторов
      operationId: getAuthorsPopular
      
      parameters:
        - in: query
          name: limit
          description: Параметр указывает лимит вывода
          schema:
            type: integer
            default: 10
        - in: query
          name: offset
          description: Смещение относительно начала списка
          schema:
            type: integer
            default: 0
      
      responses:
        '200':
          description: Список популярных авторов
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Author'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'

  /account/purchased-books:
    get:
      tags:
        - account

      summary: Список приобретенных книг
      description: Получение списка приобретенных книг
      operationId: getPurchasedBooks

      security:
        - bearerAuth: []
        
      parameters:
        - in: query
          name: limit
          description: Параметр указывает лимит вывода
          schema:
            type: integer
            default: 10
        - in: query
          name: offset
          description: Смещение относительно начала списка
          schema:
            type: integer
            default: 0

      responses:
        '200':
          description: Список купленных книг
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Book'

        '400':
          $ref: '#/components/responses/BadRequest'

        '401':
          $ref: '#/components/responses/Unauthorized'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          

  /account/favorite:
    post:
      tags:
        - account

      summary: Добавить книгу в избранное
      description: Добавление книги в избранное
      operationId: getFavoriteBooks
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                bookId:
                  type: integer
      
      responses:
        '201':
          description: Книга добавлена в избранное
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Book'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'


  /account/basket:
    get:
      tags:
        - account

      summary: Корзина с книгами
      description: Получение корзины с книгами
      operationId: getBasket

      responses:
        '200':
          description: Список книг в корзине
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Book'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          

  /account/basket/books:
    post:
      tags:
        - account

      summary: Добавить книгу в корзину
      description: Добавление одной книги в корзину
      operationId: postIntoBasket
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                bookId:
                  type: integer
      responses:
        '201':
          description: Книга добавлена в корзину
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Book'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'

  /payment/pay:
    post:
      tags:
        - payment

      summary: Оплата товаров в корзине
      description: Произведение оплаты корзины
      operationId: postPaymentPay
      security:
        - bearerAuth: []

      requestBody:
        required: true

        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PaymentRequest'

      responses:
        '200':
          description: Список книг в корзине
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '401':
          $ref: '#/components/responses/Unauthorized'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
         
  /payment/refund:
    post:
      tags:
        - payment

      summary: Возврат оплаты за книги
      operationId: refundPayBooksById

      security:
        - bearerAuth: []

      requestBody:
        required: true

        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RefundPaymentRequest'

      responses:
        '200':
          description: Оплата за книги возвращена
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '401':
          $ref: '#/components/responses/Unauthorized'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          
         
  /account/basket/{bookId}:
    delete:
      tags:
        - account
      summary: Удаление товара из корзины
      description: Удаление товара из корзины по его id
      operationId: deleteBookById

      parameters:
        - name: bookId
          in: path
          required: true
          description: Параметр id книги для удаления
          schema:
            type: integer

      responses:
        '200':
          description: Товар удален
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'
          
        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          
        
  /author/{authorId}:
    get:
      tags:
        - authors

      summary: Информация об авторе
      description: Получение информации об авторе книг
      operationId: getAuthorById

      parameters:
        - name: authorId
          in: path
          required: true
          description: Параметр для идентификации по id для поиска информации об авторе
          schema:
            type: integer

      responses:
        '200':
          description: Вывод автора
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          

  /auth/register:
    post:
      tags:
        - auth

      summary: Регистрация пользователя
      description: Осуществление регистрации пользователя
      operationId: postRegister

      requestBody:
        required: true

        content:
          application/json:
            schema:
              type: object

              required:
                - email
                - password

              properties:
                email:
                  type: string

                password:
                  type: string

      responses:
        '200':
          description: Пользователь зарегистрирован
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          

  /auth/login:
    post:
      tags:
        - auth

      summary: Ввод данных пользователя
      description: Ввод данных пользователя для осуществления входа в профиль
      operationId: postLogin

      requestBody:
        required: true

        content:
          application/json:
            schema:
              type: object

              required:
                - email
                - password

              properties:
                email:
                  type: string

                password:
                  type: string

      responses:
        '200':
          description: Вход выполнен успешно
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          
  
  /auth/logout:
    post:
      tags:
        - auth

      summary: Выход из профиля
      description: Осуществление выхода из профиля
      operationId: postLogout

      security:
        - bearerAuth: []

      responses:
        '200':
          description: Сессия удалена
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaymentResponse'

        '400':
          $ref: '#/components/responses/BadRequest'

        '401':
          $ref: '#/components/responses/Unauthorized'

        '404':
          $ref: '#/components/responses/NotFound'

        '500':
          $ref: '#/components/responses/InternalServerError'
          

components:

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT


  responses:

    BadRequest:
      description: Неверный запрос
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'

    Unauthorized:
      description: Требуется авторизация
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'

    NotFound:
      description: Ресурс не найден
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'

    InternalServerError:
      description: Внутренняя ошибка сервера
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'

  schemas:

    Book:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор книги

        title:
          type: string
          description: Название книги


        author:
          $ref: '#/components/schemas/Author'
          description: Автор книги

        genre:
          $ref: '#/components/schemas/Genre'
          description: Жанр, относящийся к книге

        price:
          type: number
          format: double
          description: Информация о цене

        publisherId:
          type: string
          description: Информация об издателе

        ISBN:
          type: string
          description: Информация о муждународном книжном номере

        series:
          $ref: '#/components/schemas/PublisherSeries'
          description: Информация о серии книги

        release:
          type: string
          description: Информация о дате выпуска книги

        description:
          type: string
          description: Описание книги

        pages:
          type: string
          description: Информация о количестве страниц

        wight:
          type: integer
          description: Информация о весе книги

        bookReview:
          $ref: '#/components/schemas/BookReview'
          description: Информация об отзывах на книгу

    Author:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор автора

        name:
          type: string
          description: Информация об имени автора

        surname:
          type: string
          description: Описание книги

        middleName:
          type: string
          description: Отчество автора

        pseudonym:
          type: string
          description: Информация о псевдониме автора

    Genre:
      type: string
      enum:
        - HORROR
        - COMEDY
        - ROMANTIC
        - DRAMA
        - THRILLER



    Basket:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор корзины

        books:
          type: array
          description: Информация о книгах в корзине
          items:
            $ref: '#/components/schemas/Book'

        totalPrice:
          type: number
          format: double
          description: Итоговая стоимость книг

        quantityBooks:
          type: integer
          description: Количество книг

        createdAt:
          type: string
          format: date-time
          description: Информация о дате создания корзины

        updatedAt:
          type: string
          format: date-time
          description: Информация о дате обновления корзины

    PaymentResponse:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор ответа на оплату

        orderId:
          type: integer
          description: Информация об идентификаторе заказа

        sumOfPay:
          type: number
          format: double
          description: Информация о сумме платежа

        status:
          type: string
          description: Информация о статусе оплаты
          enum:
            - CREATED
            - PENDING
            - PAID
            - FAILED
            - CANCELLED

        createdAt:
          type: string
          format: date-time
          description: Информация о дате оплаты

        receiptUrl:
          type: string
          format: uri
          description: Информация о чеке

    PaymentRequest:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор запроса платежа

        basket:
          $ref: '#/components/schemas/Basket'
          description: Информация о корзине, подлежащей оплате

        paymentMethod:
          type: string
          description: Информация о методе оплаты
          enum:
            - CARD
            - SBP
            - PAYPAL

        deliveryAddress:
          type: string
          description: Информация об адрерсе заставки

        recipientName:
          type: string
          description: Информация об имени получателя

        recipientPhone:
          type: string
          description: Информация о телефоне получателя

    RefundResponse:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор отмены оплаты

        refund_amount:
          type: number
          format: double
          description: Информация о сумме возврата

        refundId:
          type: integer
          description: Идентификатор возврата

        orderId:
          type: integer
          description: Идентификатор заказа

        status:
          type: string
          description: Информация о статусе заказа
          enum:
            - PENDING
            - APPROVED
            - REJECTED
            - COMPLETED

        reason:
          type: string
          description: Информация о причинах возврата

        createdAt:
          type: string
          format: date-time
          description: Информация о дате создания книги

    Publisher:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор издателя
        name:
          type: string
          description: Название издателя

        description:
          type: string
          description: Описание издателя

        country:
          type: string
          description: Информация о месте нахождения издателя

        createdAt:
          type: string
          format: date-time
          description: Информация о дате создания издателя

        updatedAt:
          type: string
          format: date-time
          description: Информация о дате обновления издателя

    PublisherSeries:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор серии

        name:
          type: string
          description: Информация о названии серии

        publisherId:
          type: integer
          description: Идентификатор издателя

        createdAt:
          type: string
          format: date-time
          description: Информация о дате создания серии

        updatedAt:
          type: string
          format: date-time
          description: Информация о дате одновления серии


    BookReview:
      type: object

      properties:
        id:
          type: integer
          description: Идентификатор отзыва

        text:
          type: string
          description: Текст отзыва

        reviewerId:
          type: string
          description: ИИдентификатор пользователя, оставившего отзыв

        rating:
          type: integer
          description: Информация о рейтинге фильма

        createdAt:
          type: string
          format: date-time
          description: Информация о дате сосздания отзыва

        updatedAt:
          type: string
          format: date-time
          description: Информация о дате обновления отзыва


    RefundPaymentRequest:
      type: object

      required:
        - orderIds

      properties:
        orderIds:
          type: array
          description: Список идентификаторов для поиска

          items:
            type: integer

    Error:
      type: object

      properties:
        message:
          type: string
          
          
    
