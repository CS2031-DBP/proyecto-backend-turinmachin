# UniLife [![Tests](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/tests.yml/badge.svg)](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/tests.yml)

**CS2031: Desarrollo Basado en Plataformas**

Integrantes de grupo:

- José Daniel Grayson Tejada
- Diego Alonso Figueroa Winkelried
- Martin Jesus Bonilla Sarmiento
- Matias Javier Anaya Manzo

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Jmnm3svF)

## Índice

1. [Introducción](#Introducción)

## Introducción

El mundo universitario, en la actualidad, presenta algunos desafíos: la incertidumbre y desinformación al elegir una carrera o una universidad. Los alumnos se ven obligados a recurrir a familiares, graduados, conocidos o comentarios en redes sociales, que no siempre reflejan la información más actual o precisa de cada universidad o carrera. Hace falta información de **primera mano** y **honesta**. Además, existe una tendencia entre los estudiantes a formar una visión sesgada o estereotipada sobre carreras o universidades ajenas, basada en experiencias limitadas. Cada universidad vive en su propia **burbuja**.

### Objetivos

UniLife pretende cumplir los siguientes objetivos:

## Identificación del problema

## Descripción de la solución

UniLife es una plataforma social al estilo de [Twitter/X](https://x.com) donde estudiantes universitarios pueden compartir mediante posts sus experiencias personales del día a día en sus universidades.

### Funcionalidades implementadas

En UniLife, un usuario tiene disponibles las siguientes funcionalidades:

- **Posts**
  - Publicar, editar y borrar posts. Pueden incluir imágenes.
  - Dar upvotes y downvotes a otros posts.
  - Comentar otros posts.
- **Manejo de cuenta**
  - Actualizar información personal.
  - Establecer o borrar una foto de perfil.

Además, UniLife cuenta con un sistema de **moderación**. Un usuario puede tener uno de 3 roles:

- **Admin:** Reservado para nosotros, los creadores de la aplicación. Otorga control total sobre todos los endpoints y recursos.
- **Moderador:** Puede borrar posts, comentarios y usuarios si detecta algún comportamiento indebido.
- **Usuario:** Usuario regular de la plataforma; no puede editar ni borrar lo que no le pertenece.

### Tecnologías utilizadas

El backend de UniLife está programado en **Java 21** y usa las siguientes tecnologías:

- **Spring Boot Web**, como la base del servidor.
- **Spring Boot Security**, para la capa de seguridad del backend.
- **Spring Boot Validation**, para todo lo relacionado a lógica de validación.
- **Spring Data JPA**, como ORM para interactuar con la base de datos.
- **Spring Mail**, para enviar correos a los usuarios (verificación, bienvenida, etc.).
- **Thymeleaf**, para gestionar las plantillas de correo.

Además, el proyecto usa algunas líneas de **SQL** en algunos queries personalizados de JPA.

Fuera del código fuente, UniLife usa las siguientes tecnologías:

- **JWT**, como estándar para el sistema de autenticación.
- **PostgreSQL**, como gestor de base de datos para almacenar toda la información persistente de la aplicación.
- **Docker** para empaquetar y desplegar la aplicación de forma reproducible.
- **Amazon ECS**, como entorno de despliegue para la aplicación.
- **Amazon RDS**, como proveedor de la base de datos.
- **Amazon ECR**, como repositorio privado para las imágenes de Docker.
- **Amazon S3**, como servicio de almacenamiento de imágenes y otros recursos.

Adicionalmente, usamos **Postman** en el desarrollo del proyecto como cliente HTTP para probar los endpoints de la aplicación.

## Modelo de entidades

![Diagrama UML](https://raw.githubusercontent.com/CS2031-DBP/proyecto-backend-turinmachin/main/assets/diagrama_uml.png)

A continuación describimos más detalladamente estas entidades.

> [!NOTE]
> Los atributos que se presentan a continuación **son no-nulos por defecto** para mayor brevedad.

### User

Representa una cuenta en la plataforma.

- **ID** (UUID, primary key, auto-generado): Identificador único.
- **Email** (string): Dirección de correo electrónico.
- **Username** (string): Nombre de usuario.
- **Password** (string): Contraseña hasheada.
- **Display name** (string): Nombre a mostrar del usuario. (e.g.: nombre real)
- **Verification ID** (UUID, opcional): La ID de verificación del usuario. Funciona como una especie de "token" de verificación. Que tome valor nulo indica que el usuario está verificado.
- **Bio** (string, opcional): Una breve descripción que el usuario puede hacer para su perfil.
- **Role** (enum Role, default = `USER`): Rol del usuario en la plataforma.
- **Created at** (instant): Fecha de creación.
- **Updated at** (instant): Última fecha de actualización.

#### Relaciones

- Un usuario **tiene una foto de perfil** (ManyToOne Image).
  - _Nota: Aunque una foto de perfil le pertence a un solo usuario, implementamos esta relación como ManyToOne porque la entidad Image es genérica; no tiene un atributo que indique a "qué" le pertenece._
- Un usuario **puede pertenecer a una universidad** (ManyToOne University).
- Un usuario **puede estar cursando una carrera** (ManyToOne Degree).
- Un usuario **es autor de varios posts** (OneToMany Post).

### Degree

Representa una carrera universitaria de la que se tiene registro en la plataforma.

- **ID** (UUID, primary key, auto-generado): Identificador único.
- **Name** (string, único): Nombre de la carrera.

#### Relaciones

- Una carrera **es ofrecida en varias universidades** (ManyToMany University).
- Una carrera **es cursada por varios usuarios** (OneToMany User).

### University

Representa una universidad de la que se tiene registro en la plataforma.

- **ID** (UUID, primary key, auto-generado): Identificador único.
- **Name** (string, único): Nombre de la universidad.
- **Website URL** (string, opcional): URL de la página web de la universidad.
- **Email domains** (string[]): Lista de dominios de correo electrónico de la universidad.
- **Active** (boolean, default `true`): Indica si la universidad está borrada lógicamente o no.

#### Relaciones

- Una universidad **ofrece varias carreras** (ManyToMany Degree).
- Una universidad **es sujeto de varios posts** (OneToMany Post).

### Post

Representa una publicación en la plataforma.

- **ID** (UUID, primary key, auto-generado): Identificador único.
- **Content** (string): Texto del post.
- **Tags** (string[]): tags del post.
- **Created at** (instant): Fecha de creación.
- **Updated at** (instant): Última fecha de actualización.

#### Relaciones

- Un post **tiene varias imágenes** (OneToMany Image).
- Un post **es sobre una universidad** (ManyToOne University).
- Un post **puede ser sobre una carrera** (ManyToOne Degree).
- Un post **tiene varios comentarios** (OneToMany Comment).
- Un post **tiene varios votos** (OneToMany PostVote).

### PostVote

Representa un upvote o un downvote en un post.

- **Post ID** (UUID, primary key): ID del post votado.
- **Author ID** (UUID, primary key): ID del autor del voto.
- **Value** (VoteType): Valor del voto (1 para upvote, -1 para downvote).

La primary key de PostVote es (Post ID, Author ID).

PostVote emplea una llave compuesta embebida con `@Embedded`.

#### Relaciones

- Un voto **es hacia un post** (ManyToOne Post).
- Un voto **tiene un autor** (ManyToOne User).

### Comment

Representa un comentario de un Post. Los comentarios pueden responder a otros comentarios, así que incluyen un atributo auto-referencial "parent".

- **ID** (UUID, primary key): ID del comentario.
- **Content** (string): Contenido del comentario.
- **Created at** (instant): fecha de creación.
- **Updated at** (instant): fecha de la última actualización.

#### Relaciones

- Un comentario **responde a un Post** (ManyToOne Post).
- Un comentario **tiene un autor** (ManyToOne User).
- Un comentario **puede responder a otro comentario** (ManyToOne Comment).

### Image

Es una representación en nuestra propia BD de una imagen almacenada en Amazon S3.

- **ID** (UUID, primary key, auto-generado): Identificador interno.
- **Key** (string, único): Key de la imagen en S3.
- **URL** (string): URL permanente de la imagen. Es provista por S3 al cargar la imagen.

## Testing y manejo de errores

### Niveles de testing

Este backend está testeado con los siguientes tipos de testing:

- **Testing unitario** para los repositorios de cada entidad. En particular, testeamos las queries personalizadas. (No es necesario testear los métodos provistos por defecto)
- **Testing de integración** para cada controlador de la aplicación. El test de cada controlador aborda cada uno de sus endpoints.
  - Las únicas excepciones a esto son `PUT /users/@self/picture` y `DELETE /users/@self/picture`, ya que evitamos testear el subido de archivos a Amazon S3.

### Resultados

El testing implementado resultó útil para hallar errores en el proyecto. Por ejemplo:

- Algunos endpoints de método `DELETE` retornaban `200 OK` en lugar de `204 No Content`
- El endpoint de `PUT /universities/{id}`, cuando hicimos el test, creaba una universidad en lugar de actualizarla.
- La actualización de los tags de un post causaba un error porque, en ese momento, el `List<String>` se estaba reemplazando completamente en cada actualización.
- El mapeo de los votos de un post a su puntuación (para `PostResponseDto`) debía producir `null` si el request no era autenticado, pero estaba produciendo `0`.

Todos estos fallos fueron corregidos a medida que desarrollamos los tests.

### Manejo de errores

El proyecto tiene muchas excepciones globales que se arrojan en diversos casos (usuario no encontrado, credenciales inválidas, usuario sin universidad, etc.); todas ellas derivan de la clases en `common/exception`. Estas corresponden a algunos códigos HTTP en el rango de los 4xx.

- `NotFoundException`: 404 No encontrado -- ID de usuario/comentario/post no existente.
- `ConflictException`: 409 Conflicto -- Email/username ya tomado, usuario ya verificado, etc.
- `UnsupportedMediaTypeException`: 415 Tipo de medio no soportado -- Post contiene archivos no-imagen.
- `ForbiddenException`: 403 Prohibido -- Usuario no verificado, usuario no pertenece a una universidad, etc.
- `UnauthorizedException`: 401 No autorizado -- Credenciales inválidas o no reconocidas.

## Medidas de seguridad implementadas

### Seguridad de datos

UniLife emplea los siguientes mecanismos de seguridad:

- **[JWT](https://jwt.io/)** como estándar para autenticar requests.
- **BCrypt** (mediate `BCryptPasswordEncoder`, de Spring Security) como algoritmo de hash para cifrar contraseñas antes de guardarlas en la base de datos.
- Un sistema de **roles** para clasificar a los usuarios y determinar qué endpoints pueden utilizar.
  - Cada usuario tiene un atributo `role`: `ADMIN`, `MODERATOR` o `USER`. La aplicación utiliza la anotación `@PreAuthorize` para permitir el acceso de cierto nivel de rol a las rutas que lo requieren.

Además, la aplicación **usa UUIDs en lugar de Longs** como IDs de las entidades. Esto reduce la posibilidad de _descubrimiento accidental_ de recursos, ya que los UUIDs son mucho más impredecibles que un número auto-incrementado.

### Prevención de vulnerabilidades

El backend de UniLife está protegido contra las siguientes vulnerabilidades:

- **Inyección SQL**: Spring Data JPA parametriza los queries apropiadamente.
- **CSRF**: El uso de JWT no usa cookies, sino un header. Esto invalida automáticamente cualquier intento de CSRF.
- **XSS**: Thymeleaf escapa el contenido de las variables que reemplaza en las plantillas: no es posible injectarles HTML.

## Eventos y asincronía

La aplicación cuenta con los siguientes eventos:

- `DeleteFilesEvent`: Se despacha para indicar la intención de **eliminar una o más imágenes** del almacenamiento en S3.
  - Debe ser asincrónico porque puede demorar mucho, sobre todo cuando se eliminan múltiples imágenes a la vez. Además, no es necesario tener el resultado del eliminado.
- `SendVerificationEmailEvent`: Se despacha para indicar que se le debe enviar un **correo de verificación** a un usuario.
  - Similarmente al evento anterior, debe ser asincrónico porque puede demorar mucho, y no se necesita de inmediato el resultado de enviar correos.
- `SendWelcomeEmailEvent`: Se despacha para indicar que se le debe enviar un **correo de bienvenida** a un usuario. Se despacha cuando un usuario es verificado.
  - Debe ser asincrónico por las mismas razones que el anterior: puede demorar y no se necesita su resultado.

Cada uno de estos eventos tiene su respectivo _listener_ asíncrono.

## Uso de GitHub

Los hitos importantes del proyecto fueron representados como [issues](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/issues?q=is%3Aissue%20state%3Aclosed) en el repositorio. Cada issue fue trabajado en su propia rama, y los cambios de cada rama fueron mergeados a la rama principal mediante [pull requests](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/pulls?q=is%3Apr+is%3Aclosed). Esto permitió a cada integrante del proyecto trabajar en sus propios tasks sin afectar al resto.

### GitHub Actions

El repositorio cuenta con un [workflow](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/deploy.yml) de GitHub Actions para desplegar el proyecto automáticamente al haber cambios en la rama principal.

## Conclusión

### Logros del proyecto

A nivel de backend, hemos logrado lo siguiente:

- Construir el backend de una plataforma social para universitarios.

### Aprendizajes clave

- **Trabajo en equipo**
-

### Trabajo futuro

Tenemos en mente algunas adiciones que podríamos implementar a futuro en este backend.

- **Chat en tiempo real:** Sería un añadido interesante a la plataforma una pequeña interfaz donde los usuarios puedan conversar en tiempo real. Podría darse mediante salas de chat públicas (por universidad, por ejemplo) y/o con mensajes privados entre usuarios.

## Apéndices

Este proyecto se encuentra bajo la [licencia MIT](https://mit-license.org/).
