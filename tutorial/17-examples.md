## Expressing Queries in DBest  

This section presents examples of different types of queries that can be expressed using **DBest**. Some queries involve SQL representations, while others explore alternative request structures.  

DBest allows multiple ways to express the same query. We will demonstrate various approaches to highlight the flexibility in operator arrangements and query execution strategies.  

---

### Handling SQL Subqueries  

Complex SQL queries often include **subqueries**, which involve nested processing and correlated variables. DBest provides a set of operators to efficiently handle subqueries and capture their nuances.  

#### Handling `EXISTS` and `IN`  

The first example retrieves movies that have at least one cast member. The query below uses `EXISTS`, where the subquery checks if a matching `movie_id` exists in the `movie_cast` table. If a match is found, the movie is included in the result set.  

```sql
SELECT * FROM movie m  
WHERE EXISTS (  
    SELECT 1 FROM movie_cast mc  
    WHERE m.movie_id = mc.movie_id  
);
```

Since the correlated condition uses **equality**, the query can also be expressed with `IN`, as shown below. The movie is considered relevant if its `movie_id` appears in a list of `movie_ids` associated with cast members.

```sql
SELECT * FROM movie  
WHERE movie_id IN (  
    SELECT movie_id FROM movie_cast  
);
```

#### Expressing the Query in DBest 

There are two primary ways to represent this query in **DBest**:

- **Using a Nested Loop Semi Join**: The join directly searches for matching `movie_id`s in the `movie_cast` table.  
- **Using a Hash Operator**: The `movie_cast` tuples are materialized in a **hash table**, allowing a **Nested Loop Semi Join** to perform efficient lookups.  
  - Alternatively, a **Hash Left Semi Join** can be used instead of the **Nested Loop Semi Join + Hash** combination.  


[Query plan visualization here]

### Materialization vs. Pipelining

- **Materialization** is preferable when there are many lookups since it avoids repeated index accesses.  
- **Pipelined execution** is better for a small number of lookups, as it eliminates materialization overhead.  

Historically, **SQL optimizers** used **materialization** for `IN` subqueries and **pipelining** for `EXISTS` subqueries. However, modern optimizers can dynamically select the best execution plan, regardless of the SQL syntax used.  


### Finding Movies Released in the Same Year as *Casablanca*

The following SQL query retrieves movies released in the same year as *Casablanca*. Although it uses an `IN` subquery, an equivalent version with `EXISTS` is also possible.  


```sql
SELECT * FROM movie  
WHERE release_year IN (  
    SELECT release_year FROM movie  
    WHERE title = 'Casablanca'  
);
```

### Expressing the Query in DBest

One way to represent this query in DBest is:

- **Hash Operator**: Materializes movies titled *Casablanca* in a hash table indexed by `release_year`.  
- **Nested Loop Semi Join**: Finds movies matching the same `release_year`.  

Since the number of movies titled *Casablanca* is expected to be small, the cost of materialization remains low.  


[Query plan visualization here]


### Finding Movies Released Before Casablanca  

The next query retrieves movies released before *Casablanca*. Unlike previous examples, this cannot be expressed using `IN` because the condition is a range comparison, not an equality check. However, `EXISTS` can handle it:  

```sql
SELECT * FROM movie2 m1  
WHERE EXISTS (  
    SELECT 1 FROM movie2 m2  
    WHERE m2.title = 'Casablanca'  
    AND m1.release_year < m2.release_year  
);
```


This example demonstrates that `EXISTS` has greater expressive power than `IN`.  

### Expressing the Query in DBest  

DBest represents this query using materialization:  

- **Materialize Operation:** Stores *Casablanca* movies for efficient access.  
- **Filter Operator:** Checks whether the `release_year` of each movie satisfies the condition.  
- **Nested Loop Semi Join:** Applied without an explicit condition, since the range check is handled by the internal filter.  

Since only a small number of tuples need to be materialized, the filtering step remains efficient.  



[Query plan visualization here]





