package com.coursework.persistence.repository.imp;

import com.coursework.domain.entity.Role;
import com.coursework.persistence.repository.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class RoleDaoImp implements RoleDao {

    private final JdbcTemplate jdbcTemplate;

    RowMapper<Role> rowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("role"));
        return role;
    };

    @Autowired
    public RoleDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Set<Role> getAllRole() {
        return new HashSet<>(jdbcTemplate.query("SELECT * FROM roles", rowMapper));
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        String sql = """
                SELECT id, role
                FROM roles
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<Role> getRoleByRoleName(String role) {
        String sql = """
                SELECT id, role
                FROM roles
                WHERE role = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, role).stream().findFirst();
    }

    @Override
    public Set<Role> getAllUserRoleByUserId(Long id) {
        String sql = """
                SELECT role_id
                FROM users_roles
                WHERE user_id = ?
                """;

        Set<Role> roles = new HashSet<>();
        List<Map<String, Object>> rows =jdbcTemplate.queryForList(sql, id);
        for (Map<String, Object> row : rows) {
            roles.add( getRoleById((Long) row.get("role_id")).get());
        }
        return roles;
    }

    @Override
    public int save(Role role) {
        String sql = """
                INSERT INTO roles(role)
                VALUES (?)
                """;
        return jdbcTemplate.update(sql, role.getName());
    }

    @Override
    public int saveUserRole(Long user_id, Long role_id) {
        String sql = """
                INSERT INTO users_roles(user_id, role_id)
                VALUES (?, ?)
                """;
        return jdbcTemplate.update(sql, user_id, role_id);
    }

    @Override
    public int deleteUserRole(Long user_id, Long role_id) {
        String sql = """
                DELETE FROM users_roles
                WHERE user_id = ? AND role_id = ?;
                """;
        return jdbcTemplate.update(sql, user_id, role_id);
    }
}
