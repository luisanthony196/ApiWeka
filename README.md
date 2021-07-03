# API Weka
API REST desarrollada con el fin de aprovechar las capacidades en minería de datos y aprendizaje automático que brinda la plataforma Weka, y ampliar su uso en navegadores web y dispositivos móviles.

## Requerimientos
- Java 11
- Maven

## Conexión a Base de Datos
- Configurar adecuadamente el archivo **DatabaseUtils.prop**
- Ingresar el *username* y el *password* en el archivo **application.properties**

## Scripts
Limpiar e instalar las librerías (dependencias)
- mvnw.cmd clean install

Ejecutar la aplicación como un servidor
- mvnw.cmd spring-boot:run

## Rutas
Agregar en {num} el numero de clusters
- localhost:8080/api/simplekmean/{num}
- localhost:8080/api/hierarchical/{num}