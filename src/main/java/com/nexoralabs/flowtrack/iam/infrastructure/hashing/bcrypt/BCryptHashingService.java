package com.nexoralabs.flowtrack.iam.infrastructure.hashing.bcrypt;

import com.nexoralabs.flowtrack.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Interface that combines HashingService and PasswordEncoder for BCrypt implementation
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}

