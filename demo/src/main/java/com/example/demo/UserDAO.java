package com.example.demo;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class UserDAO {
    private final String url = "jdbc:mysql://localhost:3306/community_db";
    private final String user = "user";
    private final String password = "1234";

    public boolean insertUser(User user) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, this.user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user (email, password, nickname) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getNickname());
            preparedStatement.executeUpdate();
        }
        return false;
    }
}
