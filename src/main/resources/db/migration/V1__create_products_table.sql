CREATE TABLE product (
                         id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         name        VARCHAR(150)    NOT NULL,
                         description VARCHAR(1000)   NULL,
                         price       DECIMAL(10, 2)  NOT NULL,
                         stock       INT UNSIGNED    NOT NULL DEFAULT 0,
                         created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (id)
) ENGINE = InnoDB;