package com.pcdiy.service.impl;

import com.pcdiy.dto.AuthRequest;
import com.pcdiy.dto.AuthResponse;
import com.pcdiy.entity.User;
import com.pcdiy.service.AuthService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final JdbcTemplate jdbcTemplate;

    public AuthServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AuthResponse register(AuthRequest request) {
        validateCredentials(request);
        String username = request.getUsername().trim();
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String passwordHash = hashPassword(request.getPassword());
        String phone = normalize(request.getPhone());
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (username, password_hash, phone, role, status) VALUES (?, ?, ?, 'user', 1)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, phone);
            return statement;
        }, keyHolder);

        User user = new User();
        Number id = keyHolder.getKey();
        user.setId(id == null ? null : id.longValue());
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setPhone(phone);
        user.setRole("user");
        user.setStatus(1);
        return toResponse(user);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        validateCredentials(request);
        User user = findByUsername(request.getUsername().trim());
        if (user == null || !user.getPasswordHash().equals(hashPassword(request.getPassword()))) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("Account is disabled");
        }
        return toResponse(user);
    }

    private User findByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, username, password_hash, phone, role, status FROM users WHERE username = ? LIMIT 1",
                    (rs, rowNum) -> {
                        User user = new User();
                        user.setId(rs.getLong("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(rs.getString("password_hash"));
                        user.setPhone(rs.getString("phone"));
                        user.setRole(rs.getString("role"));
                        user.setStatus(rs.getInt("status"));
                        return user;
                    },
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void validateCredentials(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getPhone(), user.getRole(), createToken(user));
    }

    private String createToken(User user) {
        String raw = user.getId() + ":" + user.getUsername() + ":" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
