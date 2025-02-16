-- Наполняем таблицу пользователей
INSERT INTO users (name, age, weight, height) VALUES
                                                  ('John Doe', 30, 80.0, 180.0),
                                                  ('Alice Smith', 28, 65.5, 170.0),
                                                  ('Bob Johnson', 35, 90.2, 175.0);

-- Наполняем таблицу тренировок
INSERT INTO workouts (user_id, type, duration, calories_burned) VALUES
                                                                    (1, 'running', 30, 300),
                                                                    (1, 'cycling', 45, 270),
                                                                    (2, 'swimming', 40, 320);

-- Наполняем таблицу еды
INSERT INTO meals (name, calories) VALUES
                                       ('Chicken Salad', 350),
                                       ('Pasta', 500),
                                       ('Protein Shake', 250);

-- Связываем пользователей с едой
INSERT INTO user_meals (user_id, meal_id) VALUES
                                              (1, 1), -- John Doe ел Chicken Salad
                                              (1, 2), -- John Doe ел Pasta
                                              (2, 3); -- Alice Smith выпила Protein Shake
