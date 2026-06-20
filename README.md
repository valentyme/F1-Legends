# F1 Legends

Sistema de Gestión y Simulación para un videojuego de Fórmula 1.

## Integrantes del grupo

Grupo "Los tres patrones" — TPO Proceso de Desarrollo de Software

- Castillo, Facundo
- Favre, Natalia
- Martinez, Valentina

Docente: Leon, Maria Angela

## Descripción breve del sistema

F1 Legends es una aplicación de escritorio desarrollada en Java con JavaFX que permite gestionar y simular carreras de Fórmula 1. El sistema contempla dos roles de usuario: **Jugador**, que puede crear su perfil, configurar e iniciar carreras en modo Singleplayer o Multijugador Local, y consultar el ranking global; y **Administrador**, que gestiona los datos maestros del sistema (usuarios, pilotos, escuderías, autos y circuitos).

Una vez configurada, la carrera se simula en tiempo real dentro de una ventana JavaFX (`CarreraFXWindow`): los autos avanzan según la habilidad del piloto, la estrategia de conducción elegida y el clima, pudiendo sufrir desgaste de neumáticos, entrar a boxes, sufrir accidentes o cambios de clima durante la competencia, hasta que la carrera finaliza y se actualiza el ranking del jugador.

La persistencia de datos (usuarios, pilotos, escuderías, autos, circuitos y ranking) se maneja sobre una base de datos **SQLite**.

## Instrucciones para ejecutar el proyecto

### Requisitos previos

- JDK 17 o superior
- Maven 3.8 o superior
- SQLite3 (cliente de línea de comandos) o DB Browser for SQLite, para crear la base de datos local

Las dependencias de JavaFX (`javafx-controls`, `javafx-fxml`), el driver `sqlite-jdbc` y JUnit 5 se descargan automáticamente vía Maven; no requieren instalación manual.

### 1. Clonar el repositorio

```bash
git clone https://github.com/valentyme/F1-Legends.git
cd F1-Legends
```

### 2. Crear la base de datos

La aplicación se conecta a un archivo SQLite llamado `f1_legends.db` ubicado en la raíz del proyecto (ver `ConexionBD.java`). Para crearlo con el esquema y los datos iniciales, parado en la raíz del proyecto:

```bash
sqlite3 f1_legends.db < BaseDeDatos/CreacionTablas.sql
sqlite3 f1_legends.db < BaseDeDatos/Bdd-Datos.sql
```

(Alternativamente, se puede abrir `f1_legends.db` con DB Browser for SQLite y ejecutar ambos scripts en ese orden: primero la creación de tablas y luego la carga de datos.)

### 3. Ejecutar la aplicación

```bash
mvn clean javafx:run
```

Este comando compila el proyecto y lanza la clase principal configurada en `pom.xml` (`com.f1legends.vista.MainFX`), que abre la pantalla de inicio de sesión.

También puede importarse como proyecto Maven en IntelliJ IDEA o Eclipse y ejecutarse desde el propio IDE, siempre que se mantenga el plugin `javafx-maven-plugin` para resolver el módulo de JavaFX.

### 4. Ejecutar los tests

```bash
mvn test
```

Corre la suite de JUnit 5 ubicada en `src/test/java/com/f1legends/test` (configuración de carrera, modo de juego, selección de circuito y selección de piloto).

## Patrones de diseño aplicados

| Patrón | Clases involucradas | Rol que cumple |
|---|---|---|
| **Facade** | `SistemaCarreraFacade` | Punto único de entrada para la capa de presentación: oculta la complejidad de coordinar `ModoJuegoService`, `PilotoService`, `CircuitoService`, `EscuderiaService`, `AutoService` y `ConfiguracionCarrera` al configurar e iniciar una carrera. |
| **State** | `EstadoCarrera` (interfaz), `EstadoInicio`, `EstadoBanderaVerde`, `EstadoPausada`, `EstadoFinalizada` | Modela el ciclo de vida de una `Carrera`. Cada estado decide cómo responder a `iniciar()`, `actualizar()`, `pausar()`, `reanudar()` y `abandonar()`, evitando condicionales sobre el estado actual dentro de `Carrera`. |
| **Observer** | `ObservableCarrera`, `ObservadorCarrera`, `EstadisticaObserver` | `Carrera` notifica eventos (`EventoCarrera`: vuelta completada, parada en boxes, accidente, cambio de clima, etc.) a sus observadores registrados, desacoplando la simulación de quienes reaccionan a sus eventos: la ventana `CarreraFXWindow` (vía una implementación lambda) y `EstadisticaObserver`, que acumula estadísticas para el resumen final. |
| **Strategy** | `EstrategiaConduccion` (interfaz), `EstrategiaAgresiva`, `EstrategiaEquilibrada`, `EstrategiaConservadora` | Encapsula distintas formas de calcular el rendimiento de un piloto según las condiciones de pista, intercambiables en tiempo de ejecución (`Piloto.calcularRendimiento(...)` delega en la estrategia asignada). |
| **Factory Method** | `FabricaUsuario` (crea `Jugador` / `Administrador`), `FabricaAuto` (crea `AutoReglamento2022` / `AutoReglamento2023` / `AutoReglamento2024` según `TipoAuto`), `CircuitoFactory` (crea `CircuitoMonza`, `CircuitoMonaco`, `CircuitoSpa`, `CircuitoSilverstone`, `CircuitoSuzuka` o `CircuitoGenerico` a partir de un `CircuitoDTO`) | Centralizan la decisión de qué subclase concreta instanciar según un parámetro de tipo (rol, reglamento o nombre de circuito), evitando que ese `switch`/cadena de `if` se repita en el resto del sistema. |

