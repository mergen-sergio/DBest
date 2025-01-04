<div align="left">
    <a href="./01 - data-sources.md">Previous</a>
</div>
<div align="right">
  <a href="./02 - operators.md">Next</a>
</div>

# Movie Database Example

The examples in this tutorial are based on a movie database, available at the following GitHub repository:  
[https://github.com/bbrumm/databasestar](https://github.com/bbrumm/databasestar)  

The SQL files published in the repository were converted into DBest-indexed `.dat` files. These files incorporate a primary index, enabling efficient lookups and ordered reads.

## Downloading the files

The .dat files can be found [here](https://github.com/mergen-sergio/DBest/tree/main/tutorial/assets/data). Unzip the files before loading them into the tool. 

## Using the `.dat` Files

To work with the `.dat` files:
1. Use the **'Open Indexed Data'** option in the top menu.  
2. Alternatively, drag and drop the files directly into the editor.

This approach ensures efficient access the the movie database and supports the examples discussed throughout the tutorial.


## Understanding the movie database

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

The SQL format shown above is not directly supported by the tool. It is provided solely to describe the structure and content of each `.dat` file. Additionally, while the schema includes `FOREIGN KEY` constraints to illustrate relationships between tables, these are not enforced in practice. Instead, they serve as documentation for the relational structure of the data.

### Notes

All `.dat` files are indexed, and some act as non-clustered indexes (identified by filenames starting with `idx`). These indexes link to another clustered `.dat` file containing the complete records. For example:

- **`idx_year`**:  
  This is a non-clustered B+ tree index designed for efficient lookups using the `release_year` column.  
  - The `movie_id` column in `idx_year` serves as a reference to the clustered `movie.dat` file.  
  - By using the `movie_id`, you can query the `movie.dat` file to retrieve all associated columns from the `movie` table.

DBest utilizes these indexes to facilitate efficient data access. For additional information about DBest's indexing strategy, refer to the following link: [DBest Indexing Strategy](xxx).

<br>

<div align="left">
    <a href="./01 - data-sources.md">Previous</a>
</div>
<div align="right">
  <a href="./02 - operators.md">Next</a>
</div>









