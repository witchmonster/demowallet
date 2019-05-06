create table if not exists wallet
(
    id          int auto_increment
        primary key,
    currency    enum ('EUR', 'USD', 'GBP')          not null,
    last_update timestamp default CURRENT_TIMESTAMP not null,
    created     timestamp default CURRENT_TIMESTAMP not null,
    user_id     int                                 not null,
    balance     double    default 0                 not null,
    constraint wallet_user_id_currency_uindex
        unique (user_id, currency)
);