## Principios SOLID aplicados

- **SRP (Single Responsibility Principle):** cada DAO (`UsuarioDAO`, `PilotoDAO`, `CircuitoDAO`, `AutoDAO`, `RankingGlobalDAO`, etc.) tiene como única responsabilidad la persistencia de su entidad; la lógica de negocio vive en los `servicios` y `controller`, no en el DAO. `FabricaUsuario` tiene la única responsabilidad de construir instancias de `Usuario`.
- **OCP (Open/Closed Principle):** `Usuario` está cerrada a modificación y abierta a extensión a través de `Jugador` y `Administrador` (cada una redefine `accionesDisponibles()` sin tocar la clase base). Lo mismo ocurre con `Auto` (subclases por reglamento), `Circuito` (subclases por trazado) y `EstrategiaConduccion` (nuevas estrategias se agregan sin modificar `Piloto`). Agregar un nuevo rol, reglamento o circuito solo implica sumar un `case` en la fábrica correspondiente.
- **LSP (Liskov Substitution Principle):** en cualquier punto donde el sistema espera un `Usuario`, una `EstrategiaConduccion`, un `EstadoCarrera` o un `Circuito`, puede sustituirse por cualquiera de sus implementaciones concretas (`Jugador`/`Administrador`, `EstrategiaAgresiva`/`Equilibrada`/`Conservadora`, los distintos estados de carrera, o los distintos circuitos) sin alterar el comportamiento esperado por quien los invoca.
- **ISP (Interface Segregation Principle):** las interfaces del sistema son pequeñas y específicas: `ObservadorCarrera` define un único método (`actualizar`), `ObservableCarrera` separa el rol de sujeto observable, y `EstrategiaConduccion` expone solo lo necesario para calcular rendimiento, sin forzar a sus implementaciones a depender de métodos que no usan.
- **DIP (Dependency Inversion Principle):** `SistemaCarreraFacade` y los controladores dependen de abstracciones (`EstrategiaConduccion`, `EstadoCarrera`) en lugar de implementaciones concretas. Está documentado además como mejora pendiente en `UsuarioDAO`: que los servicios dependan de una interfaz `IUsuarioDAO` en lugar del DAO concreto.

## Principios GRASP aplicados

- **Controller:** `AuthController`, `PrepararCarreraController`, `AdminUsuariosController`, etc. reciben los eventos de la interfaz gráfica (FXML) y coordinan la respuesta del sistema, sin contener lógica de persistencia ni reglas de negocio propias del dominio.
- **Creator:** `FabricaUsuario` es responsable de crear `Jugador` y `Administrador`; `FabricaAuto` y `CircuitoFactory` cumplen el mismo rol para `Auto` y `Circuito` respectivamente, agrupando la lógica de creación en una única clase responsable.
- **Pure Fabrication:** los DAO (`UsuarioDAO`, `PilotoDAO`, `CircuitoDAO`, `RankingGlobalDAO`, etc.) y `ConexionBD` son clases "artificiales" que no representan un concepto del dominio, sino que existen exclusivamente para resolver la persistencia, manteniendo a las entidades de negocio libres de SQL.
- **Low Coupling:** las entidades de dominio (`Usuario`, `Piloto`, `Auto`, `Circuito`) no conocen la capa de persistencia ni JavaFX; `SistemaCarreraFacade` reduce el acoplamiento entre la interfaz gráfica y los distintos servicios al exponer una única fachada.
- **High Cohesion:** cada DAO agrupa únicamente las operaciones relacionadas con su propia tabla (por ejemplo, `RankingGlobalDAO` solo gestiona el ranking global), y cada servicio (`ModoJuegoService`, `PilotoService`, `CircuitoService`) concentra la lógica de un único aspecto de la configuración de carrera.
- **Polymorphism:** el método abstracto `Usuario.accionesDisponibles()` se resuelve de forma distinta en `Jugador` y `Administrador`; de la misma manera, `EstadoCarrera` y `EstrategiaConduccion` responden polimórficamente según la implementación concreta que tenga asignada la `Carrera` o el `Piloto` en cada momento.
- **Indirection:** el patrón Observer introduce un punto de indirección entre `Carrera` (que genera eventos) y quienes reaccionan a ellos (`CarreraFXWindow`, `EstadisticaObserver`), de modo que la carrera no conoce ni depende de sus observadores concretos.
