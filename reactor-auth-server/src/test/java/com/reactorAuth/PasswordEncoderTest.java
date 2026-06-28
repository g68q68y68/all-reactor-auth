package com.reactorAuth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具 —— 运行测试生成 BCrypt 密文，用于数据库更新
 *
 * 使用方式：直接运行每个 Test 方法，复制控制台输出的密文或 SQL 语句执行
 */
class PasswordEncoderTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 单次加密 —— 每次运行生成不同的密文（salt 随机）
     */
    @Test
    void encodePassword() {
        String rawPassword = "admin123";
        String encoded = encoder.encode(rawPassword);

        System.out.println("========== BCrypt 加密结果 ==========");
        System.out.println("明文: " + rawPassword);
        System.out.println("密文: " + encoded);
        System.out.println("验证: " + encoder.matches(rawPassword, encoded));
        System.out.println("======================================");
    }

    /**
     * 批量生成加密密码 —— 同时生成 UPDATE SQL
     */
    @Test
    void generateUpdateSql() {
        // 在这里修改你要加密的用户名和密码
        String[][] users = {
                {"admin", "admin123"},
                {"user", "user123"},
        };

        System.out.println("========== BCrypt 密码生成 & SQL ==========\n");

        for (String[] user : users) {
            String username = user[0];
            String rawPassword = user[1];
            String encoded = encoder.encode(rawPassword);

            System.out.println("-- " + username + " / " + rawPassword);
            System.out.println("UPDATE users SET password = '" + encoded + "' WHERE username = '" + username + "';");
            System.out.println();
        }

        System.out.println("-- 验证：运行后登录测试即可");
        System.out.println("===============================================");
    }
}
