-- Esto hace que todas las referencias sin prefijo usen por defecto el esquema clinica.
SET search_path TO clinica;

-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS clinica;

DROP FUNCTION IF EXISTS clinica.actualizar_fecha_modificacion() CASCADE;


-- Trigger para actualizar el campo fecha modificación
CREATE OR REPLACE FUNCTION clinica.actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
  NEW.fecha_modificacion := CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- BORRADO
DROP TABLE clinica.cita CASCADE;
DROP TABLE clinica.informe CASCADE;
DROP TABLE clinica.negocio CASCADE;
DROP TABLE clinica.paciente CASCADE;
DROP TABLE clinica.paciente_usuario CASCADE;
DROP TABLE clinica.usuario CASCADE;
DROP TYPE IF EXISTS clinica.rol_usuario CASCADE;
DROP TYPE IF EXISTS clinica.estado_cita CASCADE;

-- Enumeradores para roles y estados
CREATE TYPE clinica.rol_usuario AS ENUM ('admin', 'profesional', 'recepcion');
CREATE TYPE clinica.estado_cita AS ENUM ('pendiente', 'confirmada', 'cancelada');


-- Tabla negocio
CREATE TABLE clinica.negocio (
    id serial4 NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT,
    telefono VARCHAR(20),
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT negocio_pk PRIMARY KEY (id)
);

CREATE TRIGGER negocio_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.negocio
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();



-- Tabla usuario (profesionales)
-- No es necesario que pertenezca a un negocio, puede ser un trabajador de la empresa sin más
-- Si se borra un negocio no se borra el usuario, pero se le pone la fk a null
CREATE TABLE clinica.usuario (
    id serial4 NOT NULL,
    nombre varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    password varchar(255) NOT NULL,
    rol clinica.rol_usuario NOT NULL,
    negocio_id int4, --  NOT NULL
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT usuario_pk PRIMARY KEY (id),
    CONSTRAINT usuario_email_uk UNIQUE (email),
    CONSTRAINT usuario_negocio_id_fk FOREIGN KEY (negocio_id) REFERENCES clinica.negocio(id) ON DELETE SET NULL
);

CREATE INDEX usuario_email_idx ON clinica.usuario (email);

CREATE TRIGGER usuario_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.usuario
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();



-- Tabla paciente
CREATE TABLE clinica.paciente (
    id serial4 NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    fecha_nacimiento DATE,
    alta boolean DEFAULT TRUE,
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT paciente_pk PRIMARY KEY (id)
);

CREATE INDEX paciente_nombre_idx ON clinica.paciente(nombre);

CREATE TRIGGER paciente_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.paciente
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();



-- Relación N:M entre pacientes y usuarios
-- Si se elimina un paciente o usuario se elimina su paciente_usuario
CREATE TABLE clinica.paciente_usuario (
    paciente_id INTEGER,
    usuario_id INTEGER,
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT paciente_usuario_pk PRIMARY KEY (paciente_id, usuario_id),
    CONSTRAINT paciente_usuario_paciente_id FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE CASCADE,
    CONSTRAINT paciente_usuario_usuario_id FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE CASCADE
);

CREATE TRIGGER paciente_usuario_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.paciente_usuario
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();



-- Tabla cita
-- Si se elimina un paciente o usuario se eliminan sus citas
CREATE TABLE clinica.cita (
    id serial4 NOT NULL,
    paciente_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    fecha_cita TIMESTAMP NOT NULL,
    motivo TEXT,
    estado clinica.estado_cita NOT NULL DEFAULT 'pendiente',
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT cita_pk PRIMARY KEY (id),
    CONSTRAINT cita_paciente_id_fk FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE CASCADE,
    CONSTRAINT cita_usuario_id_fk FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE CASCADE
);

CREATE INDEX cita_fecha_cita_idx ON clinica.cita(fecha_cita);
CREATE INDEX cita_paciente_id_idx ON clinica.cita(paciente_id);
CREATE INDEX cita_usuario_id_idx ON clinica.cita(usuario_id);

CREATE TRIGGER cita_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.cita
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();



