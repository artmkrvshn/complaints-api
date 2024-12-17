package com.complaints.repository;

import com.complaints.entity.Complaint;
import com.complaints.entity.Customer;
import com.complaints.entity.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
public class RepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("complaints-test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        entityManager.createQuery("DELETE FROM Complaint").executeUpdate();
        entityManager.createQuery("DELETE FROM Customer").executeUpdate();
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Test
    void testSaveAndRetrieveComplaint() {
        Customer customer = new Customer();
        customer.setEmail("customer@example.com");
        customer.setPassword("123456");
        customer.setName("Test Customer");
        customer = customerRepository.save(customer);

        Complaint complaint = new Complaint();
        complaint.setProductId(100L);
        complaint.setCustomer(customer);
        complaint.setDate(LocalDate.now());
        complaint.setDescription("Test complaint description");
        complaint.setStatus(Status.OPEN);

        Complaint savedComplaint = complaintRepository.save(complaint);

        assertThat(savedComplaint.getId()).isNotNull();
        assertThat(savedComplaint.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(savedComplaint.getProductId()).isEqualTo(100L);
        assertThat(savedComplaint.getDescription()).isEqualTo("Test complaint description");
        assertThat(savedComplaint.getStatus()).isEqualTo(Status.OPEN);
    }

    @Test
    void testFindComplaintsByCustomer() {
        Customer customer = new Customer();
        customer.setEmail("customer2@example.com");
        customer.setPassword("123456");
        customer.setName("Customer With Complaints");
        customer = customerRepository.save(customer);

        Complaint complaint1 = new Complaint();
        complaint1.setProductId(101L);
        complaint1.setCustomer(customer);
        complaint1.setDate(LocalDate.now());
        complaint1.setDescription("First Complaint");
        complaint1.setStatus(Status.OPEN);

        Complaint complaint2 = new Complaint();
        complaint2.setProductId(102L);
        complaint2.setCustomer(customer);
        complaint2.setDate(LocalDate.now());
        complaint2.setDescription("Second Complaint");
        complaint2.setStatus(Status.CANCELED);


        complaintRepository.saveAll(List.of(complaint1, complaint2));

        List<Complaint> complaints = complaintRepository.findAll();

        assertThat(complaints).hasSize(2);
        assertThat(complaints.get(0).getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(complaints.get(1).getDescription()).isEqualTo("Second Complaint");
    }

}
