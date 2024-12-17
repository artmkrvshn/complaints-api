CREATE TABLE complaints
(
    id          BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    product_id  BIGINT  NOT NULL,
    customer_id BIGINT  NOT NULL,
    date        DATE    NOT NULL DEFAULT now(),
    description TEXT    NOT NULL,
    status      VARCHAR NOT NULL
);

INSERT INTO complaints (product_id, customer_id, date, description, status)
VALUES (101, 1, '2024-12-01', 'The product arrived damaged.', 'OPEN'),
       (102, 1, '2024-12-02', 'Received the wrong product.', 'CANCELED'),
       (103, 2, '2024-12-03', 'Product not functioning as expected.', 'REJECTED'),
       (104, 3, '2024-12-04', 'Delayed delivery beyond the promised date.', 'OPEN'),
       (105, 3, '2024-12-05', 'Incomplete items in the package.', 'IN_PROGRESS'),
       (106, 1, '2024-12-06', 'Duplicate charge on the invoice.', 'REJECTED'),
       (107, 4, '2024-12-07', 'Customer support not responding.', 'IN_PROGRESS'),
       (108, 4, '2024-12-08', 'Refund not processed yet.', 'ACCEPTED'),
       (109, 1, '2024-12-09', 'Received defective product.', 'OPEN'),
       (110, 1, '2024-12-10', 'Incorrect billing details.', 'ACCEPTED');