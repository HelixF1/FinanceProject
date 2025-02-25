CREATE TABLE portfolio (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE portfolio_stock (
    id BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    purchase_date DATE NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES portfolio(id)
);

CREATE TABLE stock_history (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    date DATE NOT NULL
);

CREATE INDEX idx_portfolio_user_id ON portfolio(user_id);
CREATE INDEX idx_stock_history_symbol_date ON stock_history(symbol, date); 