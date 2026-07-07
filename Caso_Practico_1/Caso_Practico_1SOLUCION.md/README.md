SOLUCION - Caso Práctico 1 EventApp

Endpoints implementados

En el proyecto EventApp implemente los endpoints necesarios para administrar los eventos registrados en la aplicación.
El endpoint principal permite consultar la lista completa de eventos almacenados en la base de datos. Desde esta vista el usuario puede visualizar la información de cada evento y acceder a las opciones de crear, editar, eliminar y ver detalles.
También implementé un endpoint para filtrar eventos por categoría. Este permite recibir una categoría como parámetro y mostrar únicamente los eventos que pertenecen a esa clasificación. Para realizar este filtro se utilizó un parámetro dentro de la dirección web y la consulta se realiza mediante el servicio de eventos, manteniendo la separación entre controlador, lógica de negocio y acceso a datos.
Además, se mantuvieron los endpoints necesarios para consultar el detalle de un evento, crear nuevos registros, actualizar información existente y eliminar eventos.

Validaciones utilizadas en la entidad Evento

En la entidad Evento agregue validaciones para asegurar que los datos ingresados por el usuario sean correctos antes de guardarlos en la base de datos.
Para los campos de texto utilice validaciones que evitan valores vacíos y también controlan la cantidad máxima de caracteres permitidos. Esto ayuda a mantener información consistente y evita que se ingresen datos incompletos.
En el campo de fecha utilice una validación que obliga a que los eventos tengan una fecha futura, ya que no tendría sentido registrar nuevos eventos con fechas anteriores.
Para los cupos y precios aplique validaciones numéricas para impedir valores negativos o incorrectos. El precio permite valores en cero porque existen eventos gratuitos.



Modal de confirmación de eliminación

Para la eliminación de eventos implemente un modal utilizando Bootstrap. Esto permite solicitar una confirmación al usuario antes de borrar un registro.
El botón de eliminar envía información del evento seleccionado mediante atributos personalizados de datos. Utilice atributos relacionados con Bootstrap para abrir el modal y atributos de información para almacenar el identificador y nombre del evento seleccionado.
Cuando el modal se muestra, JavaScript obtiene esos datos y prepara la acción de eliminación correspondiente para que el usuario pueda confirmar la operación.

Decisiones técnicas realizadas

Utilice LocalDate para manejar las fechas de los eventos, ya que permite trabajar con fechas sin involucrar información de hora y facilita las validaciones de fechas futuras.
Se mantuvo la estructura MVC separando las responsabilidades del sistema. El controlador recibe las solicitudes, el servicio contiene la lógica de la aplicación y el repositorio se encarga de comunicarse con la base de datos.
Para realizar las búsquedas por categoría utilice un método derivado de Spring Data JPA, aprovechando la capacidad del framework para generar consultas automáticamente a partir del nombre del método.
La interfaz fue desarrollada utilizando Thymeleaf junto con Bootstrap para crear las vistas de listado, formulario y detalle de eventos.


