-- =====================================================
-- BASE DE DATOS DEL PROYECTO PRACTICASEMANAL
-- =====================================================

CREATE DATABASE IF NOT EXISTS cursoswebdb;

USE cursoswebdb;

-- =====================================================
-- TABLA PROFESORES
-- =====================================================

CREATE TABLE IF NOT EXISTS profesores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);

-- =====================================================
-- TABLA CURSOS
-- =====================================================

CREATE TABLE IF NOT EXISTS cursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    creditos INT NOT NULL,
    imagen_url VARCHAR(500),
    profesor_id BIGINT NOT NULL,

    CONSTRAINT fk_curso_profesor
        FOREIGN KEY (profesor_id)
        REFERENCES profesores(id)
);

-- =====================================================
-- TABLA USUARIOS
-- =====================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

-- =====================================================
-- PROFESORES DE PRUEBA
-- =====================================================

INSERT INTO profesores (
    nombre,
    email,
    especialidad
)
VALUES
(
    'Esteban',
    'esteban@ufide.ac.cr',
    'Programación'
),
(
    'María',
    'maria@ufide.ac.cr',
    'Bases de datos'
),
(
    'Carlos',
    'carlos@ufide.ac.cr',
    'Redes'
);

-- =====================================================
-- CURSOS DE PRUEBA
-- =====================================================

INSERT INTO cursos (
    nombre,
    descripcion,
    creditos,
    imagen_url,
    profesor_id
)
VALUES
(
    'Programación Web',
    'Curso de desarrollo de aplicaciones web con Spring Boot.',
    4,
    NULL,
    1
),
(
    'Bases de Datos',
    'Curso de diseño y administración de bases de datos.',
    3,
    NULL,
    2
),
(
    'Redes de Computadoras',
    'Curso de fundamentos y configuración de redes.',
    4,
    NULL,
    3
);


-- =====================================================
-- USUARIOS DE PRUEBA
-- Las contraseñas están almacenadas con BCrypt.
-- No se guardan contraseñas en texto plano.
-- =====================================================

INSERT INTO usuarios (
    username,
    password,
    rol
)
VALUES
(
    'admin',
    '$2b$12$OqwydCDf8.2QrYo638cEu.r7ZvAduMMIcBU4NMod0FlVj.WrbHbFe',
    'ADMIN'
),
(
    'usuario',
    '$2b$12$ls/sutJLIzp43o/OGvTCPO90xd7DKYHUfIppnU3J.G/AlzK68oxjm',
    'USER'
);

-- =====================================================
-- VERIFICACIONES
-- =====================================================

SELECT id, nombre, email, especialidad
FROM profesores;

SELECT
    c.id,
    c.nombre,
    c.creditos,
    p.nombre AS profesor
FROM cursos c
JOIN profesores p
    ON p.id = c.profesor_id;

SELECT id, username, rol
FROM usuarios;

-- =====================================================
-- EMAIL
-- =====================================================

INSERT INTO usuarios (username, password, rol, email)
VALUES
(
    'admin',
    '$2b$12$OqwydCDf8.2QrYo638cEu.r7ZvAduMMIcBU4NMod0FlVj.WrbHbFe',
    'ADMIN',
    'admin@ufide.ac.cr'
),
(
    'usuario',
    '$2b$12$ls/sutJLIzp43o/OGvTCPO90xd7DKYHUfIppnU3J.G/AlzK68oxjm',
    'USER',
    'usuario@ufide.ac.cr'
)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    rol = VALUES(rol),
    email = VALUES(email);


CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE,
    reset_token VARCHAR(255) UNIQUE,
    reset_token_expiracion DATETIME
);