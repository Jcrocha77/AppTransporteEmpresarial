# Transporte empresarial

Aplicacion web responsive para consultar la ubicacion de un transporte privado empresarial y reportar incidencias.

## Requisitos

- Node.js 20.19+ y npm 10+
- JDK 17+
- Maven 3.9+

## Ejecucion local

1. Iniciar el backend desde `backend/`:

   ```powershell
   mvn spring-boot:run
   ```

   El perfil predeterminado usa H2 en memoria para probar reportes sin credenciales de nube.

2. Iniciar el frontend desde `frontend/`:

   ```powershell
   npm install
   npm start
   ```

3. Abrir `http://localhost:4200`.

## Integraciones pendientes

- `TransportLocationClient` define la interfaz para consultar el API externo de coordenadas. El adaptador actual responde `503` hasta que se conecte el servicio real.
- `AccidentNotificationClient` define la interfaz `notificarAccidente`; el adaptador actual responde `503` hasta integrar el API.
- Los reportes de retraso y unidad sucia se guardan con Spring Data JPA. Para usar MySQL, iniciar con el perfil `mysql` y configurar `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en el entorno. Ejemplo de URL JDBC: `jdbc:mysql://host:3306/transporte?useSSL=true&serverTimezone=UTC`. No guardar secretos en el repositorio.
- Las imágenes de reportes de unidad sucia se almacenan en `uploads/dirty-unit` (ajustable mediante `UPLOAD_DIR`); la base de datos conserva el reporte y los nombres generados.

## Flujos

- Consulta de transporte por número de empleado y empresa.
- Reporte de retraso con los tres motivos indicados y persistencia en base de datos.
- Reporte de accidente preparado para notificacionAccidente.
- Reporte de unidad sucia con descripción de hasta 150 caracteres y hasta tres imágenes.

## Endpoints locales

- `POST /api/transport/location`: recibe `employeeNumber` y `company` en JSON. Su cliente pendiente responde `503` hasta integrar la API de coordenadas.
- `POST /api/reports/delay`: persiste el motivo (`MISSED_TRANSPORT`, `TRANSPORT_LATE` o `HEAVY_TRAFFIC`) en la base activa.
- `POST /api/reports/accident`: delega a `AccidentNotificationClient.notificarAccidente`; responde `503` mientras no exista el adaptador externo.
- `POST /api/reports/dirty-unit`: recibe `multipart/form-data`; admite descripción de 1 a 150 caracteres y hasta 3 imágenes de 10 MB cada una.

## Conexión a MySQL

Configura estas variables en el entorno del proceso backend y activa el perfil `mysql`:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'mysql'
$env:DB_URL = 'jdbc:mysql://HOST:3306/BASE?useSSL=true&serverTimezone=UTC'
$env:DB_USERNAME = 'USUARIO'
$env:DB_PASSWORD = 'SECRETO'
mvn spring-boot:run
```

No guardes credenciales ni datos reales de empleados en el repositorio. La configuración JPA usa `ddl-auto: update` para el prototipo; antes de producción, reemplázala por migraciones versionadas.

## Consideraciones antes de producción

- No se implementa autenticación ni autorización. Agrega identidad corporativa y controles de acceso antes de exponer datos o reportes reales.
- El adaptador de coordenadas y el cliente de accidentes son stubs explícitos; el UI informa que esas integraciones no están disponibles en vez de simular éxito.
- Las evidencias se guardan en disco local. Para despliegue distribuido, configura `UPLOAD_DIR` hacia almacenamiento duradero/compartido o sustituye `UploadStorageService` por almacenamiento de objetos.
- Configura un proveedor de mapas y revisa sus cuotas, atribución y términos antes de producción.
