package com.coursework.persistence.repository.imp;

import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImp implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastSName(rs.getString("second_name"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setAbout(rs.getString("about"));
        Timestamp date = (Timestamp) rs.getObject("registration_date");
        user.setRegistrationDate(date.toLocalDateTime());
        user.setActive(rs.getBoolean("active"));
        user.setImage(rs.getBytes("image"));
        return user;
    };

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", rowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                SELECT id, username, first_name, second_name, password, email, about, registration_date, active, image
                FROM users
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        String sql = """
                SELECT id, username, first_name, second_name, password, email, about, registration_date, active, image
                FROM users
                WHERE username = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, userName).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String mail) {
        String sql = """
                SELECT id, username, first_name, second_name, password, email, about, registration_date, active, image
                FROM users
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, mail).stream().findFirst();
    }

    @Override
    public int save(User user) {
        String sql = """
                INSERT INTO users(username, first_name, second_name, password, email, about, registration_date, active, image)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getFirstName(),
                user.getLastSName(),
                user.getPassword(),
                user.getEmail(),
                user.getAbout(),
                Timestamp.valueOf(user.getRegistrationDate()),
                user.isActive(),
                user.getImage());
    }

    @Override
    public int update(User user) {
        String sql = """
                UPDATE users SET username = ?, first_name = ?, second_name = ?, password = ?, email = ?, about = ?, registration_date = ?, active = ?, image = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getFirstName(),
                user.getLastSName(),
                user.getPassword(),
                user.getEmail(),
                user.getAbout(),
                Timestamp.valueOf(user.getRegistrationDate()),
                user.isActive(),
                user.getImage(),
                user.getId());
    }

    @Override
    public int update(EditUserDto user) {
        String sql = """
                UPDATE users SET first_name = ?, second_name = ?, email = ?, about = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAbout(),
                user.getId());
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public int deleteByUsername(String username) {
        return jdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }

    @Override
    public int saveImageForUserByUsername(byte[] imageBytes, String username) {
        String sql = """
                UPDATE users SET image = ?
                WHERE username = ?
                """;
        return jdbcTemplate.update(sql, imageBytes, username);
    }

    @Override
    public Integer getNumberOfEntity() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.TYPE);
    }
}
