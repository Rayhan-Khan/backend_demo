package com.healthfix.service.impl;

import com.healthfix.entity.QUser;
import com.healthfix.entity.User;
import com.healthfix.repository.UserRepository;
import com.healthfix.service.UserService;
import com.healthfix.utils.ServiceHelper;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmailExist(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> searchUserList(String email, Integer page, Integer size, String sortBy) {
        QUser user = QUser.user;
        // Build QueryDSL Predicate
        Predicate predicate = user.email.containsIgnoreCase(email);
        // Use QueryDSL to fetch paginated results
        ServiceHelper<User> helper = new ServiceHelper<>(User.class);
        Page<User> result = userRepository.findAll(predicate, helper.getPageable(sortBy, page, size));

        // Prepare paginated response
        return helper.getList(result, page, size);
    }
}
