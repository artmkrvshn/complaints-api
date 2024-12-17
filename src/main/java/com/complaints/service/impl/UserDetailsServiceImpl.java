package com.complaints.service.impl;

import com.complaints.entity.Customer;
import com.complaints.repository.CustomerRepository;
import com.complaints.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading customer by email: {}", email);

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        if (optionalCustomer.isPresent()) {
            return new UserDetailsImpl(optionalCustomer.get());
        }
        log.error("Customer not found with email: {}", email);
        throw new UsernameNotFoundException("Customer not found with email: " + email);
    }
}
