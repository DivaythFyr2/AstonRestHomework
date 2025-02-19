CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       age INT NOT NULL,
                       weight DOUBLE PRECISION NOT NULL,
                       height DOUBLE PRECISION NOT NULL
);

CREATE TABLE workouts (
                          id SERIAL PRIMARY KEY,
                          type VARCHAR(50) NOT NULL,
                          duration INT NOT NULL,
                          calories_burned INT NOT NULL,
                          user_id INT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE meals (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       calories INT NOT NULL
);

CREATE TABLE user_meals (
                            user_id INT NOT NULL,
                            meal_id INT NOT NULL,
                            PRIMARY KEY (user_id, meal_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
);
