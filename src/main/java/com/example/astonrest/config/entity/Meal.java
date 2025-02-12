package com.example.astonrest.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meal {
    private int id;
    private String name;
    private int calories;
    private List<Integer> userIds;  // ID пользователей, которые ели это блюдо

}
