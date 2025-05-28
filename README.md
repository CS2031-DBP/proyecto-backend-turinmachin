# UniLife [![Tests](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/tests.yml/badge.svg)](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/tests.yml)

**CS2031: Desarrollo Basado en Plataformas**

Integrantes de grupo:

- José Daniel Grayson Tejada
- Diego Alonso Figueroa Winkelried
- Martin Jesus Bonilla Sarmiento
- Matias Javier Anaya Manzo

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Jmnm3svF)

## Índice

- [Introducción](#introducción)
  - [Objetivos](#objetivos)
- [Identificación del problema](#identificación-del-problema)
  - [Descripción del problema](#descripción-del-problema)
  - [Justificación](#justificación)
- [Descripción de la solución](#descripción-de-la-solución)
  - [Funcionalidades implementadas](#funcionalidades-implementadas)
  - [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Modelo de entidades](#modelo-de-entidades)
- [Testing y manejo de errores](#testing-y-manejo-de-errores)
  - [Niveles de testing](#niveles-de-testing)
  - [Resultados](#resultados)
  - [Manejo de errores](#manejo-de-errores)
- [Medidas de seguridad implementadas](#medidas-de-seguridad-implementadas)
  - [Seguridad de datos](#seguridad-de-datos)
  - [Prevención de vulnerabilidades](#prevención-de-vulnerabilidades)
- [Eventos y asincronía](#eventos-y-asincronía)
- [Uso de GitHub](#uso-de-github)
  - [GitHub Actions](#github-actions)
- [Conclusión](#conclusión)
  - [Logros del proyecto](#logros-del-proyecto)
  - [Aprendizajes clave](#aprendizajes-clave)
  - [Trabajo futuro](#trabajo-futuro)
- [Apéndices](#apéndices)
  - [Referencias](#referencias)

## Introducción

En el entorno universitario actual, los estudiantes tienden a relacionarse casi exclusivamente dentro de su propia universidad o carrera, lo que genera una especie de "burbuja académica". Esta falta de interacción entre instituciones limita el intercambio de ideas y refuerza estereotipos. Este proyecto surge como una respuesta a esa desconexión.

### Objetivos

El objetivo principal de este proyecto es **crear una plataforma social para universitarios**.

- Hacer visible la vida universitaria entre instituciones.
- Fomentar la interacción social saludable entre estudiantes de distintas universidades.
- Ayudar a romper estereotipos y prejuicios entre universidades.
- Ofrecer una plataforma donde futuros estudiantes puedan conocer el día a día de diversas universidades.

## Identificación del problema

### Descripción del problema

Muchos estudiantes universitarios permanecen encerrados en la dinámica interna de su institución, sin conocer lo que ocurre en otras universidades o carreras. Esta desconexión genera ideas erróneas, prejuicios y una visión limitada del mundo académico. A su vez, quienes están por ingresar a la universidad no siempre acceden a relatos auténticos y variados sobre lo que significa estudiar en distintos contextos.

### Justificación

Promover el contacto entre estudiantes de diferentes universidades contribuye a construir una comunidad académica más diversa, empática y menos prejuiciosa. Al romper las burbujas entre instituciones, se generan oportunidades de aprendizaje e interacción que representan importantes oportunidades no sólo meramente para el aspecto social y personal, sino también para el ámbito profesional.

## Descripción de la solución

UniLife es una plataforma social al estilo de [Twitter/X](https://x.com) donde estudiantes universitarios pueden compartir mediante posts sus experiencias personales del día a día en sus universidades. La plataforma es pública a nivel de lectura; planeamos que, en el frontend, se pueda ver el contenido sin crear una cuenta.

### Funcionalidades implementadas

En UniLife, un usuario tiene disponibles las siguientes funcionalidades:

- **Posts**
  - Publicar, editar y borrar posts. Pueden incluir imágenes.
  - Dar **upvotes** y **downvotes** a otros posts.
  - Comentar otros posts,
- **Manejo de cuenta**
  - Actualizar información personal.
  - Establecer o borrar una foto de perfil.
- **Exploración**
  - Buscar posts por universidad, carrera, usuario y/o tags.
  - Buscar usuarios por universidad y/o carrera.
- **Sistema de racha**
  - Un usuario tiene una **racha**: la cantidad de días contiguos en los que haya publicado algo.
  - Eventualmente, usaremos push notifications para notificar a un usuario si, por ejemplo, está a punto de perder su racha.

Además, UniLife cuenta con un sistema de **moderación**. Un usuario tiene uno de 3 roles:

- **Admin:** Control total.
- **Moderador:** Puede borrar posts, comentarios y usuarios.
- **Usuario:** Usuario regular.
- Un usuario de UniLife puede o puede no estar registrado como en una **universidad**. De ser así, puede también estar registrado como parte de una **carrera**. Sólo los usuarios que pertenezcan a alguna universidad pueden publicar posts, pero cualquier usuario puede dar votos y comentar otros posts.

Cuando un usuario hace un post, este post automáticamente se considera como sobre la universidad del autor (y carrera, de estar el autor en una carrera).

### Tecnologías utilizadas

El backend de UniLife está programado en **Java 21** y usa las siguientes tecnologías:

- **Spring Boot Web**, como la base del servidor.
- **Spring Boot Security**, para la capa de seguridad del backend.
- **Spring Data JPA**, como ORM para interactuar con la base de datos.
- **Spring Mail y Thymeleaf**, para gestionar y enviar correo electrónico.
- **JUnit y Mockito**, para testing.
- **JWT**, como estándar para el sistema de autenticación.
- **PostgreSQL**, como gestor de base de datos para almacenar toda la información persistente de la aplicación.
- **Docker** para empaquetar y desplegar la aplicación de forma reproducible.
- **Amazon S3**, como servicio de almacenamiento de imágenes y otros recursos.
- **Amazon RDS, ECR y ECS**, como servicios para desplegar la aplicación.

Además, el proyecto usa algunas líneas de **SQL** para algunos queries personalizados de JPA.

## Modelo de entidades

![Diagrama UML](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/blob/main/assets/diagrama_uml.png?raw=true)

_Nota: Si el diagrama no carga, la imagen está en [este enlace](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/blob/main/assets/diagrama_uml.png)_.

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

- Un usuario **tiene una foto de perfil** (ManyToOne FileInfo).
  - _Nota: Aunque una foto de perfil le pertence a un solo usuario, implementamos esta relación como ManyToOne porque la entidad FileInfo es genérica; no tiene un atributo que indique a "qué" le pertenece._
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

- Un post **tiene varias imágenes** (OneToMany FileInfo).
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

### FileInfo

Es una representación en nuestra propia BD de un archivo almacenada en Amazon S3.

- **ID** (UUID, primary key, auto-generado): Identificador interno.
- **Key** (string, único): Key del archivo en S3.
- **URL** (string): URL permanente del archivo. Es provista por S3 al cargar el archivo.
- **Media type** (string): Content-Type del archivo.

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

- `NotFoundException`: 404 No encontrado
- `ConflictException`: 409 Conflicto
- `UnsupportedMediaTypeException`: 415 Tipo de medio no soportado
- `ForbiddenException`: 403 Prohibido
- `UnauthorizedException`: 401 No autorizado
- ...etc.

## Medidas de seguridad implementadas

### Seguridad de datos

UniLife emplea los siguientes mecanismos de seguridad:

- **[JWT](https://jwt.io/)** como estándar para autenticar requests.
- **BCrypt** (mediate `BCryptPasswordEncoder`, de Spring Security) como algoritmo de hash para cifrar contraseñas antes de guardarlas en la base de datos.
- Un sistema de **roles** con administradores, moderadores y usuarios. Usamos la anotación `@PreAuthorize` para permitir el acceso de cierto nivel de rol a las rutas que lo requieren.

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

Los hitos importantes del proyecto fueron representados como [issues](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/issues?q=is%3Aissue%20state%3Aclosed) en el repositorio. Los issues fueron asignados a distintos integrantes del grupo, fueron trabajados cada uno en su propia rama, y los cambios de cada rama fueron mergeados a la rama principal mediante [pull requests](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/pulls?q=is%3Apr+is%3Aclosed). Esto permitió a cada integrante del proyecto trabajar en sus propios tasks sin afectar al resto.

### GitHub Actions

El repositorio cuenta con un [workflow](https://github.com/CS2031-DBP/proyecto-backend-turinmachin/actions/workflows/deploy.yml) de GitHub Actions para testear, empaquetar y desplegar el proyecto automáticamente al haber cambios en la rama principal.

## Conclusión

### Logros del proyecto

A nivel de backend, hemos logrado lo siguiente:

- Construir el backend de una plataforma social para universitarios.
- Implementar un sistema diseñado para exploración libre de diferentes ambientes universitarios y carreras.
- Implementar medidas para retener a los usuarios y promover el uso frecuente de la app (sistema de racha).

### Aprendizajes clave

A través de este proyecto hemos logrado algunos aprendizajes importantes:

- Colaborar en equipo usando Git y GitHub.
  - Uso de comandos de Git para manejar y mergear branches apropiadamente.
- Planear y organizar flujos de trabajo para desarrollar software.
- Comunicación efectiva (para dar a entender nuestras ideas dentro del equipo).

### Trabajo futuro

Tenemos en mente algunas adiciones que podríamos implementar a futuro en este backend.

- **Chat en tiempo real:** Sería un añadido interesante a la plataforma una pequeña interfaz donde los usuarios puedan conversar en tiempo real. Podría darse mediante salas de chat públicas (por universidad, por ejemplo) y/o con mensajes privados entre usuarios.
- **Moderación con IA:** Sería útil aliviar la labor de los moderadores con algún tipo de IA que auto-modere el contenido en la plataforma.

## Apéndices

Este proyecto se encuentra bajo la [licencia MIT](https://mit-license.org/).

### Referencias

- [Baeldung](https://www.baeldung.com/), por servir como guía para varios conceptos usados en el proyecto.
