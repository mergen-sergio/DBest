# Movie Database Example

The examples in this tutorial are based on a movie database, which is available at the following webpage:  

The SQL files published in the repository were converted into DBest-indexed `.dat` files. Each file includes a primary index for efficient lookups and ordered reads.

Below is the schema of the database, presented in SQL-like format:

```sql
CREATE TABLE person (
  person_id INT,
  person_name TEXT,
  PRIMARY KEY (person_id)
);

CREATE TABLE movie (
  movie_id INT,
  title TEXT,
  release_year INT,
  PRIMARY KEY (movie_id)
);

CREATE TABLE movie_cast (
  movie_id INT,
  person_id INT,
  character_name TEXT,
  gender_id INT,
  cast_order INT,
  PRIMARY KEY (movie_id, person_id)
);

CREATE TABLE movie_crew (
  movie_id INT,
  person_id INT,
  department_id INT,
  job TEXT DEFAULT NULL,
  PRIMARY KEY (movie_id, person_id)
);

CREATE TABLE idx_cast_order (
  cast_order INT,
  movie_id INT,
  person_id INT,
  PRIMARY KEY (cast_order),
  FOREIGN KEY (movie_id, person_id) REFERENCES movie_cast (movie_id, person_id)
);

CREATE TABLE idx_year (
  release_year INT,
  movie_id INT,
  PRIMARY KEY (release_year),
  FOREIGN KEY (movie_id) REFERENCES movie (movie_id)
);

CREATE TABLE idx_cast_p (
  person_id INT,
  movie_id INT,
  PRIMARY KEY (person_id),
  FOREIGN KEY (movie_id, person_id) REFERENCES movie_cast (movie_id, person_id)
);

CREATE TABLE idx_crew_p (
  person_id INT,
  movie_id INT,
  PRIMARY KEY (person_id),
  FOREIGN KEY (movie_id, person_id) REFERENCES movie_crew (movie_id, person_id)
);

```

## Notes on Foreign Keys

While the schema includes `FOREIGN KEY` constraints to indicate relationships between columns and primary keys in target tables, no actual foreign key constraints are enforced. These commands simply serve as references to the relational structure.

## Using the `.dat` Files

To utilize the `.dat` files, you can either:

1. Use the **'Open Indexed Data'** option in the top menu.  
2. Drag and drop the files directly into the editor.  

This setup ensures efficient data access and supports the examples used throughout the tutorial.






