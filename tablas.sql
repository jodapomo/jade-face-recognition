DROP TABLE asignacion;
DROP TABLE salon;
DROP TABLE usuario;
DROP TABLE facultad;


CREATE TABLE facultad(
	nombre VARCHAR(20) PRIMARY KEY
);




CREATE TABLE usuario(
	cedula INT PRIMARY KEY,
	nombre VARCHAR(20),
	rol VARCHAR(20),
	facultad VARCHAR(20),
	FOREIGN KEY(facultad) REFERENCES facultad(nombre)
	
);



CREATE TABLE salon(
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	numero INT,
	bloque INT,
	facultad VARCHAR(20),
	FOREIGN KEY(facultad) REFERENCES facultad(nombre)
);


CREATE TABLE asignacion(

	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	dia VARCHAR(20), 
	hora VARCHAR(20),
	salon INT UNSIGNED,
	usuario INT,
	FOREIGN KEY(salon) REFERENCES salon(id),
	FOREIGN KEY(usuario) REFERENCES usuario(cedula)
);


INSERT INTO facultad VALUES ('Minas');
INSERT INTO facultad VALUES ('Ciencias Exactas');
INSERT INTO facultad VALUES ('Ciencias Humanas');


INSERT INTO usuario VALUES (1,'Andres','Estudiante','Minas');
INSERT INTO usuario VALUES (2,'Jose','Estudiante','Minas');
INSERT INTO usuario VALUES (3,'Haisson','Estudiante','Minas');

INSERT INTO salon VALUES (1,101,03,'Minas');
INSERT INTO salon VALUES (2,102,03,'Minas');
INSERT INTO salon VALUES (3,101,03,'Ciencias Exactas');

INSERT INTO asignacion VALUES (1,'Lunes',6,1,1);
INSERT INTO asignacion VALUES (2,'Lunes',8,1,2);
INSERT INTO asignacion VALUES (3,'Lunes',10,1,3);
INSERT INTO asignacion VALUES (4,'Lunes',12,1,1);

INSERT INTO asignacion VALUES (NULL,'Martes',6,1,NULL);
INSERT INTO asignacion VALUES (NULL,'Miércoles',8,2,NULL);
INSERT INTO asignacion VALUES (NULL,'Jueves',10,3,NULL);
INSERT INTO asignacion VALUES (NULL,'Viernes',12,3,NULL);
