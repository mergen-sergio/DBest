# Movie Database Example

The examples in this tutorial are based on a movie database, available at the following GitHub repository:  
[https://github.com/bbrumm/databasestar](https://github.com/bbrumm/databasestar)  


The SQL files published in the repository were converted into DBest-indexed `.dat` files. These files incorporate a primary index, enabling efficient lookups and ordered reads.


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
## Notes on Indexed Files

Some `.dat` files serve as indexes (their names start with `idx`). These indexes correspond to another `.dat` file containing the full records. For instance:

- **`idx_year`**:  
  This file is a B+ tree index optimized for lookups by the `release_year` column.  
  - The `movie_id` column in `idx_year` acts as a reference to the `movie.dat` file.  
  - Using the `movie_id`, the `movie.dat` file can be queried to retrieve all associated columns from the `movie` table.

DBest leverages these indexes to enable efficient data access. For more details on DBest's indexing strategy, visit: [DBest Indexing Strategy](xxx).

## Notes on Foreign Keys

While the schema includes `FOREIGN KEY` constraints to denote relationships between tables, these are not enforced in practice. Instead, they serve as references to the relational structure of the data.

The .dat files can be found [here](https://github.com/mergen-sergio/DBest/tree/main/tutorial/assets/data). Unzip the files before loading them into the tool. 

## Using the `.dat` Files

To work with the `.dat` files:
1. Use the **'Open Indexed Data'** option in the top menu.  
2. Alternatively, drag and drop the files directly into the editor.

This approach ensures efficient access and supports the examples discussed throughout the tutorial.







