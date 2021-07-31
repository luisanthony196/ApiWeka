# API Weka

API REST desarrollada con el fin de aprovechar las capacidades en minería de datos y aprendizaje automático que brinda la plataforma Weka, y ampliar su uso en navegadores web y dispositivos móviles.

## Requerimientos

- Java 11
- Maven
- La carpeta **wekafiles**
- Conexión a Base de Datos
    - Configurar adecuadamente el archivo **DatabaseUtils.prop**
    - Ingresar el *username* y el *password* en el archivo **application.properties**

## Scripts

Limpiar e instalar las librerías (dependencias)
- windows: `mvnw.cmd clean install`
- maven: `mvn clean install`

Ejecutar la aplicación como un servidor
- windows `mvnw.cmd spring-boot:run`
- maven: `mvn spring-boot:run`

## Rutas
Rutas para acceder por medio de una ejecucion en un entorno local, las rutas de acceso estan descritas en el Trello

### Hierarchical Service
| Ruta | Metodo | Parametros |
| ------ | ------ | ------ |
| localhost:8080/api/hierarchical | POST | link (String), clusters (int) |
| localhost:8080/api/hierarchical/list | POST |  |
* link: es el criterio de vinculacion (single, complete, average, mean, centroid, ward)
* clusters: es el numero de clusters requerido

### SimpleKMean Service
| Ruta | Metodo | Parametros |
| ------ | ------ | ------ |
| localhost:8080/api/simplekmean | POST | clusters (int) |
| localhost:8080/api/simplekmean/list | POST |  |
* link: es el criterio de vinculacion (single, complete, average, mean, centroid, ward)
* clusters: es el numero de clusters requerido# API Weka