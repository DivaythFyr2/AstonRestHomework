-- Добавляем пользователей
INSERT INTO users (name, age, weight, height) VALUES
                                                  ('Alice', 25, 60.5, 165),
                                                  ('Bob', 30, 75.2, 180),
                                                  ('Charlie', 35, 85.3, 175);

-- Добавляем тренировки для пользователей
INSERT INTO workouts (type, duration, calories_burned, user_id) VALUES
                                                                    ('running', 30, 360, 1),
                                                                    ('cycling', 45, 315, 2),
                                                                    ('swimming', 60, 480, 3);

-- Добавляем блюда
INSERT INTO meals (name, calories) VALUES
                                       ('Chicken Salad', 350),
                                       ('Steak', 600),
                                       ('Oatmeal', 250),
                                       ('Pasta', 500);

-- Связываем пользователей с приёмами пищи
INSERT INTO user_meals (user_id, meal_id) VALUES
                                              (1, 1),
                                              (1, 3),
                                              (2, 2),
                                              (3, 4);