package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    public static Sql2oUserRepository sql2oUserRepository;

    private static Connection connection;

    @BeforeAll
    public static void init() throws Exception {
        var properties = new Properties();
        try (var input = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(input);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);

        connection = DriverManager.getConnection(url, username, password);
    }

    @AfterEach
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("truncate table users restart identity")) {
            statement.execute();
        }
    }

    @Test
    void addUser() {
        var user = new User(0, "email", "name", "password");
        sql2oUserRepository.save(user);
        assertThat(sql2oUserRepository.findByEmailAndPassword("email", "password").get())
                .usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void addDuplUser() {
        var user = new User(0, "email", "name", "password");
        var user2 = new User(0, "email", "name2", "password2");
        sql2oUserRepository.save(user);
        var savedUser = sql2oUserRepository.save(user2);
        assertThat(sql2oUserRepository.findByEmailAndPassword("email", "password").get())
                .usingRecursiveComparison().isEqualTo(user);
        assertThat(savedUser.isEmpty()).isTrue();
    }
}