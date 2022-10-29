# Cibusana Postgres DB backup scripts

## Usage

Execute scripts this order

1. `create/schema.sql`
2. `create/*.sql`
3. `content/*.sql`

## Assumptions

Admin user is called `doadmin`

## Tech notes

Your server user needs to be assigned the `read_write_journal` role.