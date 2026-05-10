package com.nexoralabs.flowtrack.iam.application.internal.queryservices;

import com.nexoralabs.flowtrack.iam.domain.model.aggregates.User;
import com.nexoralabs.flowtrack.iam.domain.model.queries.GetAllUsersQuery;
import com.nexoralabs.flowtrack.iam.domain.model.queries.GetUserByIdQuery;
import com.nexoralabs.flowtrack.iam.domain.services.UserQueryService;
import com.nexoralabs.flowtrack.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserQueryService
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }
}

