# Cibusana Postgres DB backup scripts

## Usage

Execute scripts this order

1. `create.sql`
2. `content/*.sql`

## Assumptions

Admin user is called `doadmin`

## Tech notes

Your server user (called `foodserver` in script) needs to be assigned the `read_write_journal` role.