package com.caili.utils.bcrypt;
/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Implementation of PasswordEncoder that uses the BCrypt strong hashing function. Clients
 * can optionally supply a "strength" (a.k.a. log rounds in BCrypt) and a SecureRandom
 * instance. The larger the strength parameter the more work will have to be done
 * (exponentially) to hash the passwords. The default value is 10.
 *
 *  哈希函数密码编译器
 * @author
 */
public class BCryptPasswordEncoder {
    private final Log logger = LogFactory.getLog(getClass());
    private final int strength;
    //SecureRandom类提供加密的强随机数生成器
    private final SecureRandom random;

    private Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    public BCryptPasswordEncoder() {
        this(-1);
    }

    /**
     * @param strength the log rounds to use, between 4 and 31
     */
    public BCryptPasswordEncoder(int strength) {
        this(strength, null);
    }

    /**
     * @param strength the log rounds to use, between 4 and 31
     * @param random   the secure random instance to use
     */
    public BCryptPasswordEncoder(int strength, SecureRandom random) {
        if (strength != -1 && (strength < BCrypt.MIN_LOG_ROUNDS || strength > BCrypt.MAX_LOG_ROUNDS)) {
            throw new IllegalArgumentException("Bad strength");
        }
        this.strength = strength;
        this.random = random;
    }

    //把密码进行加盐
    public String encode(CharSequence rawPassword) {
        String salt;
        if (strength > 0) {
            if (random != null) {
                //生成用于BCrypt.hashpw()方法的salt
                salt = BCrypt.gensalt(strength, random);
            } else {
                // 与上面不同的是 他没有相同值的情况下调用
                salt = BCrypt.gensalt(strength);
            }
        } else {
            //生成与BCrypt.hashpw()方法一起使用的salt，选择一个合理的
            salt = BCrypt.gensalt();
        }
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            logger.warn("Empty encoded password");
            return false;
        }

        //判断是否符合要求的正则表达式规则
        if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            logger.warn("Encoded password does not look like BCrypt");
            return false;
        }
        //解密验证   检查明文密码是否与之前散列的密码匹配
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}