-- Tabla informe clínico
-- Si se elimina un usuario o paciente, no se eliminan los informes, pero su fk se pone a null
-- De esta forma el historial de informes siempre será guardado al no ser que se borre explicitamente
CREATE TABLE clinica.informe (
    id serial4 NOT NULL,
    usuario_id INTEGER,
    nombre_usuario VARCHAR(100),
    paciente_id INTEGER,
    nombre_paciente VARCHAR(100),
    contenido TEXT NOT NULL,
    fecha_creacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT informe_pk PRIMARY KEY (id),
    CONSTRAINT informe_usuario_id_fk FOREIGN KEY (usuario_id) REFERENCES clinica.usuario(id) ON DELETE SET NULL,
    CONSTRAINT informe_paciente_id_fk FOREIGN KEY (paciente_id) REFERENCES clinica.paciente(id) ON DELETE SET NULL
);

CREATE INDEX informe_paciente_id_idx ON clinica.informe(paciente_id);
CREATE INDEX informe_usuario_id_idx ON clinica.informe(usuario_id);

CREATE TRIGGER informe_fecha_modificacion_trigger
BEFORE UPDATE ON clinica.informe
FOR EACH ROW
EXECUTE FUNCTION clinica.actualizar_fecha_modificacion();


-- INSERTS
INSERT INTO clinica.negocio (nombre, direccion, telefono) VALUES
('Clínica Mediterráneo', 'Calle Mayor 12, Alicante', '965123456'),
('Salud Costa Blanca', 'Av. de la Estación 45, Elche', '966789012'),
('Centro Médico Valencia', 'Calle Colón 8, Valencia', '963456789'),
('Clínica San Vicente', 'Av. Ancha de Castelar 101, San Vicente', '965987654'),
('Clínica Levante', 'Calle San Fernando 33, Murcia', '968123456');


INSERT INTO clinica.usuario (nombre, email, password, rol, negocio_id) VALUES
('user', 'user@clinica.com', 'user', 'admin', 1),
('Laura Martínez', 'laura.martinez@clinica.com', 'pass123', 'admin', 1),
('Carlos Gómez', 'carlos.gomez@clinica.com', 'pass123', 'profesional', 2),
('Ana Ruiz', 'ana.ruiz@clinica.com', 'pass123', 'recepcion', 3),
('Javier Torres', 'javier.torres@clinica.com', 'pass123', 'profesional', 4),
('Marta López', 'marta.lopez@clinica.com', 'pass123', 'recepcion', 5),
('Luis Sánchez', 'luis.sanchez@clinica.com', 'pass123', 'admin', 1),
('Isabel Fernández', 'isabel.fernandez@clinica.com', 'pass123', 'profesional', 2),
('Pedro Navarro', 'pedro.navarro@clinica.com', 'pass123', 'recepcion', 3),
('Sonia Romero', 'sonia.romero@clinica.com', 'pass123', 'profesional', 4);

INSERT INTO clinica.paciente (nombre, dni, fecha_nacimiento) VALUES
('María García', '12345678A', '1985-03-12'),
('José Rodríguez', '23456789B', '1978-07-25'),
('Lucía Fernández', '34567890C', '1992-11-05'),
('Antonio Martínez', '45678901D', '1980-01-30'),
('Carmen López', '56789012E', '1995-06-18'),
('Francisco Sánchez', '67890123F', '1975-09-09'),
('Laura Pérez', '78901234G', '1988-12-22'),
('Manuel Gómez', '89012345H', '1990-04-14'),
('Elena Ruiz', '90123456I', '1983-08-03'),
('Jorge Torres', '01234567J', '1979-02-17');


INSERT INTO clinica.paciente_usuario (paciente_id, usuario_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10);


