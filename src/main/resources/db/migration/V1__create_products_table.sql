CREATE TABLE products (
                          id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          name        VARCHAR(255)    NOT NULL,
                          description VARCHAR(1000)   NULL,
                          price       DECIMAL(10, 2)  NOT NULL,
                          stock       INT UNSIGNED    NOT NULL DEFAULT 0,
                          created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          CONSTRAINT uq_products_name UNIQUE (name)
) ENGINE = InnoDB;