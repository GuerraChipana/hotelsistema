# Hotel Sistema - Backend (Módulo de Autenticación)

Primer módulo del backend en Spring Boot 3.5.6 + Java 21. Solo cubre **auth**
(registro y login de clientes) sobre la tabla `usuarios` que ya existe en Railway.
Las otras 6 tablas se agregan en los siguientes pasos.

## Configuración

Antes de correrlo, define estas variables de entorno (o edita los valores por
defecto en `src/main/resources/application.yml`):

| Variable | Ejemplo | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://containers-us-west-1.railway.app:1234/railway` | URL JDBC de tu MySQL en Railway |
| `DB_USERNAME` | `root` | Usuario de la BD |
| `DB_PASSWORD` | `********` | Password de la BD |
| `JWT_SECRET` | (mínimo 32 caracteres) | Clave para firmar los JWT |
| `JWT_EXPIRATION_MS` | `86400000` | Duración del token en ms (24h por defecto) |

**Importante:** `ddl-auto` está en `validate`, no en `update` ni `create`.
Esto significa que Hibernate **solo compara** las entidades contra las tablas
que ya existen — nunca las va a crear ni modificar. Si hay un desajuste entre
la entidad `Usuario` y la tabla real, la app falla al arrancar (con un mensaje
claro de qué columna no coincide), en vez de romper tu esquema en silencio.

## Correr en local

```bash
mvn spring-boot:run
```

## Probar los endpoints

### Registro (crea un usuario con rol CLIENTE)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellidos": "Perez Lopez",
    "email": "juan.perez@example.com",
    "telefono": "987654321",
    "password": "MiClaveSegura123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "MiClaveSegura123"
  }'
```

Ambos devuelven:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 61,
    "nombre": "Juan",
    "apellidos": "Perez Lopez",
    "email": "juan.perez@example.com",
    "telefono": "987654321",
    "rol": "CLIENTE"
  }
}
```

Para llamar endpoints protegidos, manda el token así:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Crear staff (solo ADMINISTRADOR)
```bash
curl -X POST http://localhost:8080/api/admin/usuarios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-de-un-admin>" \
  -d '{
    "nombre": "Lucia",
    "apellidos": "Ramos Torres",
    "email": "lucia.recepcion@hotel.com",
    "telefono": "912345678",
    "password": "ClaveStaff2026",
    "rol": "RECEPCIONISTA"
  }'
```
Devuelve `201 Created` con los datos del usuario creado (sin token, porque no es
esa persona la que se está logueando). Si el `rol` enviado es `CLIENTE`,
responde `400` — para eso está el registro público. Si el token no es de un
`ADMINISTRADOR`, responde `403`.

**Para probar esto necesitas al menos un admin existente.** Los 5 usuarios con
`rol = 'administrador'` que ya están en tu tabla `usuarios` (del script de datos)
no tienen `password_hash` real generado con BCrypt — son hashes SHA-256 de
ejemplo, no sirven para hacer login. Corre este UPDATE una vez para poder
loguearte con uno de ellos (la contraseña va a quedar como `Admin123456`):

```sql
UPDATE usuarios
SET password_hash = '$2a$10$5jfNI/zFcZipr8BPnSWMAOe4KmZvLEQ9AV/R5PqwKmMMkDS5zfJRG',
    google_auth = 0
WHERE rol = 'administrador'
LIMIT 1;
```

## Decisiones de diseño (para que sepas el porqué)

- **`rol` sigue siendo un ENUM dentro de `usuarios`**, no una tabla aparte — tal
  como lo definiste. Se traduce automáticamente vía `RolConverter`
  (Java `CLIENTE` <-> MySQL `cliente`).
- **El registro público solo crea clientes.** Crear cuentas de administrador o
  recepcionista se hará en un endpoint aparte, protegido, en un paso futuro —
  no tiene sentido que cualquiera se registre como admin desde un formulario público.
- **Login con Google todavía no está implementado.** La columna `google_auth`
  y que `password_hash` sea `NULL` ya están contempladas en el modelo; el flujo
  OAuth2 de Google se agrega como un paso aparte más adelante.
- **Nunca se expone la entidad `Usuario` en una respuesta HTTP** (contiene
  `password_hash`). Todo pasa por `UsuarioResponse`.
- **No pude compilar este proyecto en este entorno** porque no tengo acceso a
  Maven Central desde este sandbox. Revisé a mano que todos los paquetes,
  clases e imports coincidan, pero corre `mvn clean compile` de tu lado como
  primer paso y avísame si sale algún error.

## Módulo de auth: completo ✅

1. ~~Endpoint protegido para que un administrador cree recepcionistas/otros admins~~ ✅

## Próximos pasos (en orden, cuando digas)

2. Entidad `Habitacion` + endpoints CRUD (rol admin/recepcionista)
3. Entidad `Categoria` + `Producto`
4. Entidad `Reserva` (la más compleja: validar solapamiento de fechas)
5. `ReservaProducto` (carrito)
6. `Pago` (simulación Yape/tarjetas)
7. Swagger / OpenAPI (al final, documentando todo junto)

