
package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V20220513180536__Insert_photos extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        String sql = """
                UPDATE users SET image = ?
                WHERE username = ?
                """;

        byte[] defaultUserImageBytes = this.getClass().getClassLoader().getResourceAsStream("img/default.jpg").readAllBytes();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));

        jdbcTemplate.update(sql, defaultUserImageBytes, "user");
        jdbcTemplate.update(sql, defaultUserImageBytes, "admin");
        jdbcTemplate.update(sql, defaultUserImageBytes, "super_admin");

    }
}