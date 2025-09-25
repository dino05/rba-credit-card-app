CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    oib VARCHAR(11) NOT NULL UNIQUE,
    card_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    instm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clients_oib ON clients(oib);
CREATE INDEX idx_clients_status ON clients(card_status);