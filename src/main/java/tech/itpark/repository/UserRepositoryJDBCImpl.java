package tech.itpark.repository;

import tech.itpark.entity.UserEntity;
import tech.itpark.exception.DataAccessException;
import tech.itpark.exception.UserNotFoundException;
import tech.itpark.jdbc.RowMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserRepositoryJDBCImpl implements UserRepository {
    private final Connection connection;
    private final RowMapper<UserEntity> mapper = rs -> {
        try {
            return new UserEntity(
                    rs.getLong("id"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("secret"),
                    Set.of((String[]) rs.getArray("roles").getArray()),
                    rs.getBoolean("removed"),
                    rs.getLong("created")
            );
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    };

    public UserRepositoryJDBCImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<UserEntity> findAll() {
        try (
                final Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery(
                        "SELECT id, login, password, name, secret, roles, EXTRACT(EPOCH FROM created) created, removed FROM users ORDER BY id"
                );
        ) {
            List<UserEntity> result = new LinkedList<>();
            while (rs.next()) {
                final UserEntity entity = mapper.map(rs);
                result.add(entity);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // В процессе, пока не проверяйте
    @Override
    public Optional<UserEntity> findById(Long id) {
        try (
                final Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery("SELECT id, login, password, name, secret, roles, EXTRACT(EPOCH FROM created) created, removed FROM users WHERE id = ?");
        ) {
            final Optional<UserEntity> entity = Optional.ofNullable(mapper.map(rs));

            if (entity.isEmpty()) {
                throw new UserNotFoundException();
            }

            return entity;

        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }

    @Override
    public UserEntity save(UserEntity entity) {
        if (entity.getId() == 0) {

            try (final Statement stmt = connection.createStatement();
                 final ResultSet rs = stmt.executeQuery("INSERT INTO users (id, login, password, name, secret, roles, EXTRACT(EPOCH FROM created) created, removed) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ) {
                long nextId = 0;
                final UserEntity item = mapper.map(rs);
                item.setId(++nextId);
                // Save item
                final UserEntity saveNewUser = save(item);

                return item;
            } catch (SQLException e) {

            }
        }

        final Optional<UserEntity> existUser = findById(entity.getId());
        return null;
    }

    @Override
    public boolean removeById(Long aLong) {
        return false;
    }

    @Override
    public boolean existsByLogin(String login) {
        return false;
    }

    @Override
    public Optional<UserEntity> findByLogin(String login) {
        return Optional.empty();
    }
}

