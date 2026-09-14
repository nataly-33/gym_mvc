CREATE TABLE IF NOT EXISTS Client (
    ci INTEGER PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone TEXT,
    address TEXT
);

CREATE TABLE IF NOT EXISTS MuscleGroup (
    id_muscle_group INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS Exercise (
    id_exercise INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    difficulty TEXT CHECK(difficulty IN ('Beginner','Intermediate','Advanced')),
    video_url TEXT,
    id_muscle_group INTEGER NOT NULL,
    FOREIGN KEY (id_muscle_group) REFERENCES MuscleGroup (id_muscle_group)
);

CREATE TABLE IF NOT EXISTS TrainingPlan (
    id_plan INTEGER PRIMARY KEY AUTOINCREMENT,
    plan_name TEXT NOT NULL,
    objective TEXT,
    date TEXT NOT NULL,
    ci_client INTEGER NOT NULL,
    FOREIGN KEY (ci_client) REFERENCES Client(ci)
);

-- PK COMPUESTA: composición con training_plan
CREATE TABLE IF NOT EXISTS PlanDetail (
    id_plan INTEGER NOT NULL,
    id_detail INTEGER NOT NULL,
    sets INTEGER NOT NULL,
    reps INTEGER NOT NULL,
    rest_time INTEGER NOT NULL,
    exercise_order INTEGER DEFAULT 1,
    id_exercise INTEGER NOT NULL,
    PRIMARY KEY (id_plan, id_detail),
    FOREIGN KEY (id_plan) REFERENCES TrainingPlan (id_plan),
    FOREIGN KEY (id_exercise) REFERENCES Exercise (id_exercise)
);

CREATE TABLE IF NOT EXISTS Measurement (
    id_measurement INTEGER PRIMARY KEY AUTOINCREMENT,
    weight REAL,
    body_fat REAL,
    chest REAL,
    glutes REAL,
    waist REAL,
    date TEXT NOT NULL,
    ci_client INTEGER NOT NULL,
    FOREIGN KEY (ci_client) REFERENCES Client(ci)
);
