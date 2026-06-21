<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${curso.id == null} ? 'Nuevo curso' : 'Editar curso'">Curso</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/styles.css}">
</head>
<body>

<div th:replace="~{fragments/header :: navbar}"></div>

<main class="container my-4" style="max-width: 720px;">

    <h1 class="mb-4">
        <i class="bi bi-mortarboard me-2"></i>
        <span th:text="${curso.id == null} ? 'Nuevo curso' : 'Editar curso'">Curso</span>
    </h1>

    <!--
      Truco: la misma vista sirve para CREATE y UPDATE.
      Si el curso no tiene id  -> POST a /cursos
      Si tiene id              -> POST a /cursos/{id}
    -->
    <form method="post"
          th:object="${curso}"
          th:action="${curso.id == null}
                     ? @{/cursos}
                     : @{/cursos/{id}(id=${curso.id})}">

        <!-- ID oculto, necesario para que JPA distinga INSERT vs UPDATE -->
        <input type="hidden" th:field="*{id}">

        <!-- Nombre -->
        <div class="mb-3">
            <label class="form-label">Nombre del curso</label>
            <input type="text"
                   class="form-control"
                   th:classappend="${#fields.hasErrors('nombre')} ? 'is-invalid'"
                   th:field="*{nombre}"
                   placeholder="Ej: Patrones de Diseno">
            <div class="invalid-feedback" th:errors="*{nombre}">Error</div>
        </div>

        <!-- Descripcion -->
        <div class="mb-3">
            <label class="form-label">Descripcion</label>
            <textarea class="form-control"
                      rows="3"
                      th:classappend="${#fields.hasErrors('descripcion')} ? 'is-invalid'"
                      th:field="*{descripcion}"></textarea>
            <div class="invalid-feedback" th:errors="*{descripcion}">Error</div>
        </div>

        <div class="row">
            <!-- Creditos -->
            <div class="col-md-4 mb-3">
                <label class="form-label">Creditos</label>
                <input type="number"
                       class="form-control"
                       min="1" max="8"
                       th:classappend="${#fields.hasErrors('creditos')} ? 'is-invalid'"
                       th:field="*{creditos}">
                <div class="invalid-feedback" th:errors="*{creditos}">Error</div>
            </div>

            <!-- Profesor -->
            <div class="col-md-8 mb-3">
                <label class="form-label">Profesor</label>
                <input type="text"
                       class="form-control"
                       th:classappend="${#fields.hasErrors('profesor')} ? 'is-invalid'"
                       th:field="*{profesor}">
                <div class="invalid-feedback" th:errors="*{profesor}">Error</div>
            </div>
        </div>

        <!--
          CLASE 6 - PARTE E (bonus Firebase): descomentar el campo de imagen.
          IMPORTANTE: el <form> de arriba necesita enctype="multipart/form-data"
          para que el upload funcione. Cuando descomentes esto, agregalo en el form.
        -->
        <!--
        <div class="mb-3">
            <label class="form-label">Imagen del curso (opcional)</label>
            <input type="file" name="imagen" accept="image/*" class="form-control">
        </div>
        -->

        <div class="d-flex justify-content-between">
            <a class="btn btn-secondary" th:href="@{/cursos}">
                <i class="bi bi-arrow-left me-1"></i> Cancelar
            </a>
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-check-circle me-1"></i>
                <span th:text="${curso.id == null} ? 'Crear' : 'Guardar cambios'">Guardar</span>
            </button>
        </div>
    </form>

</main>

<footer th:replace="~{fragments/header :: footer}"></footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

