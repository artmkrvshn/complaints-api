package com.complaints.security;

import com.complaints.entity.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDetailsImplTest {

    private Customer customer;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("password123");
        userDetails = new UserDetailsImpl(customer);
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("Customer");
    }

    @Test
    void testGetPassword() {
        String password = userDetails.getPassword();
        assertThat(password).isEqualTo("password123");
    }

    @Test
    void testGetUsername() {
        String username = userDetails.getUsername();
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void testCustomerField() {
        assertThat(userDetails.customer()).isEqualTo(customer);
    }
}
