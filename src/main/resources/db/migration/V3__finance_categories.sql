create table if not exists finance_categories (
    id bigserial primary key,
    value varchar(80) not null unique,
    label varchar(120) not null,
    active boolean not null default true
);

alter table if exists finance_transactions
    alter column category type varchar(80);

insert into finance_categories (value, label)
values
    ('SALES', 'Sales'),
    ('RAW_MATERIALS', 'Raw Materials'),
    ('PAINT_ITEMS', 'Paint Items'),
    ('ELECTRICITY', 'Electricity'),
    ('LABOR', 'Labor'),
    ('MACHINE_MAINTENANCE', 'Machine Maintenance'),
    ('OVERHEAD', 'Overhead'),
    ('DELIVERY', 'Delivery')
on conflict (value) do nothing;