-- Citas de ejemplo para el mes actual
INSERT INTO clinica.cita (paciente_id, usuario_id, fecha_cita, motivo, estado) VALUES
(1, 1, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '0 days' + TIME '09:00:00', 'Chequeo general', 'pendiente'),
(2, 1, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '0 days' + TIME '11:00:00', 'Dolor de cabeza', 'confirmada'),
(3, 2, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 days' + TIME '10:30:00', 'Control de embarazo', 'pendiente'),
(4, 2, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 days' + TIME '09:15:00', 'Revisión post-operatoria', 'cancelada'),
(5, 3, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 days' + TIME '16:00:00', 'Consulta nutricional', 'pendiente'),
(6, 3, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '4 days' + TIME '08:45:00', 'Dolor lumbar', 'confirmada'),
(7, 4, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '4 days' + TIME '09:30:00', 'Revisión pediátrica', 'pendiente'),
(8, 4, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '6 days' + TIME '10:15:00', 'Control de tensión', 'cancelada'),
(9, 5, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '7 days' + TIME '11:00:00', 'Dolor abdominal', 'pendiente'),
(10, 5, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '8 days' + TIME '09:45:00', 'Consulta psicológica', 'confirmada'),
(1, 6, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '9 days' + TIME '12:00:00', 'Revisión cardiológica', 'pendiente'),
(2, 6, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '9 days' + TIME '09:00:00', 'Chequeo anual', 'cancelada'),
(3, 7, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '9 days' + TIME '15:00:00', 'Dolor cervical', 'pendiente'),
(4, 7, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '12 days' + TIME '09:30:00', 'Consulta dermatológica', 'confirmada'),
(5, 8, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '13 days' + TIME '10:00:00', 'Revisión ginecológica', 'pendiente'),
(6, 8, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '14 days' + TIME '11:15:00', 'Dolor en rodilla', 'cancelada'),
(7, 9, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '15 days' + TIME '09:00:00', 'Consulta de alergias', 'pendiente'),
(8, 9, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '15 days' + TIME '10:30:00', 'Revisión oftalmológica', 'confirmada'),
(9, 10, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '15 days' + TIME '09:45:00', 'Chequeo post-vacuna', 'pendiente'),
(10, 10, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '18 days' + TIME '11:00:00', 'Dolor de espalda', 'cancelada'),
(1, 1, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '19 days' + TIME '09:30:00', 'Consulta de nutrición', 'pendiente'),
(2, 2, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '20 days' + TIME '10:15:00', 'Revisión pediátrica', 'confirmada'),
(3, 3, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '21 days' + TIME '09:00:00', 'Chequeo general', 'pendiente'),
(4, 4, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '21 days' + TIME '11:30:00', 'Dolor abdominal', 'cancelada'),
(5, 5, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '23 days' + TIME '09:15:00', 'Consulta psicológica', 'pendiente'),
(6, 6, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '24 days' + TIME '16:00:00', 'Revisión cardiológica', 'confirmada'),
(7, 7, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '25 days' + TIME '08:45:00', 'Chequeo anual', 'pendiente'),
(8, 8, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '26 days' + TIME '09:30:00', 'Dolor cervical', 'cancelada'),
(9, 9, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '28 days' + TIME '10:15:00', 'Consulta dermatológica', 'pendiente'),
(10, 10, DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '28 days' + TIME '12:00:00', 'Revisión ginecológica', 'confirmada');



INSERT INTO clinica.informe (usuario_id, nombre_usuario, paciente_id, nombre_paciente, contenido) VALUES
(1, 'user', 1, 'María García', 'Paciente en buen estado general. Se recomienda seguimiento.'),
(2, 'Laura Martínez', 2, 'José Rodríguez', 'Dolor lumbar persistente. Se prescribe fisioterapia.'),
(3, 'Carlos Gómez', 3, 'Lucía Fernández', 'Revisión sin hallazgos relevantes.'),
(4, 'Ana Ruiz', 4, 'Antonio Martínez', 'Tensión arterial controlada.'),
(5, 'Javier Torres', 5, 'Carmen López', 'Dermatitis leve. Se indica crema tópica.'),
(6, 'Marta López', 6, 'Francisco Sánchez', 'Preoperatorio completado. Apto para cirugía.'),
(7, 'Luis Sánchez', 7, 'Laura Pérez', 'Dolor abdominal remitido.'),
(8, 'Isabel Fernández', 8, 'Manuel Gómez', 'Revisión ginecológica normal.'),
(9, 'Pedro Navarro', 9, 'Elena Ruiz', 'Consulta pediátrica sin incidencias.'),
(10, 'Sonia Romero', 10, 'Jorge Torres', 'Colesterol elevado. Se recomienda dieta.');
