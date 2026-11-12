# Sistema de Gestión de Gimnasio — Arquitectura MVC y Patrones GoF

[![Java](https://img.shields.io/badge/Java-11%20%7C%2017-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-5382a1?logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![SQLite](https://img.shields.io/badge/Database-SQLite%203-003B57?logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Look & Feel](https://img.shields.io/badge/Theme-FlatLaf%20Light-4CAF50)](https://www.formdev.com/flatlaf/)
[![License](https://img.shields.io/badge/License-Academic-blue.svg)]()

> **Materia:** Arquitectura de Software  
> **Docente:** Ing. Josué Obed Veizaga Gonzales  
> **Estudiante:** Nataly Martinez  
> **Entorno de Modelado:** Enterprise Architect / Java SE  

---

## 📋 Descripción del Proyecto

Aplicación de escritorio desarrollada en **Java puro (sin frameworks como Spring o Hibernate)** bajo el patrón arquitectónico **Modelo-Vista-Controlador (MVC)** estricto. El sistema automatiza la gestión integral de un centro de entrenamiento físico (gimnasio), abarcando desde el registro de miembros y ejercicios hasta la planificación de rutinas y el diagnóstico biomecánico de medidas antropométricas.

El proyecto consolida los requerimientos del **1er Parcial** (Diseño Procedimental y Arquitectura MVC Base) y evoluciona en el **2do Parcial** incorporando patrones de diseño del **Gang of Four (GoF)** para desacoplar estados y algoritmos científicos intercambiables.

---

## 🏛️ Arquitectura del Sistema (MVC Puro)

La arquitectura sigue una separación estricta de responsabilidades en tres capas fundamentales:

```
┌────────────────────────────────────────────────────────────────────────┐
│                                 VISTA                                  │
│       (Paquete `view`: JFrame, JPanels, Tablas dinámicas FlatLaf)       │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │  Captura eventos (Listeners)
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                              CONTROLADOR                               │
│      (Paquete `controller`: Mediador, sin SQL, no acopla la vista)     │
└────────────┬─────────────────────────────────────────────┬─────────────┘
             │ Actualiza Modelo                            │ Consulta datos
             ▼                                             ▼
┌────────────────────────────────────────────────────────────────────────┐
│                                 MODELO                                 │
│  (Paquete `model`: Consultas SQL, persistencia SQLite, lógica pura)    │
│  ├── Singleton: DatabaseConnection                                     │
│  ├── Composición Fuerte: TrainingPlanModel ◆──> PlanDetailModel        │
│  ├── Patrón STATE: model.state.* (Ciclo de Vida de Planes)            │
│  └── Patrón STRATEGY: model.strategy.* (Evaluación Antropométrica)    │
└────────────────────────────────────────────────────────────────────────┘
```

### Reglas Arquitectónicas Cumplidas
1. **La Vista jamás importa al Modelo:** La UI no tiene conocimiento de sentencias SQL ni de clases del paquete `model`.
2. **El Controlador es un mediador estricto:** Lee los datos de la vista mediante getters, delega las operaciones en el modelo y actualiza la vista.
3. **Persistencia limpia en cada método:** Conexión gestionada mediante `DatabaseConnection.getInstance().getConnection()` en cada operación sin atributos de conexión compartidos.
4. **Composición Master-Detail:** `TrainingPlanModel` gobierna a `PlanDetailModel`. El controlador nunca se comunica con el detalle de forma aislada.

---

## 🎯 Módulos del Sistema (1er Parcial)

El sistema cuenta con 6 Casos de Uso (CU) implementados en paneles tabulados independientes:

| Módulo / CU | Descripción | Componentes Clave |
|---|---|---|
| **CU1: Gestión de Clientes** | Altas, bajas, modificaciones y listado de socios (CI, nombres, teléfono, dirección). | `ClientView`, `ClientController`, `ClientModel` |
| **CU2: Grupos Musculares** | Catálogo anatómico de músculos (Pecho, Espalda, Piernas, etc.). | `MuscleGroupView`, `MuscleGroupController`, `MuscleGroupModel` |
| **CU3: Catálogo de Ejercicios** | Registro de ejercicios vinculados a su grupo muscular y enlace a video tutorial. | `ExerciseView`, `ExerciseController`, `ExerciseModel` |
| **CU4: Planes de Entrenamiento** | Creación y actualización de rutinas maestras con detalles de series, repeticiones y descansos. | `TrainingPlanView`, `TrainingPlanController`, `TrainingPlanModel` |
| **CU5: Medidas y Progreso Físico** | Historial de medidas corporales (Peso, Grasa, Cintura, Glúteos, Pecho) por socio. | `MeasurementView`, `MeasurementController`, `MeasurementModel` |
| **CU6: Reportes y Exportación PDF** | Exportación de rutinas y fichas técnicas completas a archivos PDF. | `ReportView`, `ReportController`, `ReportModel` (iTextPDF) |

---

## 🚀 Patrones de Diseño GoF Implementados (2do Parcial)

Para el 2do Parcial se seleccionaron **dos patrones de comportamiento GoF** (excluyendo Singleton y Facade), aplicados a casos de negocio reales:

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                    PATRONES DE COMPORTAMIENTO (GoF)                          │
├──────────────────────────────────────┬───────────────────────────────────────┤
│    1. Patrón STATE (CU4: Planes)     │   2. Patrón STRATEGY (CU6: Medidas)   │
├──────────────────────────────────────┼───────────────────────────────────────┤
│ Contexto: PlanContext                │ Contexto: EvaluacionContext           │
│ Interfaz: EstadoPlan                 │ Interfaz: EvaluacionStrategy          │
│ Estados:                             │ Estrategias:                          │
│   • EstadoBorrador                   │   • EvaluacionOMSStrategy             │
│   • EstadoActivo                     │   • EvaluacionRiesgoCardioStrategy    │
│   • EstadoCompletado                 │   • EvaluacionGrasaStrategy           │
│   • EstadoCancelado                  │                                       │
└──────────────────────────────────────┴───────────────────────────────────────┘
```

---

### Patrón 1: STATE en CU4 (Gestión del Ciclo de Vida del Plan)

Controla las transiciones del ciclo de vida de un plan de entrenamiento de acuerdo con las reglas de negocio del gimnasio, eliminando sentencias condicionales dispersas.

#### Máquina de Estados:
```
                  activar()
         ┌─────────────────────────┐
         ▼                         │
    [Borrador] ───────────────> [Activo] ────────> [Completado] (Estado Final)
         ▲      reiniciarBorrador()  │   completar()
         │                           │
         │ cancelar()                │ cancelar()
         │                           ▼
         └────────────────────── [Cancelado]
              reiniciarBorrador()
```

#### Reglas del Patrón:
* **`EstadoBorrador`:** Es el único estado donde se permite agregar, quitar ejercicios o modificar datos del formulario (`puedeEditar() == true`).
* **`EstadoActivo`:** La rutina está en ejecución. No permite modificaciones en la tabla de ejercicios. Puede pasar a `Completado` o `Cancelado`.
* **`EstadoCompletado`:** Estado terminal. No admite transiciones salientes.
* **`EstadoCancelado`:** Puede ser reabierto mediante `reiniciarBorrador()` regresando a `Borrador`.
* **Persistencia bidireccional:** El estado se almacena como texto en la columna `status` de SQLite. Al cargar un plan, `planContext.cargarEstado(statusStr)` rehidrata la instancia polimórfica adecuada en memoria.

---

### Patrón 2: STRATEGY en CU6 (Diagnóstico Físico y Evaluación Antropométrica)

Permite intercambiar dinámicamente en tiempo de ejecución la fórmula científica utilizada para evaluar el estado físico del cliente a partir de sus medidas corporales registradas.

#### Estructura GoF (Págs. 354–362 del libro):
* **Contexto (`EvaluacionContext`):** Mantiene la referencia a `EvaluacionStrategy`. Su constructor recibe por parámetro la estrategia inicial (`EvaluacionContext(EvaluacionStrategy strategy)`) con salvaguarda defensiva ante valores nulos (GoF pág. 360).
* **Interfaz (`EvaluacionStrategy`):** Declara los métodos `getNombreMetodo()` y `evaluar(weight, waist, glutes, bodyFat, chest)`.
* **Estrategias Concretas:**
  1. `EvaluacionOMSStrategy`: Clasificación clínica estándar según el Índice de Masa Corporal (IMC) y riesgo por perímetro de cintura según la OMS.
  2. `EvaluacionRiesgoCardioStrategy`: Índice Cintura/Cadera (ICC) para cuantificar riesgo cardiovascular y distribución adiposa (Androide vs Ginoide).
  3. `EvaluacionGrasaStrategy`: Composición corporal en kilogramos de masa grasa versus masa magra y clasificación fitness.
* **Cliente (`MeasurementController`):** Atiende el evento del usuario, instancia la estrategia correspondiente según el `JComboBox` de la vista y la inyecta mediante `context.setStrategy(estrategiaConcreta)`.

---

## 📂 Estructura de Directorios

```
gimnasio-mvc/
├── database/
│   └── schema.sql              # Esquema DDL de tablas y llaves foráneas SQLite
├── lib/
│   ├── flatlaf-3.4.jar         # Librería Look & Feel moderna
│   ├── itextpdf-5.5.13.3.jar   # Motor de generación de reportes PDF
│   ├── sqlite-jdbc-3.45.3.0.jar# Driver JDBC para SQLite
│   ├── slf4j-api-2.0.9.jar     # Logging API requerida por SQLite JDBC
│   └── slf4j-simple-2.0.9.jar  # Implementación de logging
├── src/
│   ├── Main.java               # Punto de entrada de la aplicación
│   ├── controller/             # Controladores mediadores (MVC)
│   │   ├── ClientController.java
│   │   ├── ExerciseController.java
│   │   ├── MeasurementController.java
│   │   ├── MuscleGroupController.java
│   │   ├── ReportController.java
│   │   └── TrainingPlanController.java
│   ├── model/                  # Modelos de persistencia SQLite
│   │   ├── ClientModel.java
│   │   ├── DatabaseConnection.java
│   │   ├── ExerciseModel.java
│   │   ├── MeasurementModel.java
│   │   ├── MuscleGroupModel.java
│   │   ├── PlanDetailModel.java
│   │   ├── ReportModel.java
│   │   ├── TrainingPlanModel.java
│   │   ├── state/              # Implementación del Patrón STATE
│   │   │   ├── EstadoActivo.java
│   │   │   ├── EstadoBorrador.java
│   │   │   ├── EstadoCancelado.java
│   │   │   ├── EstadoCompletado.java
│   │   │   ├── EstadoPlan.java
│   │   │   └── PlanContext.java
│   │   └── strategy/           # Implementación del Patrón STRATEGY
│   │       ├── EvaluacionContext.java
│   │       ├── EvaluacionGrasaStrategy.java
│   │       ├── EvaluacionOMSStrategy.java
│   │       ├── EvaluacionRiesgoCardioStrategy.java
│   │       └── EvaluacionStrategy.java
│   └── view/                   # Vistas gráficas Swing (FlatLaf)
│       ├── ClientView.java
│       ├── ExerciseView.java
│       ├── MainFrame.java
│       ├── MeasurementView.java
│       ├── MuscleGroupView.java
│       ├── ReportView.java
│       ├── TrainingPlanView.java
│       ├── UITheme.java
│       └── flatlaf-custom.properties
├── gym.db                      # Base de datos local SQLite generada automáticamente
└── run.sh                      # Script de compilación y ejecución en bash
```

---

## 🛠️ Requisitos e Instalación

### Requisitos Previos
* **Java Development Kit (JDK):** Versión 11 o 17 LTS instalada.
* **Sistema Operativo:** Linux, macOS o Windows (con Git Bash / WSL).

### Ejecución Rápida (Linux / macOS)

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/gimnasio-mvc.git
   cd gimnasio-mvc/gym_mvc
   ```

2. Dar permisos de ejecución al script y correr:
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

### Compilación Manual por Terminal
```bash
# Definir classpath con librerías de lib/
CP="lib/flatlaf-3.4.jar:lib/sqlite-jdbc-3.45.3.0.jar:lib/itextpdf-5.5.13.3.jar:lib/slf4j-api-2.0.9.jar:lib/slf4j-simple-2.0.9.jar"

# Crear directorio de binarios y compilar
mkdir -p bin
javac -cp "$CP:src" -d bin $(find src -name "*.java")

# Ejecutar la aplicación
java -cp "bin:$CP" Main
```

---

## 📊 Base de Datos (SQLite)

El sistema inicializa automáticamente la base de datos `gym.db` en la primera ejecución ejecutando [database/schema.sql](database/schema.sql):

```sql
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS client (
    ci INTEGER PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS muscle_group (
    id_muscle_group INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS exercise (
    id_exercise INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    video_url VARCHAR(255),
    id_muscle_group INTEGER NOT NULL,
    FOREIGN KEY (id_muscle_group) REFERENCES muscle_group(id_muscle_group)
);

CREATE TABLE IF NOT EXISTS training_plan (
    id_plan INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    objective TEXT,
    ci INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'Borrador',
    FOREIGN KEY (ci) REFERENCES client(ci)
);

CREATE TABLE IF NOT EXISTS plan_detail (
    id_plan INTEGER NOT NULL,
    detail_id INTEGER NOT NULL,
    sets INTEGER NOT NULL,
    reps INTEGER NOT NULL,
    rest_time INTEGER NOT NULL,
    id_exercise INTEGER NOT NULL,
    PRIMARY KEY (id_plan, detail_id),
    FOREIGN KEY (id_plan) REFERENCES training_plan(id_plan) ON DELETE CASCADE,
    FOREIGN KEY (id_exercise) REFERENCES exercise(id_exercise)
);

CREATE TABLE IF NOT EXISTS measurement (
    id_measurement INTEGER PRIMARY KEY AUTOINCREMENT,
    ci INTEGER NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL,
    body_fat DECIMAL(5,2),
    chest_cm DECIMAL(5,2),
    glutes DECIMAL(5,2),
    waist_cm DECIMAL(5,2),
    measurement_date DATE NOT NULL,
    FOREIGN KEY (ci) REFERENCES client(ci)
);
```

---

## 📚 Documentación Técnica Adicional

* [07_guia_patrones_diseno_2do_parcial.md](07_guia_patrones_diseno_2do_parcial.md): Guía completa de modelado en Enterprise Architect (las 4 fotos obligatorias: diagramas genéricos y DCDs de análisis para CU4 y CU6).
* [08_explicacion_linea_a_linea_TrainingPlanController.md](08_explicacion_linea_a_linea_TrainingPlanController.md): Desglose minucioso línea a línea del controlador de planes, manejo de transiciones y composición de detalles.

---

## 👩‍💻 Autora

* **Nataly Martinez** — *Estudiante de Ingeniería de Sistemas / Software*  
  Universidad Mayor de San Simón — Cochabamba, Bolivia.
