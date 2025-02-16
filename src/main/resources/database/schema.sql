-- Создаём таблицу пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    age INT CHECK (age > 0),
    weight DOUBLE PRECISION CHECK (weight > 0),
    height DOUBLE PRECISION CHECK (height > 0)
    );

-- Создаём таблицу тренировок (One-to-Many с пользователем)
CREATE TABLE IF NOT EXISTS workouts (
                                        id SERIAL PRIMARY KEY,
                                        user_id INT NOT NULL,
                                        type VARCHAR(255) NOT NULL,
    duration INT NOT NULL CHECK (duration > 0), -- Длительность в минутах
    calories_burned INT NOT NULL CHECK (calories_burned >= 0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Создаём таблицу еды
CREATE TABLE IF NOT EXISTS meals (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    calories INT NOT NULL CHECK (calories >= 0)
    );

-- Связующая таблица пользователей и еды (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_meals (
                                          user_id INT NOT NULL,
                                          meal_id INT NOT NULL,
                                          PRIMARY KEY (user_id, meal_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
    );