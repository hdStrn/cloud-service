CREATE TABLE IF NOT EXISTS users(
    email    VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (email)
);
CREATE TABLE IF NOT EXISTS authorities(
    email     VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (email, authority),
    FOREIGN KEY (email) REFERENCES users (email)
);

INSERT IGNORE INTO users (email, password, enabled)
VALUES ('admin@netology.ru', '$2y$10$gKJsPvguAvNLYxOozcPAOu3C/uIS4xpq064Cw/faAlcd2/PfbioPa', 1);
INSERT IGNORE INTO authorities (email, authority)
VALUES ('admin@netology.ru', 'ADMIN');

INSERT IGNORE INTO users (email, password, enabled)
VALUES ('user@netology.ru', '$2y$10$gKJsPvguAvNLYxOozcPAOu3C/uIS4xpq064Cw/faAlcd2/PfbioPa', 1);
INSERT IGNORE INTO authorities (email, authority)
VALUES ('user@netology.ru', 'USER');
